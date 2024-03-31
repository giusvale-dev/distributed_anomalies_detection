import { Component } from '@angular/core';
import { ConfirmationService, MessageService } from 'primeng/api';
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
  styleUrls: ['./users.component.css'],
  providers: [ConfirmationService, MessageService]
})
export class UsersComponent  {
  users: User[]
  cols: Column[];
  resp: any;

  constructor(private userService: UserService, private router: Router,private confirmationService: ConfirmationService, private messageService: MessageService) {}

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
        this.users = data
        
      }
    })
  }

  onClick(){
    this.router.navigateByUrl('users/edit')
  }
  confirm2(event: Event) {
    this.confirmationService.confirm({
        target: event.target as EventTarget,
        message: 'Do you want to delete this record?',
        icon: 'pi pi-info-circle',
        acceptButtonStyleClass: 'p-button-danger p-button-sm',
        accept: () => {
            this.messageService.add({ severity: 'info', summary: 'Confirmed', detail: 'Record deleted', life: 3000 });
        },
        reject: () => {
            this.messageService.add({ severity: 'error', summary: 'Rejected', detail: 'You have rejected', life: 3000 });
        }
    });
  }
}
