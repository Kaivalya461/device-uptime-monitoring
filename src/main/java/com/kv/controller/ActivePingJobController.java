package com.kv.controller;

import com.kv.dto.ActivePingJobResponseDto;
import com.kv.service.ActivePingJobService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(value = "/active-job")
@Log4j2
public class ActivePingJobController {

    @Autowired private ActivePingJobService activePingJobService;

    @GetMapping(value = "/all-jobs")
    public Map<String, ActivePingJobResponseDto> getAllActivePingJobsList() {
        log.info("TreadActiveCount: {}", Thread.activeCount());
        return activePingJobService.getAllActivePingJobMap();
    }
}
