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

// Obtener los estados de herramientas
const getToolStatus = () => {
    return httpClient.get("/api/v1/tools/status");
}

// Obtener las categorias de herramientas
const getToolCategories = () => {
    return httpClient.get("/api/v1/tools/category");
}

// Obtener los modelos de herramientas
const getToolModels = () => {
    return httpClient.get("/api/v1/tools/model");
}

// Obtener una herramienta por id
const getToolById = (id) => {
    return httpClient.get(`/api/v1/tools/${id}`);
}

// Crear una nueva herramienta
const create = data => {
    return httpClient.post("/api/v1/tools", data);
}

const update = (id, data) => {
    return httpClient.put(`/api/v1/tools/${id}/update`, data);
}

// Desactivar una herramienta
const deactivateTool = (id, data) => {
    return httpClient.put(`/api/v1/tools/${id}/deactivate`, data);
}

export default { getAll, getActiveTools, getMostBorrowedTools, getMostBorrowedToolsDateRange, 
    getToolStatus, getToolCategories, getToolModels, getToolById, create, update, deactivateTool };