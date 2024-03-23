import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { MenuItem } from 'primeng/api';
import { RouterModule } from '@angular/router';


@Component({
  selector: 'app-menu-bar',
  templateUrl: './menu-bar.component.html',
  styleUrls: ['./menu-bar.component.css']
})
export class MenuBarComponent {
  items: MenuItem[] | undefined;


  ngOnInit() {


    this.items = [
      {
        label: 'Users',
        icon: 'pi pi-fw pi-file',
        items: [
          {
            label: 'View users',
            icon: 'pi pi-fw pi-search',
          },
          {
            label: 'Add user',
            routerLink: ['/users/add'],
            icon: 'pi pi-fw pi-plus',
          },
        ]
      },
      {
        label: 'Hosts',
        icon: 'pi pi-fw pi-pencil',
        items: [
          {
            label: 'Manage hosts',
            icon: 'pi pi-fw pi-align-left',
          },
        ]
      },
      {
        label: 'Quit',
        routerLink: ['/logout'],
        icon: 'pi pi-fw pi-power-off'
      }
    ];
  }
}
