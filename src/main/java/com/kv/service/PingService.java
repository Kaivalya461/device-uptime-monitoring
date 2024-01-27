package com.kv.service;

import com.kv.model.PingDetails;

public interface PingService {
    static final short MAX_PING_COUNT = 3600;

    void ping(PingDetails pingDetails);
}
