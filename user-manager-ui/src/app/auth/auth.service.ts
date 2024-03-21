import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { User } from '../Models/user.model';
import { Observable } from 'rxjs';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};
@Injectable({
  providedIn: 'root'
})
export class AuthService {
  signInUrl = 'https://dummyjson.com/auth/login'
  isLoggedIn = false
  user: User

  
  constructor(private http: HttpClient) { }

  createUser(username: string, id: string, token: string){
    this.user = new User(username, id, token)
  }
  signIn(username: string, password: string): Observable<any>{
    return this.http.post(this.signInUrl,{ username: username, password: password},httpOptions)
  }
}
