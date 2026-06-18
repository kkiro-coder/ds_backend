package com.ds.protocol;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class TaskExecuteRequest implements Serializable {

    private String taskId;
    private String taskName;
    private String taskType;
    private Map<String, Object> params;
}
