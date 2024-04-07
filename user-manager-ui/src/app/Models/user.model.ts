import { endWith } from "rxjs";

export class User {
    constructor(
      public id: string,
      public name: string,
      public surname: string,
      public username: string,
      public email: string,
      public roles: [],
      public enabled: boolean,
      private _token: string,
      public password?: string, 
      // public newPassword?: string  //TODO: se questi campi non devono essere presenti nel modello di User allora nel component ti servono due proprietà per il binding
    ) {
    }
  
    get token() {
      return this._token
    }
    
  
  }