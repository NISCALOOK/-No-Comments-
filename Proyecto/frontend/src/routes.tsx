import { createBrowserRouter } from 'react-router-dom';


import WelcomePage from './components/WelcomePage/WelcomePage';
import LoginPage from './components/LoginPage/LoginPage';
import RegisterPage from './components/RegisterPage/RegisterPage';
import Dashboard from './components/Dashboard/Dashboard';
import AudioUpload from './components/Dashboard/AudioUpload/AudioUpload';
import ChatAIView from './components/Dashboard/ChatAI/ChatAIView'; 
import CalendarSyncView from './components/Dashboard/CalendarSync/CalendarSyncView';
import TranscriptionViewer from './components/Dashboard/TranscriptionViewer/TranscriptionViewer';
import Export from './components/Dashboard/Export/ExportView';
import TasksList from './components/Dashboard/Tasks/TasksList';
import SummaryView from './components/Dashboard/SummaryView/SummaryView';
import Home from './components/Dashboard/Home/Home'




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
    path: '/dashboard',
    element: <Dashboard />, 
    children: [
      {
    
        index: true,
        element: <AudioUpload />,
      },
      {
        path: 'upload',
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