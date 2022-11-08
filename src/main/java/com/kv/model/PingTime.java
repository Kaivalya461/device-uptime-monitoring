package com.kv.model;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Measurement(name = "PING_TIME")
public class PingTime {
    @Column(tag = true)
    private int pingTimeId; //PK, added for keeping some relation between PingTime and PingDetails
    @Column(timestamp = true)
    private Instant timestamp;
    @Column
    private int time; //Can be moved to a List of elements with these sub-elements - PingTime, Timestamp

    public PingTime(Instant timestamp, int time) {
        this.pingTimeId = (int) (Math.random() * 10000000);
        this.timestamp = timestamp;
        this.time = time;
    }
}
