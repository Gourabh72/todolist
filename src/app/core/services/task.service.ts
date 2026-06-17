import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import { Task } from '../../model/task.model';

@Injectable({
  providedIn: 'root'
})
export class TaskService {

  private http = inject(HttpClient);

  private readonly API =
    `${environment.apiUrl}/tasks`;

  getAllTasks(): Observable<Task[]> {
    return this.http.get<Task[]>(this.API);
  }

  getTaskById(id: string): Observable<Task> {
    return this.http.get<Task>(`${this.API}/${id}`);
  }

  createTask(task: Task): Observable<Task> {
    return this.http.post<Task>(this.API, task);
  }

  updateTask(id: string, task: Partial<Task>): Observable<Task> {
    return this.http.put<Task>(
      `${this.API}/${id}`,
      task
    );
  }

  deleteTask(id: string): Observable<void> {
    return this.http.delete<void>(
      `${this.API}/${id}`
    );
  }

  deleteTasks(ids: string[]): Observable<void> {
    return this.http.request<void>(
      'DELETE',
      this.API,
      { body: ids }
    );
  }

  markComplete(id: string): Observable<Task> {
    return this.http.patch<Task>(
      `${this.API}/${id}/complete`,
      {}
    );
  }

  markPending(id: string): Observable<Task> {
    return this.http.patch<Task>(
      `${this.API}/${id}/pending`,
      {}
    );
  }

  searchTasks(params: any): Observable<Task[]> {
    return this.http.get<Task[]>(
      `${this.API}/search`,
      { params }
    );
  }

  filterTasks(params: any): Observable<Task[]> {
    return this.http.get<Task[]>(
      `${this.API}/filter`,
      { params }
    );
  }
}