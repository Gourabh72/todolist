import { Priority } from "./priority.enum";
import { TaskStatus } from "./task-status.enum";

export interface Task {
  id?: string;
  title: string;
  description?: string;
  status: TaskStatus;
  priority: Priority;
  dueDate: string;
  createdDate?: string;
}
