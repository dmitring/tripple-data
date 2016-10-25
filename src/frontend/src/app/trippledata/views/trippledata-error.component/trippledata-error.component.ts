
import { Component } from '@angular/core';

import {Messages, Message} from 'primeng/primeng';

import {TrippledataErrorModel} from "../../trippledata-error-model";

@Component({
  selector: 'trippledata-error',
  templateUrl: 'trippledata-error.component.html'
})
export class TrippledataErrorComponent{
  constructor(private errorModel:TrippledataErrorModel) {
  }

  private getMessages() : Message[] {
    if (this.errorModel.error)
      return [{severity:'error', summary:'Connection error', detail:this.errorModel.error}]
    else
      return [];
  }
}
