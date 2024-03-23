import { Injectable, inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, Router, RouterStateSnapshot } from '@angular/router';
import { User } from '../Models/user.model';



@Injectable({
  providedIn: 'root'
})
class PermissionsService {
  
  constructor(private router: Router) {}
  
  canActivate(next: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    
    let jwt = localStorage.getItem("jwt");
    // In a real application shall be suggested parsing and verificate the JWT token
    let isLoggedUser = jwt !== null;
    if(isLoggedUser) {
      return true;
    } else {
      this.router.navigateByUrl('signin');
      return false;
    }
  }
}


export const AuthGuard: CanActivateFn = (next: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean => {
  return inject(PermissionsService).canActivate(next, state);
}
