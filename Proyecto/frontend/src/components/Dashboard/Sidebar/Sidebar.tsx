import { Link, useLocation } from 'react-router-dom';
import './Sidebar.css';

const Sidebar = () => {
  const location = useLocation();
  
  const isActive = (path: string) => {
   
    return location.pathname === path || location.pathname.startsWith(path + '/');
  };
  
  return (
    <div className="sidebar">
      <h2>Menú</h2>
      <nav>
        <ul>
          <li className={isActive('/dashboard') ? 'active' : ''}>
            <Link to="/dashboard">Inicio</Link>
          </li>
          <li className={isActive('/dashboard/upload') ? 'active' : ''}>
            <Link to="/dashboard/upload">Subir Audio</Link>
          </li>
          <li className={isActive('/dashboard/transcription') ? 'active' : ''}>
            <Link to="/dashboard/transcription">Transcripción</Link>
          </li>
          <li className={isActive('/dashboard/summary') ? 'active' : ''}>
            <Link to="/dashboard/summary">Resumen</Link>
          </li>
          <li className={isActive('/dashboard/tasks') ? 'active' : ''}>
            <Link to="/dashboard/tasks">Tareas</Link>
          </li>
          
          
          
          <li className={isActive('/dashboard/calendar') ? 'active' : ''}>
           
            <Link to="/dashboard/calendar">Calendario</Link>
          </li>
          
          <li className={isActive('/dashboard/chat') ? 'active' : ''}>
         
            <Link to="/dashboard/chat">Chat IA</Link>
          </li>
          
        
          <li className={isActive('/dashboard/export') ? 'active' : ''}>
            <Link to="/dashboard/export">Exportar</Link>
          </li>
        </ul>
        <button className="action-button">Comenzar</button>
      </nav>
    </div>
  );
};

export default Sidebar;