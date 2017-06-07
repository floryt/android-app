package com.floryt.common;

/**
 * Created by Steven on 6/7/2017.
 */

class UserData {
    String email, name, photoUrl;

    public UserData(String email, String name, String photoUrl) {
        this.email = email;
        this.name = name;
        this.photoUrl = photoUrl;
    }

    public UserData(){}

    public String getName() {
        return name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getEmail() {

        return email;
    }
}
