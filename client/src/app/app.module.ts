import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { HttpClientModule } from '@angular/common/http';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { ImageMapComponent } from './image-map/image-map.component';
import { LocationUpdateListingComponent } from './location-update-listing/location-update-listing.component';

@NgModule({
  declarations: [
    AppComponent,
    ImageMapComponent,
    LocationUpdateListingComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
