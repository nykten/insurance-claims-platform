import { computed, Injectable, signal } from '@angular/core';
import { User } from './auth.types';

@Injectable({providedIn: 'root'})
export class AuthStateService {
  readonly token = signal(localStorage.getItem('chx_token'));
  readonly currentUser = signal<User | null>(JSON.parse(localStorage.getItem('chx_user') ?? 'null'));
  readonly isAuthenticated = computed(() => !!this.token());
  setSession(token: string, user: User) { this.token.set(token); this.currentUser.set(user); localStorage.setItem('chx_token', token); localStorage.setItem('chx_user', JSON.stringify(user)); }
  clearSession() { this.token.set(null); this.currentUser.set(null); localStorage.removeItem('chx_token'); localStorage.removeItem('chx_user'); }
}
