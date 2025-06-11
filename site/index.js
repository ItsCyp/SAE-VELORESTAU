import L from "leaflet";
import map from "./modules/map.js";
import velibs from "./modules/velibs.js";
import restau from "./modules/restau.js";
import incidents from "./modules/incidents.js";
import meteo from "./modules/meteo.js";
import config from "./modules/config.js";
import { createIcons, icons } from "lucide";

config.initConfig();

// coordonnées de Nancy
let lat = 48.692054;
let lon = 6.184417;

const myMap = map.setupMap('map', [lat, lon], 14);

velibs.createMarker(myMap);
restau.createMarker(myMap);
incidents.createMarker(myMap);
meteo.modifyMeteo();

// Gestionnaire d'événements pour la checkbox vélibs
document.getElementById('chk-velibs').addEventListener('change', (event) => {
    if (event.target.checked) {
        myMap.addLayer(velibs.velibMarkers);
    } else {
        myMap.removeLayer(velibs.velibMarkers);
    }
});

document.getElementById('chk-restaurants').addEventListener('change', (event) => {
    if (event.target.checked) {
        myMap.addLayer(restau.restauMarkers);
    } else {
        myMap.removeLayer(restau.restauMarkers);
    }
});

document.getElementById('chk-incidents').addEventListener('change', (event) => {
    if (event.target.checked) {
        myMap.addLayer(incidents.incidentMarkers);
    } else {
        myMap.removeLayer(incidents.incidentMarkers);
    }
});

document.getElementById('btn-center').addEventListener('click', () => {
    myMap.setView([lat, lon], 14);
});







document.addEventListener('DOMContentLoaded', () => {
    createIcons({ icons });
});






