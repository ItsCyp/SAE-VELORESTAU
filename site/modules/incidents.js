import L from 'leaflet';
import config from './config.js';
import { createIcons, icons } from "lucide";

let incidentMarkers = L.layerGroup();

async function fetchIncidents() {
    const response = await fetch(config.API_INCIDENTS);
    const data = await response.json();
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
        const coordinates = incident.location.polyline.split(' ').map(Number);
        const marker = L.marker([coordinates[0], coordinates[1]], {icon: warningIcon});
        incidentMarkers.addLayer(marker);

        const popupContent = `
            <div style="min-width: 200px;">
                <h3 style="margin: 0 0 10px 0;">${incident.short_description}</h3>
                <p style="margin: 5px 0;"><strong>Description:</strong> ${incident.description}</p>
                <p style="margin: 5px 0;"><strong>Lieu:</strong> ${incident.location.location_description}</p>
                <p style="margin: 5px 0;"><strong>Début:</strong> ${new Date(incident.starttime).toLocaleDateString()}</p>
                <p style="margin: 5px 0;"><strong>Fin:</strong> ${new Date(incident.endtime).toLocaleDateString()}</p>
                <p style="margin: 5px 0;"><strong>Source:</strong> ${incident.source.name}</p>
                <p style="margin: 5px 0;"><strong>Dernière mise à jour:</strong> ${new Date(incident.updatetime).toLocaleString()}</p>
            </div>
        `;

        marker.bindPopup(popupContent);
    });
    
    // Ajouter la couche de marqueurs à la carte
    incidentMarkers.addTo(map);
}


export default {
    createMarker: createMarker,
    incidentMarkers: incidentMarkers
}

