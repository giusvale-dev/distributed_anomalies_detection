import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import { UserService } from '../../services/user.service';
import { ActivatedRoute, Router } from '@angular/router';
import { User } from '../../Models/user.model';

interface Roles {
  label: string;
  value: string;
}
@Component({
  selector: 'app-edit-user',
  templateUrl: './edit-user.component.html',
  styleUrls: ['./edit-user.component.css']
})
export class EditUserComponent implements OnInit {
  editUserForm!: FormGroup;
  roles!: Roles[] ;
  id:String;
  user: User;
  message: string;
  severity: string;
  showError: boolean = false;

  constructor(
    private service: UserService,
    private route: ActivatedRoute ,
    private router: Router,
    private fb: FormBuilder) {}
  onEditUser(){
    if ( this.editUserForm.get('newPassword').value === this.editUserForm.get('confirmPassword').value ){
      this.editUserForm.removeControl('confirmPassword');

      this.service.editUser(this.editUserForm,this.user.id).subscribe( {

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
        this.router.navigateByUrl('/users/'+ `/${ this.user.id }`)

    }

  }
  ngOnInit() {

    this.service.getUser(this.route.snapshot.paramMap.get('id')).subscribe({
      next: (data: any) => {
        this.initForm(data);
      }
    })
  }

  private initForm(data: any) {
    this.user = new User(data.id, data.firstName, data.lastName, data.username, data.email, [], '')
    this.roles = [
      {label: "ScAdmin", value: "Security Administrator"},
      {label: "User", value: "User"},
    ];

    this.editUserForm = this.fb.group({
      name: [this.user.name],
      surname: [this.user.surname],
      username: [{value: this.user.username, disabled: true}],
      email: [this.user.email],
      oldPassword: [this.user.oldPassword, Validators.required],
      newPassword: [this.user.newPassword, Validators.required],
      confirmPassword: ['', Validators.required],
      sel_roles: [''],
      enabled: [true]
    });
  }

}