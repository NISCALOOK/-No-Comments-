// src/router.tsx

// 1. Añadimos 'Navigate' a la importación desde react-router-dom
import { createBrowserRouter, Navigate } from 'react-router-dom';

// ... Tus imports de componentes ...
import WelcomePage from './components/WelcomePage/WelcomePage';
import LoginPage from './components/LoginPage/LoginPage';
import RegisterPage from './components/RegisterPage/RegisterPage';
import Dashboard from './components/Dashboard/Dashboard';
import AudioUpload from './components/Dashboard/AudioUpload/AudioUpload';
import ChatAIView from './components/Dashboard/ChatAI/ChatAIView';
import CalendarSyncView from './components/Dashboard/CalendarSync/CalendarSyncView';
import CalendarCallback from './components/Dashboard/CalendarSync/CalendarCallback';
import TranscriptionViewer from './components/Dashboard/TranscriptionViewer/TranscriptionViewer';
import Export from './components/Dashboard/Export/ExportView';
import TasksList from './components/Dashboard/Tasks/TasksList';
import SummaryView from './components/Dashboard/SummaryView/SummaryView';
import Home from './components/Dashboard/Home/Home';
import ProtectedRoute from './components/ProtectedRoute';

// 2. Eliminamos esta importación, no se necesita aquí.
// La lógica de autenticación vive en 'ProtectedRoute'.
// import { useAuthStore } from './store';

const router = createBrowserRouter([
  {
    path: '/',
    element: <WelcomePage />,
  },
  {
    path: '/welcome',
    element: <WelcomePage />,
  },
  {
    path: '/login',
    element: <LoginPage />,
  },
  {
    path: '/register',
    element: <RegisterPage />,
  },
  {
    // 3. Estructura de rutas protegida mejorada.
    path: '/calendar/callback',
    element: <CalendarCallback />,
  },
  {
    path: '/dashboard',
    element: (
      <ProtectedRoute>
        <Dashboard />
      </ProtectedRoute>
    ),
    children: [
      {
        index: true, // Se renderiza cuando la ruta es exactamente /dashboard
        element: <Navigate to="/dashboard/upload" replace />,
      },
      {
        path: 'upload', // Ruta relativa: /dashboard/upload
        element: <AudioUpload />,
      },
      {
        path: 'transcription',
        element: <TranscriptionViewer />,
      },
      {
        path: 'summary',
        element: <SummaryView />,
      },
      {
        path: 'tasks',
        element: <TasksList />,
      },
      {
        path: 'calendar',
        element: <CalendarSyncView />,
      },
      {
        path: 'chat',
        element: <ChatAIView />,
      },
      {
        path: 'export',
        element: <Export />,
      },
      {
        path: 'home',
        element: <Home />,
      },
    ],
  },
]);

export default router;
