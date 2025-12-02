// src/components/Dashboard.tsx

import { useState } from 'react';
import { Outlet } from 'react-router-dom'; // ¡Importante! Outlet renderiza las rutas anidadas.
import Sidebar from './Sidebar/Sidebar'; // Importamos tu componente Sidebar
import Header from './Header/Header';   // Importamos tu componente Header corregido
import './Dashboard.css';



const Dashboard = () => {
  const [isSidebarOpen, setIsSidebarOpen] = useState(false);

  const toggleSidebar = () => {
    setIsSidebarOpen(!isSidebarOpen);
  };
  const closeSidebar = () => {
    setIsSidebarOpen(false);
  };
  

    return (
    <div className="app-container">
      <aside className={`sidebar ${isSidebarOpen ? 'sidebar--open' : ''}`}>
      
        <Sidebar onClose={closeSidebar} />
      </aside>
      <main className="main-content">
        <Header onToggleSidebar={toggleSidebar} />
        <div className="content">
          <Outlet />
        </div>
      </main>
    </div>
  );
};

export default Dashboard;