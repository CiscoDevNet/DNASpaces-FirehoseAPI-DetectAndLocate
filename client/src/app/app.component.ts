import { Component, ViewChild } from '@angular/core';
import { LocationService } from './services/location/location.service';
import { ImageMapComponent } from './image-map/image-map.component';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {

  env = environment;
  @ViewChild('imgMap')
  imgMap: ImageMapComponent;
  markers: number[][] = [];
  hasMacAddress: boolean = false;
  macAddress: string = null;
  macAddressTemp: string = null;
  hasImageId: boolean = false;
  imageId: string = null;
  imageSrc: string = null;
  dimension =  {
      x: 1440,
      y: 793
    };

  recentLocation = {x:0,y:0};

  scaleX = 1;
  scaleY = 1;
  setTimeOutMillis = 5000;
  locationUpdates = [];

  constructor(private locationService: LocationService) { }

  ngOnInit() {

  }

  updateMarkers = () => {
    if (!this.macAddress)
      return;
    this.locationService.getLocationUpdate(this.macAddress).subscribe(
      data => {
        if(this.imageId != null){
          this.plotMarker(data);
        } else {
          this.getImageId(data);
        }
        setTimeout(this.updateMarkers, this.setTimeOutMillis);
      },
      error => {
        // console.log('error:: login', <any>error);
        setTimeout(this.updateMarkers, this.setTimeOutMillis);
      }
    );
  }

  plotMarker(data){
    if (data && data.deviceLocationUpdate && data.deviceLocationUpdate.xPos !== "NaN" && data.deviceLocationUpdate.yPos !== "NaN") {
      if(this.recentLocation.x == data.deviceLocationUpdate.xPos && this.recentLocation.y == data.deviceLocationUpdate.yPos)
        return;
      this.recentLocation.x = data.deviceLocationUpdate.xPos;
      this.recentLocation.y = data.deviceLocationUpdate.yPos;
      let percentX = (data.deviceLocationUpdate.xPos / this.dimension.x) * 100;
      let percentY = (data.deviceLocationUpdate.yPos / this.dimension.y) * 100;
      this.markers = [[percentX, percentY]];
      this.locationUpdates.push(data);
      this.imgMap.draw();
      // this.updateAP(data);
    }
  }

  getImageId(data){
    this.imageId = data.deviceLocationUpdate.mapId;
    if(this.imageId == null || this.imageId == "")
      this.imageId = "6d22e37b407cf201500b23f17ce45054";
      this.imageSrc = this.env.apiUrl+"/api/partners/v1/maps/"+this.imageId+"/image";
    this.locationService.getImageInfo(this.imageId).subscribe(
      imageInfo => {
        this.dimension.x = imageInfo.imageWidth;
        this.dimension.y = imageInfo.imageHeight;
        this.plotMarker(data);
      },
      error => {
        console.log('error:: getImageId', <any>error);
      }
    );
  }

  onMacKeyup = ($event: any) => {
    this.macAddressTemp = $event.target.value;
  }

  submit = ($event: any) => {
    if (this.macAddressTemp) {
      this.macAddress = this.macAddressTemp;
      this.hasMacAddress = true;
      this.updateMarkers();
      // this.clearMarkers({ "set": null });
    }
  }

  clearMarkers = ($event: any) => {
    this.markers = [];
    this.imgMap.draw();
  }

  onMark(marker: number[]) {
    //console.log('Markers', this.markers);
  }
  onChange(marker: number[]) {
    //console.log('Marker', marker);
  }
  selectMarker(index: number) {
    this.imgMap.markerActive = index;
    this.imgMap.draw();
  }
  removeMarker(index: number) {
    this.markers.splice(index, 1);
    if (index === this.imgMap.markerActive) {
      this.imgMap.markerActive = null;
    } else if (index < this.imgMap.markerActive) {
      this.imgMap.markerActive--;
    }
    this.imgMap.draw();
  }
}
