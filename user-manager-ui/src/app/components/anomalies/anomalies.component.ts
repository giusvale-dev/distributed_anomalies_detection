import { Component } from '@angular/core';
import { environment } from '../../../environment/environment';
import { Router } from '@angular/router';
import { AnomalyService } from '../../services/anomaly.service';
import { Anomaly } from '../../Models/anomaly.model';
import { ConfirmationService, MessageService } from 'primeng/api';

interface Column{
  field: string;
  header: string;
}
@Component({
  selector: 'app-anomalies',
  templateUrl: './anomalies.component.html',
  styleUrls: ['./anomalies.component.css'],
  providers: [ConfirmationService, MessageService]
})
export class AnomaliesComponent {
  anomalies: Anomaly[];
  cols: Column[];
  resp: any;
  message: string;
  severity: string;
  searchString: string;

  constructor(
    private anomalyService: AnomalyService,
    private router: Router,
    private confirmationService: ConfirmationService,
    private messageService: MessageService,
  ) { }

  ngOnInit(): void{
  
    this.message = null;
    
    this.cols = [
      { field: 'id', header: 'ID'},
      { field: 'date', header: 'Date'},
      { field: 'description', header: 'Description'},
      { field: 'done', header: 'Done'},
      { field: 'hostname', header: 'Hostname'},
      { field: 'ipAddress', header: 'IP Address'},
      { field: 'hashCode', header: 'Hash Code(Base64 encoded)'},
      { field: 'actions', header: 'Actions'},
    ]
    this.loadData();
  }

  loadData() {
    this.resp = this.anomalyService.getAnomalies().subscribe({
      next: (data: any) => {
        this.anomalies = data;
        this.anomalies.forEach( item => {
          let form_date = new Date(item.date);
          item.formatted_date = form_date.toDateString();
        })
      }
    });
  }
  goToTopPage() {
    const element = document.querySelector('#goUp');
    element.scrollIntoView();
  }

  cofirmDeleteDialog (event: Event, id: string) {
    this.confirmationService.confirm({
        target: event.target as EventTarget,
        message: 'Do you want to reset this anomaly?',
        icon: 'pi pi-info-circle',
        acceptButtonStyleClass: 'p-button-success p-button-sm',
        accept: () => {
            this.anomalyService.resetToGreen(id).subscribe({
              next: (data: any) => {
                console.log(data);
                this.message = 'Anomalies resetted';
                this.severity = 'info';
                this.loadData();
              },
              error: (err) => {
                console.log(err);
                this.message = err.message;
                this.severity = 'error';
                this.loadData();
              },
              complete: () => {
                this.loadData();
              }
             
            })
            
        }
    });
  }

    

}
