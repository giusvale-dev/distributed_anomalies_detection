import { endWith } from "rxjs";

export class Anomaly {
    constructor(
      public id: string,
      public date: Date,
      public formatted_date: string,
      public description: string,
      public done: boolean,
      public hostname: string,
      public ipAddress: string,
      public hashCode: string,
     
    ) {
    }
  
    
  }