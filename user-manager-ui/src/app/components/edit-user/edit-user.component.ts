import {Component, OnInit} from '@angular/core';
import {AbstractControl, FormArray, FormBuilder, FormGroup, ValidatorFn, Validators} from '@angular/forms';
import { UserService } from '../../services/user.service';
import { ActivatedRoute, Router } from '@angular/router';
import { User } from '../../Models/user.model';
import { UsersComponent } from '../users/users.component';




@Component({
  selector: 'app-edit-user',
  templateUrl: './edit-user.component.html',
  styleUrls: ['./edit-user.component.css'],
  
})

export class EditUserComponent implements OnInit {
  editUserForm!: FormGroup;
  roles!: string[];
  id:String;
  data: any;
  user: User;
  u: User;
  us: User[] ;
  message: string;
  severity: string;
  showError: boolean = false;

  constructor(
    private service: UserService,
    private route: ActivatedRoute ,
    private router: Router,
    private fb: FormBuilder) {}
    
  onEditUser(){
    let usrPayload: User;
    usrPayload = new User(this.user.id,this.editUserForm.get('name').value,this.editUserForm.get('surname').value,this.editUserForm.get('username').value,
    this.editUserForm.get('email').value,this.editUserForm.get('sel_roles').value,this.editUserForm.get('enabled').value,'',this.editUserForm.get('newPassword').value)
    this.service.editUser(usrPayload,this.user.id).subscribe( {

      next: (data: any) => {
        console.log(data)
        this.message = "User edit done"
        this.severity = 'info';
        this.router.navigateByUrl('/users')

      },
      error: (err) => {
        console.log(err);
        this.message = err.error;
        this.severity = 'error';
        this.router.navigateByUrl('/users/edit'+ `/${ this.user.id }`)
      }})
  }

  ngOnInit() {
    this.roles = [
      "ROLE_SUPERADMIN",
      "ROLE_SYSTEM_ADMINISTRATOR",
    ];
    
    let id: string = this.route.snapshot.paramMap.get('id')
    this.setUserList(this.service.getUsersList())
    if(typeof this.us !== 'undefined'){
      this.setUser(this.us[parseInt(id)-1]);
      this.initForm(this.u)
    }else{
      
      this.router.navigateByUrl('/users')
    }
  
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

  private setUserList(data:User[]){
    this.us = data;
  }
  private setUser(data: any){
    this.u = data
  }
  private initForm(data: any) {
    this.user = new User(data.id, data.name, data.surname, data.username, data.email, data.roles,data.enabled ,'')
    
    
    this.editUserForm = this.fb.group({
      name: [this.user.name],
      surname: [this.user.surname],
      username: [{value: this.user.username, disabled: true}],
      email: [this.user.email],
      newPassword: [''],
      confirmPassword: [''],
      sel_roles: [this.user.authorities],
      enabled: [this.user.enabled]
    },
    {
      validators: this.matchValidator('newPassword','confirmPassword')
    });
    
    
  }

}