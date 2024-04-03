import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { FormGroup } from '@angular/forms';
import { environment } from '../../environment/environment';




@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(private http: HttpClient) { }

  getUser(id: string): Observable<any>{
    return this.http.get(`${environment.usersUrl}`+ `/${ id }`)
  }

  getUsers(url: string): Observable<any>{
    return this.http.get(url).pipe(map(result => result));
  }

  addUser(addUserForm: FormGroup): Observable<any>{
    const httpOptions = {
      headers: new HttpHeaders({ 'Content-Type': 'application/json' })
    };
    const addUserUrl = environment.addUser;
    return this.http.post(addUserUrl,addUserForm.value, httpOptions);
  }

  editUser(editUserForm: FormGroup, id: string): Observable<any>{
    const httpOptions = {
      headers: new HttpHeaders({ 'Content-Type': 'application/json' })
    };
    const addUserUrl = environment.addUser;
    return this.http.post(`${environment.editUserUrl}`+ `/${ id }`,editUserForm.value, httpOptions);
  }
  
  deleteUser(id: string){
    const httpOptions = {
      headers: new HttpHeaders({ 'Content-Type': 'application/json' })
    };
    return this.http.post(`${environment.deleteUser}`+ `/${ id }`, httpOptions);
  }
}
