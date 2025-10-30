import httpClient from "../http-common";

// Obtener todos los registros de kardex
const getAll = () => {
    return httpClient.get('/api/v1/kardex');
}

// Obtener el historial de un herramienta específica
const getToolHistory = (id) => {
    return httpClient.get(`/api/v1/kardex/tool/${id}/history`);
}

// Obtener todos los registros de kardex en un rango de fechas
const getAllKardexDateRange = (startDate, endDate) => {
    return httpClient.get(`/api/v1/kardex/date-range?startDate=${startDate}&endDate=${endDate}`);
}

// Obtener el historial de una herramienta específica en un rango de fechas
const getToolHistoryDateRange = (id, startDate, endDate) => {
    return httpClient.get(`/api/v1/kardex/tool/${id}/history/date-range?startDate=${startDate}&endDate=${endDate}`);
}

export default { getAll, getToolHistory, getAllKardexDateRange, getToolHistoryDateRange };