import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { environment } from '../../../environment/environment';
import { HeadingPageComponent } from '../../heading-page/heading-page.component';
import { Observable, catchError } from 'rxjs';
 

@Component({
  selector: 'app-add-user',
  templateUrl: './add-user.component.html',
  styleUrl: './add-user.component.css'
})
export class AddUserComponent {
  
  addUserForm!: FormGroup;
  title: string;
  authorities!: string[];
  message: string;
  severity: string;


  constructor(private http: HttpClient, private router: Router){
  }

  onAddUser() {

      
      this.addUserForm.removeControl("confirmPassword");
      
      this.addUser(this.addUserForm).subscribe( { 
      
        next: (data: any) => {
          console.log(data)
          this.message = "User insert done"
          this.severity = 'info';
          
        },
        error: (err) => {
          console.log(err);
          this.message = err.error;
          this.severity = 'error';
        }})
        this.router.navigateByUrl('/users/add')
  }

  addUser(addUserForm: FormGroup): Observable<any>{
    const httpOptions = {
      headers: new HttpHeaders({ 'Content-Type': 'application/json' })
    };
    const addUserUrl = environment.addUser;
    return this.http.post(addUserUrl,addUserForm.value, httpOptions);
  }

  ngOnInit() {

    this.title = "Create user page"
    
    this.authorities= [
      "ROLE_SUPERADMIN",
      "ROLE_SYSTEM_ADMINISTRATOR",
    ];
    this.addUserForm = new FormGroup({
      name: new FormControl(),
      surname: new FormControl(),
      username: new FormControl("",Validators.required),
      email: new FormControl("", [Validators.required, Validators.email]),
      password: new FormControl("", Validators.required),
      confirmPassword: new FormControl("",Validators.required),
      authorities: new FormControl(""),
      enabled: new FormControl(true, Validators.required)
    })
  }

}

 


