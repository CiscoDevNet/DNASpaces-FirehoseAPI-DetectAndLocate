import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'app-location-update-listing',
  templateUrl: './location-update-listing.component.html',
  styleUrls: ['./location-update-listing.component.css']
})
export class LocationUpdateListingComponent implements OnInit {

  @Input()
  locationUpdates = [];

  constructor() { }

  ngOnInit() {
  }

}
