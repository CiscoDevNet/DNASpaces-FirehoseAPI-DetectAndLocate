import { Injectable } from '@angular/core';
import { Http, Response, Headers, URLSearchParams, RequestOptions } from '@angular/http';
import { Observable } from 'rxjs';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';

@Injectable()
export class LocationService {

  serverUrl: string = 'http://localhost:8887/';
  apiUrl : string = "http://localhost:9090/";
  apiKey : string = "{{API_KEY}}";

  constructor(
    private http: Http,
  ) {
  }

  private handleErrorObservable = (error: any) => {
    // console.log('Unauthorised', error.status == 401, error);
    return Observable.throw(error);
  }

  private extractData = (res: Response) => {
    // console.log('Response>>', res);
    return (res['_body'] !== "")? res.json() : null;
  }

  getLocationUpdate(macAddress: string): Observable<any> {
    let headers = new Headers();
    headers.append('Content-Type', "application/json");
    let opts = new RequestOptions();
    opts.headers = headers;
    return this.http.get(this.serverUrl + '?mac=' + macAddress)
      .map(this.extractData)
      .catch(this.handleErrorObservable);
  }

  getImageInfo(imageId : string):Observable<any>{
    let headers = new Headers();
    headers.append('Content-Type', "application/json");
    headers.append('X-API-KEY', this.apiKey);
    // let opts = new RequestOptions();
    // opts.headers = headers;
    return this.http.get(this.apiUrl+'api/partners/v1/maps/' + imageId,{ headers: headers })
      .map(this.extractData)
      .catch(this.handleErrorObservable);
  }

}    