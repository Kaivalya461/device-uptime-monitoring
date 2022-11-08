package com.kv.monitoring;

import com.kv.DeviceUptimeMonitoringApplication;
import com.kv.model.PingDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

//@Service
@RestController("/ping-service")
public class ManualTrigger {
    @Autowired private DeviceUptimeMonitorJob deviceUptimeMonitorJob;

    @GetMapping("/manual-trigger")
    public String manualTrigger(@RequestParam String targetIpAddress, @RequestParam int pingCount) {
        deviceUptimeMonitorJob.initiateMonitorJob(true, targetIpAddress, pingCount);
        return "Completed";
    }
}
