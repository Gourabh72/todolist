import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TaskService } from '../../../core/services/task.service';
import { Task } from '../../../model/task.model';

@Component({
  selector: 'app-task-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './task-list.component.html',
  styleUrls: ['./task-list.component.scss']
})
export class TaskListComponent implements OnInit {

  @Output() addTask = new EventEmitter<void>();
  @Output() editTask = new EventEmitter<Task>();
  @Output() completeTask = new EventEmitter<Task>();

  tasks: Task[] = [];
  searchTerm = '';
  selectedTasks = new Set<string>();

  constructor(private taskService: TaskService) {}

  ngOnInit(): void {
    this.loadTasks();
  }

  addTaskClicked(): void {
    this.addTask.emit();
  }

  editTaskClicked(task: Task): void {
    this.editTask.emit(task);
  }

  completeTaskClicked(task: Task): void {
    this.completeTask.emit(task);
  }

  onSearch(): void {
    const query = this.searchTerm.trim();
    if (!query) {
      this.loadTasks();
      return;
    }

    this.taskService.searchTasks({ title: query }).subscribe({
      next: (data: Task[]) => {
        this.tasks = data;
        console.log('Search results:', data);
      },
      error: (err: any) => {
        console.error('Error searching tasks', err);
      }
    });
  }

  clearSearch(): void {
    this.searchTerm = '';
    this.loadTasks();
  }

  toggleTaskSelection(task: Task): void {
    if (!task.id) return;
    if (this.selectedTasks.has(task.id)) {
      this.selectedTasks.delete(task.id);
    } else {
      this.selectedTasks.add(task.id);
    }
  }

  selectAll(): void {
    this.tasks.forEach(task => {
      if (task.id) {
        this.selectedTasks.add(task.id);
      }
    });
  }

  toggleSelectAll(): void {
    const allSelected = this.selectedTasks.size === this.tasks.length && this.tasks.length > 0;
    if (allSelected) {
      this.clearAll();
    } else {
      this.selectAll();
    }
  }

  clearAll(): void {
    this.selectedTasks.clear();
  }

  deleteSelectedTasks(): void {
    if (this.selectedTasks.size === 0) {
      alert('Please select tasks to delete');
      return;
    }

    const confirmed = window.confirm(`Are you sure you want to delete ${this.selectedTasks.size} selected task(s)?`);
    if (!confirmed) {
      return;
    }

    const ids = Array.from(this.selectedTasks);
    console.log('Deleting tasks with IDs:', ids);
    this.taskService.deleteTasks(ids).subscribe({
      next: () => {
        console.log('Successfully deleted selected tasks');
        this.selectedTasks.clear();
        this.loadTasks();
      },
      error: (err: any) => {
        console.error('Error deleting selected tasks:', err);
        alert('Failed to delete tasks. Please try again.');
      }
    });
  }

  deleteTaskClicked(task: Task): void {
    if (!task.id) {
      console.error('Delete failed: task id is missing', task);
      return;
    }

    const confirmed = window.confirm(`Are you sure you want to delete task "${task.title}"?`);
    if (!confirmed) {
      return;
    }

    this.taskService.deleteTask(task.id).subscribe({
      next: () => {
        console.log('Deleted task', task.id);
        this.loadTasks();
      },
      error: (err: any) => {
        console.error('Error deleting task', err);
      }
    });
  }

  loadTasks(): void {
    this.taskService.getAllTasks().subscribe({
      next: (data: Task[]) => {
        this.tasks = data;
        // console.log('Tasks:', data);
      },
      error: (err: any) => {
        console.error('Error loading tasks', err);
      }
    });
  }
}
