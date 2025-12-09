import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import loanService from "../services/loan.service";
import Breadcrumb from "./Breadcrumb";
import userService from "../services/user.service";
import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableCell, { tableCellClasses } from "@mui/material/TableCell";
import TableContainer from "@mui/material/TableContainer";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import Paper from "@mui/material/Paper";
import Button from "@mui/material/Button";
import Box from "@mui/material/Box";
import Typography from "@mui/material/Typography";
import AddCircleRoundedIcon from "@mui/icons-material/AddCircleRounded";
import AssignmentReturnedIcon from "@mui/icons-material/AssignmentReturned";
import PaidIcon from "@mui/icons-material/Paid";
import FilterListIcon from "@mui/icons-material/FilterList";
import RefreshIcon from "@mui/icons-material/Refresh";
import CheckCircleIcon from "@mui/icons-material/CheckCircle";
import WarningIcon from "@mui/icons-material/Warning";
import InfoIcon from "@mui/icons-material/Info";
import TaskAltIcon from "@mui/icons-material/TaskAlt";
import Chip from "@mui/material/Chip";
import Tooltip from "@mui/material/Tooltip";
import * as React from "react";
import Alert from "@mui/material/Alert";
import CheckIcon from "@mui/icons-material/Check";
import Swal from "sweetalert2";

