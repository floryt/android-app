package com.floryt.common;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

/**
 * Created by Steven on 15/04/2017.
 */

public class Common {
    private static final Common ourInstance = new Common();

    public static Common getInstance() {
        return ourInstance;
    }

    private Common() {
    }

    public void saveToken(){ //TODO make static ?
        @SuppressWarnings("ConstantConditions") String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String token = FirebaseInstanceId.getInstance().getToken();
        FirebaseDatabase.getInstance().getReference("Users").child(uid).child("deviceToken").setValue(token);
    }

    public void removeToken(){ //TODO make static ?
        //noinspection ConstantConditions
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String token = FirebaseInstanceId.getInstance().getToken();
        if(token == null) return;
        // TODO add listeners
        FirebaseDatabase.getInstance().getReference("Users").child(uid).child("deviceToken").removeValue();
    }

    static public void uploadIdentityAnswer(Context context, boolean isApproved, String verificationUID){
        //noinspection ConstantConditions
        FirebaseDatabase.getInstance()
                .getReference("IdentityVerifications")
                .child(verificationUID)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .setValue(isApproved);
        Toast.makeText(context,
                isApproved? "Identity approved successfully" : "Identity was not approved"
                , Toast.LENGTH_SHORT).show();
    }

    public static void uploadPermission(final Context context, final boolean isPermitted, String permissionUID, String computerUID, String guestUID) {
        FirebaseDatabase.getInstance()
                .getReference("Permissions")
                .child(permissionUID)
                .child(computerUID)
                .child(guestUID)
                .setValue(isPermitted).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(context,
                            isPermitted? "Permission is given" : "Permission is denied"
                            , Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(context,
                            "Failed to send answer"
                            , Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
