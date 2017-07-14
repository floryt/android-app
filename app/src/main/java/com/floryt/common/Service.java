package com.floryt.common;

import android.util.Pair;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Steven on 6/14/2017.
 */

public class Service {
    public static Task<Void> sendLockCommand(String computerUid) {
        return FirebaseDatabase.getInstance().getReference("Commands").child(computerUid).child(Common.getUid()).push().setValue(new Pair<>("LockWorkstation", true));
    }

    public static Task<Void> screenshotCommand(String computerUid) {
        return FirebaseDatabase.getInstance().getReference("Commands").child(computerUid).child(Common.getUid()).push().setValue(new Pair<>("TakeScreenshot", true));
    }

    public static Task<Void> shutdownCommand(String computerUid) {
        return FirebaseDatabase.getInstance().getReference("Commands").child(computerUid).child(Common.getUid()).push().setValue(new Pair<>("Shutdown", true));
    }

    public static Task<Void> messageCommand(String computerUid, String message) {
        return FirebaseDatabase.getInstance().getReference("Commands").child(computerUid).child(Common.getUid()).push().setValue(new Pair<>("ShowMessage", message));
    }


}
