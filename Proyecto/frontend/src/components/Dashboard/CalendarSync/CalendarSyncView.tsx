import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './CalendarSyncView.css';

interface CalendarEvent {
  id: string;
  summary: string;
  description?: string;
  start: string;
  end: string;
  htmlLink?: string;
}

interface NewEvent {
  summary: string;
  description: string;
  startDateTime: string;
  endDateTime: string;
}

const CalendarSyncView: React.FC = () => {
  const [events, setEvents] = useState<CalendarEvent[]>([]);
  const [isAuthorized, setIsAuthorized] = useState(false);
  const [loading, setLoading] = useState(false);
  const [showCreateForm, setShowCreateForm] = useState(false);
  const [newEvent, setNewEvent] = useState<NewEvent>({
    summary: '',
    description: '',
    startDateTime: '',
    endDateTime: ''
  });

  // Get userId from localStorage or use default
  const userId = localStorage.getItem('userId') || 'default-user';
  const API_BASE_URL = 'http://localhost:8080/api/calendar';
  
  // Add axios interceptor for debugging
  useEffect(() => {
    axios.interceptors.request.use(request => {
      console.log('🔵 Starting Request:', request.url);
      return request;
    });
    
    axios.interceptors.response.use(
      response => {
        console.log('🟢 Response:', response);
        return response;
      },
      error => {
        console.error('🔴 Response Error:', error);
        console.error('🔴 Error Details:', {
          status: error.response?.status,
          data: error.response?.data,
          headers: error.response?.headers
        });
        return Promise.reject(error);
      }
    );
  }, []);

  useEffect(() => {
    checkAuthorization();
  }, []);

  const checkAuthorization = async () => {
    try {
      const response = await axios.get(`${API_BASE_URL}/events`, {
        params: { userId, maxResults: 10 }
      });
      setEvents(response.data);
      setIsAuthorized(true);
    } catch (error) {
      console.log('User not authorized yet');
      setIsAuthorized(false);
    }
  };

  const handleAuthorize = async () => {
    try {
      const response = await axios.get(`${API_BASE_URL}/auth-url`, {
        params: { userId }
      });
      
      const authUrl = response.data.authUrl;
      const authWindow = window.open(authUrl, '_blank', 'width=600,height=700');
      
      // Listen for OAuth callback
      const messageHandler = async (event: MessageEvent) => {
        if (event.data.type === 'GOOGLE_AUTH_SUCCESS') {
          const code = event.data.code;
          try {
            await axios.post(`${API_BASE_URL}/authorize`, null, {
              params: { code, userId }
            });
            setIsAuthorized(true);
            loadEvents();
            authWindow?.close();
          } catch (error) {
            console.error('Authorization error:', error);
            alert('Error al autorizar con Google Calendar');
          }
          window.removeEventListener('message', messageHandler);
        }
      };
      
      window.addEventListener('message', messageHandler);
    } catch (error) {
      console.error('Authorization error:', error);
      alert('Error al iniciar autorización con Google Calendar');
    }
  };

  const loadEvents = async () => {
    setLoading(true);
    try {
      const response = await axios.get(`${API_BASE_URL}/events`, {
        params: { userId, maxResults: 10 }
      });
      setEvents(response.data);
    } catch (error) {
      console.error('Error loading events:', error);
      alert('Error al cargar eventos del calendario');
    } finally {
      setLoading(false);
    }
  };

  const handleCreateEvent = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    
    try {
      await axios.post(`${API_BASE_URL}/events`, newEvent, {
        params: { userId }
      });
      
      setShowCreateForm(false);
      setNewEvent({
        summary: '',
        description: '',
        startDateTime: '',
        endDateTime: ''
      });
      loadEvents();
      alert('¡Evento creado exitosamente en Google Calendar!');
    } catch (error) {
      console.error('Error creating event:', error);
      alert('Error al crear el evento');
    } finally {
      setLoading(false);
    }
  };

  const handleDeleteEvent = async (eventId: string) => {
    if (!window.confirm('¿Estás seguro de eliminar este evento de Google Calendar?')) return;
    
    setLoading(true);
    try {
      await axios.delete(`${API_BASE_URL}/events/${eventId}`, {
        params: { userId }
      });
      loadEvents();
      alert('Evento eliminado exitosamente');
    } catch (error) {
      console.error('Error deleting event:', error);
      alert('Error al eliminar el evento');
    } finally {
      setLoading(false);
    }
  };

  const formatDateTime = (dateString: string) => {
    try {
      const date = new Date(dateString);
      return date.toLocaleString('es-CO', {
        weekday: 'short',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      });
    } catch (error) {
      return dateString;
    }
  };

  // Authorization prompt view
  if (!isAuthorized) {
    return (
      <div className="calendar-sync-container">
        <div className="auth-prompt">
          <div className="auth-icon">📅</div>
          <h2>Conecta tu Google Calendar</h2>
          <p>
            Sincroniza ClassMate AI con tu calendario de Google para gestionar 
            automáticamente las tareas y eventos de tus clases.
          </p>
          <ul className="auth-benefits">
            <li>✅ Sincronización automática de tareas</li>
            <li>✅ Ver eventos de tus clases</li>
            <li>✅ Crear recordatorios</li>
            <li>✅ Gestionar horarios</li>
          </ul>
          <button className="btn-primary btn-large" onClick={handleAuthorize}>
            <span>🔗</span> Conectar con Google Calendar
          </button>
          <p className="auth-note">
            ClassMate AI solo solicitará permisos para gestionar tu calendario.
          </p>
        </div>
      </div>
    );
  }

  // Main calendar view
  return (
    <div className="calendar-sync-container">
      <div className="calendar-header">
        <div className="header-title">
          <h2>📅 Mi Calendario</h2>
          <p className="header-subtitle">Sincronizado con Google Calendar</p>
        </div>
        <div className="header-actions">
          <button 
            className="btn-secondary" 
            onClick={loadEvents} 
            disabled={loading}
            title="Actualizar eventos"
          >
            {loading ? '⏳ Cargando...' : '🔄 Actualizar'}
          </button>
          <button 
            className="btn-primary" 
            onClick={() => setShowCreateForm(!showCreateForm)}
          >
            ➕ Nuevo Evento
          </button>
        </div>
      </div>

      {/* Create Event Form */}
      {showCreateForm && (
        <div className="create-event-card">
          <div className="card-header">
            <h3>✏️ Crear Nuevo Evento</h3>
            <button 
              className="btn-close" 
              onClick={() => setShowCreateForm(false)}
              title="Cerrar"
            >
              ✕
            </button>
          </div>
          <form onSubmit={handleCreateEvent} className="event-form">
            <div className="form-group">
              <label htmlFor="event-title">
                <span className="label-icon">📝</span> Título del evento
              </label>
              <input
                id="event-title"
                type="text"
                placeholder="Ej: Examen de Ingeniería de Software"
                value={newEvent.summary}
                onChange={(e) => setNewEvent({ ...newEvent, summary: e.target.value })}
                required
                maxLength={100}
              />
            </div>

            <div className="form-group">
              <label htmlFor="event-description">
                <span className="label-icon">📄</span> Descripción (opcional)
              </label>
              <textarea
                id="event-description"
                placeholder="Agrega detalles sobre el evento..."
                value={newEvent.description}
                onChange={(e) => setNewEvent({ ...newEvent, description: e.target.value })}
                rows={3}
                maxLength={500}
              />
            </div>

            <div className="datetime-grid">
              <div className="form-group">
                <label htmlFor="event-start">
                  <span className="label-icon">🕐</span> Fecha y hora de inicio
                </label>
                <input
                  id="event-start"
                  type="datetime-local"
                  value={newEvent.startDateTime}
                  onChange={(e) => setNewEvent({ ...newEvent, startDateTime: e.target.value })}
                  required
                />
              </div>

              <div className="form-group">
                <label htmlFor="event-end">
                  <span className="label-icon">🕑</span> Fecha y hora de fin
                </label>
                <input
                  id="event-end"
                  type="datetime-local"
                  value={newEvent.endDateTime}
                  onChange={(e) => setNewEvent({ ...newEvent, endDateTime: e.target.value })}
                  required
                />
              </div>
            </div>

            <div className="form-actions">
              <button 
                type="button" 
                className="btn-secondary" 
                onClick={() => setShowCreateForm(false)}
              >
                Cancelar
              </button>
              <button 
                type="submit" 
                className="btn-primary" 
                disabled={loading}
              >
                {loading ? '⏳ Creando...' : '✅ Crear Evento'}
              </button>
            </div>
          </form>
        </div>
      )}

      {/* Events List */}
      <div className="events-section">
        <div className="section-header">
          <h3>📋 Próximos Eventos</h3>
          <span className="events-count">{events.length} evento(s)</span>
        </div>

        {loading && (
          <div className="loading-state">
            <div className="spinner"></div>
            <p>Cargando eventos del calendario...</p>
          </div>
        )}
        
        {!loading && events.length === 0 && (
          <div className="empty-state">
            <div className="empty-icon">📭</div>
            <h4>No hay eventos próximos</h4>
            <p>Crea un nuevo evento o sincroniza tus clases</p>
            <button 
              className="btn-primary" 
              onClick={() => setShowCreateForm(true)}
            >
              ➕ Crear Primer Evento
            </button>
          </div>
        )}

        {!loading && events.length > 0 && (
          <div className="events-list">
            {events.map((event) => (
              <div key={event.id} className="event-card">
                <div className="event-header">
                  <div className="event-badge">📅</div>
                  <div className="event-title-section">
                    <h4>{event.summary}</h4>
                    {event.description && (
                      <p className="event-description">{event.description}</p>
                    )}
                  </div>
                </div>

                <div className="event-details">
                  <div className="event-time">
                    <span className="time-icon">🕐</span>
                    <div className="time-info">
                      <strong>Inicio:</strong> {formatDateTime(event.start)}
                    </div>
                  </div>
                  {event.end && (
                    <div className="event-time">
                      <span className="time-icon">🕑</span>
                      <div className="time-info">
                        <strong>Fin:</strong> {formatDateTime(event.end)}
                      </div>
                    </div>
                  )}
                </div>

                <div className="event-actions">
                  {event.htmlLink && (
                    <a 
                      href={event.htmlLink} 
                      target="_blank" 
                      rel="noopener noreferrer" 
                      className="btn-link"
                      title="Ver en Google Calendar"
                    >
                      🔗 Ver en Google
                    </a>
                  )}
                  <button 
                    className="btn-delete" 
                    onClick={() => handleDeleteEvent(event.id)}
                    title="Eliminar evento"
                  >
                    🗑️ Eliminar
                  </button>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>

      {/* Footer info */}
      <div className="calendar-footer">
        <p>
          🔄 Sincronizado con Google Calendar • 
          Los cambios se reflejan automáticamente
        </p>
      </div>
    </div>
  );
};

export default CalendarSyncView;

