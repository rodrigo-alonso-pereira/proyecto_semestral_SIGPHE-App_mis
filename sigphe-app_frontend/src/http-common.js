import axios from "axios";

// Obtener variables de entorno (.env) para el servidor backend
const sigpheAppBackendServer = import.meta.env.VITE_SIGPHE_APP_BACKEND_SERVER;
const sigpheAppBackendPort = import.meta.env.VITE_SIGPHE_APP_BACKEND_PORT;

// Configuración de la instancia de axios
export default axios.create({
    baseURL: `http://${sigpheAppBackendServer}:${sigpheAppBackendPort}`, // URL base del servidor backend
    headers: { 
        'Content-Type': 'application/json' // Tipo de contenido por defecto
    }
});