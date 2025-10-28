import httpClient from "../http-common";

// Obtener todos los registros de kardex
const getAll = () => {
    return httpClient.get('/api/v1/kardex');
}

// Obtener el historial de un herramienta específica
const getToolHistory = (id) => {
    return httpClient.get(`/api/v1/kardex/tools/${id}/history`);
}

// Obtener todos los registros de kardex en un rango de fechas
const getAllKardexDateRange = data => {
    return httpClient.get("/api/v1/kardex/date-range", data);
}

// Obtener el historial de una herramienta específica en un rango de fechas
const getToolHistoryDateRange = (id, data) => {
    return httpClient.get(`/api/v1/kardex/tools/${id}/history/date-range`, data);
}

export default { getAll, getToolHistory, getAllKardexDateRange, getToolHistoryDateRange };