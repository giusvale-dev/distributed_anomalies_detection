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
  showError = false;
  errorMessage = '';


  constructor(private authService: AuthService, private router: Router) {}

  ngOnInit() {

    if(localStorage.getItem('jwt') !== null) {
      localStorage.clear();
    }

    this.signinForm = new FormGroup({
      username: new FormControl('',[Validators.required]),
      password: new FormControl('',[Validators.required])

    })
  }

  onSignIn(form: FormGroup){
    const username = form.value.username
    const password = form.value.password

    this.authService.signIn(username, password).subscribe( { 
      
      next: (data: any) => {
        localStorage.setItem('jwt', data.jwt)
        this.router.navigateByUrl('/anomalies')
      },
      error: (err) => {
        this.showError = true;
        this.errorMessage = 'Username/Password not valid';
      }})
    
  }
}
