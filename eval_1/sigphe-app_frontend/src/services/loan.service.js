import httpClient from "../http-common";

const getAll = () => {
    return httpClient.get('/api/v1/loans');
}

const getActiveLoans = () => {
    return httpClient.get("/api/v1/loans/active");
}

const getActiveLoansDateRange = data => {
    return httpClient.get("/api/v1/loans/active/date-range", data);
}

const create = data => {
    return httpClient.post("/api/v1/loans/", data);
}

const returnLoan = (id, data) => {
    return httpClient.put(`/api/v1/loans/${id}`, data);
}

const makePayment = (id, data) => {
    return httpClient.put(`/api/v1/loans/${id}/payment`, data);
}

export default { getAll, create, getActiveLoans, getActiveLoansDateRange, returnLoan, makePayment };