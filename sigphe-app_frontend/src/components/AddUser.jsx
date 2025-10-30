import { useState, useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";
import Box from "@mui/material/Box";
import TextField from "@mui/material/TextField";
import Button from "@mui/material/Button";
import FormControl from "@mui/material/FormControl";
import MenuItem from "@mui/material/MenuItem";
import SaveIcon from "@mui/icons-material/Save";
import userService from "../services/user.service";

const AddUser = () => {
  const [userTypes, setUserTypes] = useState([]); // Lista de tipos de usuario

  const [nationalId, setNationalId] = useState("");
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [userTypeId, setUserTypeId] = useState("");

  const [titleUserForm, setTitleUserForm] = useState("");
  const navigate = useNavigate();

  const init = () => {
    console.log("Cargando tipos de usuario...");
    
    // Cargar tipos de usuario
    userService
      .getUserTypes()
      .then((response) => {
        console.log("Tipos de usuario cargados:", response.data);
        setUserTypes(response.data);
      })
      .catch((error) => {
        console.log("Error al cargar tipos de usuario:", error);
      });
  };

  const saveUser = (e) => {
    e.preventDefault();

    // Validaciones
    if (nationalId.trim() === "") {
      alert("Por favor, ingrese un RUT válido.");
      return;
    }
    if (name.trim() === "") {
      alert("Por favor, ingrese un nombre válido.");
      return;
    }
    if (email.trim() === "") {
      alert("Por favor, ingrese un email válido.");
      return;
    }
    if (!userTypeId) {
      alert("Por favor, seleccione un tipo de usuario.");
      return;
    }

    // Validar formato de email
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) {
      alert("Por favor, ingrese un email válido.");
      return;
    }

    // Crear nuevo usuario
    const user = {
      nationalId,
      name,
      email,
      userTypeId: Number(userTypeId),
    };

    console.log("Datos del usuario a guardar:", user);

    userService
      .create(user)
      .then((response) => {
        console.log("Usuario ha sido añadido.", response.data);
        alert("Usuario creado exitosamente.");
        navigate("/user/list");
      })
      .catch((error) => {
        console.log(
          "Ha ocurrido un error al intentar crear nuevo usuario.",
          error
        );
        if (error.response && error.response.data) {
          alert(`Error: ${error.response.data.message || "No se pudo crear el usuario"}`);
        } else {
          alert("Ha ocurrido un error al intentar crear el usuario.");
        }
      });
  };

  useEffect(() => {
    setTitleUserForm("Nuevo Usuario");
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
      <h3> {titleUserForm} </h3>
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
            id="nationalId"
            label="RUT"
            value={nationalId}
            variant="standard"
            onChange={(e) => setNationalId(e.target.value)}
            helperText="Ej. 12.345.678-9"
          />
        </FormControl>

        <FormControl fullWidth>
          <TextField
            id="userName"
            label="Nombre Completo"
            value={name}
            variant="standard"
            onChange={(e) => setName(e.target.value)}
            helperText="Ej. Juan Pérez González"
          />
        </FormControl>

        <FormControl fullWidth>
          <TextField
            id="userEmail"
            label="Email"
            type="email"
            value={email}
            variant="standard"
            onChange={(e) => setEmail(e.target.value)}
            helperText="Ej. juan.perez@mail.com"
          />
        </FormControl>

        <FormControl fullWidth>
          <TextField
            id="userTypeId"
            label="Tipo de Usuario"
            value={userTypeId}
            select
            variant="standard"
            onChange={(e) => {
              const value = e.target.value;
              setUserTypeId(value);
            }}
          >
            {userTypes.length === 0 ? (
              <MenuItem disabled>Cargando tipos de usuario...</MenuItem>
            ) : (
              userTypes.map((type) => (
                <MenuItem key={type.id} value={type.id}>
                  {type.name}
                </MenuItem>
              ))
            )}
          </TextField>
        </FormControl>

        <FormControl>
          <Button
            variant="contained"
            color="info"
            onClick={(e) => saveUser(e)}
            startIcon={<SaveIcon />}
            sx={{
              marginTop: 2,
              padding: { xs: "10px 20px", md: "12px 24px" },
              fontSize: { xs: "0.9rem", md: "1rem" },
            }}
          >
            Guardar Usuario
          </Button>
        </FormControl>
      </Box>
      <hr style={{ width: "100%", marginTop: "2rem" }} />
      <Link to="/user/list">Volver a lista de usuarios</Link>
    </Box>
  );
};

export default AddUser;
