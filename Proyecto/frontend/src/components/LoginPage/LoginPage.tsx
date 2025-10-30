import React from 'react';
import GoogleButton from '../GoogleButton/GoogleButton';

interface LoginPageProps {
  onRegisterClick: () => void;
}

const LoginPage: React.FC<LoginPageProps> = ({ onRegisterClick }) => {
  return (
    <div className="form-container">
      <h2 className="form-title">Iniciar Sesión</h2>
      <form className="form">
        <div className="form-group">
          <label htmlFor="email">Email</label>
          <input type="email" id="email" placeholder="tu@email.com" />
        </div>
        <div className="form-group">
          <label htmlFor="password">Contraseña</label>
          <input type="password" id="password" placeholder="Contraseña" />
        </div>
        <button type="submit" className="btn btn-primary">Iniciar Sesión</button>
        
        <div className="divider">
          <span className="divider-text"></span>
        </div>
        
        <div className="google-button-container">
          
        </div>
        
        <div className="form-footer">
          ¿No tienes cuenta?{' '}
          <button type="button" className="link-button" onClick={onRegisterClick}>
            Regístrate
          </button>
        </div>
      </form>
    </div>
  );
};

export default LoginPage;