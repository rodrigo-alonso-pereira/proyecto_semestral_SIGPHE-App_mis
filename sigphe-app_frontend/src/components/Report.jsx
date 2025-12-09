import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import loanService from "../services/loan.service";
import userService from "../services/user.service";
import toolService from "../services/tool.service";
import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableCell, { tableCellClasses } from "@mui/material/TableCell";
import TableContainer from "@mui/material/TableContainer";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import Paper from "@mui/material/Paper";
import Chip from "@mui/material/Chip";
import Box from "@mui/material/Box";
import Button from "@mui/material/Button";
import * as React from "react";
import Alert from "@mui/material/Alert";
import CheckIcon from "@mui/icons-material/Check";
import SearchIcon from "@mui/icons-material/Search";
import RefreshIcon from "@mui/icons-material/Refresh";
import CheckCircleIcon from "@mui/icons-material/CheckCircle";
import WarningIcon from "@mui/icons-material/Warning";
import InfoIcon from "@mui/icons-material/Info";
import TaskAltIcon from "@mui/icons-material/TaskAlt";
import PersonIcon from "@mui/icons-material/Person";
import MoneyOffIcon from "@mui/icons-material/MoneyOff";
import HandshakeIcon from "@mui/icons-material/Handshake";
import PersonOffIcon from "@mui/icons-material/PersonOff";
import { DateTimePicker } from "@mui/x-date-pickers/DateTimePicker";
import { LocalizationProvider } from "@mui/x-date-pickers/LocalizationProvider";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import Swal from "sweetalert2";

