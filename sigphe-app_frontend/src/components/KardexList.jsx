import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import kardexService from "../services/kardex.service";
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
import TextField from "@mui/material/TextField";
import MenuItem from "@mui/material/MenuItem";
import FormControl from "@mui/material/FormControl";
import Alert from "@mui/material/Alert";
import SearchIcon from "@mui/icons-material/Search";
import RefreshIcon from "@mui/icons-material/Refresh";
import { DateTimePicker } from "@mui/x-date-pickers/DateTimePicker";
import { LocalizationProvider } from "@mui/x-date-pickers/LocalizationProvider";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";

const KardexList = () => {
  const [kardexs, setKardex] = useState([]);
  const [tools, setTools] = useState([]); // Lista de herramientas para el filtro

  // Estados para los filtros
  const [startDate, setStartDate] = useState(null);
  const [endDate, setEndDate] = useState(null);
  const [selectedToolId, setSelectedToolId] = useState("");
  const [isFiltering, setIsFiltering] = useState(false);
  const [filterType, setFilterType] = useState(""); // "date", "tool", "both"

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

  // Cargar todas las herramientas para el selector
  const loadTools = () => {
    toolService
      .getAll()
      .then((response) => {
        console.log("Herramientas cargadas:", response.data);
        setTools(response.data);
      })
      .catch((error) => {
        console.log("Error al cargar herramientas:", error);
      });
  };

  // Cargar todo el kardex sin filtro
  const loadAllKardex = () => {
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

  // Cargar kardex filtrado por fechas
  const loadKardexByDateRange = (start, end) => {
    const startDateFormatted = start.format("YYYY-MM-DDTHH:mm:ss");
    const endDateFormatted = end.format("YYYY-MM-DDTHH:mm:ss");

    kardexService
      .getAllKardexDateRange(startDateFormatted, endDateFormatted)
      .then((response) => {
        console.log("Kardex filtrado por fechas:", response.data);
        setKardex(response.data);
      })
      .catch((error) => {
        console.log("Error al cargar kardex filtrado por fechas:", error);
      });
  };

  // Cargar kardex filtrado por herramienta
  const loadKardexByTool = (toolId) => {
    kardexService
      .getToolHistory(toolId)
      .then((response) => {
        console.log("Kardex filtrado por herramienta:", response.data);
        setKardex(response.data);
      })
      .catch((error) => {
        console.log("Error al cargar kardex filtrado por herramienta:", error);
      });
  };

  // Cargar kardex filtrado por herramienta y fechas
  const loadKardexByToolAndDateRange = (toolId, start, end) => {
    const startDateFormatted = start.format("YYYY-MM-DDTHH:mm:ss");
    const endDateFormatted = end.format("YYYY-MM-DDTHH:mm:ss");

    kardexService
      .getToolHistoryDateRange(toolId, startDateFormatted, endDateFormatted)
      .then((response) => {
        console.log("Kardex filtrado por herramienta y fechas:", response.data);
        setKardex(response.data);
      })
      .catch((error) => {
        console.log("Error al cargar kardex filtrado por herramienta y fechas:", error);
      });
  };

  // Aplicar filtros
  const handleFilter = () => {
    const hasDateFilter = startDate && endDate;
    const hasToolFilter = selectedToolId !== "" && selectedToolId !== null && selectedToolId !== undefined;

    // Validaciones
    if (!hasDateFilter && !hasToolFilter) {
      alert("Por favor, seleccione al menos un filtro (fechas o herramienta).");
      return;
    }

    if (hasDateFilter && startDate.isAfter(endDate)) {
      alert("La fecha de inicio debe ser anterior a la fecha de fin.");
      return;
    }

    console.log("Aplicando filtro - hasDateFilter:", hasDateFilter, "hasToolFilter:", hasToolFilter);
    console.log("selectedToolId:", selectedToolId, "tipo:", typeof selectedToolId);

    // Determinar qué filtro aplicar
    if (hasDateFilter && hasToolFilter) {
      // Filtro por herramienta Y fechas
      console.log("Aplicando filtro: herramienta Y fechas");
      setFilterType("both");
      loadKardexByToolAndDateRange(Number(selectedToolId), startDate, endDate);
    } else if (hasDateFilter) {
      // Solo filtro por fechas
      console.log("Aplicando filtro: solo fechas");
      setFilterType("date");
      loadKardexByDateRange(startDate, endDate);
    } else if (hasToolFilter) {
      // Solo filtro por herramienta
      console.log("Aplicando filtro: solo herramienta, ID:", selectedToolId);
      setFilterType("tool");
      loadKardexByTool(Number(selectedToolId));
    }

    setIsFiltering(true);
  };

  // Limpiar filtros
  const handleClearFilter = () => {
    setStartDate(null);
    setEndDate(null);
    setSelectedToolId("");
    setIsFiltering(false);
    setFilterType("");
    loadAllKardex();
  };

  const init = () => {
    loadAllKardex();
    loadTools();
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

  // Función para obtener el mensaje del filtro activo
  const getFilterMessage = () => {
    if (filterType === "both") {
      const tool = tools.find(t => t.id === selectedToolId);
      return `Mostrando kardex de la herramienta "${tool?.name}" desde ${startDate?.format("DD/MM/YYYY HH:mm")} hasta ${endDate?.format("DD/MM/YYYY HH:mm")}`;
    } else if (filterType === "date") {
      return `Mostrando kardex desde ${startDate?.format("DD/MM/YYYY HH:mm")} hasta ${endDate?.format("DD/MM/YYYY HH:mm")}`;
    } else if (filterType === "tool") {
      const tool = tools.find(t => t.id === selectedToolId);
      return `Mostrando kardex de la herramienta "${tool?.name}"`;
    }
    return "";
  };

  useEffect(() => {
    init();
  }, []);

  return (
    <div>
      {/* Filtros */}
      <Paper sx={{ padding: 3, marginBottom: 3 }}>
        <h3 style={{ marginTop: 0 }}>Filtrar Kardex</h3>
        <LocalizationProvider dateAdapter={AdapterDayjs}>
          <Box
            sx={{
              display: "flex",
              gap: 2,
              flexWrap: "wrap",
              alignItems: "center",
              marginBottom: 2,
            }}
          >
            <DateTimePicker
              label="Fecha de Inicio"
              value={startDate}
              onChange={(newValue) => setStartDate(newValue)}
              sx={{ flex: 1, minWidth: 200 }}
            />
            <DateTimePicker
              label="Fecha de Fin"
              value={endDate}
              onChange={(newValue) => setEndDate(newValue)}
              sx={{ flex: 1, minWidth: 200 }}
            />
            <FormControl sx={{ flex: 1, minWidth: 200 }}>
              <TextField
                id="toolFilter"
                label="Filtrar por Herramienta"
                value={selectedToolId}
                select
                variant="outlined"
                onChange={(e) => {
                  const value = e.target.value;
                  console.log("Herramienta seleccionada:", value, "tipo:", typeof value);
                  setSelectedToolId(value);
                }}
              >
                <MenuItem value="">
                  <em>Todas las herramientas</em>
                </MenuItem>
                {tools.map((tool) => (
                  <MenuItem key={tool.id} value={tool.id}>
                    {tool.name} (SKU: {tool.id})
                  </MenuItem>
                ))}
              </TextField>
            </FormControl>
          </Box>
          <Box sx={{ display: "flex", gap: 2 }}>
            <Button
              variant="contained"
              color="primary"
              startIcon={<SearchIcon />}
              onClick={handleFilter}
            >
              Filtrar
            </Button>
            {isFiltering && (
              <Button
                variant="outlined"
                color="secondary"
                startIcon={<RefreshIcon />}
                onClick={handleClearFilter}
              >
                Limpiar Filtro
              </Button>
            )}
          </Box>
        </LocalizationProvider>
        {isFiltering && (
          <Alert severity="info" sx={{ marginTop: 2 }}>
            {getFilterMessage()}
          </Alert>
        )}
      </Paper>

      <TableContainer component={Paper}>
        <br />
        <h2 style={{ textAlign: "center" }}>Registro de Movimientos de Herramientas</h2>
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
              SKU
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
              <TableCell align="center">{kardex.toolId}</TableCell>
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
    </div>
  );
};

export default KardexList;
