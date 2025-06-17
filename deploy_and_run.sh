#!/bin/bash

set -e

if [ "$#" -lt 2 ] || [ "$#" -gt 3 ]; then
    echo "Usage: $0 [hosts-file] <local-project-dir> <remote-dir>"
    echo "  hosts-file: fichier contenant les IPs des machines (défaut: hosts.txt)"
    echo "  local-project-dir: répertoire du projet local"
    echo "  remote-dir: répertoire de destination sur les machines distantes"
    echo ""
    echo "Exemples :"
    echo "  bash deploy_and_run.sh hosts.txt . service/"
    echo "  bash deploy_and_run.sh . service/  # utilise hosts.txt par défaut"
    exit 1
fi

# Si 2 arguments : utiliser hosts.txt par défaut
if [ "$#" -eq 2 ]; then
    HOSTS_FILE="hosts.txt"
    LOCAL_DIR="$1"
    REMOTE_DIR="$2"
else
    HOSTS_FILE="$1"
    LOCAL_DIR="$2"
    REMOTE_DIR="$3"
fi

if [ ! -f "$HOSTS_FILE" ]; then
    echo "Hosts file '$HOSTS_FILE' not found"
    echo "Vérifiez que le fichier existe dans le répertoire courant ou spécifiez le bon chemin"
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

cd "$LOCAL_DIR"

# Obtenir l'IP locale pour les autres services
LOCAL_IP=$(hostname -I | awk '{print $1}')

# Lire les machines de la liste
HOSTS=($(grep -v '^#' "$HOSTS_FILE" | xargs))

echo "Machines trouvées dans $HOSTS_FILE : ${#HOSTS[@]}"

# Fonction pour déployer serviceDb
deploy_service_db() {
    local target_host="$1"
    local is_local="$2"
    
    if [ "$is_local" = "true" ]; then
        echo "Deploying serviceDb locally..."
        cd "serviceDb"
        javac -cp ".:lib/*" *.java
        nohup java -cp ".:lib/*" Main "$LOCAL_IP" conf.ini >/tmp/serviceDb.log 2>&1 &
        cd - > /dev/null
    else
        echo "Deploying serviceDb to $target_host..."
        echo "$USER_PASS" | sshpass -p "$USER_PASS" rsync -av --delete --exclude='.git' "$LOCAL_DIR/" "$target_host:$REMOTE_DIR/"
        
        echo "Running serviceDb setup on $target_host..."
        cat > remote_script_db.sh << EOF
#!/bin/bash
cd $REMOTE_DIR

# Compiler et démarrer serviceDb
if [ -d serviceDb ]; then
    echo "Compiling serviceDb..."
    cd serviceDb
    javac -cp ".:lib/*" *.java
    nohup java -cp ".:lib/*" Main "$LOCAL_IP" conf.ini >/tmp/serviceDb.log 2>&1 &
    cd ..
fi
EOF

        echo "$USER_PASS" | sshpass -p "$USER_PASS" scp remote_script_db.sh "$target_host:$REMOTE_DIR/"
        echo "$USER_PASS" | sshpass -p "$USER_PASS" ssh "$target_host" "chmod +x $REMOTE_DIR/remote_script_db.sh && cd $REMOTE_DIR && ./remote_script_db.sh"
        rm remote_script_db.sh
    fi
}

# Fonction pour déployer serviceHttp
deploy_service_http() {
    local target_host="$1"
    local is_local="$2"
    
    if [ "$is_local" = "true" ]; then
        echo "Deploying serviceHttp locally..."
        cd "serviceHttp"
        javac *.java
        nohup java Main "$LOCAL_IP" conf.ini >/tmp/serviceHttp.log 2>&1 &
        cd - > /dev/null
    else
        echo "Deploying serviceHttp to $target_host..."
        echo "$USER_PASS" | sshpass -p "$USER_PASS" rsync -av --delete --exclude='.git' "$LOCAL_DIR/" "$target_host:$REMOTE_DIR/"
        
        echo "Running serviceHttp setup on $target_host..."
        cat > remote_script_http.sh << EOF
#!/bin/bash
cd $REMOTE_DIR

# Compiler et démarrer serviceHttp
if [ -d serviceHttp ]; then
    echo "Compiling serviceHttp..."
    cd serviceHttp
    javac *.java
    nohup java Main "$LOCAL_IP" conf.ini >/tmp/serviceHttp.log 2>&1 &
    cd ..
fi
EOF

        echo "$USER_PASS" | sshpass -p "$USER_PASS" scp remote_script_http.sh "$target_host:$REMOTE_DIR/"
        echo "$USER_PASS" | sshpass -p "$USER_PASS" ssh "$target_host" "chmod +x $REMOTE_DIR/remote_script_http.sh && cd $REMOTE_DIR && ./remote_script_http.sh"
        rm remote_script_http.sh
    fi
}

# Gestion des différents cas
if [ ${#HOSTS[@]} -eq 0 ]; then
    echo "Aucune machine dans la liste. Déploiement local des deux services."
    deploy_service_db "" "true"
    deploy_service_http "" "true"
elif [ ${#HOSTS[@]} -eq 1 ]; then
    echo "Une seule machine trouvée. Déploiement des deux services sur ${HOSTS[0]}"
    deploy_service_db "${HOSTS[0]}" "false"
    deploy_service_http "${HOSTS[0]}" "false"
else
    echo "Deux machines ou plus trouvées. Déploiement séparé :"
    echo "- serviceDb sur ${HOSTS[0]}"
    echo "- serviceHttp sur ${HOSTS[1]}"
    deploy_service_db "${HOSTS[0]}" "false"
    deploy_service_http "${HOSTS[1]}" "false"
fi

echo "Déploiement terminé. Les services sont en cours d'exécution."
echo "Pour arrêter tous les services, exécutez : kill $RMI_PID $SERVER_PID"
echo "Les logs sont disponibles dans /tmp :"
echo "- rmiregistry.log"
echo "- serveurCentral.log"
echo "- serviceDb.log (sur chaque machine distante)"
echo "- serviceHttp.log (sur chaque machine distante)" 