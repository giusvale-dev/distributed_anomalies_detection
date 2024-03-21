import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';


 
interface Roles {
  label: string;
  value: string;
}
@Component({
  selector: 'app-add-user',
  templateUrl: './add-user.component.html',
  styleUrl: './add-user.component.css'
})
export class AddUserComponent {
  
  addUserForm!: FormGroup;

  roles!: Roles[] ;


  constructor(private http: HttpClient, private router: Router){
  }

  onAddUser(){

    
      localStorage.setItem('JWT', 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4iLCJzdXJuYW1lIjoiRG9lIiwidXNlcm5hbWUiOiJqZG9lIiwiZW1haWwiOiJqZG9lQGVtYWlsLmNvbSIsImVuYWJsZWQiOnRydWUsInJvbGVzIjpbIlJPTEVfU1VQRVJVU0VSIiwiUk9MRV9TWVNURU1fQURNSU5JU1RSQVRPUiJdLCJpYXQiOjE1MTYyMzkwMjJ9.OVtTGLNGddvLkvWTwptQuLU9zLXxgTKHphGYR4wO5IM');
      this.http.post('https://7c2ba1b2-2c32-40ca-993c-50eabc33c918.mock.pstmn.io', this.addUserForm.value).subscribe((res:any)=>{
        if(res.result){
          alert('created user');
          this.router.navigateByUrl('/addUser');
          
        } else{
          alert(res.message);
        }
      })
    
  }

  ngOnInit() {
    this.roles= [
      {label: "ScAdmin", value: "Security Administrator"},
      {label: "User", value: "User"},
    ];
    this.addUserForm = new FormGroup({
      name: new FormControl(),
      surname: new FormControl(),
      username: new FormControl("",Validators.required),
      email: new FormControl("", [Validators.required, Validators.email]),
      password: new FormControl("", Validators.required),
      confirmPassword: new FormControl("",Validators.required),
      sel_roles: new FormControl("",Validators.required)
    })
  }

}

 


