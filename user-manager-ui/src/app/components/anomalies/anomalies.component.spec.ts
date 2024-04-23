import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AnomaliesComponent } from './anomalies.component';

describe('AnomaliesComponent', () => {
  let component: AnomaliesComponent;
  let fixture: ComponentFixture<AnomaliesComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AnomaliesComponent]
    });
    fixture = TestBed.createComponent(AnomaliesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
