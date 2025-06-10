import L from 'leaflet';

export function setupMap(containerId, centerCoords, zoomLevel) {
  const map = L.map(containerId).setView(centerCoords, zoomLevel);
  L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
        maxZoom: 19,
    }).addTo(map);
  return map;
}

export default {
    setupMap: setupMap
}