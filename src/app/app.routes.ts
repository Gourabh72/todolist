import { Routes } from '@angular/router';
import { TaskFormComponent } from './features/tasks/task-form/task-form.component';

export const routes: Routes = [
  {
    path: 'tasks',
    component: TaskFormComponent
  }
];