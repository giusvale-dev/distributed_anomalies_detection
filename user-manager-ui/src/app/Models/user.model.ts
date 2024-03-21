import { endWith } from "rxjs";

export class User{
    constructor(
        public id: string,
        public username: string,
        private _token: string
    ) {}

    get token(){
        return this._token
    }

}