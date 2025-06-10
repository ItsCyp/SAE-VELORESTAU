import L from 'leaflet';
import config from './config.js';

let velibMarkers = L.layerGroup();

async function fetchStationInfo(){
    try {
        const response = await fetch(config.API_VELIBS_STATIONS);
        const data = await response.json();
        return data.data.stations;
    } catch (error) {
        console.error('Erreur lors de la récupération des stations:', error);
        return [];
    }
}

async function fetchStationStatus(){
    try {
        const response = await fetch(config.API_VELIBS_STATUS);
        const data = await response.json();
        return data.data.stations;
    } catch (error) {
        console.error('Erreur lors de la récupération du statut des stations:', error);
        return [];
    }
}

async function createMarker(map){
    const stations = await fetchStationInfo();
    const statuses = await fetchStationStatus();

    // Création d'une icône personnalisée pour les vélos
    const bikeIcon = L.icon({
        iconUrl: 'icons/bike.svg',
        iconSize: [32, 32],
        iconAnchor: [16, 16],
        popupAnchor: [0, -10]
    });

    stations.forEach(station => {
        const status = statuses.find(s => s.station_id === station.station_id);
        const marker = L.marker([station.lat, station.lon], {icon: bikeIcon});
        velibMarkers.addLayer(marker);

        const frenchName = station.name.find(n => n.language === 'fr')?.text || station.name[0].text;

        const popupContent = `
            <div style="min-width: 200px;">
                <h3 style="margin: 0 0 10px 0;">${frenchName}</h3>
                <p style="margin: 5px 0;"><strong>Adresse:</strong> ${station.address}</p>
                <p style="margin: 5px 0;"><strong>Vélos disponibles:</strong> ${status ? status.num_vehicles_available : 'N/A'}</p>
                <p style="margin: 5px 0;"><strong>Places disponibles:</strong> ${status ? status.num_docks_available : 'N/A'}</p>
                <p style="margin: 5px 0;"><strong>Capacité totale:</strong> ${station.capacity}</p>
                ${station.rental_methods ? `<p style="margin: 5px 0;"><strong>Méthodes de paiement:</strong> ${station.rental_methods.join(', ')}</p>` : ''}
                ${status ? `<p style="margin: 5px 0;"><strong>Dernière mise à jour:</strong> ${new Date(status.last_reported).toLocaleString()}</p>` : ''}
            </div>
        `;
        
        marker.bindPopup(popupContent);
    });

    // Ajouter la couche de marqueurs à la carte
    velibMarkers.addTo(map);
}

export default {
    createMarker: createMarker,
    velibMarkers: velibMarkers
}