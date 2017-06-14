package com.floryt.common;

import android.util.Pair;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Steven on 6/14/2017.
 */

public class Service {
    public static Task<Void> sendLockCommand(String computerUid) {
        return FirebaseDatabase.getInstance().getReference("Commands").child(computerUid).child(Common.getUid()).push().setValue(new Pair<>("lock", true));
    }

    public static Task<Void> screenshotCommand(String computerUid) {
        return FirebaseDatabase.getInstance().getReference("Commands").child(computerUid).child(Common.getUid()).push().setValue(new Pair<>("take_screenshot", true));
    }

    public static Task<Void> shutdownCommand(String computerUid) {
        return FirebaseDatabase.getInstance().getReference("Commands").child(computerUid).child(Common.getUid()).push().setValue(new Pair<>("shutdown", true));
    }

    public static Task<Void> messageCommand(String computerUid, String message) {
        return FirebaseDatabase.getInstance().getReference("Commands").child(computerUid).child(Common.getUid()).push().setValue(new Pair<>("present_message", message));
    }


}
