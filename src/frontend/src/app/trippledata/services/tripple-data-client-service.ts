import {Injectable}    from '@angular/core';
import {Headers, Http} from '@angular/http';

import {endpoints} from '../../endpoints'

import {Observable} from "rxjs";
import "rxjs/Rx";
import {TrippleDataClientEntity} from "../trippledata-client-entity";

@Injectable()
export class TrippleDataClientService {
  private registerClientUrl:string = endpoints.getRegisterClientUrl();

  constructor(private http:Http) {
  }

  public registerClient():Observable<any> {
    let client: TrippleDataClientEntity = new TrippleDataClientEntity();
    let body = JSON.stringify(client.id);
    let headers = new Headers({'Content-Type': 'application/json'});

    return this.http.post(this.registerClientUrl, body, {headers: headers});
  }
}
