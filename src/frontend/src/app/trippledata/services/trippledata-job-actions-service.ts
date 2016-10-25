import {Injectable}    from '@angular/core';
import {Headers, Http, Response} from '@angular/http';

import {endpoints} from '../../endpoints'

import {TrippleDataJobRequest} from "../trippledata-job-entity";
import {Observable} from "rxjs";
import "rxjs/Rx";

@Injectable()
export class TrippledataJobActionsService {
  private addJobUrl:string = endpoints.getAddJobUrl();
  private cancelJobUrl:string = endpoints.getCancelJobUrl();

  constructor(private http:Http) {
  }

  public addJob(jobRequest:TrippleDataJobRequest):Observable<any> {
    let body = JSON.stringify(jobRequest);
    let headers = new Headers({'Content-Type': 'application/json'});

    return this.http.post(this.addJobUrl, body, {headers: headers})
      .map((res:Response) => res.json())
  }

  public cancelJob(jobId: string):Observable<any> {
    let body = JSON.stringify(jobId);
    let headers = new Headers({'Content-Type': 'application/json'});

    return this.http.post(this.cancelJobUrl, body, {headers: headers})
      .map((res:Response) => res.json())
  }
}
