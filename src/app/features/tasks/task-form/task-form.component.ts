import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';

import { CommonModule } from '@angular/common';

import { TaskService } from '../../../core/services/task.service';
import { Task } from '../../../model/task.model';

@Component({
  selector: 'app-task-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule
  ],
  templateUrl: './task-form.component.html',
  styleUrls: ['./task-form.component.scss']
})
export class TaskFormComponent implements OnChanges {

  @Input() task?: Task;
  @Output() saved = new EventEmitter<void>();
  @Output() cancel = new EventEmitter<void>();

  taskForm: FormGroup;
  isEditMode = false;

  constructor(
    private fb: FormBuilder,
    private taskService: TaskService
  ) {

    this.taskForm = this.fb.group({
      title: ['', Validators.required],
      description: [''],
      status: ['PENDING'],
      priority: ['MEDIUM'],
      dueDate: ['', Validators.required]
    });
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['task']) {
      this.patchForm();
    }
  }

  private patchForm(): void {
    if (this.task && this.task.id) {
      this.isEditMode = true;
      this.taskForm.setValue({
        title: this.task.title,
        description: this.task.description ?? '',
        status: this.task.status,
        priority: this.task.priority,
        dueDate: this.task.dueDate
      });
    } else {
      this.isEditMode = false;
      this.taskForm.reset({
        title: '',
        description: '',
        status: 'PENDING',
        priority: 'MEDIUM',
        dueDate: ''
      });
    }
  }

  saveTask(): void {

    if (this.taskForm.invalid) {
      return;
    }

    const payload = this.taskForm.value;

    if (this.isEditMode && this.task?.id) {
      this.taskService
        .updateTask(this.task.id, payload)
        .subscribe({
          next: (response) => {
            console.log('Updated', response);
            this.saved.emit();
          },
          error: (error) => {
            console.error(error);
          }
        });
    } else {
      this.taskService
        .createTask(payload)
        .subscribe({
          next: (response) => {
            console.log('Created', response);
            this.saved.emit();
          },
          error: (error) => {
            console.error(error);
          }
        });
    }
  }

  cancelForm(): void {
    this.cancel.emit();
  }

}