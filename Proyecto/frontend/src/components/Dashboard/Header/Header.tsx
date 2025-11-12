import { useNavigate } from 'react-router-dom';
import './Header.css';

const Header = () => {
  const navigate = useNavigate();
  
  const handleLogout = () => {
    localStorage.removeItem('authToken');
    localStorage.removeItem('user');
    navigate('/login');
  };
  
  return (
    <div className="header">
      <h2>ClassMate AI</h2>
       <p className="subtitle">Tu asistente de aprendizaje inteligente</p>
      <div className="header-actions">
        <span>Usuario</span>
        <button onClick={handleLogout} className="btn-logout">
          Cerrar Sesión
        </button>
      </div>
    </div>
  );
};

export default Header;