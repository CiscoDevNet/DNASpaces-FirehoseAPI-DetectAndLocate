import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { LocationUpdateListingComponent } from './location-update-listing.component';

describe('LocationUpdateListingComponent', () => {
  let component: LocationUpdateListingComponent;
  let fixture: ComponentFixture<LocationUpdateListingComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ LocationUpdateListingComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LocationUpdateListingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
