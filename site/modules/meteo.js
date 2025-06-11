import L from 'leaflet';
import config from './config.js';
import { createIcons, icons } from "lucide";

async function fetchMeteo() {
    const response = await fetch(config.getWeatherApiUrl());
    const data = await response.json();
    console.log(data);
    return data;
}

async function modifyMeteo() {
    const data = await fetchMeteo();
    const degres = Math.round(data.main.temp);  
    const vent = Math.round(data.wind.speed);
    let icon = data.weather[0].icon;
    switch(icon) {
        case '01d':
            icon = 'sun';
            break;
        case '01n':
            icon = 'moon';
            break;
        case '02d':
            icon = 'cloud-sun';
            break;
        case '02n':
            icon = 'cloud-moon';
            break;
        case '03d':
            icon = 'cloud';
            break;
        case '03n':
            icon = 'cloud';
            break;
        // case '04d':
        //     icon = 'broken-clouds';
        //     break;
        // case '04n':
        //     icon = 'broken clouds';
        //     break;
        case '09d':
            icon = 'cloud-sun-rain';
            break;
        case '09n':
            icon = 'cloud-moon-rain';
            break;
        case '10d':
            icon = 'cloud-rain';
            break;
        case '10n':
            icon = 'cloud-rain';
            break;
        case '11d':
            icon = 'cloud-lightning';
            break;
        case '11n':
            icon = 'cloud-lightning';
            break;
        case '13d':
            icon = 'cloud-snow';
            break;
        case '13n':
            icon = 'cloud-snow';
            break;
        case '50d':
            icon = 'cloud-fog';
            break;
        case '50n':
            icon = 'cloud-fog';
            break;
        default:
            icon = 'sun';
            break;
    }
    document.querySelector('section.weather .weather-card').innerHTML = `
        <i data-lucide="${icon}"></i>
        <div>${degres}°C<br><small>Vent ${vent} km/h</small></div>
    `;
    createIcons({ icons });
}

export default {
    modifyMeteo: modifyMeteo
}
