import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import loanService from "../services/loan.service";
import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableCell, { tableCellClasses } from "@mui/material/TableCell";
import TableContainer from "@mui/material/TableContainer";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import Paper from "@mui/material/Paper";
import Button from "@mui/material/Button";
import AddCircleRoundedIcon from "@mui/icons-material/AddCircleRounded";
import AssignmentReturnedIcon from '@mui/icons-material/AssignmentReturned';

const LoanList = () => {
  const [loans, setLoans] = useState([]);

  const navigate = useNavigate();

  // Función para formatear fechas
  const formatDate = (dateString) => {
    if (!dateString) return '-';
    
    const date = new Date(dateString);
    const day = date.getDate().toString().padStart(2, '0');
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const year = date.getFullYear();
    const hours = date.getHours().toString().padStart(2, '0');
    
    return `${day}/${month}/${year} ${hours}:00`;
  };

  // Función para formatear valores monetarios
  const formatCurrency = (value) => {
    if (!value || value === 0) return '$0';
    
    const number = parseFloat(value);
    return new Intl.NumberFormat('es-CL', {
      style: 'currency',
      currency: 'CLP',
      minimumFractionDigits: 0,
      maximumFractionDigits: 0
    }).format(number);
  };

  const init = () => {
    loanService
      .getAll()
      .then((response) => {
        console.log("Mostrando listado de todos los prestamos.", response.data);
        setLoans(response.data);
      })
      .catch((error) => {
        console.log(
          "Se ha producido un error al intentar mostrar listado de todos los prestamos.",
          error
        );
      });
  };

  useEffect(() => {
    init();
  }, []);

  const handleReturnLoan = (id) => {
    console.log("Retornando préstamo con id:", id);
    navigate(`/loan/return/${id}`);
  };

  return (
    <TableContainer component={Paper}>
      <br />
      <Link
        to="/loan/add"
        style={{ textDecoration: "none", marginBottom: "1rem" }}
      >
        <Button
          variant="contained"
          color="primary"
          startIcon={<AddCircleRoundedIcon />}
        >
          Añadir Prestamo
        </Button>
      </Link>
      <br /> <br />
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
          {loans.map((loan) => (
            <TableRow
              key={loan.id}
              sx={{ "&:last-child td, &:last-child th": { border: 0 } }}
            >
              <TableCell align="center">{formatDate(loan.startDate)}</TableCell>
              <TableCell align="center">{formatDate(loan.returnDate)}</TableCell>
              <TableCell align="center">{formatDate(loan.dueDate)}</TableCell>
              <TableCell align="center">{formatDate(loan.paymentDate)}</TableCell>
              <TableCell align="right">{formatCurrency(loan.totalAmount)}</TableCell>
              <TableCell align="right">{formatCurrency(loan.totalPenalties)}</TableCell>
              <TableCell align="center">{loan.loanStatus}</TableCell>
              <TableCell align="center">{loan.customerName}</TableCell>
              <TableCell>
                <Button
                  variant="contained"
                  color="info"
                  size="small"
                  onClick={() => handleReturnLoan(loan.id)}
                  style={{ marginLeft: "0.5rem" }}
                  startIcon={<AssignmentReturnedIcon />}
                >
                  Retornar
                </Button>

                
              </TableCell>
              
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </TableContainer>
  );
};

export default LoanList;