{
  /* Componente de tipo funcion que muestra la lista de prestamos */
}
const LoanList = () => {
  const [loans, setLoans] = useState([]);
  const [customers, setCustomers] = useState([]);

  const [showingActiveOnly, setShowingActiveOnly] = useState(false);

  {
    /* Hook de navegacion entre paginas */
  }
  const navigate = useNavigate();

  {
    /* Función para formatear fechas */
  }
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

  {
    /* Función para inicializar el componente y cargar los prestamos */
  }
  const init = () => {
    loanService
      .getAll()
      .then((response) => {
        console.log("Mostrando listado de todos los prestamos.", response.data);
        setLoans(response.data);
        setShowingActiveOnly(false);
      })
      .catch((error) => {
        console.log(
          "Se ha producido un error al intentar mostrar listado de todos los prestamos.",
          error
        );
      });

    userService
      .getCostumers()
      .then((response) => {
        console.log("Clientes cargados:", response.data);
        setCustomers(response.data);
      })
      .catch((error) => {
        console.log("Error al cargar clientes:", error);
      });
  };

  // Función para cargar solo préstamos activos
  const loadActiveLoans = () => {
    loanService
      .getActiveLoans()
      .then((response) => {
        console.log("Mostrando listado de préstamos activos.", response.data);
        setLoans(response.data);
        setShowingActiveOnly(true);
      })
      .catch((error) => {
        console.log(
          "Se ha producido un error al intentar mostrar listado de préstamos activos.",
          error
        );
      });
  };

  {
    /* Hook de efecto para cargar los prestamos al montar el componente */
  }
  useEffect(() => {
    init();
  }, []);

  const handleReturnLoan = (id) => {
    console.log("Retornando préstamo con id:", id);
    navigate(`/loan/return/${id}`);
  };

  const handlePayLoan = (id) => {
    console.log("Procesando pago de préstamo con id:", id);
    const loan = loans.find((loan) => loan.id === id);

    if (!loan) {
      Swal.fire({
        title: 'Error',
        text: 'No se encontró el préstamo.',
        icon: 'error',
        confirmButtonColor: '#d33'
      });
      return;
    }

    const totalPayment = loan.totalAmount + loan.totalPenalties;

    Swal.fire({
      title: '¿Confirmar pago?',
      html: `¿Está seguro que desea pagar este préstamo por un monto de:<br><strong>${formatCurrency(totalPayment)}</strong>?`,
      icon: 'question',
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: 'Sí, pagar',
      cancelButtonText: 'Cancelar'
    }).then((result) => {
      if (result.isConfirmed) {
        // Buscar el cliente y validar que existe
        const customer = customers.find((c) => c.name === loan.customerName);

        if (!customer) {
          Swal.fire({
            title: 'Error',
            text: `No se encontró el cliente "${loan.customerName}" en la base de datos.`,
            icon: 'error',
            confirmButtonColor: '#d33'
          });
          console.error("Cliente no encontrado.");
          console.error("Nombre del cliente buscado:", loan.customerName);
          console.error(
            "Clientes disponibles:",
            customers.map((c) => c.name)
          );
          return;
        }

        const paymentData = {
          customerId: customer.id,
          paymentAmount: totalPayment,
        };
        console.log("Datos de pago del préstamo:", paymentData);

        loanService
          .makePayment(id, paymentData)
          .then((response) => {
            console.log("Préstamo pagado con éxito:", response.data);
            Swal.fire({
              title: '¡Pagado!',
              text: 'El préstamo ha sido pagado con éxito.',
              icon: 'success',
              confirmButtonColor: '#3085d6',
              timer: 3000
            });
            init(); // Recargar la lista de préstamos
          })
          .catch((error) => {
            console.log("Error al pagar el préstamo:", error);
            
            const errorMessage = error.response?.data?.message 
              || error.response?.data 
              || 'Se ha producido un error al pagar el préstamo.';
            
            Swal.fire({
              title: 'Error',
              text: errorMessage,
              icon: 'error',
              confirmButtonColor: '#d33'
            });
          });
      }
    });
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


  return (
    <Box sx={{ mx: '20px', marginBottom: '20px', bgcolor: 'background.paper' }}>
      <Breadcrumb />
      <TableContainer component={Paper}>
        <Box sx={{ p: 2, textAlign: 'center' }}>
          <Typography variant="h4" component="h1" sx={{ fontWeight: 600 }}>
            Listado de Préstamos
          </Typography>
        </Box>
      <Box sx={{ display: "flex", gap: 2, flexWrap: "wrap", justifyContent: "center", marginY: 2 }}>
        <Link to="/loan/add" style={{ textDecoration: "none" }}>
          <Tooltip title="Crear un nuevo préstamo" arrow>
            <Button
              variant="contained"
              color="primary"
              startIcon={<AddCircleRoundedIcon />}
            >
              Añadir Prestamo
            </Button>
          </Tooltip>
        </Link>
        
        {!showingActiveOnly ? (
          <Tooltip title="Filtrar solo préstamos activos (Vigente y Atrasada)" arrow>
            <Button
              variant="contained"
              color="success"
              startIcon={<FilterListIcon />}
              onClick={loadActiveLoans}
            >
              Mostrar Préstamos Activos
            </Button>
          </Tooltip>
        ) : (
          <Tooltip title="Mostrar todos los préstamos sin filtro" arrow>
            <Button
              variant="outlined"
              color="secondary"
              startIcon={<RefreshIcon />}
              onClick={init}
            >
              Mostrar Todos los Préstamos
            </Button>
          </Tooltip>
        )}
      </Box>
      {showingActiveOnly && (
        <Alert severity="info" sx={{ marginBottom: 2 }}>
          Mostrando solo préstamos activos (Vigente o Atrasado)
        </Alert>
      )}
      <br />
      <Table sx={{ minWidth: 650 }} size="small" aria-label="a dense table">
        <TableHead>
          <TableRow>
            <TableCell align="left" sx={{ fontWeight: "bold" }}>
              Fecha de Inicio
            </TableCell>
            <TableCell align="left" sx={{ fontWeight: "bold" }}>
              Fecha de retorno
            </TableCell>
            <TableCell align="right" sx={{ fontWeight: "bold" }}>
              Fecha Limite
            </TableCell>
            <TableCell align="right" sx={{ fontWeight: "bold" }}>
              Fecha de pago
            </TableCell>
            <TableCell align="right" sx={{ fontWeight: "bold" }}>
              Valor arriendo
            </TableCell>
            <TableCell align="left" sx={{ fontWeight: "bold" }}>
              Valor de multas
            </TableCell>
            <TableCell align="left" sx={{ fontWeight: "bold" }}>
              Estado
            </TableCell>
            <TableCell align="left" sx={{ fontWeight: "bold" }}>
              Nombre del cliente
            </TableCell>
            <TableCell align="left" sx={{ fontWeight: "bold" }}>
              Acciones
            </TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {/* Itera sobre los prestamos y crea una fila por cada uno */}
          {loans.map((loan) => (
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
              <TableCell align="right">
                {formatCurrency(loan.totalAmount)}
              </TableCell>
              <TableCell align="right">
                {formatCurrency(loan.totalPenalties)}
              </TableCell>
              <TableCell align="center">
                <Chip
                  label={loan.loanStatus}
                  color={getStatusColor(loan.loanStatus)}
                  icon={getStatusIcon(loan.loanStatus)}
                  sx={{ minWidth: '110px' }}
                />
              </TableCell>
              <TableCell align="center">{loan.customerName}</TableCell>
              <TableCell>
                <Tooltip title="Registrar la devolución de la herramienta" arrow>
                  <span>
                    <Button
                      variant="contained"
                      color="info"
                      size="small"
                      onClick={() => handleReturnLoan(loan.id)}
                      style={{ marginLeft: "0.5rem" }}
                      startIcon={<AssignmentReturnedIcon />}
                      disabled={
                        loan.returnDate ||
                        loan.loanStatus === "Retornado" ||
                        loan.loanStatus === "Finalizado"
                      }
                    >
                      Retornar
                    </Button>
                  </span>
                </Tooltip>
                <Tooltip title="Registrar el pago de prestamos" arrow>
                  <span>
                    <Button
                      variant="contained"
                      color="success"
                      size="small"
                      onClick={() => handlePayLoan(loan.id)}
                      style={{ marginLeft: "0.5rem" }}
                      startIcon={<PaidIcon />}
                      disabled={
                        loan.loanStatus === "Vigente" ||
                        loan.loanStatus === "Finalizado" ||
                        customers.length === 0
                      }
                    >
                      Pagar
                    </Button>
                  </span>
                </Tooltip>
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </TableContainer>
    </Box>
  );
};

export default LoanList;
