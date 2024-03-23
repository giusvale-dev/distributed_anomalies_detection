import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HeadingPageComponent } from './heading-page.component';

describe('HeadingPageComponent', () => {
  let component: HeadingPageComponent;
  let fixture: ComponentFixture<HeadingPageComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [HeadingPageComponent]
    });
    fixture = TestBed.createComponent(HeadingPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
