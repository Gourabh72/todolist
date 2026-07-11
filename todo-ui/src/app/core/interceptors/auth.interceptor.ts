import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';

import { AuthService } from '../services/auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const platformId = inject(PLATFORM_ID);
  const router = inject(Router);

  const token = authService.getToken();
  const shouldAttachToken = !!token && !req.url.includes('/login') && !req.url.includes('/register');
  const authReq = shouldAttachToken
    ? req.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      })
    : req;

  return next(authReq).pipe(
    catchError((error: HttpErrorResponse) => {
      const unauthorized = error.status === 401;

      if (unauthorized && isPlatformBrowser(platformId) && shouldAttachToken) {
        authService.logout();
        router.navigateByUrl('/login');
      }

      return throwError(() => error);
    })
  );
};