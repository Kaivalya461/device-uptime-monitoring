package com.kv.monitoring;

import com.kv.dao.InfluxDbDao;
import com.kv.dao.InfluxDbDaoImpl;
import com.kv.model.PingDetails;
import com.kv.model.PingTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Instant;

@Service
public class WindowsPingService {

    @Autowired private InfluxDbDaoImpl influxDbDaoImpl;

    private static final short MAX_PING_COUNT = 3600;
    //Reply from 127.0.0.1: bytes=32 time<1ms TTL=128

    public void ping(PingDetails pingDetails, String hostIpAddress, String targetIpAddress, int pingCount) {
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
//            PingTest.PINGING_ENABLED = true;

            in = new BufferedReader(new
                    InputStreamReader(p.getInputStream()));
            String inputLine;
            while (((inputLine = in.readLine()) != null)) {
                long timeStamp = System.currentTimeMillis();
                inputLine += " TimestampInMillis=" + timeStamp;
                System.out.println(inputLine);
                pingResult += inputLine;
                if(inputLine.contains("time")) {
                    PingTime pingTime = new PingTime(Instant.ofEpochMilli(timeStamp), fetchPingTimeFromString(inputLine));
                    influxDbDaoImpl.savePingTimes(pingDetails.getTargetIpAddress(), pingDetails.getHostIpAddress(), pingTime);
                    pingDetails.getPingTimeList().add(pingTime);
                }
            }
            in.close();

        } catch (IOException e) {
//            PingTest.PINGING_ENABLED = false;
            System.out.println(e);
        } finally {
            //Add logic to save PingDetails data
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
