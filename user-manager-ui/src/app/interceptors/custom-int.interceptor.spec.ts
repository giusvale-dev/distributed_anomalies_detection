import { TestBed } from '@angular/core/testing';

import { CustomIntInterceptor } from './custom-int.interceptor';

describe('CustomIntInterceptor', () => {
  beforeEach(() => TestBed.configureTestingModule({
    providers: [
      CustomIntInterceptor
      ]
  }));

  it('should be created', () => {
    const interceptor: CustomIntInterceptor = TestBed.inject(CustomIntInterceptor);
    expect(interceptor).toBeTruthy();
  });
});
