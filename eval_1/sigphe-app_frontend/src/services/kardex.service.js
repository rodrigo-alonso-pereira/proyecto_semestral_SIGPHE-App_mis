import httpClient from "../http-common";

const getAll = () => {
    return httpClient.get('/api/v1/kardex');
}

const getToolHistory = (id) => {
    return httpClient.get(`/api/v1/kardex/tools/${id}/history`);
}

const getAllKardexDateRange = data => {
    return httpClient.get("/api/v1/kardex/date-range", data);
}

const getToolHistoryDateRange = (id, data) => {
    return httpClient.get(`/api/v1/kardex/tools/${id}/history/date-range`, data);
}

export default { getAll, getToolHistory, getAllKardexDateRange, getToolHistoryDateRange };