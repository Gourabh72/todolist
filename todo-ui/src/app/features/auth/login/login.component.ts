import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';

import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);

  isRegisterMode = false;
  isSubmitting = false;
  errorMessage = '';

  authForm = this.fb.group({
    username: ['', Validators.required],
    email: [''],
    password: ['', Validators.required]
  });

  constructor() {
    this.updateValidators();
  }

  toggleMode(): void {
    this.isRegisterMode = !this.isRegisterMode;
    this.errorMessage = '';
    this.authForm.reset({
      username: '',
      email: '',
      password: ''
    });
    this.updateValidators();
  }

  private updateValidators(): void {
    const emailControl = this.authForm.get('email');

    if (!emailControl) {
      return;
    }

    if (this.isRegisterMode) {
      emailControl.setValidators([Validators.required, Validators.email]);
    } else {
      emailControl.clearValidators();
    }

    emailControl.updateValueAndValidity({ emitEvent: false });
  }

  submit(): void {
    if (this.authForm.invalid || this.isSubmitting) {
      return;
    }

    const username = this.authForm.value.username ?? '';
    const email = this.authForm.value.email ?? '';
    const password = this.authForm.value.password ?? '';

    this.isSubmitting = true;
    this.errorMessage = '';

    const request$ = this.isRegisterMode
      ? this.authService.register({ username, email, password })
      : this.authService.login(username, password);

    request$.subscribe({
      next: (response) => {
        console.log(
          this.isRegisterMode
            ? '[LoginComponent] Token received from register call:'
            : '[LoginComponent] Token received from login call:',
          response?.token ? 'YES' : 'NO'
        );
        this.isSubmitting = false;
        void this.router.navigateByUrl('/tasks');
      },
      error: (error) => {
        this.isSubmitting = false;
        console.error('[LoginComponent] Login failed:', error);
        this.errorMessage =
          this.isRegisterMode
            ? this.getRegisterErrorMessage(error)
            : error.status === 401
              ? 'Invalid username or password.'
              : 'Unable to sign in right now.';
      }
    });
  }

  private getRegisterErrorMessage(error: any): string {
    if (error.status === 409) {
      return error.error === 'username_taken'
        ? 'Username is already taken.'
        : error.error === 'email_taken'
          ? 'Email is already registered.'
          : 'Account already exists.';
    }

    return 'Unable to create account right now.';
  }
}