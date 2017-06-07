package com.floryt.common;

/**
 * Created by Steven on 6/8/2017.
 */

public class ComputerActivityLog {
    private String type;
    private String result;
    private String message;
    private long time;

    public ComputerActivityLog(String type, String result, String message, long time) {
        this.type = type;
        this.result = result;
        this.message = message;
        this.time = time;
    }

    public ComputerActivityLog() {}

    public String getType() {
        return type;
    }

    public String getResult() {
        return result;
    }

    public String getMessage() {
        return message;
    }

    public long getTime() {
        return time;
    }
}
