import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';


import { AppComponent } from './app.component';
import { ImageMapComponent } from './image-map/image-map.component';
import { LocationService } from './services/location/location.service';
import { HttpModule } from '@angular/http';
import { LocationUpdateListingComponent } from './location-update-listing/location-update-listing.component';


@NgModule({
  declarations: [
    AppComponent,
    ImageMapComponent,
    LocationUpdateListingComponent
  ],
  imports: [
    BrowserModule,
    HttpModule
  ],
  providers: [
    LocationService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
