package com.kv.model.dto;

import com.kv.enums.HostOS;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PingTriggerRequestBody {
    private String hostIpAddress;
    private String targetIpAddress;
    private boolean isManualTrigger; //mostly not needed
    private HostOS hostOS;
    private int pingCount;

}
