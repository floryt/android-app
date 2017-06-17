package com.floryt.common;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

/**
 * Created by StevenD on 16/04/2017.
 */

public class Computer {
    private String name;
    private String status;
    private String lastUser;
    private String ip;
    private long lastSeen;
    private String latitude, longitude;
    private ArrayList<String> users;


    public Computer(String name, String status, String lastUser, String ip, long lastSeen, String latitude, String longitude) {
        this.name = name;
        this.status = status;
        this.lastUser = lastUser;
        this.ip = ip;
        this.lastSeen = lastSeen;
        this.latitude = latitude;
        this.longitude = longitude;

        users = new ArrayList<>();
        //noinspection ConstantConditions
        users.add(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
    }

    public Computer() {}

    public String getIp() {
        return ip;
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getUsers() {
        return users;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String createUsersString() {
        String usersString = "";

        for (int i = 0; i < users.size() - 1; i++){
            usersString += users.get(i) + ", ";
        }

        return usersString + users.get(users.size() - 1);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(long lastSeen) {
        this.lastSeen = lastSeen;
    }

    public String getLastUser() {
        return lastUser;
    }

    public void setLastUser(String lastUser) {
        this.lastUser = lastUser;
    }
}
