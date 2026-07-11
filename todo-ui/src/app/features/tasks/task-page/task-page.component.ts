import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';

import { Task } from '../../../model/task.model';
import { TaskStatus } from '../../../model/task-status.enum';
import { TaskFormComponent } from '../task-form/task-form.component';
import { TaskListComponent } from '../task-list/task-list.component';

@Component({
  selector: 'app-task-page',
  standalone: true,
  imports: [CommonModule, TaskListComponent, TaskFormComponent],
  templateUrl: './task-page.component.html',
  styleUrls: ['./task-page.component.scss']
})
export class TaskPageComponent {
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