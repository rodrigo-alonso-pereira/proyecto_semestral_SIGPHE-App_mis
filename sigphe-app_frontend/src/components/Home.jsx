import { Box, Typography, Container, Button, Tooltip } from "@mui/material";
import { useNavigate } from "react-router-dom";
import LoginIcon from "@mui/icons-material/Login";

const Home = () => {
  const navigate = useNavigate();

  return (
    <Box sx={{ minHeight: '100vh', py: 6 }}>
      <Container maxWidth="lg" sx={{ textAlign: 'center' }}>
        <Box sx={{ mb: 4 }}>
          <Typography variant="h3" component="h1" gutterBottom sx={{ fontWeight: 600 }}>
            SIGPHE-App
          </Typography>
          <Typography variant="h5" color="text.secondary" gutterBottom>
            Sistema integrado de gestión de préstamos de herramientas
          </Typography>
        </Box>
        
        <Typography variant="body1" sx={{ maxWidth: '800px', mx: 'auto', lineHeight: 1.8, mb: 4 }}>
          SIGPHE-App es una aplicación web para gestionar préstamos de
          herramientas. Esta aplicación ha sido desarrollada usando tecnologías
          como <a href="https://spring.io/projects/spring-boot">Spring Boot</a>{" "}
          (para el backend), <a href="https://reactjs.org/">React</a> (para el
          Frontend) y <a href="https://www.postgresql.org/">PostgreSQL</a> (para
          la base de datos).
        </Typography>

        <Tooltip title="Acceder al sistema de gestión de préstamos" arrow>
          <Button
            variant="contained"
            size="large"
            startIcon={<LoginIcon />}
            onClick={() => navigate("/loan/list")}
            sx={{
              mt: 2,
              px: 4,
              py: 1.5,
              fontSize: '1.1rem',
              fontWeight: 600,
              textTransform: 'none',
            }}
          >
            Ingresar al Sistema
          </Button>
        </Tooltip>
      </Container>
    </Box>
  );
};

export default Home;
