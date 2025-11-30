// src/main.tsx
import React from 'react'
import ReactDOM from 'react-dom/client'
import { RouterProvider } from 'react-router-dom'; // <-- 1. Importa RouterProvider

// Importa tu objeto 'router' desde donde lo hayas definido
// (Probablemente se llama 'router' o 'appRouter' y está en ./router/index.ts o similar)
import routes from './routes'; // <-- 2. Asegúrate que esta ruta sea correcta

// Importa aquí tus estilos globales

// No necesitas el componente App para el enrutamiento.
// Si App.tsx no hace nada más, puedes ignorarlo por ahora.
ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    {/* 
      3. Usa RouterProvider y pásale tu objeto de configuración.
      ¡Esto es lo que hace que todo funcione!
    */}
    <RouterProvider router={routes} />
  </React.StrictMode>,
)