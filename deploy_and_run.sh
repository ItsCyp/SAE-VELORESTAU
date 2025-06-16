#!/bin/bash

set -e

if [ "$#" -ne 3 ]; then
    echo "Usage: $0 <hosts-file> <local-project-dir> <remote-dir>"
    # Exemple : bash deploy_and_run.sh hosts.txt SAE-VELORESTAU/ service/
    exit 1
fi

HOSTS_FILE="$1"
LOCAL_DIR="$2"
REMOTE_DIR="$3"

if [ ! -f "$HOSTS_FILE" ]; then
    echo "Hosts file '$HOSTS_FILE' not found"
    exit 1
fi

# Demander le mot de passe de session une seule fois
echo "Le script a besoin de votre mot de passe pour :"
echo "1. Démarrer rmiregistry sur le port 1099"
echo "2. Communiquer avec les autres machines"
echo "Entrez votre mot de passe de session :"
read -s USER_PASS
echo

# Compiler et démarrer le serveur central sur la machine locale
echo "Compilation et démarrage du serveur central..."
cd "$LOCAL_DIR/serveurHttp"
javac *.java

# Démarrer rmiregistry en arrière-plan
echo "$USER_PASS" | sudo -S rmiregistry 1099 >/tmp/rmiregistry.log 2>&1 &
RMI_PID=$!

# Attendre que rmiregistry soit prêt
sleep 2

# Démarrer le serveur central
java Main conf.ini >/tmp/serveurCentral.log 2>&1 &
SERVER_PID=$!

# Obtenir l'IP locale pour les autres services
LOCAL_IP=$(hostname -I | awk '{print $1}')

# Déployer sur les autres machines
for host in $(grep -v '^#' "$HOSTS_FILE" | xargs); do
    echo "Deploying to $host..."
    # Utiliser sshpass pour éviter de redemander le mot de passe
    echo "$USER_PASS" | sshpass -p "$USER_PASS" rsync -av --delete --exclude='.git' "$LOCAL_DIR/" "$host:$REMOTE_DIR/"
    
    echo "Running remote setup on $host..."
    # Créer un script temporaire pour la machine distante
    cat > remote_script.sh << EOF
#!/bin/bash
cd $REMOTE_DIR

# Compiler et démarrer les services
if [ -d serviceDb ]; then
    echo "Compiling serviceDb..."
    cd serviceDb
    javac -cp ".:lib/*" *.java
    nohup java -cp ".:lib/*" Main "$LOCAL_IP" conf.ini >/tmp/serviceDb.log 2>&1 &
    cd ..
fi

if [ -d serviceHttp ]; then
    echo "Compiling serviceHttp..."
    cd serviceHttp
    javac *.java
    nohup java Main "$LOCAL_IP" conf.ini >/tmp/serviceHttp.log 2>&1 &
    cd ..
fi
EOF

    # Copier et exécuter le script sur la machine distante
    echo "$USER_PASS" | sshpass -p "$USER_PASS" scp remote_script.sh "$host:$REMOTE_DIR/"
    echo "$USER_PASS" | sshpass -p "$USER_PASS" ssh "$host" "chmod +x $REMOTE_DIR/remote_script.sh && cd $REMOTE_DIR && ./remote_script.sh"
    rm remote_script.sh
done

echo "Déploiement terminé. Les services sont en cours d'exécution."
echo "Pour arrêter tous les services, exécutez : kill $RMI_PID $SERVER_PID"
echo "Les logs sont disponibles dans /tmp :"
echo "- rmiregistry.log"
echo "- serveurCentral.log"
echo "- serviceDb.log (sur chaque machine distante)"
echo "- serviceHttp.log (sur chaque machine distante)" 