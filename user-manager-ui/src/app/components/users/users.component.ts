import { Component } from '@angular/core';
import { UserService } from '../../services/user.service';
import { User } from '../../Models/user.model';
import { environment } from '../../../environment/environment';
import { Router } from '@angular/router';

interface Column{
  field: string;
  header: string;
}

@Component({
  selector: 'app-users',
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.css']
})
export class UsersComponent {
  users: User[]
  cols: Column[];
  resp: any;

  constructor(private userService: UserService, private router: Router) {}

  ngOnInit(): void{
    this.cols = [
      { field: 'id', header: 'ID'},
      { field: 'firstName', header: 'Name'},
      { field: 'lastName', header: 'Surname'},
      { field: 'username', header: 'Username'},
      { field: 'email', header: 'Email'},
      { field: 'roles', header: 'Roles'},
      { field: 'actions', header: 'Actions'},
    ]
    this.resp = this.userService.getUsers(`${environment.usersUrl}`).subscribe({
      next: (data: any) => {
        this.users = data.users
        
      }
    })
  }

  onClick(){
    this.router.navigateByUrl('users/edit')
  }
}
