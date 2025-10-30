import httpClient from "../http-common";

// Obtener todos los usuarios
const getAll = () => {
    return httpClient.get('/api/v1/users');
}

// Obtener todos los clientes
const getCostumers = () => {
    return httpClient.get("/api/v1/users/costumers");
}

// Obtener clientes activos
const getActiveCostumers = () => {
    return httpClient.get("/api/v1/users/costumers/active");
}

// Obtener todos los empleados
const getEmployees = () => {
    return httpClient.get("/api/v1/users/employees");
}

// Obtener usuarios con deudas
const getUserWithDebts =  () => {
    return httpClient.get("/api/v1/users/with-debts");
}

// Obtener usuarios con deudas en un rango de fechas
const getUserWithDebtsDateRange = (startDate, endDate) => {
    return httpClient.get(`/api/v1/users/with-debts/date-range?startDate=${startDate}&endDate=${endDate}`);
}

// Crear un nuevo usuario
const create = data => {
    return httpClient.post("/api/v1/users", data);
}

// Obtener todos los tipos de usuarios
const getUserTypes = () => {
    return httpClient.get("/api/v1/users/types");
}

export default { getAll, getCostumers, getActiveCostumers, getEmployees, getUserWithDebts, 
    getUserWithDebtsDateRange, create, getUserTypes };