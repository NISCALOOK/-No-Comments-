import React, { useEffect, useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';

const CalendarCallback: React.FC = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const [status, setStatus] = useState<'processing' | 'success' | 'error'>('processing');

  useEffect(() => {
    const handleCallback = () => {
      const code = searchParams.get('code');
      const error = searchParams.get('error');

      console.log('🔵 Callback received - code:', code, 'error:', error);

      if (error) {
        console.error('🔴 OAuth error:', error);
        setStatus('error');
        setTimeout(() => {
          if (window.opener) {
            window.opener.postMessage(
              { type: 'GOOGLE_AUTH_ERROR', error },
              window.location.origin
            );
            window.close();
          } else {
            navigate('/calendar');
          }
        }, 2000);
        return;
      }

      if (code) {
        console.log('🟢 Authorization code received, sending to parent window');
        // Send the authorization code to the parent window
        if (window.opener) {
          window.opener.postMessage(
            {
              type: 'GOOGLE_AUTH_SUCCESS',
              code: code
            },
            window.location.origin
          );
          setStatus('success');
          
          // Close the popup after a short delay
          setTimeout(() => {
            window.close();
          }, 1500);
        } else {
          // If not in a popup, redirect to calendar view
          console.log('🟡 Not in popup, redirecting to calendar');
          setStatus('success');
          setTimeout(() => {
            navigate('/calendar');
          }, 1500);
        }
      } else {
        console.error('🔴 No code or error received');
        setStatus('error');
        setTimeout(() => {
          if (window.opener) {
            window.close();
          } else {
            navigate('/calendar');
          }
        }, 2000);
      }
    };

    handleCallback();
  }, [searchParams, navigate]);

  return (
    <div style={styles.container}>
      <div style={styles.card}>
        {status === 'processing' && (
          <>
            <div style={styles.spinner}></div>
            <h2 style={styles.title}>Autorizando...</h2>
            <p style={styles.message}>
              Conectando con Google Calendar
            </p>
          </>
        )}

        {status === 'success' && (
          <>
            <div style={styles.successIcon}>✅</div>
            <h2 style={styles.title}>¡Autorización Exitosa!</h2>
            <p style={styles.message}>
              Tu cuenta de Google Calendar ha sido conectada correctamente.
              <br />
              Esta ventana se cerrará automáticamente.
            </p>
          </>
        )}

        {status === 'error' && (
          <>
            <div style={styles.errorIcon}>❌</div>
            <h2 style={styles.title}>Error de Autorización</h2>
            <p style={styles.message}>
              No se pudo completar la autorización con Google Calendar.
              <br />
              Por favor, intenta nuevamente.
            </p>
          </>
        )}
      </div>
    </div>
  );
};

// Inline styles for the callback page
const styles: { [key: string]: React.CSSProperties } = {
  container: {
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
    minHeight: '100vh',
    background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
    padding: '2rem',
    fontFamily: '-apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif'
  },
  card: {
    background: 'white',
    padding: '3rem',
    borderRadius: '20px',
    boxShadow: '0 20px 60px rgba(0, 0, 0, 0.3)',
    textAlign: 'center',
    maxWidth: '500px',
    width: '100%'
  },
  spinner: {
    width: '60px',
    height: '60px',
    border: '6px solid #e2e8f0',
    borderTopColor: '#667eea',
    borderRadius: '50%',
    margin: '0 auto 2rem',
    animation: 'spin 1s linear infinite'
  },
  successIcon: {
    fontSize: '4rem',
    marginBottom: '1rem'
  },
  errorIcon: {
    fontSize: '4rem',
    marginBottom: '1rem'
  },
  title: {
    fontSize: '1.75rem',
    color: '#2d3748',
    marginBottom: '1rem',
    fontWeight: '700'
  },
  message: {
    fontSize: '1rem',
    color: '#718096',
    lineHeight: '1.6',
    margin: '0'
  }
};

// Add keyframes for spinner animation
const styleSheet = document.createElement('style');
styleSheet.textContent = `
  @keyframes spin {
    to { transform: rotate(360deg); }
  }
`;
document.head.appendChild(styleSheet);

export default CalendarCallback;

