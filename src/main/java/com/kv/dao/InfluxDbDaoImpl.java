package com.kv.dao;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.kv.model.PingTime;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

@Repository
@Log4j2
public class InfluxDbDaoImpl implements InfluxDbDao<PingTime> {
    @Value("${kv.influxdb.url}")
    private String DB_URL;
    @Value("${kv.influxdb.token}")
    private String TOKEN;

    private final String PING_DETAILS_BUCKET_NAME = "ping-details";
    private final String INFLUX_DB_ORG_NAME = "kv-corp";

    @Override//This function is in-dev
    public void save(PingTime pingTime) {
        InfluxDBClient client = InfluxDBClientFactory.create(DB_URL, TOKEN.toCharArray());
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
        try (InfluxDBClient client = getInfluxDBClient(); client) {
            WriteApiBlocking writeApi = client.getWriteApiBlocking();

            Map<String, String> tagsToAdd = new HashMap<>();
            tagsToAdd.put("targetIpAddress", targetIpAddress);
            tagsToAdd.put("hostIpAddress", hostIpAddress);

            Point point = Point.measurement("PING_TIME")
                    .addTags(tagsToAdd)
                    .addField("pingTime", pingTime.getTime())
                    .time(pingTime.getTimestamp(), WritePrecision.MS);
            writeApi.writePoint(PING_DETAILS_BUCKET_NAME, INFLUX_DB_ORG_NAME, point);
        } catch (Exception exception) {
            log.error("InfluxDbDaoImpl::savePingTimes -> Exception while saving PingTimes into InfluxDB for targetIpAddress: {}, with errorMessage: {}, errorCause: {}, stackTrace: {}",
                    targetIpAddress,
                    exception.getMessage(),
                    exception.getCause(),
                    exception.getStackTrace()
            );
        }
    }

    private InfluxDBClient getInfluxDBClient() {
        log.debug("InfluxDbDaoImpl::getInfluxDBClient ----> Creating new InfluxDBClient, currentTimeInUtc: {}",
                ZonedDateTime.now(ZoneId.of("UTC")));
        return InfluxDBClientFactory.create(DB_URL, TOKEN.toCharArray());
    }
}

