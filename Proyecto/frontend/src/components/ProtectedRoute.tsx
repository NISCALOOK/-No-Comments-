// src/components/ProtectedRoute.tsx

import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuthStore } from '../store';

// 1. Definimos las propiedades que nuestro componente recibirá.
// En este caso, solo `children`.
interface ProtectedRouteProps {
  children: React.ReactNode; // React.ReactNode es el tipo correcto para cualquier cosa que se puede renderizar.
}

// 2. Actualizamos el componente para que use esas propiedades.
const ProtectedRoute: React.FC<ProtectedRouteProps> = ({ children }) => {
  const isAuthenticated = useAuthStore((state) => state.isAuthenticated);

  // 3. Si está autenticado, renderiza los hijos. Si no, redirige.
  return isAuthenticated ? <>{children}</> : <Navigate to="/login" replace />;
};

export default ProtectedRoute;