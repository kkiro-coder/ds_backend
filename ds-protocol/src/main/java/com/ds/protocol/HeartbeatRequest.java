package com.ds.protocol;

import lombok.Data;

import java.io.Serializable;

@Data
public class HeartbeatRequest implements Serializable {

    private String workerId;
    private String workerHost;
    private Integer workerPort;
    private Double cpuUsage;
    private Double memoryUsage;
    private Long timestamp;
}
