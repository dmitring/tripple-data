import {Component, Input} from '@angular/core';
import {DataTableModule, SharedModule} from 'primeng/primeng';

import {TrippleDataJobEntity, TrippledataHashingAlgorithm, TrippleDataAddedJobInfo} from "../../trippledata-job-entity";
import {TrippledataJobsModel} from "../../trippledata-jobs-model";

@Component({
  selector: 'trippledata-jobs-table',
  templateUrl: 'trippledata-jobs-table.component.html'
})
export class TrippledataJobsTableComponent {
  private displayAddDialog:boolean = false;
  private addingUrl:string;
  private hashingAlgorithm;
  private algorithms = TrippledataHashingAlgorithm.statuses;

  constructor(private jobsModel:TrippledataJobsModel) {
    this.clearData();
  }


  private cancelJob(job:TrippleDataJobEntity) {
    this.jobsModel.cancelJob(job);
  }

  private showAddJobDialog() {
    this.displayAddDialog = true;
  }

  private addJob() {
    let job:TrippleDataAddedJobInfo = new TrippleDataAddedJobInfo(
      this.addingUrl,
      TrippledataHashingAlgorithm.getStatusByValue(this.hashingAlgorithm)
    );
    this.jobsModel.addJob(job);
    this.hideAddJobDialog();
  }

  private hideAddJobDialog() {
    this.displayAddDialog = false;
    this.clearData();
  }

  private clearData() {
    this.addingUrl = "";
    this.hashingAlgorithm = TrippledataHashingAlgorithm.SHA256.value;
  }
}
