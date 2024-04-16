import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { FormGroup } from '@angular/forms';
import { environment } from '../../environment/environment';
import { User } from '../Models/user.model';




@Injectable({
  providedIn: 'root'
})
export class UserService {
  public users: Array<User> ;
  constructor(private http: HttpClient,
              ) { }

  getUser(id: string): Observable<any>{
    return this.http.get(`${environment.usersUrl}`+ `/${ id }`)
  }

  getUsers(url: string): Observable<any>{
    return this.http.get(url).pipe(map(result => result));
  }

  addUser(addUser: User): Observable<any>{
    const httpOptions = {
      headers: new HttpHeaders({ 'Content-Type': 'application/json' })
    };
    const addUserUrl = environment.addUser;
    return this.http.post(addUserUrl,addUser, httpOptions);
  }

  editUser(editUser: User, id: string): Observable<any>{
    const httpOptions = {
      headers: new HttpHeaders({ 'Content-Type': 'application/json' })
    };
    const addUserUrl = environment.addUser;
    return this.http.post(`${environment.editUserUrl}`,editUser, httpOptions);
  }
  
  deleteUser(id: string){
    const httpOptions = {
      headers: new HttpHeaders({ 'Content-Type': 'application/json' })
    };
    return this.http.post(`${environment.deleteUser}`+ `/${ id }`, httpOptions);
  }

  public getUsersList(): Array<User>  {
    return this.users;
  }
  public setUsersList(users: any){
    this.users = users;
  }
}
