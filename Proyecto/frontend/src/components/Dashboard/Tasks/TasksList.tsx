// src/components/Dashboard/Tasks/TasksList.tsx
import React, { useState, useEffect } from 'react';
import type { Task } from '../../../types';
import { getAllTasks, updateTask, deleteTask } from '../../../api/task';
import './TasksList.css'; // <-- Asegúrate de importar el CSS

const TasksList: React.FC = () => {
  const [tasks, setTasks] = useState<Task[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchTasks = async () => {
      try {
        const data = await getAllTasks();
        setTasks(data);
      } catch (err: any) {
        setError(err.response?.data?.message || 'Error al cargar las tareas.');
      } finally {
        setLoading(false);
      }
    };
    fetchTasks();
  }, []);

  const handleToggleComplete = async (taskId: number) => {
    const taskToUpdate = tasks.find(t => t.id === taskId);
    if (!taskToUpdate) return;
    const originalState = taskToUpdate.completed;
    // Optimistic UI update
    setTasks(tasks.map(t => t.id === taskId ? { ...t, completed: !t.completed } : t));
    try {
      await updateTask(taskId, { isCompleted: !taskToUpdate.completed });
    } catch (err) {
      // Revert on error
      setTasks(tasks.map(t => t.id === taskId ? { ...t, completed: originalState } : t));
      setError('No se pudo actualizar la tarea.');
    }
  };

  const handleDelete = async (taskId: number) => {
    if (!window.confirm('¿Estás seguro de que quieres eliminar esta tarea?')) return;
    
    const originalTasks = tasks;
    setTasks(tasks.filter(t => t.id !== taskId));
    try {
      await deleteTask(taskId);
    } catch (err) {
      setTasks(originalTasks); // Revert on error
      setError('No se pudo eliminar la tarea.');
    }
  };

  if (loading) return <div className="loading-container"><p>Cargando tareas...</p></div>;
  if (error) return <p className="error-message">{error}</p>;

  return (
    // <-- CLASE PRINCIPAL DEL CONTENEDOR
    <div className="tasks-list-container">
      <h2 className="tasks-header">Mis Tareas</h2>
      {tasks.length === 0 ? (
        <p className="empty-state-message">No tienes tareas pendientes.</p>
      ) : (
        // <-- LISTA DE TAREAS
        <ul className="tasks-grid">
          {tasks.map(task => (
            // <-- ITEM INDIVIDUAL DE TAREA
            <li key={task.id} className="task-item">
              <input
                type="checkbox"
                checked={task.completed}
                onChange={() => handleToggleComplete(task.id)}
                className="task-checkbox"
              />
              {/* <-- CLASE DINÁMICA PARA EL TEXTO COMPLETADO */}
              <span className={`task-description ${task.completed ? 'is-completed' : ''}`}>
                {task.description}
              </span>
              <span className="task-meta">
                (Prioridad: {task.priority}, Vence: {new Date(task.dueDate).toLocaleDateString()})
              </span>
              {/* <-- BOTÓN DE ELIMINAR */}
              <button onClick={() => handleDelete(task.id)} className="delete-button">
                Eliminar
              </button>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
};

export default TasksList;