package com.ds.protocol;

import lombok.Data;

import java.io.Serializable;

@Data
public class HeartbeatResponse implements Serializable {

    private boolean success;
    private String message;
    private Long serverTimestamp;
}
