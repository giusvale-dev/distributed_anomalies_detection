import { Directive } from '@angular/core';
import {  AbstractControl, Validator, NG_VALIDATORS } from '@angular/forms';

@Directive({
  selector: '[appUserValidator]',
   providers: [{
      provide: NG_VALIDATORS,
      useExisting: UserValidatorDirective,
      multi: true
   }]
})
export class UserValidatorDirective implements Validator{
  validate(control: AbstractControl) : {[key: string]: any} | null {
    if (control.value && control.value.length != 10){
      return {'userNameInvalid': true}
    }
    return null;
  }
  

}
