import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { Auth } from '../../services/auth';

@Component({
  selector: 'app-login',
  imports: [
  CommonModule,
  FormsModule
  ],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class Login {

  email = '';
  password = '';

  errorMessage = '';
  loading = false;

  constructor(
    private authService: Auth,
    private router: Router
  ) {}

  onSubmit(): void {

    this.errorMessage = '';
    this.loading = true;

    this.authService.login({
      email: this.email,
      password: this.password
    })
    .subscribe({

      next: (response) => {

        this.authService.saveToken(
          response.token
        );

        this.loading = false;

        this.router.navigate([
          '/dashboard'
        ]);
      },

      error: () => {

        this.loading = false;

        this.errorMessage =
          'Invalid email or password.';
      }
    });
  }
}