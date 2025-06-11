// Variables de configuration avec valeurs par défaut
let config = null;
let API_VELIBS = "https://api.cyclocity.fr/contracts/nancy/gbfs/gbfs.json";
let API_VELIBS_STATIONS = "https://api.cyclocity.fr/contracts/nancy/gbfs/v3/station_information.json";
let API_VELIBS_STATUS = "https://api.cyclocity.fr/contracts/nancy/gbfs/v3/station_status.json";
let SERVER_IP = "localhost";
let SERVER_PORT = "3000";
let API_INCIDENTS = null;
let API_RESTAU = null;
let NANCY_LAT = 48.692054;
let NANCY_LON = 6.184417;
let WEATHER_API_KEY = "cf398fc73cae4d949549dca5ae03a6d4";
let WEATHER_API_URL = null;

// Fonction pour initialiser la configuration
const initConfig = async () => {
    try {
        const response = await fetch('./config/config.json');
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        config = await response.json();
        
        // Initialiser les variables avec la configuration
        API_VELIBS = config.API_VELIBS?.address || API_VELIBS;
        API_VELIBS_STATIONS = config.API_VELIBS?.address_stations || API_VELIBS_STATIONS;
        API_VELIBS_STATUS = config.API_VELIBS?.address_status || API_VELIBS_STATUS;
        
        SERVER_IP = config.server?.ip || SERVER_IP;
        SERVER_PORT = config.server?.port || SERVER_PORT;
        
        API_INCIDENTS = `http://${SERVER_IP}:${SERVER_PORT}/api/accidents`;
        API_RESTAU = `http://${SERVER_IP}:${SERVER_PORT}/api/restau`;
        
        NANCY_LAT = config.weather?.nancy_lat || NANCY_LAT;
        NANCY_LON = config.weather?.nancy_lon || NANCY_LON;
        WEATHER_API_KEY = config.weather?.api_key || WEATHER_API_KEY;
        WEATHER_API_URL = `https://api.openweathermap.org/data/2.5/weather?lat=${NANCY_LAT}&lon=${NANCY_LON}&appid=${WEATHER_API_KEY}&units=metric&lang=fr`;
        
        return true;
    } catch (error) {
        console.warn('Erreur lors du chargement de la configuration, utilisation des valeurs par défaut:', error);
        // Utiliser les valeurs par défaut
        API_INCIDENTS = `http://${SERVER_IP}:${SERVER_PORT}/api/accidents`;
        API_RESTAU = `http://${SERVER_IP}:${SERVER_PORT}/api/restau`;
        WEATHER_API_URL = `https://api.openweathermap.org/data/2.5/weather?lat=${NANCY_LAT}&lon=${NANCY_LON}&appid=${WEATHER_API_KEY}&units=metric&lang=fr`;
        return false;
    }
};

// Fonction pour recharger la configuration
const reloadConfig = async () => {
    return await initConfig();
};

export default {
    initConfig,
    reloadConfig,
    get API_VELIBS() { return API_VELIBS; },
    get API_VELIBS_STATIONS() { return API_VELIBS_STATIONS; },
    get API_VELIBS_STATUS() { return API_VELIBS_STATUS; },
    get API_INCIDENTS() { return API_INCIDENTS; },
    get API_RESTAU() { return API_RESTAU; },
    get WEATHER_API_URL() { return WEATHER_API_URL; },
    get SERVER_IP() { return SERVER_IP; },
    get SERVER_PORT() { return SERVER_PORT; }
}
