import { ChangeDetectorRef , Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';


import {
  EventService,
  EventStats,
  EventResponse
} from '../../services/event';

import { Auth } from '../../services/auth';

@Component({
  selector: 'app-dashboard',

  imports: [
    CommonModule
  ],

  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css'
})
export class Dashboard implements OnInit {

  stats: EventStats | null = null;

  events: EventResponse[] = [];

  loading = true;

  constructor(
    private eventService: EventService,
    private router: Router,
    private authService: Auth,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {

    this.loadStats();
    this.loadEvents();
  }


  loadStats(): void {

  this.eventService
    .getStats()
    .subscribe({

      next: (data) => {

        this.stats = data;

        this.cdr.markForCheck();
      },

      error: (error) => {

        console.error(
          'Error loading stats:',
          error
        );

        this.cdr.markForCheck();
      }

    });
}


  loadEvents(): void {

  this.loading = true;

  this.eventService
    .getAllEvents()
    .subscribe({

      next: (data) => {

        this.events = data;

        this.loading = false;

        this.cdr.markForCheck();
      },

      error: (error) => {

        console.error(
          'Error loading events:',
          error
        );

        this.loading = false;

        this.cdr.markForCheck();
      }

    });
}

  openEvent(id: number): void {

    this.router.navigate([
      '/events',
      id
    ]);
  }


  logout(): void {

    this.authService.logout();

    this.router.navigate([
      '/login'
    ]);
  }


  getInitial(eventName: string): string {

    return eventName
      ? eventName.charAt(0).toUpperCase()
      : '?';
  }
}