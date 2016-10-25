import {trippledataUUID} from "./utils/trippledata-uuid";
export class TrippledataJobStatus {
  public value: string;
  public label: string;

  public static WAITING: TrippledataJobStatus = {value: "WAITING", label: "Waiting"};
  public static PROCESSING: TrippledataJobStatus = {value: "PROCESSING", label: "Processing"};
  public static COMPLETED: TrippledataJobStatus = {value: "COMPLETED", label: "Competed"};
  public static FAILED: TrippledataJobStatus = {value: "FAILED", label: "Failed"};
  public static CANCELED: TrippledataJobStatus = {value: "CANCELED", label: "Canceled"};

  public static getStatusByValue(statusValue: string): TrippledataJobStatus {
    return TrippledataJobStatus.statuses.find(jobStatus => jobStatus.value == statusValue);
  }

  public static statuses = [
    TrippledataJobStatus.WAITING,
    TrippledataJobStatus.PROCESSING,
    TrippledataJobStatus.COMPLETED,
    TrippledataJobStatus.FAILED,
    TrippledataJobStatus.CANCELED
  ];
}

export class TrippledataHashingAlgorithm {
  public value: string;
  public label: string;

  public static MD5: TrippledataHashingAlgorithm = {value: "MD5", label: "md5"};
  public static SHA1: TrippledataHashingAlgorithm = {value: "SHA1", label: "sha1"};
  public static SHA256: TrippledataHashingAlgorithm = {value: "SHA256", label: "sha256"};

  public static getStatusByValue(algValue: string): TrippledataHashingAlgorithm {
    return TrippledataHashingAlgorithm.statuses.find(alg => alg.value == algValue);
  }

  public static statuses = [
    TrippledataHashingAlgorithm.MD5,
    TrippledataHashingAlgorithm.SHA1,
    TrippledataHashingAlgorithm.SHA256
  ];
}

export class TrippleDataJobEntity {
  constructor(
    public id:string,
    public clientId:string,
    public sourceUri:string,
    public status:TrippledataJobStatus,
    public hashingAlgorithm:TrippledataHashingAlgorithm,
    public hexHash:string,
    public stackTrace:string,
    public arrivedTime:number,
    public startProcessingTime:number,
    public endProcessingTime:number,
    public totalWaitTime:number,
    public processingTime:number
  ) {

  }

  public static fromJson(json:any):TrippleDataJobEntity {
    return new TrippleDataJobEntity(
      json.id,
      json.clientId,
      json.sourceUri,
      TrippledataJobStatus.getStatusByValue(json.status),
      TrippledataHashingAlgorithm.getStatusByValue(json.hashingAlgorithm),
      json.hexHash,
      json.stackTrace,
      json.arrivedTime,
      json.startProcessingTime,
      json.endProcessingTime,
      parseInt((json.totalWaitTime / 1000000.0).toFixed(2)),
      parseInt((json.processingTime / 1000000.0).toFixed(2))
    );
  }

  public isCancellable() : boolean {
    return (this.status != TrippledataJobStatus.CANCELED);
  }
}

export class TrippleDataAddedJobInfo {
  constructor(
    public sourceUri:string,
    public hashingAlgorithm:TrippledataHashingAlgorithm
  ) {
  }
}

export class TrippleDataJobRequest {
  constructor(
    public id:string,
    public clientId:string,
    public sourceUri:string,
    public hashingAlgorithm:string
  ) {

  }

  static fromJobInfo(clientId:string, jobInfo:TrippleDataAddedJobInfo):TrippleDataJobRequest {
    return new TrippleDataJobRequest(
      trippledataUUID.generateUUID(),
      clientId,
      jobInfo.sourceUri,
      jobInfo.hashingAlgorithm.value
    );
  }
}
