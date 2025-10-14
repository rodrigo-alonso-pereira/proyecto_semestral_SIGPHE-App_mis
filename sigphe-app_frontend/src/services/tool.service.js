import httpClient from "../http-common";

const getAll = () => {
    return httpClient.get('/api/v1/tools');
}

const getActiveTools = () => {
    return httpClient.get("/api/v1/tools/active");
}

const getMostBorrowedTools = () => {
    return httpClient.get("/api/v1/tools/most-borrowed");
}

const getMostBorrowedToolsDateRange = data => {
    return httpClient.get("/api/v1/tools/most-borrowed/date-range", data);
}

const create = data => {
    return httpClient.post("/api/v1/tools/", data);
}

const deactivateTool = (id, data) => {
    return httpClient.put(`/api/v1/tools/${id}/deactivate`, data);
}

export default { getAll, getActiveTools, getMostBorrowedTools, getMostBorrowedToolsDateRange, create, deactivateTool };