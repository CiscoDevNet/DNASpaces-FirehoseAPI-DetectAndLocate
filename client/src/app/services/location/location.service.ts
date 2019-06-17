import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class LocationService {

  env = environment;

  constructor(private http: HttpClient) {}

  getLocationUpdate(macAddress: string): Observable<any> {
    let headers = new Headers();
    headers.append('Content-Type', "application/json");
    return this.http.get(this.env.serverUrl + '/api/v1/findmac?mac=' + macAddress);
  }

  getImageInfo(imageId : string):Observable<any>{
    let headers = new Headers();
    headers.append('Content-Type', "application/json");
    return this.http.get(this.env.apiUrl+'/api/partners/v1/maps/' + imageId);
  }

}
