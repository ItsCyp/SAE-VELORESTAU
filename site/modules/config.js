let API_VELIBS_STATIONS = "https://api.cyclocity.fr/contracts/nancy/gbfs/v3/station_information.json";
let API_VELIBS_STATUS = "https://api.cyclocity.fr/contracts/nancy/gbfs/v3/station_status.json";
const SERVER_IP = "100.64.80.62";
const SERVER_PORT = "8008";
let API_INCIDENTS = "https://" + SERVER_IP + ":" + SERVER_PORT + "/api/accidents";
let API_RESTAU = "https://" + SERVER_IP + ":" + SERVER_PORT + "/api/restau";
let WEATHER_API_URL = "https://api.openweathermap.org/data/2.5/weather?lat=48.692054&lon=6.184417&appid=cf398fc73cae4d949549dca5ae03a6d4&units=metric";


export default {
    API_VELIBS_STATIONS,
    API_VELIBS_STATUS,
    API_INCIDENTS,
    API_RESTAU,
    WEATHER_API_URL,
    SERVER_IP,
    SERVER_PORT
}
