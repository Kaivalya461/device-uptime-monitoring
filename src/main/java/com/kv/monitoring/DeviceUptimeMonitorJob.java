package com.kv.monitoring;

import com.kv.dao.FileStorage;
import com.kv.dao.InfluxDbDao;
import com.kv.dao.InfluxDbDaoImpl;
import com.kv.model.PingDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Service
public class DeviceUptimeMonitorJob {
    @Autowired private WindowsPingService windowsPingService;
    @Autowired private FileStorage<PingDetails, String> fileStorage;
//    @Autowired private InfluxDbDao<PingDetails> influxDbDao;

    public void initiateMonitorJob (boolean isManualJob, String targetIpAddress, int pingCount) {
        PingDetails pingDetails = new PingDetails();
        try {
            pingDetails.setHostIpAddress(InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException unknownHostException) {
            System.out.println("Exception at DUMJ::initiateMonitorJob with errorMessage: " + unknownHostException.getMessage());
            pingDetails.setHostIpAddress("0.0.0.0");
        }
        pingDetails.setPingDetailsId((int) (Math.random() * 1000));
        pingDetails.setTargetIpAddress(targetIpAddress);
        pingDetails.setStartTimestamp(System.currentTimeMillis());
        if(isManualJob) {
            windowsPingService.ping(pingDetails, null, targetIpAddress, pingCount);
            fileStorage.save(pingDetails, pingDetails.getTargetIpAddress() + "_" + pingDetails.getStartTimestamp());
//            influxDbDao.save(pingDetails);
        }
    }
}
