import {Component} from '@angular/core';
import {TrippledataJobStatus} from "../../trippledata-job-entity";
import {TrippledataJobsModel} from "../../trippledata-jobs-model";

@Component({
  selector: 'trippledata-status-filter',
  templateUrl: 'trippledata-status-filter.component.html'
})
export class TrippledataStatusFilterComponent {
  private statuses = TrippledataJobStatus.statuses;
  private userSelectedStatuses:string[];

  constructor(private picturesModel:TrippledataJobsModel) {
    this.userSelectedStatuses = picturesModel.currentlySelectedStatuses.slice();
  }

  private applyStatusFilter() {
    console.log(this.picturesModel.currentlySelectedStatuses);
    console.log(this.userSelectedStatuses);
    this.picturesModel.getCurrentDataWithStatuses(this.userSelectedStatuses);
  }

  private isDisabled():boolean {
    return this.userSelectedStatuses.length < 1;
  }

  private getCurrentlySelected():string {
    return this.picturesModel.currentlySelectedStatuses
      .map(status => this.statuses.find(selectItem => selectItem.value == status).label)
      .join(', ');
  }
}
