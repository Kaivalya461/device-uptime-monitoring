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
public class LinuxPingService implements PingService{
    @Autowired
    private InfluxDbDaoImpl influxDbDaoImpl;

    public void ping(PingDetails pingDetails) {
        try {
            pingService(pingDetails);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void pingService(PingDetails pingDetails) throws IOException {
        log.info("Inside LinuxPingService::ping method, TargetIP: {} ", pingDetails.getTargetIpAddress());
        //start ping to targetIpAddress
        String pingResult = "";
        BufferedReader in = null;
        if(pingDetails.getPingCount() > MAX_PING_COUNT) {
            pingDetails.setPingCount(MAX_PING_COUNT);
        }
        String pingCmd = "ping " + pingDetails.getTargetIpAddress() + " -c " + pingDetails.getPingCount();//in linux -c is used for ping count
        log.info("LinuxPingService::ping -> PingCommand for targetIP: {}, -> {}", pingDetails.getTargetIpAddress(), pingCmd);

        try {
            Runtime r = Runtime.getRuntime();
            Process p = r.exec(pingCmd);
            in = new BufferedReader(new
                    InputStreamReader(p.getInputStream()));
            String inputLine;

            while (((inputLine = in.readLine()) != null)) {
                if(Thread.interrupted()) {
                    return;
                } else {
                    long timeStamp = System.currentTimeMillis();
                    inputLine += " TimestampInMillis=" + timeStamp;
                    log.debug("Thread Details, ThreadID={} and ThreadInterrupted Status={}", Thread.currentThread().getId(), Thread.interrupted());
                    pingResult += inputLine;
                    if (inputLine.contains("time")) {
                        PingTime pingTime = new PingTime(Instant.ofEpochMilli(timeStamp), fetchPingTimeFromString(inputLine));
                        log.debug("Time = {}", pingTime.getTime());
                        try {
                            influxDbDaoImpl.savePingTimes(pingDetails.getTargetIpAddress(), pingDetails.getHostIpAddress(), pingTime);
                        } catch (Exception ex) {
                            log.error("Exception while saving PingTimes to InfluxDB for targetIP: {} with ExceptionMessage: {}, StackTrace: {}",
                                    pingDetails.getTargetIpAddress(), ex.getMessage(), ex.getStackTrace());
                        }
                        pingDetails.getPingTimeList().add(pingTime);
                    }
                }
            }

            in.close();
        } catch (IOException e) {
            log.error("Error in WindowsPingService::ping method, ErrorMessage: {}, TargetIP: {} ",
                    e.getMessage(), pingDetails.getTargetIpAddress());
        } finally {
            //Add logic to save PingDetails data
            if(in != null)
                in.close();
        }
    }

    //64 bytes from 45.33.15.213: icmp_seq=0 ttl=55 time=90.163 ms
    private int fetchPingTimeFromString(String pingOutput) {
        String pingTime = "";
        try {
            if (pingOutput.contains("time") && pingOutput.contains("ttl")) {
                pingTime = pingOutput.substring(pingOutput.indexOf("time") + 5); //output is 90.163 ms
                pingTime = pingTime.replaceAll("[^\\d.]", ""); //This regex, replaces all non digits characters except "." with blank spaces
                return (int) Double.parseDouble(pingTime);
            }
        } catch (NumberFormatException ex) {
            log.error("NumberFormatException at LinuxPingService::fetchPingTimeFromString for PingOutput String: {} with Exception: {}, ErrorMessage: {}",
                    pingOutput, ex.getClass(), ex.getMessage());
        }
        return 0;
    }
}
