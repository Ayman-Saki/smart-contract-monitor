import {
  ChangeDetectorRef,
  Component,
  OnInit
} from '@angular/core';

import { CommonModule } from '@angular/common';

import {
  ActivatedRoute,
  Router
} from '@angular/router';

import {
  EventDetailResponse,
  EventService
} from '../../services/event';

@Component({
  selector: 'app-event-detail',

  imports: [
    CommonModule
  ],

  templateUrl: './event-detail.html',
  styleUrl: './event-detail.css'
})
export class EventDetail implements OnInit {

  event: EventDetailResponse | null = null;

  loading = true;

  errorMessage = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private eventService: EventService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {

    const id =
      Number(
        this.route.snapshot.paramMap.get('id')
      );

    this.loadEvent(id);
  }


  loadEvent(id: number): void {

    this.loading = true;

    this.eventService
      .getEventById(id)
      .subscribe({

        next: (data) => {

          this.event = data;

          this.loading = false;

          this.cdr.markForCheck();
        },

        error: (error) => {

          console.error(
            'Error loading event:',
            error
          );

          this.errorMessage =
            'Unable to load event details.';

          this.loading = false;

          this.cdr.markForCheck();
        }

      });
  }


  goBack(): void {

    this.router.navigate([
      '/dashboard'
    ]);
  }


  shortenAddress(
    value: string | null
  ): string {

    if (!value) {
      return 'Not applicable';
    }

    if (value.length <= 18) {
      return value;
    }

    return (
      value.substring(0, 10)
      + '...'
      + value.substring(
          value.length - 8
        )
    );
  }
}