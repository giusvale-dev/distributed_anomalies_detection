import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { User } from '../Models/user.model';
import { Observable } from 'rxjs';
import { environment } from '../../environment/environment';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/x-www-form-urlencoded' })
};
@Injectable({
  providedIn: 'root'
})
export class AuthService {
  
  isLoggedIn = false
  
  constructor(private http: HttpClient) { }

  signIn(username: string, password: string): Observable<any>{
    const url = `${environment.loginUrl}?username=${username}&password=${password}`;
    return this.http.post(url,httpOptions);
  }
}
