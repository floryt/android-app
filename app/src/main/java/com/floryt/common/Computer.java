package com.floryt.common;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

/**
 * Created by StevenD on 16/04/2017.
 */

public class Computer {
    private String name;
    private String ownerUid;
    private ArrayList<String> users;

    public Computer(String ownerUid, String name) {
        this.ownerUid = ownerUid;
        this.name = name;
        users = new ArrayList<String>();
        users.add(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
    }

    public Computer() {}

    public String getName() {
        return name;
    }
    public String getOwnerUid() {
        return ownerUid;
    }
    public ArrayList<String> getUsers() {
        return users;
    }

    public String myGetUsersString() {
        String usersString = "";

        for (int i = 0; i < users.size() - 1; i++){
            usersString += users.get(i) + ", ";
        }

        return usersString + users.get(users.size() - 1);
    }
}
