import { Component } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../../auth/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-signin',
  templateUrl: './signin.component.html',
  styleUrls: ['./signin.component.css']
})
export class SigninComponent {
  signinForm: FormGroup;


  constructor(private authService: AuthService, private router: Router) {}

  ngOnInit() {
    this.signinForm = new FormGroup({
      username: new FormControl('',[Validators.required]),
      password: new FormControl('',[Validators.required])

    })
  }

  onSignIn(form: FormGroup){
    const username = form.value.username
    const password = form.value.password

    this.authService.signIn(username, password).subscribe({ next: (data: any) => {

      this.authService.createUser(data.id, data.username, data.token)
      localStorage.setItem('user', JSON.stringify(this.authService.user))
      this.router.navigateByUrl('/users')
    },
      error: (err) => {
        console.log(err.error.message)
      }})
    
  }
}
