import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface EventStats {
  totalEvents: number;
  unusualEvents: number;
  highOrCriticalEvents: number;
}

export interface EventResponse {
  id: number;
  eventName: string;
  transactionHash: string;
  blockNumber: number;
  eventTimestamp: string;
  severity: string;
  status: string;
  blockchainType: string;
}

export interface EventDetailResponse {
  id: number;
  eventName: string;
  transactionHash: string;
  blockNumber: number;
  eventTimestamp: string;

  fromAddress: string | null;
  toAddress: string | null;

  amount: number | null;

  severity: string;
  status: string;

  blockchainType: string;
  contractAddress: string;

  rawData: string | null;

  summary: string | null;
  summaryModel: string | null;
}

@Injectable({
  providedIn: 'root'
})
export class EventService {

  private readonly apiUrl =
    'http://localhost:8080/api/events';

  constructor(
    private http: HttpClient
  ) {}

  getStats(): Observable<EventStats> {

    return this.http.get<EventStats>(
      `${this.apiUrl}/stats`
    );
  }

  getAllEvents(): Observable<EventResponse[]> {

    return this.http.get<EventResponse[]>(
      this.apiUrl
    );
  }

  getEventById(id: number): Observable<EventDetailResponse> {

  return this.http.get<EventDetailResponse>(
    `${this.apiUrl}/${id}`
  );
}
}