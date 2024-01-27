package com.kv.service;

import com.kv.monitoring.DeviceUptimeMonitorJob;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Service
@Log4j2
public class ManualTriggerService {
    @Autowired private DeviceUptimeMonitorJob deviceUptimeMonitorJob;

    public void initiateSingleJob(boolean isManualJob, String targetIpAddress, int pingCount) {
        CompletableFuture.runAsync(
                () -> deviceUptimeMonitorJob.initiateSingleJob(
                isManualJob, targetIpAddress, pingCount)
        );
    }

    public void initiateBatchJob(Set<String> targetIpAddressSet, int pingCount) {
        targetIpAddressSet
                .forEach(targetIpAddress -> initiateSingleJob(true, targetIpAddress, pingCount));
    }

    public void initiateSingleJobSyncVersion(boolean isManualJob, String targetIpAddress, int pingCount) {
        deviceUptimeMonitorJob.initiateSingleJob(
                isManualJob, targetIpAddress, pingCount
        );
    }
}
