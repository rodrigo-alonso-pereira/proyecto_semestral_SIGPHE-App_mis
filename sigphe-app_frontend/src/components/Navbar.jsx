import * as React from "react";
import { useContext } from "react";
import AppBar from "@mui/material/AppBar";
import Box from "@mui/material/Box";
import Toolbar from "@mui/material/Toolbar";
import Typography from "@mui/material/Typography";
import IconButton from "@mui/material/IconButton";
import Button from "@mui/material/Button";
import Brightness4Icon from "@mui/icons-material/Brightness4";
import Brightness7Icon from "@mui/icons-material/Brightness7";
import Chip from "@mui/material/Chip";
import { useTheme } from "@mui/material/styles";
import Sidemenu from "./SideMenu";
import packageJson from "../../package.json";
import { ColorModeContext } from "../ThemeContext";

export default function Navbar() {
  const theme = useTheme();
  const colorMode = useContext(ColorModeContext);

  return (
    <>
      <AppBar 
        position="fixed"
        elevation={0}
        sx={{
          backgroundColor: '#1e3a5f',
          borderColor: 'divider',
          zIndex: (theme) => theme.zIndex.drawer + 1,
        }}
      >
        <Toolbar>
          <Typography variant="h6" component="div" sx={{ flexGrow: 1, ml: 1 }}>
            SIGPHE-App
          </Typography>
          
          <Button 
            onClick={colorMode.toggleColorMode} 
            color="inherit"
            startIcon={theme.palette.mode === 'dark' ? <Brightness7Icon /> : <Brightness4Icon />}
            sx={{ mr: 2, textTransform: 'none' }}
          >
            Tema: {theme.palette.mode === 'dark' ? 'Dark' : 'Light'}
          </Button>
          
          <Chip 
            label={`v${packageJson.version}`}
            size="small"
            sx={{ 
              backgroundColor: 'rgba(255, 255, 255, 0.2)',
              color: 'white',
              fontWeight: 600,
            }}
          />
        </Toolbar>
      </AppBar>
      <Sidemenu />
    </>
  );
}
