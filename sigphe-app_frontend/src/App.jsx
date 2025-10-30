import "./App.css";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Home from "./components/Home";
import AddLoan from "./components/AddLoan";
import ReturnPayLoan from "./components/ReturnPayLoan";
import LoanList from "./components/LoanList";
import ToolList from "./components/ToolList";
import AddEditTool from "./components/AddEditTool";
import KardexList from "./components/KardexList";
import Report from "./components/Report";
import Navbar from "./components/Navbar";
import NotFound from "./components/NotFound";

function App() {
  return (
    <Router> {/* Gestor de rutas */}
      <div className="container">
        <Navbar></Navbar>
        <Routes>
          {/* Rutas de home */}
          <Route path="/" element={<Home/>} />
          <Route path="/home" element={<Home/>} />
          {/* Rutas de loans */}
          <Route path="/loan/list" element={<LoanList/>} />
          <Route path="/loan/add" element={<AddLoan/>} />
          <Route path="/loan/return/:id" element={<ReturnPayLoan/>} />
          {/* Rutas de tools */}
          <Route path="/tool/list" element={<ToolList/>} />
          <Route path="/tool/add" element={<AddEditTool/>} />
          <Route path="/tool/edit/:id" element={<AddEditTool/>} />
          {/* Rutas de kardex */}
          <Route path="/kardex/list" element={<KardexList/>} />
          {/* Rutas de reportes */}
          <Route path="/report/list" element={<Report/>} />
          {/* Ruta 404 */}
          <Route path="*" element={<NotFound/>} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;
