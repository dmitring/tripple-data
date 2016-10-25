import {Injectable} from '@angular/core';
import {Http, Response, URLSearchParams} from '@angular/http';
import {Observable} from "rxjs/Rx";
import "rxjs/Rx";

import {endpoints} from '../../endpoints'

@Injectable()
export class TrippledataJobsService {
  private jobsUrl:string = endpoints.getJobsByClientIdAndStatusesUrl();

  constructor(private http:Http) {
  }

  public getData(clientId:string, statuses:string[]):Observable<any> {
    let params:URLSearchParams = new URLSearchParams();
    params.set('clientId', clientId);
    let statusParam = statuses
      .map(status => "alloweedStatuses=" + status)
      .join('&');
    params.appendAll(new URLSearchParams(statusParam));

    return this.http.get(this.jobsUrl, {search: params})
      .map((res:Response) => res.json())
  }
}
