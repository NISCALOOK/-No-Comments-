import { RouterProvider } from 'react-router-dom';
import router from './routes'; // Importamos el archivo de rutas
import './styles/App.css';

function App() {
  return <RouterProvider router={router} />;
}

export default App;