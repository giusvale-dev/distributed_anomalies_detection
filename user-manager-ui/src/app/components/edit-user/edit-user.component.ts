import { Component } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
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
export class EditUserComponent {
  editUserForm!: FormGroup;
  roles!: Roles[] ;
  id:String;
  user: User;
  message: string;
  severity: string;
  showError: boolean = false;

  constructor(private service: UserService,private route: ActivatedRoute ,private router: Router ) {}
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
       
        this.user = new User(data.id, data.firstName, data.lastName, data.username, data.email, [], '')
        this.roles= [
          {label: "ScAdmin", value: "Security Administrator"},
          {label: "User", value: "User"},
        ];

        this.editUserForm = new FormGroup({
          name: new FormControl(),
          surname: new FormControl(),
          username: new FormControl(),
          email: new FormControl(),
          oldPassword: new FormControl<string>('', Validators.required),
          newPassword: new FormControl<string>('',Validators.required),
          confirmPassword: new FormControl<string>('',Validators.required),
          sel_roles: new FormControl(""),
          enabled: new FormControl(true),
        });
        this.editUserForm.get('name').setValue(this.user.name);
        this.editUserForm.get('surname').setValue(this.user.surname);      
        this.editUserForm.get('username').setValue(this.user.username);
        this.editUserForm.get('username').disable();
        this.editUserForm.get('email').setValue(this.user.email);     
        // this.editUserForm.get('roles').setValue(this.user.roles);
      
        
      }
    })

   

   
  }
}