{
  /* Componente de tipo funcion que muestra la lista de prestamos */
}
const ReportList = () => {
  const [loans, setLoans] = useState([]); // Lista de prestamos activos
  const [customers, setCustomers] = useState([]); // Lista de clientes con atrasos
  const [tools, setTools] = useState([]); // Lista de herramientas mas prestadas

  const [showSuccessAlert, setShowSuccessAlert] = useState(false);
  
  // Estados para el filtro de fechas
  const [startDate, setStartDate] = useState(null);
  const [endDate, setEndDate] = useState(null);
  const [isFiltering, setIsFiltering] = useState(false);

  // Hook de navegacion entre paginas
  const navigate = useNavigate();

  // Función para formatear fechas
  const formatDate = (dateString) => {
    if (!dateString) return "-";

    const date = new Date(dateString);
    const day = date.getDate().toString().padStart(2, "0");
    const month = (date.getMonth() + 1).toString().padStart(2, "0");
    const year = date.getFullYear();
    const hours = date.getHours().toString().padStart(2, "0");
    const minutes = date.getMinutes().toString().padStart(2, "0");

    return `${day}/${month}/${year} ${hours}:${minutes}`;
  };

  {
    /* Función para formatear valores monetarios */
  }
  const formatCurrency = (value) => {
    if (!value || value === 0) return "$0";

    const number = parseFloat(value);
    return new Intl.NumberFormat("es-CL", {
      style: "currency",
      currency: "CLP",
      minimumFractionDigits: 0,
      maximumFractionDigits: 0,
    }).format(number);
  };

  // Función para cargar datos sin filtro (todos los datos)
  const loadAllData = () => {
    loanService
      .getActiveLoans()
      .then((response) => {
        console.log(
          "Mostrando listado de todos los prestamos activos.",
          response.data
        );
        setLoans(response.data);
      })
      .catch((error) => {
        console.log(
          "Se ha producido un error al intentar mostrar listado de todos los prestamos activos.",
          error
        );
      });

    userService
      .getUserWithDebts()
      .then((response) => {
        console.log("Clientes con atrasos cargados:", response.data);
        setCustomers(response.data);
      })
      .catch((error) => {
        console.log("Error al cargar clientes con atrasos:", error);
      });

    toolService
      .getMostBorrowedTools()
      .then((response) => {
        console.log("Herramientas más prestadas cargadas:", response.data);
        setTools(response.data);
      })
      .catch((error) => {
        console.log("Error al cargar herramientas más prestadas:", error);
      });
  };

  // Función para cargar datos filtrados por rango de fechas
  const loadFilteredData = (start, end) => {
    // Formatear fechas al formato que espera el backend: YYYY-MM-DDTHH:MM:SS
    const startDateFormatted = start.format("YYYY-MM-DDTHH:mm:ss");
    const endDateFormatted = end.format("YYYY-MM-DDTHH:mm:ss");

    console.log("Filtrando con rango de fechas:", {
      startDate: startDateFormatted,
      endDate: endDateFormatted
    });

    loanService
      .getActiveLoansDateRange(startDateFormatted, endDateFormatted)
      .then((response) => {
        console.log("Préstamos filtrados cargados:", response.data);
        setLoans(response.data);
      })
      .catch((error) => {
        console.log("Error al cargar préstamos filtrados:", error);
      });

    userService
      .getUserWithDebtsDateRange(startDateFormatted, endDateFormatted)
      .then((response) => {
        console.log("Clientes con deudas filtrados cargados:", response.data);
        setCustomers(response.data);
      })
      .catch((error) => {
        console.log("Error al cargar clientes filtrados:", error);
      });

    toolService
      .getMostBorrowedToolsDateRange(startDateFormatted, endDateFormatted)
      .then((response) => {
        console.log("Herramientas más prestadas filtradas cargadas:", response.data);
        setTools(response.data);
      })
      .catch((error) => {
        console.log("Error al cargar herramientas filtradas:", error);
      });
  };

  // Función para aplicar el filtro
  const handleFilter = () => {
    if (!startDate || !endDate) {
      Swal.fire({
        title: 'Fechas requeridas',
        text: 'Por favor, seleccione ambas fechas (inicio y fin).',
        icon: 'warning',
        confirmButtonColor: '#3085d6'
      });
      return;
    }

    if (startDate.isAfter(endDate)) {
      Swal.fire({
        title: 'Fechas inválidas',
        text: 'La fecha de inicio debe ser anterior a la fecha de fin.',
        icon: 'warning',
        confirmButtonColor: '#3085d6'
      });
      return;
    }

    setIsFiltering(true);
    loadFilteredData(startDate, endDate);
  };

  // Función para limpiar el filtro
  const handleClearFilter = () => {
    setStartDate(null);
    setEndDate(null);
    setIsFiltering(false);
    loadAllData();
  };

  // Función de inicialización
  const init = () => {
    loadAllData();
  };

  // Función para obtener el color según el estado del préstamo
  const getStatusColor = (status) => {
    switch (status) {
      case "Vigente":
        return "success"; // Verde
      case "Atrasada":
        return "warning"; // Naranja
      case "Retornado":
        return "info"; // Azul
      case "Finalizado":
        return "default"; // Gris
      default:
        return "default";
    }
  };

  const getStatusClientColor = (status) => {
    switch (status) {
      case "Activo":
        return "success"; // Verde
      case "Con Deuda":
        return "warning"; // Naranja
      case "Con Prestamos":
        return "info"; // Azul
      case "Inactivo":
        return "default"; // Gris
      default:
        return "default";
    }
  };

  // Función para obtener el icono según el estado del préstamo
  const getStatusIcon = (status) => {
    switch (status) {
      case "Vigente":
        return <CheckCircleIcon />; // Verde - préstamo activo
      case "Atrasada":
        return <WarningIcon />; // Naranja - advertencia de atraso
      case "Retornado":
        return <InfoIcon />; // Azul - información de retorno
      case "Finalizado":
        return <TaskAltIcon />; // Gris - tarea completada
      default:
        return null;
    }
  };

  // Función para obtener el icono según el estado del cliente
  const getStatusClientIcon = (status) => {
    switch (status) {
      case "Activo":
        return <PersonIcon />; // Verde - cliente activo
      case "Con Deuda":
        return <MoneyOffIcon />; // Naranja - cliente con deuda
      case "Con Prestamos":
        return <HandshakeIcon />; // Azul - cliente con préstamos
      case "Inactivo":
        return <PersonOffIcon />; // Gris - cliente inactivo
      default:
        return null;
    }
  };

  // Hook de efecto para cargar los prestamos al montar el componente
  useEffect(() => {
    init();
  }, []);

  return (
    <div>
      {showSuccessAlert && (
        <Alert
          icon={<CheckIcon fontSize="inherit" />}
          severity="success"
          onClose={() => setShowSuccessAlert(false)}
          sx={{ marginBottom: 2 }}
        >
          Préstamo pagado con éxito.
        </Alert>
      )}

      {/* Filtro de rango de fechas */}
      <Paper sx={{ padding: 3, marginBottom: 3 }}>
        <h3 style={{ marginTop: 0 }}>Filtrar reportes por rango de fechas</h3>
        <LocalizationProvider dateAdapter={AdapterDayjs}>
          <Box
            sx={{
              display: "flex",
              gap: 2,
              flexWrap: "wrap",
              alignItems: "center",
            }}
          >
            <DateTimePicker
              label="Fecha de Inicio"
              value={startDate}
              onChange={(newValue) => setStartDate(newValue)}
              sx={{ flex: 1, minWidth: 250 }}
            />
            <DateTimePicker
              label="Fecha de Fin"
              value={endDate}
              onChange={(newValue) => setEndDate(newValue)}
              sx={{ flex: 1, minWidth: 250 }}
            />
            <Button
              variant="contained"
              color="primary"
              startIcon={<SearchIcon />}
              onClick={handleFilter}
              sx={{ height: 56 }}
            >
              Filtrar
            </Button>
            {isFiltering && (
              <Button
                variant="outlined"
                color="secondary"
                startIcon={<RefreshIcon />}
                onClick={handleClearFilter}
                sx={{ height: 56 }}
              >
                Limpiar Filtro
              </Button>
            )}
          </Box>
        </LocalizationProvider>
        {isFiltering && (
          <Alert severity="info" sx={{ marginTop: 2 }}>
            Mostrando datos filtrados desde {startDate?.format("DD/MM/YYYY HH:mm")} hasta {endDate?.format("DD/MM/YYYY HH:mm")}
          </Alert>
        )}
      </Paper>

      <TableContainer component={Paper}>
        <br />
        <h2 style={{ textAlign: "center" }}>Listado de préstamos activos</h2>
        <br />
      <Table sx={{ minWidth: 650 }} size="small" aria-label="a dense table">
        <TableHead>
          <TableRow>
            <TableCell align="center" sx={{ fontWeight: "bold" }}>
              Fecha de Inicio
            </TableCell>
            <TableCell align="center" sx={{ fontWeight: "bold" }}>
              Fecha de retorno
            </TableCell>
            <TableCell align="center" sx={{ fontWeight: "bold" }}>
              Fecha Limite
            </TableCell>
            <TableCell align="center" sx={{ fontWeight: "bold" }}>
              Fecha de pago
            </TableCell>
            <TableCell align="center" sx={{ fontWeight: "bold" }}>
              Valor arriendo
            </TableCell>
            <TableCell align="center" sx={{ fontWeight: "bold" }}>
              Valor de multas
            </TableCell>
            <TableCell align="center" sx={{ fontWeight: "bold" }}>
              Estado
            </TableCell>
            <TableCell align="center" sx={{ fontWeight: "bold" }}>
              Nombre del cliente
            </TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {loans.length === 0 ? (
            <TableRow>
              <TableCell colSpan={8} align="center" sx={{ padding: 4 }}>
                <Alert severity="info">
                  No hay datos para los filtros seleccionados
                </Alert>
              </TableCell>
            </TableRow>
          ) : (
            loans.map((loan) => (
              <TableRow
                key={loan.id}
                sx={{ "&:last-child td, &:last-child th": { border: 0 } }}
              >
                <TableCell align="center">{formatDate(loan.startDate)}</TableCell>
                <TableCell align="center">
                  {formatDate(loan.returnDate)}
                </TableCell>
                <TableCell align="center">{formatDate(loan.dueDate)}</TableCell>
                <TableCell align="center">
                  {formatDate(loan.paymentDate)}
                </TableCell>
                <TableCell align="center">
                  {formatCurrency(loan.totalAmount)}
                </TableCell>
                <TableCell align="center">
                  {formatCurrency(loan.totalPenalties)}
                </TableCell>
                <TableCell align="center">
                  <Chip
                    label={loan.loanStatus}
                    color={getStatusColor(loan.loanStatus)}
                    icon={getStatusIcon(loan.loanStatus)}
                    sx={{ minWidth: '120px' }}
                  />
                </TableCell>
                <TableCell align="center">{loan.customerName}</TableCell>
              </TableRow>
            ))
          )}
        </TableBody>
      </Table>
      <br />
      <h2 style={{ textAlign: "center" }}>Listado de clientes con mas atrasos</h2>
      <br />
      <Table sx={{ minWidth: 650 }} size="small" aria-label="a dense table">
        <TableHead>
          <TableRow>
            <TableCell align="center" sx={{ fontWeight: "bold" }}>
              Nombre
            </TableCell>
            <TableCell align="center" sx={{ fontWeight: "bold" }}>
              Email
            </TableCell>
            <TableCell align="center" sx={{ fontWeight: "bold" }}>
              Estado
            </TableCell>
            <TableCell align="center" sx={{ fontWeight: "bold" }}>
              Cantidad de préstamos atrasados
            </TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {customers.length === 0 ? (
            <TableRow>
              <TableCell colSpan={4} align="center" sx={{ padding: 4 }}>
                <Alert severity="info">
                  No hay datos para los filtros seleccionados
                </Alert>
              </TableCell>
            </TableRow>
          ) : (
            customers.map((customer) => (
              <TableRow
                key={customer.id}
                sx={{ "&:last-child td, &:last-child th": { border: 0 } }}
              >
                <TableCell align="center">{customer.name}</TableCell>
                <TableCell align="center">{customer.email}</TableCell>
                <TableCell align="center">
                  <Chip
                    label={customer.status}
                    color={getStatusClientColor(customer.status)}
                    icon={getStatusClientIcon(customer.status)}
                    sx={{ minWidth: '130px' }}
                  />
                </TableCell>
                <TableCell align="center">{customer.totalOverdueLoans}</TableCell>
              </TableRow>
            ))
          )}
        </TableBody>
      </Table>
      <br />
      <h2 style={{ textAlign: "center" }}>Listado de herramientas mas prestadas</h2>
      <br />
      <Table sx={{ minWidth: 650 }} size="small" aria-label="a dense table">
        <TableHead>
          <TableRow>
            <TableCell align="center" sx={{ fontWeight: "bold" }}>
              SKU
            </TableCell>
            <TableCell align="center" sx={{ fontWeight: "bold" }}>
              Nombre
            </TableCell>
            <TableCell align="center" sx={{ fontWeight: "bold" }}>
              Marca
            </TableCell>
            <TableCell align="center" sx={{ fontWeight: "bold" }}>
              Modelo
            </TableCell>
            <TableCell align="center" sx={{ fontWeight: "bold" }}>
              Cantidad de préstamos
            </TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {tools.length === 0 ? (
            <TableRow>
              <TableCell colSpan={5} align="center" sx={{ padding: 4 }}>
                <Alert severity="info">
                  No hay datos para los filtros seleccionados
                </Alert>
              </TableCell>
            </TableRow>
          ) : (
            tools.map((tool) => (
              <TableRow
                key={tool.id}
                sx={{ "&:last-child td, &:last-child th": { border: 0 } }}
              >
                <TableCell align="center">{tool.id}</TableCell>
                <TableCell align="center">{tool.name}</TableCell>
                <TableCell align="center">{tool.brand}</TableCell>
                <TableCell align="center">{tool.model}</TableCell>
                <TableCell align="center">{tool.usageCount}</TableCell>
              </TableRow>
            ))
          )}
        </TableBody>
      </Table>
      </TableContainer>
    </div>
  );
};

export default ReportList;
