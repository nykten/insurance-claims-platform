import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Claim } from './claims.models';
import { Observable } from 'rxjs';

@Injectable({providedIn:'root'})
export class ClaimsApiService { private http=inject(HttpClient); list():Observable<Claim[]>{return this.http.get<Claim[]>('/api/claims')} submit(request:unknown):Observable<Claim>{return this.http.post<Claim>('/api/claims',request)} queue():Observable<Claim[]>{return this.http.get<Claim[]>('/api/officer/queue')} }
