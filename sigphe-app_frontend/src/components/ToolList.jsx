import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import toolService from "../services/tool.service";
import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableCell, { tableCellClasses } from "@mui/material/TableCell";
import TableContainer from "@mui/material/TableContainer";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import Paper from "@mui/material/Paper";
import Button from "@mui/material/Button";
import Chip from "@mui/material/Chip";
import AddCircleRoundedIcon from "@mui/icons-material/AddCircleRounded";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";

const ToolList = () => {
  const [tools, setTools] = useState([]);

  const navigate = useNavigate();

  // Función para formatear valores monetarios
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

  const init = () => {
    toolService
      .getAll()
      .then((response) => {
        console.log(
          "Mostrando listado de todas las herramientas.",
          response.data
        );
        setTools(response.data);
      })
      .catch((error) => {
        console.log(
          "Se ha producido un error al intentar mostrar listado de todas las herramientas.",
          error
        );
      });
  };

  useEffect(() => {
    init();
  }, []);

  const handleEdit = (id) => {
    console.log("Editando herramienta con id:", id);
    navigate(`/tool/edit/${id}`);
  };

  const handleDelete = (id) => {
    console.log("Desactivando herramienta con id:", id);
    const confirmDeactivate = window.confirm(
      "¿Está seguro que desea retornar esta herramienta?"
    );
    if (confirmDeactivate) {
      const data = {
        workerId: 1, // ID del empleado que realiza la desactivación (debe ser dinámico en una app real)
      };
      toolService
        .deactivateTool(id, data)
        .then((response) => {
          console.log("Herramienta desactivada:", response.data);
          init(); // Recargar la lista de herramientas después de la desactivación
        })
        .catch((error) => {
          console.log("Error al desactivar herramienta:", error);
        });
    }
  };

  // Función para obtener el color según el estado del préstamo
  const getStatusColor = (status) => {
    switch (status) {
      case "Disponible":
        return "success"; // Verde
      case "En Reparacion":
        return "warning"; // Naranja
      case "Prestada":
        return "info"; // Azul
      case "Dada de baja":
        return "default"; // Gris
      default:
        return "default";
    }
  };

  return (
    <TableContainer component={Paper}>
      <br />
      <Link
        to="/tool/add"
        style={{ textDecoration: "none", marginBottom: "1rem" }}
      >
        <Button
          variant="contained"
          color="primary"
          startIcon={<AddCircleRoundedIcon />}
        >
          Añadir Herramienta
        </Button>
      </Link>
      <br /> <br />
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
              Valor de renta
            </TableCell>
            <TableCell align="center" sx={{ fontWeight: "bold" }}>
              Valor de reemplazo
            </TableCell>
            <TableCell align="center" sx={{ fontWeight: "bold" }}>
              Categoria
            </TableCell>
            <TableCell align="center" sx={{ fontWeight: "bold" }}>
              Estado
            </TableCell>
            <TableCell align="center" sx={{ fontWeight: "bold" }}>
              Modelo
            </TableCell>
            <TableCell align="center" sx={{ fontWeight: "bold" }}>
              Operaciones
            </TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {tools.map((tool) => (
            <TableRow
              key={tool.id}
              sx={{ "&:last-child td, &:last-child th": { border: 0 } }}
            >
              <TableCell align="center">{tool.id}</TableCell>
              <TableCell align="center">{tool.name}</TableCell>
              <TableCell align="center">
                {formatCurrency(tool.rentalValue)}
              </TableCell>
              <TableCell align="center">
                {formatCurrency(tool.replacementValue)}
              </TableCell>
              <TableCell align="center">{tool.category}</TableCell>
              <TableCell align="center">
                <Chip
                  label={tool.status}
                  color={getStatusColor(tool.status)}
                  size="small"
                />
              </TableCell>
              <TableCell align="center">{tool.model}</TableCell>
              <TableCell>
                <Button
                  variant="contained"
                  color="info"
                  size="small"
                  onClick={() => handleEdit(tool.id)}
                  style={{ marginLeft: "0.5rem" }}
                  startIcon={<EditIcon />}
                  disabled={
                    tool.status === "Prestada" || tool.status === "Dada de baja"
                  }
                >
                  Editar
                </Button>
                <Button
                  variant="contained"
                  color="error"
                  size="small"
                  onClick={() => handleDelete(tool.id)}
                  style={{ marginLeft: "0.5rem" }}
                  startIcon={<DeleteIcon />}
                  disabled={
                    tool.status === "Prestada" || tool.status === "Dada de baja"
                  }
                >
                  Desactivar
                </Button>
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </TableContainer>
  );
};

export default ToolList;
