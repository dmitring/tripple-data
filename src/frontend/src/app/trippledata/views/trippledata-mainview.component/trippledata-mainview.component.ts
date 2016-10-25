import {Component, OnInit} from '@angular/core';

import {TrippledataJobsModel} from "../../trippledata-jobs-model";
import {TrippledataJobsTableComponent} from "../trippledata-jobs-table.component/trippledata-jobs-table.component";
import {TrippledataStatusFilterComponent} from "../trippledata-status-filter.component/trippledata-status-filter.component";
import {TrippledataErrorComponent} from "../trippledata-error.component/trippledata-error.component";

@Component({
  selector: 'trippledata-mainview',
  templateUrl: 'trippledata-mainview.component.html',
})
export class TrippledataMainview implements OnInit {
  constructor(private jobModel:TrippledataJobsModel) {
  }

  ngOnInit() {
    this.jobModel.getCurrentData();
  }
}
