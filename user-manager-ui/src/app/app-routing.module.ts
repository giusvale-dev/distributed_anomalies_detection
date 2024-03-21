import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AddUserComponent } from './components/add-user/add-user.component';
import { EditUserComponent } from './components/edit-user/edit-user.component';
import { UsersComponent } from './components/users/users.component';
import { SigninComponent } from './components/signin/signin.component';

const routes: Routes = [
  
  {
    path: 'signin',
    component: SigninComponent
  },
  {
    path: 'users',
    component: UsersComponent
  },
  {
    path: 'users/add',
    component: AddUserComponent
  },
  {
    path: 'users/edit',
    component: EditUserComponent
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
