import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { AddUserComponent } from './components/add-user/add-user.component';
import { CardModule } from 'primeng/card';
import { InputTextModule } from 'primeng/inputtext';
import { FloatLabelModule } from 'primeng/floatlabel';
import { FormsModule } from '@angular/forms';
import { ReactiveFormsModule } from '@angular/forms';
import { ButtonModule } from 'primeng/button';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { CustomIntInterceptor } from './interceptors/custom-int.interceptor';
import { MultiSelectModule } from 'primeng/multiselect';
import { MessagesModule } from 'primeng/messages';
import { MessageModule } from 'primeng/message';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { UserValidatorDirective } from './directives/user-validator.directive';
import { EditUserComponent } from './components/edit-user/edit-user.component';
import { UsersComponent } from './components/users/users.component';
import { SigninComponent } from './components/signin/signin.component';
import { MenuBarComponent } from './components/menu-bar/menu-bar.component';
import { MenubarModule } from 'primeng/menubar';
import {CheckboxModule} from 'primeng/checkbox';
import { TableModule } from 'primeng/table';
import { ConfirmPopupModule } from 'primeng/confirmpopup';
import { HeadingPageComponent } from './heading-page/heading-page.component';
import { ToastModule } from 'primeng/toast';




@NgModule({
  declarations: [
    AppComponent,
    AddUserComponent,
    UserValidatorDirective,
    EditUserComponent,
    UsersComponent,
    SigninComponent,
    MenuBarComponent,
    HeadingPageComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    CardModule,
    InputTextModule,
    FormsModule,
    ButtonModule,
    HttpClientModule,
    MultiSelectModule,
    ReactiveFormsModule,
    BrowserAnimationsModule,
    FloatLabelModule,
    MessagesModule,
    MessageModule,
    MenubarModule,
    CheckboxModule,
    TableModule,
    ConfirmPopupModule,
    ToastModule
    
  ],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: CustomIntInterceptor,
      multi: true
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
