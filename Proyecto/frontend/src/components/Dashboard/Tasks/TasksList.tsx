import TaskItem from './TaskItem';

const TasksList = () => {
  const tasks = [
    { id: 1, title: 'Completar transcripción', completed: false },
    { id: 2, title: 'Revisar resumen', completed: true },
    { id: 3, title: 'Programar reunión', completed: false },
  ];

  return (
    <div className="tasks-list">
      <h2>Tasks List Component</h2>
      <ul>
        {tasks.map(task => (
          <TaskItem key={task.id} task={task} />
        ))}
      </ul>
    </div>
  );
};

export default TasksList;