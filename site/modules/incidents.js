import L from 'leaflet';
import config from './config.js';
import { createIcons, icons } from "lucide";

let incidentMarkers = L.layerGroup();

async function fetchIncidents() {
    const response = await fetch(config.API_INCIDENTS);
    const data = await response.json();
    console.log(data);
    return data.incidents;
}

async function createMarker(map) {
    const incidents = await fetchIncidents();

    const warningIcon = L.icon({
        iconUrl: 'icons/warning.png',
        iconSize: [32, 32],
        iconAnchor: [16, 16],
        popupAnchor: [0, -10]
    });

    incidents.forEach(incident => {
        const marker = L.marker([incident.lat, incident.lon], {icon: warningIcon});
        incidentMarkers.addLayer(marker);

        const popupContent = `
            <div style="min-width: 200px;">
                <h3 style="margin: 0 0 10px 0;">${incident.description}</h3>
                <p style="margin: 5px 0;"><strong>Date:</strong> ${incident.date}</p>
                <p style="margin: 5px 0;"><strong>Statut:</strong> ${incident.status}</p>
            </div>
        `;

        marker.bindPopup(popupContent);
    });
    
    // Ajouter la couche de marqueurs à la carte
    incidentMarkers.addTo(map);
}


export default {
    createMarker: createMarker
}

