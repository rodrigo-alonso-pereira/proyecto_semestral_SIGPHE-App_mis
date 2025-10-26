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
const getUserWithDebtsDateRange = data => {
    return httpClient.get("/api/v1/users/with-debts", data);
}

export default { getAll, getCostumers, getActiveCostumers, getEmployees, getUserWithDebts, getUserWithDebtsDateRange };