package com.kv.controller;

import com.kv.dto.ActivePingJobResponseDto;
import com.kv.model.dto.PingTriggerRequestBody;
import com.kv.service.ActivePingJobService;
import com.kv.service.ManualTriggerService;
import io.reactivex.annotations.NonNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;

@RestController()
@RequestMapping(value = "/ping-service")
@Log4j2
public class ManualTriggerController {
    @Autowired private ManualTriggerService manualTriggerService;

    /**
     * This Controller method is Blocking ping service
    * */
    @GetMapping("/manual-trigger")
    public String manualTriggerSyncVersion(@RequestParam String targetIpAddress, @RequestParam int pingCount) {
        manualTriggerService.initiateSingleJobSyncVersion(true, targetIpAddress, pingCount);
        return "Completed";
    }

    /**
    * This controller method is used for Triggering multiple Ping services at once
    * */
    @PostMapping("/manual-batch-trigger")
    public String manualBatchTrigger(@RequestBody Set<String> targetIpAddressSet, @RequestParam int pingCount) {
        manualTriggerService.initiateBatchJob(targetIpAddressSet, pingCount);
        return "Successfully invoked manual ping job for all IP Addresses Set";
    }

    @PostMapping("/manual-trigger")
    public String manualTrigger(@RequestBody PingTriggerRequestBody requestBody) {
        manualTriggerService.initiateSingleJob(
                        requestBody.isManualTrigger(), requestBody.getTargetIpAddress(), requestBody.getPingCount());
        return "Successfully invoked manual ping job for TargetIP: " + requestBody.getTargetIpAddress() + " with PingCount: " + requestBody.getPingCount();
    }

    @PutMapping("/manual-stop/job-id/{threadId}")
    public String manualStop(@PathVariable @NonNull long threadId) {
        //kill the running thread based on threadId
        Thread.getAllStackTraces().keySet()
                .stream()
                .filter(t -> t.getId() == threadId)
                .findAny()
                .ifPresent(Thread::interrupt);

        return "Successfully stopped ping job for JobId: " + threadId;
    }

    @PutMapping("/manual-stop/all-jobs")
    public String manualStop() {
        //kill the running thread based on threadId
        Thread.getAllStackTraces().keySet()
                .stream()
                .filter(t -> ActivePingJobService.activePingJobMap
                        .values()
                        .stream().map(ActivePingJobResponseDto::getJobId).collect(Collectors.toList())
                        .contains(t.getId()))
                .collect(Collectors.toList())
                .forEach(Thread::interrupt);

        return "Successfully stopped ping job for All Ping Jobs";
    }
}
