package com.floryt.common;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

/**
 * Created by StevenD on 16/04/2017.
 */

public class Computer {
    private String name;
    private ArrayList<String> users;

    public Computer(String name) {
        this.name = name;
        users = new ArrayList<>();
        //noinspection ConstantConditions
        users.add(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
    }

    public Computer() {}

    public String getName() {
        return name;
    }
    public ArrayList<String> getUsers() {
        return users;
    }

    public String createUsersString() {
        String usersString = "";

        for (int i = 0; i < users.size() - 1; i++){
            usersString += users.get(i) + ", ";
        }

        return usersString + users.get(users.size() - 1);
    }
}
