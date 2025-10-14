import "./App.css";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Home from "./components/Home";
import AddLoan from "./components/AddLoan";
import LoanList from "./components/LoanList";
import ToolList from "./components/ToolList";
import KardexList from "./components/KardexList";
import Navbar from "./components/Navbar";
import NotFound from "./components/NotFound";

function App() {
  return (
    <Router>
      <div className="container">
        <Navbar></Navbar>
        <Routes>
          <Route path="/" element={<Home/>} />
          <Route path="/home" element={<Home/>} />
          <Route path="/loan/list" element={<LoanList/>} />
          <Route path="/loan/add" element={<AddLoan/>} />
          <Route path="/tool/list" element={<ToolList/>} />
          <Route path="/kardex/list" element={<KardexList/>} />
          <Route path="*" element={<NotFound/>} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;
