package com.kv.model;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Measurement(name = "PING_DETAILS")
public class PingDetails {
    @Column(tag = true)
    private int pingDetailsId;
    @Column
    private String targetIpAddress; //PK
    @Column
    private long startTimestamp; //PK2
    @Column
    private String hostIpAddress;
    List<PingTime> pingTimeList = new ArrayList<>();

    PingDetails(String targetIpAddress, String hostIpAddress, List<PingTime> pingTimeList) {
        this.pingDetailsId = (int) (Math.random() * 10000000);
        this.targetIpAddress = targetIpAddress;
        this.startTimestamp = System.currentTimeMillis();
        this.hostIpAddress = hostIpAddress;
        this.pingTimeList = pingTimeList;
    }
}
