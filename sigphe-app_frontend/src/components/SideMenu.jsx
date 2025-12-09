import * as React from "react";
import Box from "@mui/material/Box";
import Drawer from "@mui/material/Drawer";
import List from "@mui/material/List";
import Divider from "@mui/material/Divider";
import ListItemButton from "@mui/material/ListItemButton";
import ListItemIcon from "@mui/material/ListItemIcon";
import ListItemText from "@mui/material/ListItemText";
import Avatar from "@mui/material/Avatar";
import Typography from "@mui/material/Typography";
import HomeIcon from "@mui/icons-material/Home";
import HandshakeIcon from "@mui/icons-material/Handshake";
import BuildIcon from "@mui/icons-material/Build";
import InventoryIcon from "@mui/icons-material/Inventory";
import PeopleIcon from "@mui/icons-material/People";
import AssessmentIcon from "@mui/icons-material/Assessment";
import { useNavigate, useLocation } from "react-router-dom";

const drawerWidth = 260;

export default function Sidemenu() {
  const navigate = useNavigate();
  const location = useLocation();

  const listOptions = () => (
    <Box role="presentation">
      <List>
        <ListItemButton 
          onClick={() => navigate("/home")}
          selected={location.pathname === "/home"}
        >
          <ListItemIcon>
            <HomeIcon />
          </ListItemIcon>
          <ListItemText primary="Home" />
        </ListItemButton>

        <ListItemButton 
          onClick={() => navigate("/loan/list")}
          selected={location.pathname === "/loan/list"}
        >
          <ListItemIcon>
            <HandshakeIcon />
          </ListItemIcon>
          <ListItemText primary="Prestamos" />
        </ListItemButton>

        <ListItemButton 
          onClick={() => navigate("/tool/list")}
          selected={location.pathname === "/tool/list"}
        >
          <ListItemIcon>
            <BuildIcon />
          </ListItemIcon>
          <ListItemText primary="Herramientas" />
        </ListItemButton>

        <ListItemButton 
          onClick={() => navigate("/Kardex/list")}
          selected={location.pathname === "/Kardex/list"}
        >
          <ListItemIcon>
            <InventoryIcon />
          </ListItemIcon>
          <ListItemText primary="Registro movimientos herramientas" />
        </ListItemButton>

        <ListItemButton 
          onClick={() => navigate("/report/list")}
          selected={location.pathname === "/report/list"}
        >
          <ListItemIcon>
            <AssessmentIcon />
          </ListItemIcon>
          <ListItemText primary="Reporte de Prestamos" />
        </ListItemButton>

        <ListItemButton 
          onClick={() => navigate("/user/list")}
          selected={location.pathname === "/user/list"}
        >
          <ListItemIcon>
            <PeopleIcon />
          </ListItemIcon>
          <ListItemText primary="Usuarios" />
        </ListItemButton>
      </List>
    </Box>
  );

  return (
    <Drawer
      variant="permanent"
      sx={{
        width: drawerWidth,
        flexShrink: 0,
        '& .MuiDrawer-paper': {
          width: drawerWidth,
          boxSizing: 'border-box',
          paddingTop: '64px',
          borderRight: 1,
          borderColor: 'divider',
          backgroundColor: '#1e3a5f',
          borderTopLeftRadius: '16px',
          display: 'flex',
          flexDirection: 'column',
          '& .MuiListItemButton-root': {
            color: '#ffffff',
            '&.Mui-selected': {
              backgroundColor: 'rgba(255, 255, 255, 0.15)',
              borderLeft: '4px solid #ffffff',
              '&:hover': {
                backgroundColor: 'rgba(255, 255, 255, 0.2)',
              },
            },
            '&:hover': {
              backgroundColor: 'rgba(255, 255, 255, 0.08)',
            },
          },
          '& .MuiListItemText-primary': {
            color: '#ffffff',
          },
          '& .MuiListItemIcon-root': {
            color: '#ffffff',
          },
        },
      }}
    >
      <Box sx={{ flexGrow: 1 }}>
        {listOptions()}
      </Box>
      
      {/* Sección de usuario en la parte inferior */}
      <Box
        sx={{
          p: 2,
          borderTop: '1px solid rgba(255, 255, 255, 0.12)',
          backgroundColor: 'rgba(0, 0, 0, 0.1)',
        }}
      >
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.5 }}>
          <Avatar
            alt="Usuario"
            src="https://i.pravatar.cc/150?img=68"
            sx={{ width: 40, height: 40 }}
          />
          <Box sx={{ flexGrow: 1 }}>
            <Typography variant="body2" sx={{ color: '#ffffff', fontWeight: 600 }}>
              Usuario Admin
            </Typography>
            <Typography variant="caption" sx={{ color: 'rgba(255, 255, 255, 0.7)' }}>
              admin@sigphe.com
            </Typography>
          </Box>
        </Box>
      </Box>
    </Drawer>
  );
}
