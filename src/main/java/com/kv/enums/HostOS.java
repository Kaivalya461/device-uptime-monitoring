package com.kv.enums;

import java.util.Arrays;
import java.util.Optional;

public enum HostOS {
    WINDOWS_10("Windows 10"),
    WINDOWS_11("Windows 11"),
    LINUX("Linux"),
    UNIX("Unix"), //Untested
    MAC("Mac"); //Untested

    final String displayName;

    HostOS(String displayName) {
        this.displayName = displayName;
    }

    public static HostOS getHostOSBasedOnDisplayName(String displayName) {
        Optional<HostOS> hostOSOptional = Arrays.stream(values()).filter(x -> x.displayName.equals(displayName)).findFirst();
        if(hostOSOptional.isPresent()) {
            return hostOSOptional.get();
        } else {
            throw new EnumConstantNotPresentException(HostOS.class, displayName);
        }
    }
}
