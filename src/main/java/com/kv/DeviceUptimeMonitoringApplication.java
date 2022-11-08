package com.kv;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class DeviceUptimeMonitoringApplication {
    public static void main( String[] args ) {
        SpringApplication.run(DeviceUptimeMonitoringApplication.class, args);
    }

    //Todo add scheduling to start ping-service
}
