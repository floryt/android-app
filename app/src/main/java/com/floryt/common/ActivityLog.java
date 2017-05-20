package com.floryt.common;


/**
 * Created by Steven on 19/05/2017.
 */

public class ActivityLog {
    private String computerName;
    private String ip;
    private long time;
    private String user;

    public ActivityLog(String computerName, String user, String ip, long time) {
        this.computerName = computerName;
        this.user = user;
        this.ip = ip;
        this.time = time;
    }

    public ActivityLog() {}

    public String getComputerName() {
        return computerName;
    }

    public String getIp() {
        return ip;
    }

    public long getTime() {
        return time;
    }

    public String getUser() {
        return user;
    }
}
