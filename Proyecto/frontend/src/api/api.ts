// src/api/api.ts

import axios from 'axios';

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

// --- INTERCEPTOR DE PETICIÓN (CORREGIDO) ---
api.interceptors.request.use((config) => {
  // 1. Obtenemos el string completo de localStorage usando la clave correcta: 'auth-storage'
  const authDataString = localStorage.getItem('auth-storage');

  if (authDataString) {
    try {
      // 2. Parseamos el string para convertirlo en un objeto JavaScript.
      // Esto nos da: { state: { token: "...", user: {...} }, version: 0 }
      const authData = JSON.parse(authDataString);
      
      // 3. Accedemos al token a través de la ruta correcta: state.token
      const token = authData.state.token;

      if (token) {
        // 4. Si encontramos el token, lo añadimos a la cabecera de la petición.
        config.headers.Authorization = `Bearer ${token}`;
      }
    } catch (error) {
      console.error("Error parsing auth data from localStorage", error);
      // Si algo falla al parsear, continuamos sin token (la petición fallará con 403, pero no romperá la app)
    }
  }
  return config;
});

// --- INTERCEPTOR DE RESPUESTA (sin cambios) ---
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Token inválido o expirado
      localStorage.removeItem('auth-storage'); // Limpiamos la clave correcta
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default api;