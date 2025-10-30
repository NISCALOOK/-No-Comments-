import React from 'react';
import './RegisterPage.css'

interface RegisterPageProps {
  onLoginClick: () => void;
}

const RegisterPage: React.FC<RegisterPageProps> = ({ onLoginClick }) => {
  return (
    <div className="form-container">
      <h2 className="form-title">Registrarse</h2>
      <form className="form">
        <div className="form-group">
          <label htmlFor="name">Nombre</label>
          <input type="text" id="name" placeholder="Tu nombre" />
        </div>
        <div className="form-group">
          <label htmlFor="email">Email</label>
          <input type="email" id="email" placeholder="tu@email.com" />
        </div>
        <div className="form-group">
          <label htmlFor="password">Contraseña</label>
          <input type="password" id="password" placeholder="Contraseña" />
        </div>
        <div className="form-group">
          <label htmlFor="confirmPassword">Confirmar Contraseña</label>
          <input type="password" id="confirmPassword" placeholder="Repite tu contraseña" />
        </div>
        <button type="submit" className="btn btn-primary">Registrarse</button>
        
        <div className="form-footer">
          ¿Ya tienes cuenta?{' '}
          <button type="button" className="link-button" onClick={onLoginClick}>
            Inicia sesión
          </button>
        </div>
      </form>
    </div>
  );
};

export default RegisterPage;