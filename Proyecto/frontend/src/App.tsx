import React, { useState } from 'react';
import WelcomePage from './components/WelcomePage/WelcomePage';
import LoginPage from './components/LoginPage/LoginPage';
import RegisterPage from './components/RegisterPage/RegisterPage';
import './styles/App.css';

function App() {
  const [currentScreen, setCurrentScreen] = useState<'welcome' | 'login' | 'register'>('welcome');

  const renderScreen = () => {
    switch (currentScreen) {
      case 'welcome':
        return <WelcomePage onLoginClick={() => setCurrentScreen('login')} onRegisterClick={() => setCurrentScreen('register')} />;
      case 'login':
        return <LoginPage onRegisterClick={() => setCurrentScreen('register')} />;
      case 'register':
        return <RegisterPage onLoginClick={() => setCurrentScreen('login')} />;
      default:
        return <WelcomePage onLoginClick={() => setCurrentScreen('login')} onRegisterClick={() => setCurrentScreen('register')} />;
    }
  };

  return (
    <div className="app-container">
      <div className="screen-wrapper">
        {renderScreen()}
      </div>
    </div>
  );
}

export default App;