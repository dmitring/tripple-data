import {Injectable}    from '@angular/core';
import {Response} from "@angular/http";
import "rxjs/Rx";

import {TrippleDataJobEntity, TrippledataJobStatus, TrippleDataAddedJobInfo, TrippleDataJobRequest} from "./trippledata-job-entity";
import {TrippledataJobActionsService} from "./services/trippledata-job-actions-service";
import {TrippledataJobsService} from "./services/trippledata-jobs-service";
import {TrippleDataClientService} from "./services/tripple-data-client-service";
import {TrippledataErrorModel} from "./trippledata-error-model";

//TODO: display successful operations

@Injectable()
export class TrippledataJobsModel {
  jobs:TrippleDataJobEntity[];
  currentlySelectedStatuses:string[] = [
    TrippledataJobStatus.WAITING.value,
    TrippledataJobStatus.PROCESSING.value,
    TrippledataJobStatus.COMPLETED.value,
    TrippledataJobStatus.FAILED.value
  ];
  clientId:string = null;

  constructor(private jobActionsService:TrippledataJobActionsService,
              private jobsProviderService:TrippledataJobsService,
              private clientService:TrippleDataClientService,
              private errorModel:TrippledataErrorModel) {
  }

  public applyJob(job:TrippleDataJobEntity) {
    let jobIndex = this.jobs.findIndex(candidate => candidate.id == job.id);
    if (this.isJobStatusAlloweed(job)) {
      if (jobIndex == -1)
        this.jobs.unshift(job);
      else
        this.jobs[jobIndex] = job;
    } else {
      this.jobs.splice(jobIndex, 1);
    }
  }

  public getCurrentData() {
    this.getCurrentDataWithStatuses(this.currentlySelectedStatuses);
  }

  public getCurrentDataWithStatuses(statuses:string[]) {
    if (this.clientId == null)
      this.registerClient();
    else
      this.getJobsDataWithStatuses(statuses);
  }

  public areActionsAllowed():boolean {
    return this.clientId != null;
  }

  public addJob(addedJobInfo:TrippleDataAddedJobInfo) {
    if (!this.areActionsAllowed())
      return;

    let addingJobRequest:TrippleDataJobRequest = TrippleDataJobRequest.fromJobInfo(this.clientId, addedJobInfo);
    this.jobActionsService.addJob(addingJobRequest).subscribe(
      json => {
        let job:TrippleDataJobEntity = this.extractJob(json);
        this.applyJob(job);
      },
      error => this.errorModel.handleError(error),
      () => this.errorModel.handleSuccess()
    );
  }

  public cancelJob(job:TrippleDataJobEntity) {
    if (!this.areActionsAllowed())
      return;

    this.jobActionsService.cancelJob(job.id).subscribe(
      json => {
        console.log(json);
        let job:TrippleDataJobEntity = this.extractJob(json);
        this.applyJob(job);
      },
      error => this.errorModel.handleError(error),
      () => this.errorModel.handleSuccess()
    );
  }

  private registerClient() {
    this.clientService.registerClient().subscribe(
      (json:Response) => {
        this.clientId = json.json();
      },
      error => this.errorModel.handleError(error),
      () => {
        this.errorModel.handleSuccess();
        this.getCurrentData();
      });
  }

  private getJobsDataWithStatuses(statuses:string[]) {
    return this.getJobsDataWithClientIdStatuses(this.clientId, statuses);
  }

  private getJobsDataWithClientIdStatuses(clientId:string, statuses:string[]) {
    this.jobsProviderService.getData(clientId, statuses).subscribe(
      json => {
        console.log(json);
        this.jobs = json.map((rawJob:any) => this.extractJob(rawJob));
        this.currentlySelectedStatuses = statuses;
      },
      error => this.errorModel.handleError(error),
      () => this.errorModel.handleSuccess()
    );
  }

  private extractJob(rawJob:any):TrippleDataJobEntity {
    return TrippleDataJobEntity.fromJson(rawJob);
  }

  private isJobStatusAlloweed(job:TrippleDataJobEntity):boolean {
    return (this.currentlySelectedStatuses.find(status => status == job.status.value) != undefined);
  }
}
