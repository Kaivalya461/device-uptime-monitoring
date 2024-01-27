package com.kv.service;

import com.kv.dao.InfluxDbDaoImpl;
import com.kv.model.PingDetails;
import com.kv.model.PingTime;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Instant;

@Service
@Log4j2
public class WindowsPingService implements PingService {

    @Autowired private InfluxDbDaoImpl influxDbDaoImpl;

    //Reply from 127.0.0.1: bytes=32 time<1ms TTL=128

    public void ping(PingDetails pingDetails) {
        try {
            ping(pingDetails, null, null, pingDetails.getPingCount());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void ping(PingDetails pingDetails, String hostIpAddress, String targetIpAddress, int pingCount) throws IOException {
        log.info("Inside WindowsPingService::ping method, TargetIP: {} ", pingDetails.getTargetIpAddress());
        //start ping to targetIpAddress
        String pingResult = "";
        BufferedReader in = null;
        if(pingCount > MAX_PING_COUNT) {
            pingCount = MAX_PING_COUNT;
        }
        String pingCmd = "ping " + pingDetails.getTargetIpAddress() + " -n " + pingCount;

        try {
            Runtime r = Runtime.getRuntime();
            Process p = r.exec(pingCmd);
            in = new BufferedReader(new
                    InputStreamReader(p.getInputStream()));
            String inputLine;

            while (((inputLine = in.readLine()) != null)) {
                if(Thread.interrupted()){
                    return;
                } else {
                    long timeStamp = System.currentTimeMillis();
                    inputLine += " TimestampInMillis=" + timeStamp;
                    log.debug("Thread Details, ThreadID={} and ThreadInterrupted Status={}", Thread.currentThread().getId(), Thread.interrupted());
                    pingResult += inputLine;
                    if(inputLine.contains("time")) {
                        PingTime pingTime = new PingTime(Instant.ofEpochMilli(timeStamp), fetchPingTimeFromString(inputLine));
                        log.debug("Time = {}", pingTime.getTime());
                        try {
                            influxDbDaoImpl.savePingTimes(pingDetails.getTargetIpAddress(), pingDetails.getHostIpAddress(), pingTime);
                        } catch(Exception ex) {
                            log.error("Exception while saving PingTimes to InfluxDB for targetIP: {} with ExceptionMessage: {}, StackTrace: {}",
                                    pingDetails.getTargetIpAddress(), ex.getMessage(), ex.getStackTrace());
//                            log.error("ExceptionStackTrace while saving PingTimes to InfluxDB for targetIP: {} with ExceptionMessage: {}", pingDetails.getTargetIpAddress(), ex.getStackTrace());
                        }
                        pingDetails.getPingTimeList().add(pingTime);
                    }
                }
            }

        } catch (IOException e) {
//            PingTest.PINGING_ENABLED = false;
            log.error("Error in WindowsPingService::ping method, ErrorMessage: {}, TargetIP: {} ",
                    e.getMessage(), pingDetails.getTargetIpAddress());
        } finally {
            //Add logic to save PingDetails data
            if(in != null)
                in.close();
        }
    }

    private int fetchPingTimeFromString(String pingOutput) {
        //Reply from 127.0.0.1: bytes=32 time<1ms TTL=128
        String pingTime = "";
        if(pingOutput.contains("time") && pingOutput.contains("TTL")) {
            pingTime = pingOutput.substring(pingOutput.indexOf("time")+5, pingOutput.indexOf("TTL"));
            pingTime = pingTime.replaceAll("(\\D)", "");
            return Integer.parseInt(pingTime);
        }
        return 0;
    }
}
