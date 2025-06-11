let API_VELIBS = "https://api.cyclocity.fr/contracts/nancy/gbfs/gbfs.json"
let API_VELIBS_STATIONS = "https://api.cyclocity.fr/contracts/nancy/gbfs/v3/station_information.json"
let API_VELIBS_STATUS = "https://api.cyclocity.fr/contracts/nancy/gbfs/v3/station_status.json"

let API_INCIDENTS = "https://carto.g-ny.org/data/cifs/cifs_waze_v2.json" // API incidents mais bloquer par CORS

let NANCY_LAT = 48.692054;
let NANCY_LON = 6.184417;
const WEATHER_API_KEY = "cf398fc73cae4d949549dca5ae03a6d4";

const getWeatherApiUrl = () => {
    return `https://api.openweathermap.org/data/2.5/weather?lat=${NANCY_LAT}&lon=${NANCY_LON}&appid=${WEATHER_API_KEY}&units=metric&lang=fr`;
}

export default {
    API_VELIBS : API_VELIBS,
    API_VELIBS_STATIONS : API_VELIBS_STATIONS,
    API_VELIBS_STATUS : API_VELIBS_STATUS,
    API_INCIDENTS : API_INCIDENTS,
    getWeatherApiUrl : getWeatherApiUrl
}
