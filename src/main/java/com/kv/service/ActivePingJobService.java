package com.kv.service;

import com.kv.dto.ActivePingJobResponseDto;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ActivePingJobService {
    //Map of all threads and targetIp
    public static Map<String, ActivePingJobResponseDto> activePingJobMap = new HashMap<>();

    public Map<String, ActivePingJobResponseDto> getAllActivePingJobMap() {
        return activePingJobMap;
    }
}
