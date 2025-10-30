import { useState, useEffect } from "react";
import { Link, useParams, useNavigate } from "react-router-dom";
import Box from "@mui/material/Box";
import TextField from "@mui/material/TextField";
import Button from "@mui/material/Button";
import FormControl from "@mui/material/FormControl";
import MenuItem from "@mui/material/MenuItem";
import SaveIcon from "@mui/icons-material/Save";
import toolService from "../services/tool.service";
import userService from "../services/user.service";

const AddEditTool = () => {
  const [toolCategories, setToolCategories] = useState([]); // Lista de categorias herramientas
  const [toolStatuses, setToolStatus] = useState([]); // Lista de estados herramientas
  const [toolModels, setToolModels] = useState([]); // Lista de modelos herramientas
  const [employees, setEmployees] = useState([]); // Lista de empleados

  const [name, setName] = useState("");
  const [replacementValue, setReplacementValue] = useState(0);
  const [rentalValue, setRentalValue] = useState(0);
  const [categoryId, setCategoryId] = useState("");
  const [statusId, setStatusId] = useState("");
  const [modelId, setModelId] = useState("");
  const [workerId, setWorkerId] = useState("");
  const [quantity, setQuantity] = useState(1);

  // Guardar temporalmente los nombres que vienen del backend
  const [tempCategory, setTempCategory] = useState("");
  const [tempStatus, setTempStatus] = useState("");
  const [tempModel, setTempModel] = useState("");
  const [tempWorker, setTempWorker] = useState("");

  const { id } = useParams();
  const [titleToolForm, setTitleToolForm] = useState("");
  const navigate = useNavigate();

  const init = () => {
    console.log("Iniciando carga de datos...");

    // Cargar categorias herramientas
    toolService
      .getToolCategories()
      .then((response) => {
        console.log("Categorias de herramientas cargadas:", response.data);
        setToolCategories(response.data);
      })
      .catch((error) => {
        console.log("Error al cargar categorias de herramientas:", error);
      });

    // Cargar estados herramientas
    toolService
      .getToolStatus()
      .then((response) => {
        console.log("Estados de herramientas cargados:", response.data);
        setToolStatus(response.data);
      })
      .catch((error) => {
        console.log("Error al cargar estados de herramientas:", error);
      });

    // Cargar modelos herramientas
    toolService
      .getToolModels()
      .then((response) => {
        console.log("Modelos de herramientas cargados:", response.data);
        setToolModels(response.data);
      })
      .catch((error) => {
        console.log("Error al cargar modelos de herramientas:", error);
      });

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

  const saveTool = (e) => {
    e.preventDefault();

    if (id) {
      const tool = {
        name,
        rentalValue,
        replacementValue,
        toolCategoryId: categoryId,
        toolStatusId: statusId,
        modelId,
        workerId,
      };

      if (!workerId) {
        alert("Por favor, seleccione un empleado para procesar el retorno.");
        return;
      }

      console.log("Datos de la herramienta a actualizar:", tool);
      // Actualizar herramienta existente
      toolService
        .update(id, tool)
        .then((response) => {
          console.log("Herramienta ha sido actualizada.", response.data);
          navigate("/tool/list");
        })
        .catch((error) => {
          console.log(
            "Ha ocurrido un error al intentar actualizar la herramienta.",
            error
          );
        });
    } else {
      // Validaciones antes de crear
      if (name.trim() === "") {
        alert("Por favor, ingrese un nombre válido para la herramienta.");
        return;
      }
      if (!replacementValue || replacementValue <= 0) {
        alert("Por favor, ingrese un valor de reemplazo válido (mayor a 0).");
        return;
      }
      if (!rentalValue || rentalValue <= 0) {
        alert("Por favor, ingrese un valor de arriendo válido (mayor a 0).");
        return;
      }
      if (!categoryId) {
        alert("Por favor, seleccione una categoría para la herramienta.");
        return;
      }
      if (!modelId) {
        alert("Por favor, seleccione un modelo para la herramienta.");
        return;
      }
      if (!quantity || quantity < 1) {
        alert("Por favor, ingrese una cantidad válida (mínimo 1 herramienta).");
        return;
      }
      if (!workerId) {
        alert("Por favor, seleccione un trabajador responsable.");
        return;
      }

      // Crear nueva herramienta
      const tool = {
        name,
        replacementValue,
        rentalValue,
        toolCategoryId: categoryId,
        modelId,
        workerId,
        quantity,
      };

      console.log("Datos de la herramienta a guardar:", tool);

      toolService
        .create(tool)
        .then((response) => {
          console.log("Herramienta ha sido añadida.", response.data);
          navigate("/tool/list");
        })
        .catch((error) => {
          console.log(
            "Ha ocurrido un error al intentar crear nueva herramienta.",
            error
          );
        });
    }
  };

  useEffect(() => {
    // Cargar las listas
    init();

    if (id) {
      setTitleToolForm("Editar Herramienta");
      // Cargar datos de la herramienta a editar
      toolService
        .getToolById(id)
        .then((response) => {
          const tool = response.data;
          console.log("Datos de la herramienta cargados:", tool);

          setName(tool.name);
          setReplacementValue(tool.replacementValue);
          setRentalValue(tool.rentalValue);

          // Guardar los nombres temporalmente
          setTempCategory(tool.category);
          setTempStatus(tool.status);
          setTempModel(tool.model);
        })
        .catch((error) => {
          console.log(
            "Ha ocurrido un error al intentar cargar los datos de la herramienta.",
            error
          );
        });
    } else {
      setTitleToolForm("Nueva Herramienta");
    }
  }, []);

  // useEffect separado que se ejecuta cuando las listas se cargan
  useEffect(() => {
    // Solo ejecutar si hay nombres temporales guardados y las listas están cargadas
    if (tempCategory && toolCategories.length > 0) {
      const category = toolCategories.find((c) => c.name === tempCategory);
      if (category) {
        setCategoryId(category.id);
        console.log("Categoría encontrada:", category.name, "ID:", category.id);
      } else {
        console.warn("No se encontró categoría con nombre:", tempCategory);
      }
    }

    if (tempStatus && toolStatuses.length > 0) {
      const status = toolStatuses.find((s) => s.name === tempStatus);
      if (status) {
        setStatusId(status.id);
        console.log("Estado encontrado:", status.name, "ID:", status.id);
      } else {
        console.warn("No se encontró estado con nombre:", tempStatus);
      }
    }

    if (tempModel && toolModels.length > 0) {
      const model = toolModels.find((m) => m.name === tempModel);
      if (model) {
        setModelId(model.id);
        console.log("Modelo encontrado:", model.name, "ID:", model.id);
      } else {
        console.warn("No se encontró modelo con nombre:", tempModel);
      }
    }
  }, [
    toolCategories,
    toolStatuses,
    toolModels,
    tempCategory,
    tempStatus,
    tempModel,
  ]);

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
      <h3> {titleToolForm} </h3>
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
            id="toolName"
            label="Nombre de la Herramienta"
            value={name}
            variant="standard"
            onChange={(e) => setName(e.target.value)}
            helperText="Ej. Taladro"
          />
        </FormControl>

        <FormControl fullWidth>
          <TextField
            id="toolReplacementValue"
            label="Valor de reemplazo de la Herramienta"
            type="number"
            value={replacementValue}
            variant="standard"
            onChange={(e) => setReplacementValue(Number(e.target.value))}
            helperText="Ej. 100000"
          />
        </FormControl>

        <FormControl fullWidth>
          <TextField
            id="toolRentalValue"
            label="Valor de arriendo diario de la Herramienta"
            type="number"
            value={rentalValue}
            variant="standard"
            onChange={(e) => setRentalValue(Number(e.target.value))}
            helperText="Ej. 100000"
          />
        </FormControl>

        <FormControl fullWidth>
          <TextField
            id="categoryId"
            label="Agregar Categoria"
            value={categoryId}
            select
            variant="standard"
            onChange={(e) => {
              const value = e.target.value;
              setCategoryId(Number(value));
            }}
          >
            {toolCategories.length === 0 ? (
              <MenuItem disabled>Cargando categorias...</MenuItem>
            ) : (
              toolCategories.map((category) => (
                <MenuItem key={category.id} value={category.id}>
                  {category.name}
                </MenuItem>
              ))
            )}
          </TextField>
        </FormControl>

        {/* Solo mostrar el select de estado cuando se está editando */}
        {id && (
          <FormControl fullWidth>
            <TextField
              id="statusId"
              label="Cambiar Estado de la Herramienta"
              value={statusId}
              select
              variant="standard"
              onChange={(e) => {
                const value = e.target.value;
                setStatusId(Number(value));
              }}
            >
              {toolStatuses.length === 0 ? (
                <MenuItem disabled>Cargando estados...</MenuItem>
              ) : (
                toolStatuses
                  .filter(
                    (status) =>
                      status.name === "Disponible" ||
                      status.name === "En Reparacion"
                  )
                  .map((status) => (
                    <MenuItem key={status.id} value={status.id}>
                      {status.name}
                    </MenuItem>
                  ))
              )}
            </TextField>
          </FormControl>
        )}

        <FormControl fullWidth>
          <TextField
            id="modelId"
            label="Agregar Modelo de la Herramienta"
            value={modelId}
            select
            variant="standard"
            onChange={(e) => {
              const value = e.target.value;
              setModelId(Number(value));
            }}
          >
            {toolModels.length === 0 ? (
              <MenuItem disabled>Cargando modelos...</MenuItem>
            ) : (
              toolModels.map((model) => (
                <MenuItem key={model.id} value={model.id}>
                  {model.name}
                </MenuItem>
              ))
            )}
          </TextField>
        </FormControl>

        {!id && (
          <FormControl fullWidth>
            <TextField
              id="quantity"
              label="Cantidad de Herramientas a agregar"
              type="number"
              value={quantity}
              variant="standard"
              onChange={(e) => setQuantity(Number(e.target.value))}
              helperText="Ej. 2"
            />
          </FormControl>
        )}

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

        <FormControl>
          <Button
            variant="contained"
            color="info"
            onClick={(e) => saveTool(e)}
            startIcon={<SaveIcon />}
            sx={{
              marginTop: 2,
              padding: { xs: "10px 20px", md: "12px 24px" },
              fontSize: { xs: "0.9rem", md: "1rem" },
            }}
          >
            Guardar Herramienta
          </Button>
        </FormControl>
      </Box>
      <hr style={{ width: "100%", marginTop: "2rem" }} />
      <Link to="/tool/list">Volver a lista de herramientas</Link>
    </Box>
  );
};

export default AddEditTool;
