import httpClient from "../http-common";

// Define las peticiones al backend relacionadas con los préstamos (loans)

// Obtener todos los préstamos
const getAll = () => {
    return httpClient.get('/api/v1/loans');
}

// Obtener préstamos activos
const getActiveLoans = () => {
    return httpClient.get("/api/v1/loans/active");
}

// Obtener préstamos activos en un rango de fechas
const getActiveLoansDateRange = data => {
    return httpClient.get("/api/v1/loans/active/date-range", data);
}

// Obtener prestamo con sus detalles
const getLoanDetails = id => {
    return httpClient.get(`/api/v1/loans/${id}/detail`);
}

// Crear un nuevo préstamo
const create = data => {
    return httpClient.post("/api/v1/loans", data);
}

// Devolver un préstamo
const returnLoan = (id, data) => {
    return httpClient.put(`/api/v1/loans/${id}/return`, data);
}

// Realizar un pago de un préstamo
const makePayment = (id, data) => {
    return httpClient.put(`/api/v1/loans/${id}/payment`, data);
}

export default { getAll, create, getActiveLoans, getActiveLoansDateRange, getLoanDetails, returnLoan, makePayment };