package com.kv.monitoring;

import com.kv.dto.ActivePingJobResponseDto;
import com.kv.enums.HostOS;
import com.kv.model.PingDetails;
import com.kv.service.ActivePingJobService;
import com.kv.service.LinuxPingService;
import com.kv.service.PingService;
import com.kv.service.WindowsPingService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Service
@Log4j2
public class DeviceUptimeMonitorJob {
    @Autowired private WindowsPingService windowsPingService;
    @Autowired private LinuxPingService linuxPingService;

    public void initiateSingleJob(boolean isManualJob, String targetIpAddress, int pingCount) {
        PingDetails pingDetails = new PingDetails();
        pingDetails.setHostIpAddress(getHostIpAddress());
        pingDetails.setPingDetailsId((int) (Math.random() * 1000));
        pingDetails.setTargetIpAddress(targetIpAddress);
        pingDetails.setStartTimestamp(System.currentTimeMillis());
        pingDetails.setPingCount(pingCount);

        if(isManualJob) {
            try {
                PingService pingService = getPingServiceImpl(getHostOS());
                log.info("=========############## Initiating ping service for: {} #############==============", pingDetails);
                populateActivePingJobMap(pingDetails);
                pingService.ping(pingDetails);
            } catch(Exception ex) {
                log.error("Exception at DUMJ::initiateMonitorJob for targetIP: {}, with ExceptionMessage: {}, StackTrace: {}", targetIpAddress, ex.getMessage(), ex.getStackTrace());
            } finally {
                ActivePingJobService.activePingJobMap.remove(pingDetails.getTargetIpAddress());
            }
        }
    }

    private String getHostIpAddress() {
        try {
            String hostIpAddress = InetAddress.getLocalHost().getHostAddress();
            log.info("Host OS: {} and it's IPAddress:{}", System.getProperty("os.name"), hostIpAddress);
            return hostIpAddress;
        } catch (UnknownHostException unknownHostException) {
            System.out.println("UnknownHostException at DUMJ::getHostIpAddress with errorMessage: " + unknownHostException.getMessage());
            return "0.0.0.0";
        }
    }

    private HostOS getHostOS() {
        String hostOS = System.getProperty("os.name");
        log.info("DUMJ::getHostOS Host OS: {}", hostOS);
        return HostOS.getHostOSBasedOnDisplayName(hostOS);
    }

    private PingService getPingServiceImpl(HostOS hostOS) throws Exception {
        switch (hostOS) {
            case WINDOWS_10:
            case WINDOWS_11:
                return windowsPingService;
            case LINUX:
                return linuxPingService;
            default:
                throw new Exception("OS Not supported");
        }
    }

    private void populateActivePingJobMap(PingDetails pingDetails) {
        ActivePingJobResponseDto activeJobObject = new ActivePingJobResponseDto();
        activeJobObject.setJobId(Thread.currentThread().getId());
        activeJobObject.setTargetIpAddress(pingDetails.getTargetIpAddress());
        activeJobObject.setHostIpAddress(pingDetails.getHostIpAddress());
        String stopLinkUrl = "/ping-service/manual-stop/job-id/" + activeJobObject.getJobId();
        activeJobObject.setStopLink(stopLinkUrl);

        ActivePingJobService.activePingJobMap.put(pingDetails.getTargetIpAddress(), activeJobObject);
    }
}
