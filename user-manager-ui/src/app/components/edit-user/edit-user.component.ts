import { Component } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
interface Roles {
  label: string;
  value: string;
}
@Component({
  selector: 'app-edit-user',
  templateUrl: './edit-user.component.html',
  styleUrls: ['./edit-user.component.css']
})
export class EditUserComponent {
  editUserForm!: FormGroup;
  roles!: Roles[] ;

  onEditUser(){

  }
  ngOnInit() {
    
    this.roles= [
      {label: "ScAdmin", value: "Security Administrator"},
      {label: "User", value: "User"},
    ];
    this.editUserForm = new FormGroup({
      name: new FormControl(),
      surname: new FormControl(),
      username: new FormControl("",Validators.required),
      email: new FormControl("", [Validators.required, Validators.email]),
      oldPassword: new FormControl("", Validators.required),
      newPassword: new FormControl("",Validators.required),
      confirmPassword: new FormControl("",Validators.required),
      sel_roles: new FormControl("",Validators.required)
    })
  }
}
