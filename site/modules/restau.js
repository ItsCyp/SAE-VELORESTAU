import L from 'leaflet';
import config from './config.js';
import { createIcons, icons } from "lucide";

let restauMarkers = L.layerGroup();
let restaurants = []; // Variable globale pour stocker les restaurants

async function fetchRestaurants(){
    const response = await fetch(config.API_RESTAU);
    const data = await response.json();
    return data;
}

async function createMarker(map){
    // Récupérer les données des restaurants depuis l'API
    restaurants = await fetchRestaurants(); // Stocker les restaurants dans la variable globale
    
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
                    <button class="reserve-btn"><i data-lucide="calendar"></i> Réserver</button>
                </div>
            `;
            document.querySelector('.reserve-btn').addEventListener('click', () => {
                reserveRestaurant(restaurant.id);
            });

            createIcons({ icons });
        });
    });
    
    // Ajouter la couche de marqueurs à la carte
    restauMarkers.addTo(map);
}

function showConfirmationModal(message, type = 'success') {
    const modal = document.createElement('div');
    modal.className = 'confirmation-modal';
    modal.innerHTML = `
        <div class="modal-content">
            <div class="modal-header ${type}">
                <h3>${type === 'success' ? 'Succès' : 'Information'}</h3>
            </div>
            <div class="modal-body">
                <p>${message}</p>
            </div>
            <div class="modal-footer">
                <button class="ok-button">OK</button>
            </div>
        </div>
    `;

    document.body.appendChild(modal);

    // Gérer la fermeture de la modale
    const closeModal = () => {
        modal.remove();
    };

    // Fermer en cliquant sur le bouton OK
    modal.querySelector('.ok-button').onclick = closeModal;

    // Fermer en cliquant en dehors de la modale
    modal.onclick = (event) => {
        if (event.target === modal) {
            closeModal();
        }
    };
}

function showReservationModal(restaurantId) {
    // Récupérer les données du restaurant sélectionné
    const restaurant = restaurants.find(r => r.id === restaurantId);
    if (!restaurant) return;

    // Créer la fenêtre modale
    const modal = document.createElement('div');
    modal.className = 'reservation-modal';
    modal.innerHTML = `
        <div class="modal-content">
            <span class="close-modal">&times;</span>
            <h2>Réserver une table - ${restaurant.name}</h2>
            <form id="reservation-form">
                <div class="tables-list">
                    <h3>Tables disponibles</h3>
                    <div class="tables-grid">
                        ${generateTablesList(restaurant.tables)}
                    </div>
                </div>
                <div class="reservation-form">
                    <h3>Détails de la réservation</h3>
                    <div class="form-group">
                        <label for="first_name">Prénom :</label>
                        <input type="text" id="first_name" name="first_name" required>
                    </div>
                    <div class="form-group">
                        <label for="last_name">Nom :</label>
                        <input type="text" id="last_name" name="last_name" required>
                    </div>
                    <div class="form-group">
                        <label for="phone">Téléphone :</label>
                        <input type="tel" id="phone" name="phone" required pattern="[0-9+ ]{6,20}">
                    </div>
                    <div class="form-group">
                        <label for="date">Date :</label>
                        <input type="date" id="date" required>
                    </div>
                    <div class="form-group">
                        <label for="time">Heure :</label>
                        <input type="time" id="time" required>
                    </div>
                    <div class="form-group">
                        <label for="guests">Nombre de personnes :</label>
                        <input type="number" id="guests" min="1" value="1" required>
                    </div>
                    <button type="submit" class="submit-reservation">Confirmer la réservation</button>
                </div>
            </form>
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

    // Mettre à jour le nombre maximum de personnes en fonction de la table sélectionnée
    const guestsInput = modal.querySelector('#guests');
    const tableInputs = modal.querySelectorAll('input[name="table"]');
    
    tableInputs.forEach(input => {
        input.addEventListener('change', () => {
            const selectedTable = restaurant.tables.find(t => t.id === parseInt(input.value));
            if (selectedTable) {
                guestsInput.max = selectedTable.seats;
                if (parseInt(guestsInput.value) > selectedTable.seats) {
                    guestsInput.value = selectedTable.seats;
                }
            }
        });
    });

    // Gérer la soumission du formulaire
    const form = modal.querySelector('#reservation-form');
    form.onsubmit = async (e) => {
        e.preventDefault();
        const date = form.querySelector('#date').value;
        const time = form.querySelector('#time').value;
        const guests = parseInt(form.querySelector('#guests').value);
        const selectedTableInput = form.querySelector('input[name="table"]:checked');
        const firstName = form.querySelector('#first_name').value;
        const lastName = form.querySelector('#last_name').value;
        const phone = form.querySelector('#phone').value;
        
        if (!selectedTableInput) {
            showConfirmationModal('Veuillez sélectionner une table', 'error');
            return;
        }

        const selectedTable = selectedTableInput.value;

        // Vérifier si le nombre de personnes correspond à la capacité de la table
        const table = restaurant.tables.find(t => t.id === parseInt(selectedTable));
        if (guests > table.seats) {
            showConfirmationModal(`Cette table ne peut accueillir que ${table.seats} personnes maximum`, 'error');
            return;
        }

        try {
            // Créer l'objet de réservation
            const reservationData = {
                restaurantId: restaurantId,
                tableId: parseInt(selectedTable),
                first_name: firstName,
                last_name: lastName,
                phone: phone,
                party_size: guests,
                date: date,
                time: time,
                timestamp: new Date().toISOString()
            };

            // Envoyer la réservation au serveur
            const response = await fetch(`${config.API_RESERVATION}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(reservationData)
            });

            if (!response.ok) {
                const error = await response.json();
                if (error.code === 'CONFLICT') {
                    showConfirmationModal('Cette table a déjà été réservée. Veuillez en sélectionner une autre.', 'error');
                    return;
                }
                throw new Error('Erreur lors de la réservation');
            }

            const result = await response.json();
            showConfirmationModal(`Réservation confirmée pour le ${date} à ${time} pour ${guests} personnes`);
            modal.remove();
        } catch (error) {
            console.error('Erreur:', error);
            showConfirmationModal('Une erreur est survenue lors de la réservation. Veuillez réessayer.', 'error');
        }
    };

    createIcons({ icons });
}

function generateTablesList(tables) {
    let tablesHtml = '';
    tables.forEach(table => {
        tablesHtml += `
            <div class="table-item">
                <input type="radio" name="table" id="table-${table.id}" value="${table.id}">
                <label for="table-${table.id}">
                    Table ${table.table_number}
                    <span class="capacity">(${table.seats} personnes)</span>
                </label>
            </div>
        `;
    });
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