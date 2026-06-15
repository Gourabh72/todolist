import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

import { Task } from './model/task.model';
import { TaskStatus } from './model/task-status.enum';
import { TaskFormComponent } from './features/tasks/task-form/task-form.component';
import { TaskListComponent } from './features/tasks/task-list/task-list.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, TaskListComponent, TaskFormComponent],
  templateUrl: './app.component.html'
})
export class AppComponent {
  showTaskForm = false;
  selectedTask?: Task;

  onAddTask(): void {
    this.selectedTask = undefined;
    this.showTaskForm = true;
  }

  onEditTask(task: Task): void {
    this.selectedTask = task;
    this.showTaskForm = true;
  }

  onCompleteTask(task: Task): void {
    this.selectedTask = {
      ...task,
      status: TaskStatus.COMPLETED
    };
    this.showTaskForm = true;
  }

  onTaskSaved(): void {
    this.showTaskForm = false;
  }

  onTaskCanceled(): void {
    this.showTaskForm = false;
  }
}