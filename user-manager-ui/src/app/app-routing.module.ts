import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AddUserComponent } from './components/add-user/add-user.component';
import { EditUserComponent } from './components/edit-user/edit-user.component';
import { UsersComponent } from './components/users/users.component';
import { SigninComponent } from './components/signin/signin.component';
import { AuthGuard } from './auth/auth.guard';


const routes: Routes = [
  
  {
    path: 'signin',
    component: SigninComponent
  },
  {
    path: 'users',
    component: UsersComponent,
    // canActivate: [AuthGuard]
  },
  {
    path: 'users/add',
    component: AddUserComponent,
    // canActivate: [AuthGuard]
  },
  {
    path: 'users/edit/:id',
    component: EditUserComponent,
    // canActivate: [AuthGuard]
  },
  {
    path: 'logout',
    component: SigninComponent
    
  },

  {
    path: '',
    redirectTo: 'signin',
    pathMatch: 'full'
  }

  

];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
