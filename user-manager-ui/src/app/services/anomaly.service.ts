import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from '../../environment/environment';
import { Observable, map } from 'rxjs';



@Injectable({
  providedIn: 'root'
})
export class AnomalyService {

  constructor(
    private http: HttpClient,         
  ) { }

  getAnomalies(): Observable<any> {
    return this.http.get(`${environment.anomaliesUrl}`).pipe(map(result => result));
  }

  resetToGreen(id : string): Observable<any>{
    const httpOptions = {
      headers: new HttpHeaders({ 'Content-Type': 'application/json' })
    };
    return this.http.post(`${environment.resetToGreenUrl}`+ `/${ id }`, httpOptions).pipe(map(result => result));
  }
}
