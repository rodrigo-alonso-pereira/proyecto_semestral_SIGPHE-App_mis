import { useState, useEffect } from "react";
import { Link, useParams, useNavigate } from "react-router-dom";
import loanService from "../services/loan.service";
import Box from "@mui/material/Box";
import TextField from "@mui/material/TextField";
import Button from "@mui/material/Button";
import FormControl from "@mui/material/FormControl";
import MenuItem from "@mui/material/MenuItem";
import SaveIcon from "@mui/icons-material/Save";
import Chip from "@mui/material/Chip";
import toolService from "../services/tool.service";
import userService from "../services/user.service";
import { DateTimePicker } from "@mui/x-date-pickers/DateTimePicker";
import { LocalizationProvider } from "@mui/x-date-pickers/LocalizationProvider";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import dayjs from "dayjs";
import Swal from "sweetalert2";

const AddLoan = () => {
  const [tools, setTools] = useState([]); // Lista de herramientas activas
  const [customers, setCustomers] = useState([]); // Lista de clientes activos
  const [employees, setEmployees] = useState([]); // Lista de empleados

  const [dueDate, setDueDate] = useState(null);
  const [customerId, setCustomerId] = useState(0);
  const [workerId, setWorkerId] = useState(0);
  const [toolIds, setToolIds] = useState([]);
  const [titleLoanForm, setTitleLoanForm] = useState("");
  const navigate = useNavigate();

  const init = () => {
    console.log("Iniciando carga de datos...");
    
    // Cargar herramientas activas
    toolService
      .getActiveTools()
      .then((response) => {
        console.log("Herramientas cargadas:", response.data);
        setTools(response.data);
      })
      .catch((error) => {
        console.log("Error al cargar herramientas:", error);
      });

    // Cargar clientes activos
    userService
      .getActiveCostumers()
      .then((response) => {
        console.log("Clientes cargados:", response.data);
        setCustomers(response.data);
      })
      .catch((error) => {
        console.log("Error al cargar clientes:", error);
      });

    // Cargar empleados
    userService
      .getEmployees()
      .then((response) => {
        console.log("Empleados cargados:", response.data);
        setEmployees(response.data);
      })
      .catch((error) => {
        console.log("Error al cargar empleados:", error);
      });
  };

  // Función para manejar la selección de herramientas
  const handleToolSelection = (toolId) => {
    if (!toolIds.includes(toolId)) {
      setToolIds([...toolIds, toolId]);
    }
  };

  // Función para eliminar una herramienta seleccionada
  const handleRemoveTool = (toolIdToRemove) => {
    setToolIds(toolIds.filter(id => id !== toolIdToRemove));
  };

  const saveLoan = (e) => {
    e.preventDefault();
    
    // Validaciones antes de confirmar
    if (!dueDate) {
      Swal.fire({
        title: 'Campo requerido',
        text: 'Por favor, ingrese la fecha de devolución.',
        icon: 'warning',
        confirmButtonColor: '#3085d6'
      });
      return;
    }

    if (!customerId || customerId === 0) {
      Swal.fire({
        title: 'Campo requerido',
        text: 'Por favor, seleccione un cliente.',
        icon: 'warning',
        confirmButtonColor: '#3085d6'
      });
      return;
    }

    if (!workerId || workerId === 0) {
      Swal.fire({
        title: 'Campo requerido',
        text: 'Por favor, seleccione un trabajador.',
        icon: 'warning',
        confirmButtonColor: '#3085d6'
      });
      return;
    }

    if (!toolIds || toolIds.length === 0) {
      Swal.fire({
        title: 'Campo requerido',
        text: 'Por favor, seleccione al menos una herramienta.',
        icon: 'warning',
        confirmButtonColor: '#3085d6'
      });
      return;
    }
    
    // Mostrar alerta de confirmación con SweetAlert2
    Swal.fire({
      title: '¿Desea guardar el préstamo?',
      text: 'Se creará un nuevo registro de préstamo',
      icon: 'question',
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: 'Sí, guardar',
      cancelButtonText: 'Cancelar'
    }).then((result) => {
      if (result.isConfirmed) {
        // Formatear la fecha manteniendo la hora local (horario Chile)
        const formattedDueDate = dueDate ? dueDate.format('YYYY-MM-DDTHH:mm:ss') : null;
        const loan = { dueDate: formattedDueDate, customerId, workerId, toolIds };
        
        //Crear nuevo prestamo
        loanService
          .create(loan)
          .then((response) => {
            console.log("Prestamo ha sido añadido.", response.data);
            Swal.fire({
              title: '¡Guardado!',
              text: 'El préstamo ha sido creado exitosamente.',
              icon: 'success',
              confirmButtonColor: '#3085d6'
            });
            navigate("/loan/list");
          })
          .catch((error) => {
            console.log(
              "Ha ocurrido un error al intentar crear nuevo prestamo.",
              error
            );
            
            // Capturar el mensaje de error del backend
            const errorMessage = error.response?.data?.message 
              || error.response?.data 
              || 'Ha ocurrido un error al intentar crear el préstamo.';
            
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

  useEffect(() => {
    setTitleLoanForm("Nuevo Prestamo");
    init();
  }, []);

  return (
    <Box
      display="flex"
      flexDirection="column"
      alignItems="center"
      justifyContent="center"
      component="form"
      sx={{ 
        maxWidth: { xs: '95%', sm: '600px', md: '700px' },
        margin: '0 auto',
        padding: { xs: 2, md: 3 }
      }}
    >
      <h3> {titleLoanForm} </h3>
      <hr style={{ width: '100%' }} />
      <Box 
        component="form" 
        sx={{ 
          width: '100%',
          display: 'flex',
          flexDirection: 'column',
          gap: 3
        }}
      >
        <FormControl fullWidth>
          <LocalizationProvider dateAdapter={AdapterDayjs}>
            <DateTimePicker
              id="dueDate"
              label="Ingrese fecha de devolución"
              value={dueDate}
              onChange={(newValue) => setDueDate(newValue)}
              minDate={dayjs()}
              minTime={dueDate && dayjs(dueDate).isSame(dayjs(), 'day') ? dayjs() : undefined}
            />
          </LocalizationProvider>
        </FormControl>

        <FormControl fullWidth>
          <TextField
            id="customerId"
            label="Agregar Cliente que hará la reserva"
            value={customerId}
            select
            variant="standard"
            onChange={(e) => {
              const value = e.target.value;
              setCustomerId(Number(value));
            }}
          >
            {customers.length === 0 ? (
              <MenuItem disabled>Cargando clientes...</MenuItem>
            ) : (
              customers.map((customer) => (
                <MenuItem key={customer.id} value={customer.id}>
                  {customer.name}
                </MenuItem>
              ))
            )}
          </TextField>
        </FormControl>

        <FormControl fullWidth>
          <TextField
            id="workerId"
            label="Agregar Trabajador que realiza la reserva"
            value={workerId}
            select
            variant="standard"
            onChange={(e) => {
              const value = e.target.value;
              setWorkerId(Number(value));
            }}
          >
            {employees.length === 0 ? (
              <MenuItem disabled>Cargando empleados...</MenuItem>
            ) : (
              employees.map((employee) => (
                <MenuItem key={employee.id} value={employee.id}>
                  {employee.name}
                </MenuItem>
              ))
            )}
          </TextField>
        </FormControl>

        <FormControl fullWidth>
          <TextField
            id="toolId"
            label="Seleccionar herramientas para el préstamo"
            value=""
            select
            variant="standard"
            onChange={(e) => {
              const value = e.target.value;
              if (value) {
                handleToolSelection(Number(value));
              }
            }}
          >
            <MenuItem value="" disabled>
              Seleccione una herramienta...
            </MenuItem>
            {tools.length === 0 ? (
              <MenuItem disabled>Cargando herramientas...</MenuItem>
            ) : (
              tools
                .filter(tool => !toolIds.includes(tool.id))
                .map((tool) => (
                  <MenuItem key={tool.id} value={tool.id}>
                    {"Nombre: " + tool.name + "  -  Modelo: " + tool.model}
                  </MenuItem>
                ))
            )}
          </TextField>
        </FormControl>

        {/* Mostrar herramientas seleccionadas como Chips */}
        {toolIds.length > 0 && (
          <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 1, mt: 1 }}>
            {toolIds.map((toolId) => {
              const tool = tools.find(t => t.id === toolId);
              return tool ? (
                <Chip
                  key={toolId}
                  label={`${tool.name} - ${tool.model}`}
                  onDelete={() => handleRemoveTool(toolId)}
                  color="primary"
                  variant="outlined"
                />
              ) : null;
            })}
          </Box>
        )}

        <FormControl>
          <Button
            variant="contained"
            color="info"
            onClick={(e) => saveLoan(e)}
            startIcon={<SaveIcon />}
            sx={{ 
              marginTop: 2,
              padding: { xs: '10px 20px', md: '12px 24px' },
              fontSize: { xs: '0.9rem', md: '1rem' }
            }}
          >
            Guardar Préstamo
          </Button>
        </FormControl>
      </Box>
      <hr style={{ width: '100%', marginTop: '2rem' }} />
      <Link to="/loan/list">Volver a lista de Prestamos</Link>
    </Box>
  );
};

export default AddLoan;
