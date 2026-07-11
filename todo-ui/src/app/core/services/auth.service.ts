import { Injectable, PLATFORM_ID, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { isPlatformBrowser } from '@angular/common';
import { Observable, tap } from 'rxjs';

import { environment } from '../../../environments/environment';

interface AuthResponse {
  token: string;
}

interface RegisterRequest {
  username: string;
  email: string;
  password: string;
  role?: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private http = inject(HttpClient);
  private platformId = inject(PLATFORM_ID);
  private readonly authKey = 'todolist_token';
  private readonly API = `${environment.apiUrl}/login`;
  private readonly REGISTER_API = `${environment.apiUrl}/register`;

  private saveTokenResponse(response: AuthResponse): AuthResponse {
    console.log('[AuthService] Auth response received:', response);
    if (!response?.token) {
      console.error('[AuthService] Auth response did not include a token.');
    }

    this.setToken(response.token);
    return response;
  }

  login(username: string, password: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(this.API, { username, password }).pipe(
      tap((response) => this.saveTokenResponse(response))
    );
  }

  register(data: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(this.REGISTER_API, data).pipe(
      tap((response) => this.saveTokenResponse(response))
    );
  }

  setToken(token: string): void {
    if (!isPlatformBrowser(this.platformId)) {
      console.warn('[AuthService] Not storing token because the app is not running in the browser.');
      return;
    }

    console.log('[AuthService] Storing token in localStorage. Token present:', !!token);
    localStorage.setItem(this.authKey, token);
  }

  getToken(): string | null {
    if (!isPlatformBrowser(this.platformId)) {
      return null;
    }

    const token = localStorage.getItem(this.authKey);
    console.log('[AuthService] Read token from localStorage:', token ? 'FOUND' : 'MISSING');
    return token;
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  logout(): void {
    if (!isPlatformBrowser(this.platformId)) {
      return;
    }

    console.log('[AuthService] Removing token from localStorage.');
    localStorage.removeItem(this.authKey);
  }
}