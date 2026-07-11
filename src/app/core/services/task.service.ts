import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

import { AuthService } from './auth.service';
import { environment } from '../../../environments/environment';
import { Task } from '../../model/task.model';

@Injectable({
  providedIn: 'root'
})
export class TaskService {

  private http = inject(HttpClient);
  private authService = inject(AuthService);

  private readonly API =
    `${environment.apiUrl}/tasks`;

  private authOptions(extraOptions: Record<string, any> = {}): Record<string, any> {
    const token = this.authService.getToken();

    if (!token) {
      console.error('[TaskService] No auth token available for task request. Check login, token storage, or browser localStorage.');
      return extraOptions;
    }

    console.log('[TaskService] Attaching auth token to task request.');

    return {
      ...extraOptions,
      headers: new HttpHeaders({
        Authorization: `Bearer ${token}`
      })
    };
  }

  getAllTasks(): Observable<Task[]> {
    return this.http.get<Task[]>(this.API, this.authOptions());
  }

  getTaskById(id: string): Observable<Task> {
    return this.http.get<Task>(`${this.API}/${id}`, this.authOptions());
  }

  createTask(task: Task): Observable<Task> {
    return this.http.post<Task>(this.API, task, this.authOptions());
  }

  updateTask(id: string, task: Partial<Task>): Observable<Task> {
    return this.http.put<Task>(
      `${this.API}/${id}`,
      task,
      this.authOptions()
    );
  }

  deleteTask(id: string): Observable<void> {
    return this.http.delete<void>(
      `${this.API}/${id}`,
      this.authOptions()
    );
  }

  deleteTasks(ids: string[]): Observable<void> {
    return this.http.request<void>(
      'DELETE',
      this.API,
      this.authOptions({ body: ids })
    );
  }

  markComplete(id: string): Observable<Task> {
    return this.http.patch<Task>(
      `${this.API}/${id}/complete`,
      {},
      this.authOptions()
    );
  }

  markPending(id: string): Observable<Task> {
    return this.http.patch<Task>(
      `${this.API}/${id}/pending`,
      {},
      this.authOptions()
    );
  }

  searchTasks(params: any): Observable<Task[]> {
    return this.http.get<Task[]>(
      `${this.API}/search`,
      this.authOptions({ params })
    );
  }

  filterTasks(params: any): Observable<Task[]> {
    return this.http.get<Task[]>(
      `${this.API}/filter`,
      this.authOptions({ params })
    );
  }
}