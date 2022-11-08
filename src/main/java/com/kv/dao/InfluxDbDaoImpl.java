package com.kv.dao;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.kv.model.PingDetails;
import com.kv.model.PingTime;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Repository
public class InfluxDbDaoImpl implements InfluxDbDao<PingTime> {
    private static final String token = "BdE4GHeMbHZZ46HrKdDcsl14QEuibNN6RtfR4THmO_TY08jOC7WJgdaw3DREdLic9KWP-4julRSwsUWHmyhswQ==";
    String bucket = "ping-details";
    String org = "kv";
    InfluxDBClient client = InfluxDBClientFactory.create("http://192.168.0.101:8086", token.toCharArray());


    @Override//This function is in-dev
    public void save(PingTime pingTime) {
        WriteApiBlocking writeApi = client.getWriteApiBlocking();

        Map<String, String> tagsToAdd = new HashMap<>();
//        tagsToAdd.put("targetHostName", pingDetails.getTargetIpAddress());

        Point point = Point.measurement("PING_TIME")
                .addTags(tagsToAdd)
                .addField("randomNo", (int) (Math.random() * 100));
//                .time(Instant.ofEpochMilli(pingDetails.getStartTimestamp()), WritePrecision.MS);
//        writeApi.writePoint(bucket, org, point);

//        client.close();
    }

    public void savePingTimes(String targetIpAddress, String hostIpAddress, PingTime pingTime) {
        WriteApiBlocking writeApi = client.getWriteApiBlocking();

        Map<String, String> tagsToAdd = new HashMap<>();
        tagsToAdd.put("targetIpAddress", targetIpAddress);
        tagsToAdd.put("hostIpAddress", hostIpAddress);

        Point point = Point.measurement("PING_TIME")
                .addTags(tagsToAdd)
                .addField("pingTime", pingTime.getTime())
                .time(pingTime.getTimestamp(), WritePrecision.MS);
        writeApi.writePoint(bucket, org, point);
    }
}

