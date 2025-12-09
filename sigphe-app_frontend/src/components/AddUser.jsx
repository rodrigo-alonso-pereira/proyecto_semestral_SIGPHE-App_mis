import { useState, useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";
import Box from "@mui/material/Box";
import TextField from "@mui/material/TextField";
import Button from "@mui/material/Button";
import FormControl from "@mui/material/FormControl";
import MenuItem from "@mui/material/MenuItem";
import SaveIcon from "@mui/icons-material/Save";
import userService from "../services/user.service";
import Swal from "sweetalert2";

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

  // Función para formatear RUT automáticamente
  const formatRut = (value) => {
    // Eliminar todo excepto números y K/k
    const cleanRut = value.replace(/[^0-9kK]/g, '');
    
    // Validar longitud (mínimo 8, máximo 9 caracteres)
    if (cleanRut.length < 8 || cleanRut.length > 9) {
      return value; // Retornar sin formatear si no tiene la longitud correcta
    }

    // Separar número y dígito verificador
    const dv = cleanRut.slice(-1).toUpperCase();
    const number = cleanRut.slice(0, -1);

    // Formatear con puntos y guión
    const formattedNumber = number.replace(/(\d)(?=(\d{3})+(?!\d))/g, '$1.');
    
    return `${formattedNumber}-${dv}`;
  };

  // Función para manejar el cambio de RUT
  const handleRutChange = (e) => {
    const value = e.target.value;
    
    // Si el usuario está borrando, permitir
    if (value.length < nationalId.length) {
      setNationalId(value);
      return;
    }

    // Limpiar y contar solo dígitos + K
    const cleanValue = value.replace(/[^0-9kK]/g, '');
    
    // Limitar a 9 caracteres máximo
    if (cleanValue.length > 9) {
      return;
    }

    // Si tiene 8 o 9 caracteres, formatear automáticamente
    if (cleanValue.length >= 8) {
      const formatted = formatRut(cleanValue);
      setNationalId(formatted);
    } else {
      setNationalId(value);
    }
  };

  // Función para validar RUT
  const validateRut = (rut) => {
    // Limpiar RUT
    const cleanRut = rut.replace(/[^0-9kK]/g, '');
    
    // Verificar longitud
    if (cleanRut.length < 8 || cleanRut.length > 9) {
      return false;
    }

    return true;
  };

  const saveUser = (e) => {
    e.preventDefault();

    // Validaciones
    if (nationalId.trim() === "") {
      Swal.fire({
        title: 'Campo requerido',
        text: 'Por favor, ingrese un RUT válido.',
        icon: 'warning',
        confirmButtonColor: '#3085d6'
      });
      return;
    }
    
    // Validar formato y longitud del RUT
    if (!validateRut(nationalId)) {
      Swal.fire({
        title: 'RUT inválido',
        text: 'El RUT debe tener entre 8 y 9 dígitos (incluyendo dígito verificador).',
        icon: 'warning',
        confirmButtonColor: '#3085d6'
      });
      return;
    }
    if (name.trim() === "") {
      Swal.fire({
        title: 'Campo requerido',
        text: 'Por favor, ingrese un nombre válido.',
        icon: 'warning',
        confirmButtonColor: '#3085d6'
      });
      return;
    }
    if (email.trim() === "") {
      Swal.fire({
        title: 'Campo requerido',
        text: 'Por favor, ingrese un email válido.',
        icon: 'warning',
        confirmButtonColor: '#3085d6'
      });
      return;
    }
    if (!userTypeId) {
      Swal.fire({
        title: 'Campo requerido',
        text: 'Por favor, seleccione un tipo de usuario.',
        icon: 'warning',
        confirmButtonColor: '#3085d6'
      });
      return;
    }

    // Validar formato de email
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) {
      Swal.fire({
        title: 'Email inválido',
        text: 'Por favor, ingrese un email válido.',
        icon: 'warning',
        confirmButtonColor: '#3085d6'
      });
      return;
    }

    // Confirmación antes de crear
    Swal.fire({
      title: '¿Desea guardar el usuario?',
      text: 'Se creará un nuevo registro de usuario',
      icon: 'question',
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: 'Sí, guardar',
      cancelButtonText: 'Cancelar'
    }).then((result) => {
      if (result.isConfirmed) {
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
            Swal.fire({
              title: '¡Guardado!',
              text: 'El usuario ha sido creado exitosamente.',
              icon: 'success',
              confirmButtonColor: '#3085d6'
            });
            navigate("/user/list");
          })
          .catch((error) => {
            console.log(
              "Ha ocurrido un error al intentar crear nuevo usuario.",
              error
            );
            
            const errorMessage = error.response?.data?.message 
              || error.response?.data 
              || 'Ha ocurrido un error al intentar crear el usuario.';
            
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
            onChange={handleRutChange}
            helperText="Ej. 12.345.678-9 o ingrese sin formato: 123456789"
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
            label="Ingrese el tipo de usuario que va a crear"
            value={userTypeId}
            select
            variant="standard"
            onChange={(e) => {
              const value = e.target.value;
              setUserTypeId(value);
            }}
            helperText="Ej. Cliente / Trabajador"
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
