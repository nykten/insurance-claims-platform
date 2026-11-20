import { HttpInterceptorFn } from '@angular/common/http';
import { catchError, throwError } from 'rxjs';

export const problemDetailInterceptor: HttpInterceptorFn = (request, next) => next(request).pipe(catchError(error => {
  const detail = error?.error?.detail ?? (error.status === 409 ? 'This claim was updated by another user.' : 'The request could not be completed.');
  return throwError(() => new Error(detail));
}));
