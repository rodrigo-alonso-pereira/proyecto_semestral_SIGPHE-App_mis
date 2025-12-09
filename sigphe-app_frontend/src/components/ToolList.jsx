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
import Box from "@mui/material/Box";
import Alert from "@mui/material/Alert";
import Chip from "@mui/material/Chip";
import AddCircleRoundedIcon from "@mui/icons-material/AddCircleRounded";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";
import FilterListIcon from "@mui/icons-material/FilterList";
import RefreshIcon from "@mui/icons-material/Refresh";
import CheckCircleIcon from "@mui/icons-material/CheckCircle";
import BuildIcon from "@mui/icons-material/Build";
import HandshakeIcon from "@mui/icons-material/Handshake";
import RemoveCircleIcon from "@mui/icons-material/RemoveCircle";
import Swal from "sweetalert2";

const ToolList = () => {
  const [tools, setTools] = useState([]);
  const [showingActiveOnly, setShowingActiveOnly] = useState(false);

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
        setShowingActiveOnly(false);
      })
      .catch((error) => {
        console.log(
          "Se ha producido un error al intentar mostrar listado de todas las herramientas.",
          error
        );
      });
  };

  // Función para cargar solo herramientas activas
  const loadActiveTools = () => {
    toolService
      .getActiveTools()
      .then((response) => {
        console.log("Mostrando listado de herramientas activas.", response.data);
        setTools(response.data);
        setShowingActiveOnly(true);
      })
      .catch((error) => {
        console.log(
          "Se ha producido un error al intentar mostrar listado de herramientas activas.",
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
    
    // Buscar la herramienta por id
    const tool = tools.find((t) => t.id === id);
    
    if (!tool) {
      Swal.fire({
        title: 'Error',
        text: 'No se encontró la herramienta.',
        icon: 'error',
        confirmButtonColor: '#d33'
      });
      return;
    }
    
    Swal.fire({
      title: '¿Desactivar herramienta?',
      html: `
        <div style="text-align: left; margin-top: 15px;">
          <p><strong>Nombre:</strong> ${tool.name}</p>
          <p><strong>Modelo:</strong> ${tool.model}</p>
          <p style="margin-top: 15px; font-style: italic; color: #666;">
            ¿Está seguro que desea desactivar esta herramienta?
          </p>
        </div>
      `,
      icon: 'question',
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: 'Sí, desactivar',
      cancelButtonText: 'Cancelar'
    }).then((result) => {
      if (result.isConfirmed) {
        const data = {
          workerId: 1, // ID del empleado que realiza la desactivación (debe ser dinámico en una app real)
        };
        toolService
          .deactivateTool(id, data)
          .then((response) => {
            console.log("Herramienta desactivada:", response.data);
            Swal.fire({
              title: '¡Desactivada!',
              text: 'La herramienta ha sido desactivada exitosamente.',
              icon: 'success',
              confirmButtonColor: '#3085d6',
              timer: 3000
            });
            init(); // Recargar la lista de herramientas después de la desactivación
          })
          .catch((error) => {
            console.log("Error al desactivar herramienta:", error);
            
            const errorMessage = error.response?.data?.message 
              || error.response?.data 
              || 'Se ha producido un error al desactivar la herramienta.';
            
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

  // Función para obtener el icono según el estado de la herramienta
  const getStatusIcon = (status) => {
    switch (status) {
      case "Disponible":
        return <CheckCircleIcon />; // Verde - disponible para préstamo
      case "En Reparacion":
        return <BuildIcon />; // Naranja - en reparación
      case "Prestada":
        return <HandshakeIcon />; // Azul - prestada
      case "Dada de baja":
        return <RemoveCircleIcon />; // Gris - dada de baja
      default:
        return null;
    }
  };

  return (
    <TableContainer component={Paper}>
      <br />
      <Box sx={{ display: "flex", gap: 2, flexWrap: "wrap", justifyContent: "center", marginBottom: 2 }}>
        <Link to="/tool/add" style={{ textDecoration: "none" }}>
          <Button
            variant="contained"
            color="primary"
            startIcon={<AddCircleRoundedIcon />}
          >
            Añadir Herramienta
          </Button>
        </Link>
        
        {!showingActiveOnly ? (
          <Button
            variant="contained"
            color="success"
            startIcon={<FilterListIcon />}
            onClick={loadActiveTools}
          >
            Mostrar Herramientas Activas
          </Button>
        ) : (
          <Button
            variant="outlined"
            color="secondary"
            startIcon={<RefreshIcon />}
            onClick={init}
          >
            Mostrar Todas las Herramientas
          </Button>
        )}
      </Box>
      {showingActiveOnly && (
        <Alert severity="info" sx={{ marginBottom: 2 }}>
          Mostrando solo herramientas activas (Disponible)
        </Alert>
      )}
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
                  icon={getStatusIcon(tool.status)}
                  sx={{ minWidth: '140px' }}
                />
              </TableCell>
              <TableCell align="center">{tool.model}</TableCell>
              <TableCell style={{ display: 'flex', gap: '0.5rem' }}>
                <Button
                  variant="contained"
                  color="info"
                  size="small"
                  onClick={() => handleEdit(tool.id)}
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
