import L from "leaflet";
import map from "./modules/map.js";
import velibs from "./modules/velibs.js";
import { createIcons, icons } from "lucide";

// Création des icônes
createIcons({ icons });

// coordonnées de Nancy
let lat = 48.692054;
let lon = 6.184417;

const myMap = map.setupMap('map', [lat, lon], 13);

velibs.createMarker(myMap);

// Gestionnaire d'événements pour la checkbox vélibs
document.getElementById('chk-velibs').addEventListener('change', (event) => {
    if (event.target.checked) {
        myMap.addLayer(velibs.velibMarkers);
    } else {
        myMap.removeLayer(velibs.velibMarkers);
    }
});

document.getElementById('btn-center').addEventListener('click', () => {
    myMap.setView([lat, lon], 13);
});






