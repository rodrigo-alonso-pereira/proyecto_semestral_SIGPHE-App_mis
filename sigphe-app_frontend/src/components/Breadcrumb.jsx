import * as React from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { Breadcrumbs, Link, Typography, Box } from "@mui/material";
import NavigateNextIcon from "@mui/icons-material/NavigateNext";
import HomeIcon from "@mui/icons-material/Home";

const routeNames = {
  "/home": "Home",
  "/loan/list": "Préstamos",
  "/loan/add": "Agregar Préstamo",
  "/loan/return": "Devolver Préstamo",
  "/tool/list": "Herramientas",
  "/tool/add": "Agregar Herramienta",
  "/tool/edit": "Editar Herramienta",
  "/Kardex/list": "Registro de Movimientos",
  "/kardex/list": "Registro de Movimientos",
  "/user/list": "Usuarios",
  "/user/add": "Agregar Usuario",
  "/report/list": "Reporte de Préstamos",
};

export default function Breadcrumb() {
  const location = useLocation();
  const navigate = useNavigate();

  const pathnames = location.pathname.split("/").filter((x) => x);

  // Filtrar los segmentos que no deben mostrarse (loan, tool, user, kardex, report)
  const skipSegments = ['loan', 'tool', 'user', 'kardex', 'Kardex', 'report'];

  const getBreadcrumbName = (path, index) => {
    // Construir la ruta acumulada
    let fullPath = "/" + pathnames.slice(0, index + 1).join("/");
    
    // Si es un ID numérico, usar el path anterior
    if (!isNaN(path)) {
      const previousPath = "/" + pathnames.slice(0, index).join("/");
      return routeNames[previousPath] || path;
    }
    
    return routeNames[fullPath] || path.charAt(0).toUpperCase() + path.slice(1);
  };

  if (location.pathname === "/" || location.pathname === "/home") {
    return null; // No mostrar breadcrumb en home
  }

  return (
    <Box sx={{ mb: 3, mt: 2 }}>
      <Breadcrumbs 
        separator={<NavigateNextIcon fontSize="small" />}
        aria-label="breadcrumb"
      >
        <Link
          underline="hover"
          sx={{ 
            display: "flex", 
            alignItems: "center",
            cursor: "pointer",
            color: "text.primary",
          }}
          onClick={() => navigate("/home")}
        >
          <HomeIcon sx={{ mr: 0.5 }} fontSize="inherit" />
          Home
        </Link>
        {pathnames.map((value, index) => {
          // Saltar los segmentos que no deben mostrarse
          if (skipSegments.includes(value)) {
            return null;
          }

          const last = index === pathnames.length - 1;
          const to = `/${pathnames.slice(0, index + 1).join("/")}`;
          const name = getBreadcrumbName(value, index);

          return last ? (
            <Typography 
              key={to} 
              color="text.primary"
              sx={{ fontWeight: 600 }}
            >
              {name}
            </Typography>
          ) : (
            <Link
              key={to}
              underline="hover"
              sx={{ cursor: "pointer", color: "text.secondary" }}
              onClick={() => navigate(to)}
            >
              {name}
            </Link>
          );
        })}
      </Breadcrumbs>
    </Box>
  );
}
