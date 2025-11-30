// src/components/LoginPage/LoginPage.tsx

import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { login } from '../../api/auth'; // <-- Importa la función de login
import { useAuthStore } from '../../store'; // <-- Importa el store
import './LoginPage.css';

const LoginPage = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [apiError, setApiError] = useState('');
  
  const navigate = useNavigate();

  // Obtén la función setAuth del store
  const setAuth = useAuthStore((state) => state.setAuth);
  
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);
    setApiError('');
    
    try {
      // 1. Llama a la API a través de nuestro servicio
      const authData = await login(email, password);
      
      // 2. ¡Actualiza el estado global de la aplicación!
      setAuth(authData);
      // Gracias al middleware 'persist' de Zustand, esto también guardará
      // el token y el usuario en localStorage automáticamente.
      
      // 3. Ahora sí, redirige con confianza
      navigate('/dashboard');
      
    } catch (err: any) {
      // El interceptor de axios ya maneja errores 401, pero aquí capturamos otros
      const errorMessage = err.response?.data?.message || err.message || 'Error de conexión. Inténtalo de nuevo.';
      setApiError(errorMessage);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    // ... Tu JSX se mantiene exactamente igual ...
    <div className="form-container">
      <h2 className="form-title">Iniciar Sesión</h2>
      <form onSubmit={handleSubmit} className="form">
        <div className="form-group">
          <label htmlFor="email">Email</label>
          <input
            type="email"
            id="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            placeholder="tu@email.com"
            required
          />
        </div>
        
        <div className="form-group">
          <label htmlFor="password">Contraseña</label>
          <input
            type="password"
            id="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            placeholder="Contraseña"
            required
          />
        </div>
        
        {apiError && <div className="api-error">{apiError}</div>}
        
        <button 
          type="submit" 
          className="btn btn-primary" 
          disabled={isLoading}
        >
          {isLoading ? 'Iniciando...' : 'Iniciar Sesión'}
        </button>
        
        <div className="form-footer">
          ¿No tienes cuenta?{' '}
          <Link to="/register" className="link-button">
            Regístrate
          </Link>
        </div>
      </form>
    </div>
  );
};

export default LoginPage;