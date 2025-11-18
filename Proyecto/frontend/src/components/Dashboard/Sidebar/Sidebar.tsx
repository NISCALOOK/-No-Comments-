// src/components/layout/Sidebar/Sidebar.tsx
import { Link, useLocation } from 'react-router-dom';
import { useMemo } from 'react'; // 1. Importamos useMemo
import './Sidebar.css';

// 3. Definimos la configuración de nuestros enlaces en un array.
// Ahora es muy fácil añadir, quitar o modificar enlaces.
const sidebarLinksConfig = [
  { path: '/dashboard/home', label: 'Inicio' },
  { path: '/dashboard/upload', label: 'Subir Audio' },
  { path: '/dashboard/transcription', label: 'Transcripción' },
  { path: '/dashboard/summary', label: 'Resumen' },
  { path: '/dashboard/tasks', label: 'Tareas' },
  { path: '/dashboard/calendar', label: 'Calendario' },
  { path: '/dashboard/chat', label: 'Chat IA' },
  { path: '/dashboard/export', label: 'Exportar' },
];

interface SidebarProps {
  onClose: () => void;
}

const Sidebar = ({ onClose }: SidebarProps) => {
  const location = useLocation();
  
  // 2. Usamos useMemo para calcular los enlaces solo cuando la ubicación cambie.
  const linkElements = useMemo(() => {
    return sidebarLinksConfig.map((link) => {
      const isActive = location.pathname === link.path || location.pathname.startsWith(link.path + '/');
      
      return (
        <li key={link.path} className={isActive ? 'active' : ''}>
          <Link 
            to={link.path} 
            onClick={onClose} // 1. Cierra el sidebar al hacer clic
            // 4. Añadimos accesibilidad semántica
            aria-current={isActive ? 'page' : undefined}
          >
            {link.label}
          </Link>
        </li>
      );
    });
  }, [location.pathname, onClose]); // Dependencias: solo se recalcula si la ruta o la función onClose cambian.
  
  return (
    <div className="sidebar-content">
      <button className="btn-close" onClick={onClose} aria-label="Cerrar menú">
        &times;
      </button>
      <h2>Menú</h2>
      <nav>
        <ul>
          {/* 3. Renderizamos la lista de enlaces memorizada */}
          {linkElements}
        </ul>
      </nav>
    </div>
  );
};

export default Sidebar;