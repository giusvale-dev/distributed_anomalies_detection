import { ChangeDetectorRef, Component, Input } from '@angular/core';
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
  message: string;
  severity: string;
  searchString: string;

  constructor(
  private userService: UserService,
  private router: Router,
  private confirmationService: ConfirmationService,
  private messageService: MessageService,
  ) {}

  ngOnInit(): void{
   
    this.message = null;
    
    this.cols = [
      { field: 'id', header: 'ID'},
      { field: 'firstName', header: 'Name'},
      { field: 'lastName', header: 'Surname'},
      { field: 'username', header: 'Username'},
      { field: 'email', header: 'Email'},
      { field: 'authorities', header: 'Roles'},
      { field: 'actions', header: 'Actions'},
    ]
    this.loadData();
  }

  loadData() {
    this.resp = this.userService.getUsers(`${environment.usersUrl}`).subscribe({
      next: (data: any) => {
        this.users = data
        this.userService.setUsersList(this.users );
      }
    });
  }

  goToTopPage() {
    const element = document.querySelector('#goUp');
    element.scrollIntoView();
  }

  onClick(id : string){
    this.router.navigateByUrl('users/edit/' + `${id}`);
  }

  cofirmDeleteDialog (event: Event, id: string) {
    this.confirmationService.confirm({
        target: event.target as EventTarget,
        message: 'Do you want to delete this record?',
        icon: 'pi pi-info-circle',
        acceptButtonStyleClass: 'p-button-danger p-button-sm',
        accept: () => {
            this.userService.deleteUser(id).subscribe({
              next: (data: any) => {
                this.message = 'User deleted';
                this.severity = 'info';
                this.loadData();
              },
              error: (err) => {
                this.message = err.message;
                this.severity = 'error';
              },
              complete: () => {
                this.goToTopPage();  
              }
             
            })
            
        }
    });
    
  }
}
