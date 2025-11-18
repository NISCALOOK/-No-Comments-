// src/components/Header.tsx
import { useNavigate } from 'react-router-dom';
import { useEffect, useState } from 'react';
import './Header.css';

// Definimos la prop que recibirá para comunicarse con el componente padre
interface HeaderProps {
  onToggleSidebar: () => void;
}

const Header = ({ onToggleSidebar }: HeaderProps) => {
  const navigate = useNavigate();
  // Estado para almacenar y mostrar el nombre del usuario dinámicamente
  const [userName, setUserName] = useState<string | null>(null);

  // useEffect para obtener el nombre del usuario desde localStorage al montar el componente
  useEffect(() => {
    const userFromStorage = localStorage.getItem('user');
    if (userFromStorage) {
      try {
        // Asumimos que guardas el usuario como un JSON string: '{"name": "Nico"}'
        const parsedUser = JSON.parse(userFromStorage);
        setUserName(parsedUser.name); // Ej: Nico
      } catch (error) {
        console.error('Error al parsear el usuario desde localStorage:', error);
      }
    }
  }, []); // El array vacío asegura que se ejecute solo una vez

  const handleLogout = () => {
    localStorage.removeItem('authToken');
    localStorage.removeItem('user');
    navigate('/welcome');
  };

  return (
    // 1. Usamos la etiqueta semántica <header>
    <header className="header">
      <div className="header-left">
        {/* 2. Botón de menú para móviles, visible solo en pantallas pequeñas por CSS */}
        <button className="btn-menu" onClick={onToggleSidebar} aria-label="Abrir menú de navegación">
          ☰
        </button>
        <div className="header-title-group">
          <h2>ClassMate AI</h2>
          <p className="subtitle">Tu asistente de aprendizaje inteligente</p>
        </div>
      </div>
      
      <div className="header-actions">
        {/* 3. Mostramos el nombre del usuario si está disponible */}
        {userName && <span className="welcome-text">Bienvenido, {userName}</span>}
        <button onClick={handleLogout} className="btn-logout">
          Cerrar Sesión
        </button>
      </div>
    </header>
  );
};

export default Header;