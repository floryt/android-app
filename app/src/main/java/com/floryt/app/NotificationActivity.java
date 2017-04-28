package com.floryt.app;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import java.util.HashMap;

public class NotificationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        HashMap<String, String> data = (HashMap<String, String>) getIntent().getSerializableExtra("data");

        final ImageView userImageImageView = (ImageView) findViewById(R.id.userIcon);
        TextView userNameTextView = (TextView) findViewById(R.id.userName);
        TextView userEmailTextView = (TextView) findViewById(R.id.userEmail);

        Glide.with(this)
                .load(data.get("userPhotoUrl"))
                .asBitmap()
                .centerCrop()
                .into(new BitmapImageViewTarget(userImageImageView) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(getApplicationContext().getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        userImageImageView.setImageDrawable(circularBitmapDrawable);
                    }
                });
        userNameTextView.setText(data.get("userName"));
        userEmailTextView.setText(data.get("userEmail"));
    }

}
