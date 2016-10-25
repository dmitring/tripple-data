
import {trippledataUUID} from "./utils/trippledata-uuid";

export class TrippleDataClientEntity {
  id:string;

  constructor() {
    this.id = trippledataUUID.generateUUID()
  }
}
