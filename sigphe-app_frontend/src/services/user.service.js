import httpClient from "../http-common";

const getAll = () => {
    return httpClient.get('/api/v1/users');
}

const getCostumers = () => {
    return httpClient.get("/api/v1/users/costumers");
}

const getActiveCostumers = () => {
    return httpClient.get("/api/v1/users/costumers/active");
}

const getEmployees = () => {
    return httpClient.get("/api/v1/users/employees");
}

const getUserWithDebts =  () => {
    return httpClient.get("/api/v1/users/with-debts");
}

const getUserWithDebtsDateRange = data => {
    return httpClient.get("/api/v1/users/with-debts", data);
}

export default { getAll, getCostumers, getActiveCostumers, getEmployees, getUserWithDebts, getUserWithDebtsDateRange };