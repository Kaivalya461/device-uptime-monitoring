package com.kv.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivePingJobResponseDto {
    private String targetIpAddress;
    private String hostIpAddress;
    private long jobId;
    private String stopLink;
}
