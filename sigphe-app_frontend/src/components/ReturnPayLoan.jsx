import { useState, useEffect } from "react";
import { Link, useParams, useNavigate } from "react-router-dom";
import loanService from "../services/loan.service";
import userService from "../services/user.service";
import Box from "@mui/material/Box";
import TextField from "@mui/material/TextField";
import Button from "@mui/material/Button";
import FormControl from "@mui/material/FormControl";
import MenuItem from "@mui/material/MenuItem";
import SaveIcon from "@mui/icons-material/Save";

const ReturnPayLoan = () => {
  const [employees, setEmployees] = useState([]); // Lista de empleados
  const [loanDetail, setLoan] = useState({}); // Detalles del préstamo

  const [workerId, setWorkerId] = useState("");
  const [customerId, setCustomerId] = useState("");
  const [toolConditions, setToolConditions] = useState({}); // Cambiar a objeto para mantener {toolId: "condicion"}

  const [titleLoanForm, setTitleLoanForm] = useState(""); // Título del formulario

  const { id } = useParams(); // Obtener ID del préstamo desde los parámetros de la URL
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
    console.log("Iniciando carga de datos...");

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

    // Cargar prestamo con detalles
    loanService
      .getLoanDetails(id)
      .then((response) => {
        console.log("Prestamo con detalles cargado:", response.data);
        setLoan(response.data);

        // Inicializar toolConditions con "ok" por defecto para cada herramienta
        if (response.data.tools && response.data.tools.length > 0) {
          const initialConditions = {};
          response.data.tools.forEach((tool) => {
            initialConditions[tool.id] = "ok";
          });
          setToolConditions(initialConditions);
        }
      })
      .catch((error) => {
        console.log("Error al cargar prestamo:", error);
      });
  };

  const returnLoan = (e) => {
    e.preventDefault();
    
    // Validar que existan los datos necesarios
    if (!loanDetail.customer?.id) {
      alert("Error: No se pudo obtener la información del cliente. Por favor, recargue la página.");
      console.error("Customer ID no disponible en loanDetail:", loanDetail);
      return;
    }
    
    if (!workerId) {
      alert("Por favor, seleccione un trabajador para procesar el retorno.");
      return;
    }
    
    const customerIdValue = loanDetail.customer.id;
    const workerIdValue = Number(workerId);
    const returnData = { 
      workerId: workerIdValue, 
      customerId: customerIdValue, 
      toolConditions 
    };

    console.log("Datos a enviar:", returnData);

    // Retornar préstamo
    loanService
      .returnLoan(id, returnData)
      .then((response) => {
        console.log("Préstamo ha sido retornado.", response.data);
        navigate("/loan/list"); // Navegar de vuelta a la lista de préstamos
      })
      .catch((error) => {
        alert("Error al retornar el préstamo. Por favor, inténtelo de nuevo.");
        console.log(
          "Ha ocurrido un error al intentar retornar el préstamo.",
          error
        );
      });
  };

  {
    /* Función para manejar el cambio de condición de una herramienta */
  }
  const handleToolConditionChange = (toolId, condition) => {
    setToolConditions((prevConditions) => ({
      ...prevConditions,
      [toolId]: condition,
    }));
  };

  useEffect(() => {
    setTitleLoanForm("Datos de Retorno del Préstamo");
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
        maxWidth: { xs: "95%", sm: "600px", md: "700px" },
        margin: "0 auto",
        padding: { xs: 2, md: 3 },
      }}
    >
      <h3> {titleLoanForm} </h3>
      <hr style={{ width: "100%" }} />
      <Box
        component="form"
        sx={{
          width: "100%",
          display: "flex",
          flexDirection: "column",
          gap: 3,
        }}
      >
        <FormControl fullWidth>
          <TextField
            id="customerName"
            label="Nombre del Cliente"
            value={loanDetail.customer?.name || ""}
            slotProps={{
              input: {
                readOnly: true,
              },
            }}
          />
        </FormControl>
        <FormControl fullWidth>
          <TextField
            id="startDate"
            label="Fecha de Inicio del Préstamo"
            value={formatDate(loanDetail.startDate)}
            slotProps={{
              input: {
                readOnly: true,
              },
            }}
          />
        </FormControl>
        <FormControl fullWidth>
          <TextField
            id="dueDate"
            label="Fecha plazo de retorno del Préstamo"
            value={formatDate(loanDetail.dueDate)}
            slotProps={{
              input: {
                readOnly: true,
              },
            }}
          />
        </FormControl>
        <FormControl fullWidth>
          <TextField
            id="totalAmount"
            label="Valor del Préstamo"
            value={formatCurrency(loanDetail.totalAmount)}
            slotProps={{
              input: {
                readOnly: true,
              },
            }}
          />
        </FormControl>
        <FormControl fullWidth>
          <TextField
            id="loanStatus"
            label="Estado del Préstamo"
            value={loanDetail.loanStatus || ""}
            slotProps={{
              input: {
                readOnly: true,
              },
            }}
          />
        </FormControl>
        <FormControl fullWidth>
          <TextField
            id="workerId"
            label="Agregar trabajador que procesa el retorno del préstamo"
            value={workerId}
            select
            variant="standard"
            onChange={(e) => {
              setWorkerId(e.target.value);
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
        
        {/* Sección de herramientas del préstamo */}
        {loanDetail.tools && loanDetail.tools.length > 0 && (
          <>
            <h4>Herramientas del Préstamo</h4>
            {loanDetail.tools.map((tool) => (
              <FormControl fullWidth key={tool.id}>
                <TextField
                  select
                  label={tool.name}
                  value={toolConditions[tool.id] || "ok"}
                  onChange={(e) =>
                    handleToolConditionChange(tool.id, e.target.value)
                  }
                  variant="outlined"
                >
                  <MenuItem value="ok">OK</MenuItem>
                  <MenuItem value="dañada">Dañada</MenuItem>
                  <MenuItem value="perdida">Perdida</MenuItem>
                </TextField>
              </FormControl>
            ))}
          </>
        )}

        <FormControl>
          <Button
            variant="contained"
            color="info"
            onClick={(e) => returnLoan(e)}
            startIcon={<SaveIcon />}
            sx={{
              marginTop: 2,
              padding: { xs: "10px 20px", md: "12px 24px" },
              fontSize: { xs: "0.9rem", md: "1rem" },
            }}
          >
            Retornar Prestamo
          </Button>
        </FormControl>
      </Box>
      <hr style={{ width: "100%", marginTop: "2rem" }} />
      <Link to="/loan/list">Volver a lista de Prestamos</Link>
    </Box>
  );
};

export default ReturnPayLoan;
