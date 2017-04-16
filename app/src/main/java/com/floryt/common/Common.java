package com.floryt.common;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

/**
 * Created by StevenD on 15/04/2017.
 */

public class Common {
    private static final Common ourInstance = new Common();

    public static Common getInstance() {
        return ourInstance;
    }

    private Common() {
    }

    public void saveToken(){
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String token = FirebaseInstanceId.getInstance().getToken();
        FirebaseDatabase.getInstance().getReference("Users").child(uid).child("deviceToken").setValue(token);
    }

    public void removeToken(){
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String token = FirebaseInstanceId.getInstance().getToken();
        if(token == null) return;
        // TODO add listeners
        FirebaseDatabase.getInstance().getReference("Users").child(uid).child("deviceToken").removeValue();
    }
}
