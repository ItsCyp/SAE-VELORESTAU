import L from 'leaflet';
import config from './config.js';
import { createIcons, icons } from "lucide";

let restauMarkers = L.layerGroup();

async function fetchRestaurants(){
    const response = await fetch(config.API_RESTAU);
    const data = await response.json();
    return data;
}

async function createMarker(map){
    // Récupérer les données des restaurants depuis l'API
    const restaurants = await fetchRestaurants();
    
    // Créer un marqueur pour chaque restaurant
    restaurants.forEach(restaurant => {
        const marker = L.marker([restaurant.latitude, restaurant.longitude], {
            icon: L.icon({
                iconUrl: 'icons/restaurant.png',
                iconSize: [48, 48],
                iconAnchor: [24, 24],
                popupAnchor: [0, -10]
            })
        });
        restauMarkers.addLayer(marker);

        // Gérer le clic sur le marqueur
        marker.on('click', () => {
            // Mettre à jour la section des réservations
            const reservationsSection = document.querySelector('section#restaurants');
            reservationsSection.innerHTML = `
                <h2><i data-lucide="coffee"></i> Restaurant sélectionné</h2>
                <div class="restaurant-details">
                    <h3>${restaurant.name}</h3>
                    <p><strong>Adresse:</strong> ${restaurant.address}</p>
                    <p><strong>Téléphone:</strong> ${restaurant.phone}</p>
                    <button id="reserve-btn"><i data-lucide="calendar"></i> Réserver</button>
                </div>
            `;
            document.querySelector('#reserve-btn').addEventListener('click', () => {
                reserveRestaurant(restaurant.id);
            });

            createIcons({ icons });
        });
    });
    
    // Ajouter la couche de marqueurs à la carte
    restauMarkers.addTo(map);
}

function showReservationModal(restaurantId) {
    // Créer la fenêtre modale
    const modal = document.createElement('div');
    modal.className = 'reservation-modal';
    modal.innerHTML = `
        <div class="modal-content">
            <span class="close-modal">&times;</span>
            <h2>Réserver une table</h2>
            <div class="tables-list">
                <h3>Tables disponibles</h3>
                <div class="tables-grid">
                    ${generateTablesList()}
                </div>
            </div>
            <div class="reservation-form">
                <h3>Détails de la réservation</h3>
                <form id="reservation-form">
                    <div class="form-group">
                        <label for="date">Date:</label>
                        <input type="date" id="date" required>
                    </div>
                    <div class="form-group">
                        <label for="time">Heure:</label>
                        <input type="time" id="time" required>
                    </div>
                    <div class="form-group">
                        <label for="guests">Nombre de personnes:</label>
                        <input type="number" id="guests" min="1" max="8" required>
                    </div>
                    <button type="submit" class="submit-reservation">Confirmer la réservation</button>
                </form>
            </div>
        </div>
    `;

    // Ajouter la modale au document
    document.body.appendChild(modal);

    // Gérer la fermeture de la modale
    const closeBtn = modal.querySelector('.close-modal');
    closeBtn.onclick = () => {
        modal.remove();
    };

    // Fermer la modale en cliquant en dehors
    window.onclick = (event) => {
        if (event.target === modal) {
            modal.remove();
        }
    };

    // Gérer la soumission du formulaire
    const form = modal.querySelector('#reservation-form');
    form.onsubmit = (e) => {
        e.preventDefault();
        const date = form.querySelector('#date').value;
        const time = form.querySelector('#time').value;
        const guests = form.querySelector('#guests').value;
        
        // Ici, vous pouvez ajouter la logique pour sauvegarder la réservation
        alert(`Réservation confirmée pour le ${date} à ${time} pour ${guests} personnes`);
        modal.remove();
    };
}

function generateTablesList() {
    // Générer une liste de 10 tables avec différentes capacités
    let tablesHtml = '';
    for (let i = 1; i <= 10; i++) {
        const capacity = Math.floor(Math.random() * 4) + 2; // Capacité entre 2 et 6 personnes
        tablesHtml += `
            <div class="table-item">
                <input type="radio" name="table" id="table-${i}" value="${i}">
                <label for="table-${i}">
                    Table ${i}
                    <span class="capacity">(${capacity} personnes)</span>
                </label>
            </div>
        `;
    }
    return tablesHtml;
}

function reserveRestaurant(restaurantId) {
    showReservationModal(restaurantId);
}

export default {
    createMarker: createMarker,
    reserveRestaurant: reserveRestaurant,
    restauMarkers: restauMarkers
}