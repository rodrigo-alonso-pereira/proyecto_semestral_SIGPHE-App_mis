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
              <TableCell align="center">{tool.name}</TableCell>
              <TableCell align="center">{formatCurrency(tool.rentalValue)}</TableCell>
              <TableCell align="center">{formatCurrency(tool.replacementValue)}</TableCell>
              <TableCell align="center">{tool.category}</TableCell>
              <TableCell align="center">{tool.status}</TableCell>
              <TableCell align="center">{tool.model}</TableCell>
              <TableCell>
                <Button
                  variant="contained"
                  color="info"
                  size="small"
                  status="disabled"
                  onClick={() => handleEdit(tool.id)}
                  style={{ marginLeft: "0.5rem" }}
                  startIcon={<EditIcon />}
                >
                  Editar
                </Button>

                <Button
                  variant="contained"
                  color="error"
                  size="small"
                  status="disabled"
                  onClick={() => handleDelete(tool.id)}
                  style={{ marginLeft: "0.5rem" }}
                  startIcon={<DeleteIcon />}
                >
                  Eliminar
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
