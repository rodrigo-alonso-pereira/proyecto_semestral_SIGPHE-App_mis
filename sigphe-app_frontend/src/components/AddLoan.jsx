import { useState, useEffect } from "react";
import { Link, useParams, useNavigate } from "react-router-dom";
import loanService from "../services/loan.service";
import Box from "@mui/material/Box";
import TextField from "@mui/material/TextField";
import Button from "@mui/material/Button";
import FormControl from "@mui/material/FormControl";
import MenuItem from "@mui/material/MenuItem";
import SaveIcon from "@mui/icons-material/Save";
import toolService from "../services/tool.service";
import userService from "../services/user.service";
import { DateTimePicker } from "@mui/x-date-pickers/DateTimePicker";
import { LocalizationProvider } from "@mui/x-date-pickers/LocalizationProvider";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";

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

  const saveLoan = (e) => {
    e.preventDefault();

    const formattedDueDate = dueDate ? dueDate.toISOString() : null; // Formatear la fecha a ISO 8601
    const loan = { dueDate: formattedDueDate, customerId, workerId, toolIds };
    //Crear nuevo prestamo
    loanService
      .create(loan)
      .then((response) => {
        console.log("Prestamo ha sido añadido.", response.data);
        navigate("/loan/list");
      })
      .catch((error) => {
        console.log(
          "Ha ocurrido un error al intentar crear nuevo prestamo.",
          error
        );
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
              label="Fecha de Devolución"
              value={dueDate}
              onChange={(newValue) => setDueDate(newValue)}
            />
          </LocalizationProvider>
        </FormControl>

        <FormControl fullWidth>
          <TextField
            id="customerId"
            label="Agregar Cliente"
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
            label="Agregar Trabajador"
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
            label="Agregar Herramienta"
            value={toolIds}
            select
            variant="standard"
            onChange={(e) => {
              const value = e.target.value;
              setToolIds(typeof value === 'string' ? value.split(',') : value);
            }}
            SelectProps={{
              multiple: true,
            }}
          >
            {tools.length === 0 ? (
              <MenuItem disabled>Cargando herramientas...</MenuItem>
            ) : (
              tools.map((tool) => (
                <MenuItem key={tool.id} value={tool.id}>
                  {tool.name}
                </MenuItem>
              ))
            )}
          </TextField>
        </FormControl>

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
            Grabar
          </Button>
        </FormControl>
      </Box>
      <hr style={{ width: '100%', marginTop: '2rem' }} />
      <Link to="/loan/list">Volver a lista de Prestamos</Link>
    </Box>
  );
};

export default AddLoan;
