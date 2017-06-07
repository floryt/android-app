package com.floryt.common;

/**
 * Created by Steven on 6/7/2017.
 */

public class User {
    UserData profileInfo;

    public User(UserData profileInfo) {
        this.profileInfo = profileInfo;
    }

    public User(){

    }

    public UserData getProfileInfo() {
        return profileInfo;
    }
}
