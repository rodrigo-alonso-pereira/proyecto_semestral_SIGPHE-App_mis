import httpClient from "../http-common";

// Obtener todas las herramientas
const getAll = () => {
    return httpClient.get('/api/v1/tools');
}

// Obtener herramientas activas
const getActiveTools = () => {
    return httpClient.get("/api/v1/tools/active");
}

// Obtener herramientas más prestadas
const getMostBorrowedTools = () => {
    return httpClient.get("/api/v1/tools/most-borrowed");
}

// Obtener herramientas más prestadas en un rango de fechas
const getMostBorrowedToolsDateRange = data => {
    return httpClient.get("/api/v1/tools/most-borrowed/date-range", data);
}

// Crear una nueva herramienta
const create = data => {
    return httpClient.post("/api/v1/tools/", data);
}

// Desactivar una herramienta
const deactivateTool = (id, data) => {
    return httpClient.put(`/api/v1/tools/${id}/deactivate`, data);
}

export default { getAll, getActiveTools, getMostBorrowedTools, getMostBorrowedToolsDateRange, create, deactivateTool };