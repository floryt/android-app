package com.floryt.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;

public class LoadingActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        imageView = (ImageView)findViewById(R.id.logo);
        RotateAnimation rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(500);
        imageView.setAnimation(rotate);
        Thread authCheck = new Thread() {
            @Override
            public void run() {
                try {
                    super.run();
                    sleep(600);
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                } finally {
                    if (FirebaseAuth.getInstance().getCurrentUser() != null){
                        Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(mainActivity);
                        finish();
                    }else{
                        Intent loginActivity = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(loginActivity);
                        finish();
                    }
                }
            }
        };
        authCheck.start();
    }
}
