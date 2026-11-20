import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthStateService } from './auth.service';
import { Role } from './auth.types';

export const roleGuard = (roles: Role[]): CanActivateFn => () => {
  const auth = inject(AuthStateService); const router = inject(Router);
  return auth.isAuthenticated() && !!auth.currentUser() && roles.includes(auth.currentUser()!.role) ? true : router.parseUrl('/login');
};
