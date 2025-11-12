interface Task {
  id: number;
  title: string;
  completed: boolean;
}

interface TaskItemProps {
  task: Task;
}

const TaskItem: React.FC<TaskItemProps> = ({ task }) => {
  return (
    <li className={`task-item ${task.completed ? 'completed' : ''}`}>
      <span>{task.title}</span>
      <input type="checkbox" checked={task.completed} readOnly />
    </li>
  );
};

export default TaskItem;