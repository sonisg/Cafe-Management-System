import { TestBed } from '@angular/core/testing';

import { TokenInterceptoInterceptor } from './token-intercepto.interceptor';

describe('TokenInterceptoInterceptor', () => {
  beforeEach(() => TestBed.configureTestingModule({
    providers: [
      TokenInterceptoInterceptor
      ]
  }));

  it('should be created', () => {
    const interceptor: TokenInterceptoInterceptor = TestBed.inject(TokenInterceptoInterceptor);
    expect(interceptor).toBeTruthy();
  });
});
