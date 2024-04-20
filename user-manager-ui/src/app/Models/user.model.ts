import { endWith } from "rxjs";

export class User {
    constructor(
      public id: string,
      public name: string,
      public surname: string,
      public username: string,
      public email: string,
      public authorities: [],
      public enabled: boolean,
      private _token: string,
      public password?: string, 
    ) {
    }
  
    get token() {
      return this._token
    }
  }