import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { environment } from '../../../environment/environment';
import { HeadingPageComponent } from '../../heading-page/heading-page.component';
import { Observable, catchError } from 'rxjs';
import { UserService } from '../../services/user.service';


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
  showError: boolean = false;

  constructor(private service: UserService, private router: Router){
  }

  onAddUser() {
      
      if (this.addUserForm.get("confirmPassword").value === this.addUserForm.get("password").value) {
        this.addUserForm.removeControl("confirmPassword");
    
        this.service.addUser(this.addUserForm).subscribe( { 
          
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
      } else {
        this.showError = true;
      }
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

 


