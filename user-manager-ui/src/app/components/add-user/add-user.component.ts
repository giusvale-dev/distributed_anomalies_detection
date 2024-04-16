import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component } from '@angular/core';
import { AbstractControl, FormControl, FormGroup, ValidatorFn, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { environment } from '../../../environment/environment';
import { HeadingPageComponent } from '../../heading-page/heading-page.component';
import { Observable, catchError } from 'rxjs';
import { UserService } from '../../services/user.service';
import { User } from '../../Models/user.model';



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
    let usrPayload: User;
    usrPayload = new User('',this.addUserForm.get('name').value,this.addUserForm.get('surname').value,this.addUserForm.get('username').value,
    this.addUserForm.get('email').value,this.addUserForm.get('authorities').value,this.addUserForm.get('enabled').value, '', this.addUserForm.get('password').value)
    
    
    console.log(usrPayload)

    this.service.addUser(usrPayload).subscribe( { 
      
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
    },
    { validators: this.matchValidator('password','confirmPassword') })
  }

  matchValidator(controlName: string, matchingControlName: string): ValidatorFn {
    return (abstractControl: AbstractControl) => {
        const control = abstractControl.get(controlName);
        const matchingControl = abstractControl.get(matchingControlName);

        if (matchingControl!.errors && !matchingControl!.errors?.['confirmedValidator']) {
            return null;
        }

        if (control!.value !== matchingControl!.value) {
          const error = { confirmedValidator: 'Passwords do not match.' };
          matchingControl!.setErrors(error);
          return { passwordMismatch: true };
        } else {
          matchingControl!.setErrors(null);
          return null;
        }
    }
  }

}

 


