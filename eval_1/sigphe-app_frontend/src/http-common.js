import axios from "axios";

const sigpheAppBackendServer = import.meta.env.VITE_SIGPHE_APP_BACKEND_SERVER;
const sigpheAppBackendPort = import.meta.env.VITE_SIGPHE_APP_BACKEND_PORT;

export default axios.create({
    baseURL: `http://${sigpheAppBackendServer}:${sigpheAppBackendPort}`,
    headers: {
        'Content-Type': 'application/json'
    }
});