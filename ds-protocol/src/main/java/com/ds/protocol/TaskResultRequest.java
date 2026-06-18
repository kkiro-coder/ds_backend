package com.ds.protocol;

import lombok.Data;

import java.io.Serializable;

@Data
public class TaskResultRequest implements Serializable {

    private String taskId;
    private String workerId;
    private boolean success;
    private String message;
    private String resultData;
    private Long executeTime;
    private Long finishTimestamp;
}
