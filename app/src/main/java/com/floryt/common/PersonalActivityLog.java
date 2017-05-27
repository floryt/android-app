package com.floryt.common;

/**
 * Created by Steven on 5/26/2017.
 */

public class PersonalActivityLog{
    private String type;
    private String result;
    private String computerName;
    private long time;

    public PersonalActivityLog(String type, String result, String computerName, long time) {
        this.type = type;
        this.result = result;
        this.computerName = computerName;
        this.time = time;
    }

    public PersonalActivityLog() {}

    public String getType() {
        return type;
    }

    public String getResult() {
        return result;
    }

    public String getComputerName() {
        return computerName;
    }

    public long getTime() {
        return time;
    }


}
