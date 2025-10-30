import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import userService from "../services/user.service";
import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableCell from "@mui/material/TableCell";
import TableContainer from "@mui/material/TableContainer";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import Paper from "@mui/material/Paper";
import Button from "@mui/material/Button";
import Box from "@mui/material/Box";
import Chip from "@mui/material/Chip";
import AddCircleRoundedIcon from "@mui/icons-material/AddCircleRounded";

const UserList = () => {
  const [users, setUsers] = useState([]);

  const navigate = useNavigate();

  // Función para cargar todos los usuarios
  const init = () => {
    userService
      .getAll()
      .then((response) => {
        console.log("Mostrando listado de todos los usuarios.", response.data);
        setUsers(response.data);
      })
      .catch((error) => {
        console.log(
          "Se ha producido un error al intentar mostrar listado de todos los usuarios.",
          error
        );
      });
  };

  useEffect(() => {
    init();
  }, []);

  // Función para obtener el color según el estado del usuario
  const getStatusColor = (status) => {
    switch (status) {
      case "Activo":
        return "success"; // Verde
      case "Con Deuda":
        return "error"; // Rojo
      case "Con Prestamos":
        return "warning"; // Naranja
      case "Inactivo":
        return "default"; // Gris
      default:
        return "default";
    }
  };

  // Función para obtener el color según el tipo de usuario
  const getUserTypeColor = (type) => {
    switch (type) {
      case "Trabajador":
        return "primary"; // Azul
      case "Cliente":
        return "secondary"; // Morado
      default:
        return "default";
    }
  };

  return (
    <TableContainer component={Paper}>
      <br />
      <Box sx={{ display: "flex", gap: 2, flexWrap: "wrap", justifyContent: "center", marginBottom: 2 }}>
        <Button
          variant="contained"
          color="primary"
          startIcon={<AddCircleRoundedIcon />}
          component={Link}
          to="/user/add"
        >
          Añadir Usuario
        </Button>
      </Box>
      <br />
      <Table sx={{ minWidth: 650 }} size="small" aria-label="a dense table">
        <TableHead>
          <TableRow>
            <TableCell align="center" sx={{ fontWeight: "bold" }}>
              ID
            </TableCell>
            <TableCell align="center" sx={{ fontWeight: "bold" }}>
              Nombre
            </TableCell>
            <TableCell align="center" sx={{ fontWeight: "bold" }}>
              Email
            </TableCell>
            <TableCell align="center" sx={{ fontWeight: "bold" }}>
              Estado
            </TableCell>
            <TableCell align="center" sx={{ fontWeight: "bold" }}>
              Tipo de Usuario
            </TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {users.length === 0 ? (
            <TableRow>
              <TableCell colSpan={5} align="center" sx={{ padding: 4 }}>
                <Chip label="No hay usuarios registrados" color="info" />
              </TableCell>
            </TableRow>
          ) : (
            users.map((user) => (
              <TableRow
                key={user.id}
                sx={{ "&:last-child td, &:last-child th": { border: 0 } }}
              >
                <TableCell align="center">{user.id}</TableCell>
                <TableCell align="center">{user.name}</TableCell>
                <TableCell align="center">{user.email}</TableCell>
                <TableCell align="center">
                  <Chip
                    label={user.userStatus}
                    color={getStatusColor(user.userStatus)}
                    size="small"
                  />
                </TableCell>
                <TableCell align="center">
                  <Chip
                    label={user.userType}
                    color={getUserTypeColor(user.userType)}
                    size="small"
                  />
                </TableCell>
              </TableRow>
            ))
          )}
        </TableBody>
      </Table>
    </TableContainer>
  );
};

export default UserList;
