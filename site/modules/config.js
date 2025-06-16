var config = require('../config/config.json');

const SERVER_IP = config.server.ip;
const SERVER_PORT = config.server.port;

var API_VELIBS_STATIONS = config.API_VELIBS.address_stations;
var API_VELIBS_STATUS = config.API_VELIBS.address_status;
var API_RESERVATION = `https://${SERVER_IP}:${SERVER_PORT}/api/reservations`;
var API_INCIDENTS = `https://${SERVER_IP}:${SERVER_PORT}/api/accidents`;
var API_RESTAU = `https://${SERVER_IP}:${SERVER_PORT}/api/restaurants`;
var WEATHER_API_URL = `https://api.openweathermap.org/data/2.5/weather?lat=${config.weather.nancy_lat}&lon=${config.weather.nancy_lon}&appid=${config.weather.api_key}&units=metric`;

export default {
    API_VELIBS_STATIONS,
    API_VELIBS_STATUS,
    API_INCIDENTS,
    API_RESTAU,
    API_RESERVATION,
    WEATHER_API_URL
}
