import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import kardexService from "../services/kardex.service";
import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableCell, { tableCellClasses } from "@mui/material/TableCell";
import TableContainer from "@mui/material/TableContainer";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import Paper from "@mui/material/Paper";
import Chip from "@mui/material/Chip";
import Button from "@mui/material/Button";
import AddCircleRoundedIcon from "@mui/icons-material/AddCircleRounded";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";

const KardexList = () => {
  const [kardexs, setKardex] = useState([]);

  const navigate = useNavigate();

  // Función para formatear fechas
  const formatDate = (dateString) => {
    if (!dateString) return "-";

    const date = new Date(dateString);
    const day = date.getDate().toString().padStart(2, "0");
    const month = (date.getMonth() + 1).toString().padStart(2, "0");
    const year = date.getFullYear();
    const hours = date.getHours().toString().padStart(2, "0");

    return `${day}/${month}/${year} ${hours}:00`;
  };

  const init = () => {
    kardexService
      .getAll()
      .then((response) => {
        console.log(
          "Mostrando listado de todas las transacciones.",
          response.data
        );
        setKardex(response.data);
      })
      .catch((error) => {
        console.log(
          "Se ha producido un error al intentar mostrar listado de todas las transacciones.",
          error
        );
      });
  };

  // Función para obtener el color según el estado del préstamo
  const getStatusColor = (status) => {
    switch (status) {
      case "Ingreso":
        return "success"; // Verde
      case "Prestamo":
        return "primary"; // Azul
      case "Reparacion":
        return "warning"; // Naranja
      case "Devolucion":
        return "secondary"; // Azul
      case "Baja":
        return "default"; // Gris
      default:
        return "default";
    }
  };

  useEffect(() => {
    init();
  }, []);

  return (
    <TableContainer component={Paper}>
      <br />
      <br />
      <br />
      <Table sx={{ minWidth: 650 }} size="small" aria-label="a dense table">
        <TableHead>
          <TableRow>
            <TableCell align="center" sx={{ fontWeight: "bold" }}>
              Fecha de registro
            </TableCell>
            <TableCell align="center" sx={{ fontWeight: "bold" }}>
              Cantidad
            </TableCell>
            <TableCell align="center" sx={{ fontWeight: "bold" }}>
              Nombre herramienta
            </TableCell>
            <TableCell align="center" sx={{ fontWeight: "bold" }}>
              Tipo de movimiento
            </TableCell>
            <TableCell align="center" sx={{ fontWeight: "bold" }}>
              Empleado
            </TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {kardexs.map((kardex) => (
            <TableRow
              key={kardex.id}
              sx={{ "&:last-child td, &:last-child th": { border: 0 } }}
            >
              <TableCell align="center">
                {formatDate(kardex.registrationDate)}
              </TableCell>
              <TableCell align="center">{kardex.quantity}</TableCell>
              <TableCell align="center">{kardex.toolName}</TableCell>
              <TableCell align="center">
                <Chip
                  label={kardex.kardexTypeName}
                  color={getStatusColor(kardex.kardexTypeName)}
                  size="small"
                />
              </TableCell>
              <TableCell align="center">{kardex.workerName}</TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </TableContainer>
  );
};

export default KardexList;
