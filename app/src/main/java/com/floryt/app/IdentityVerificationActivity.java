package com.floryt.app;

import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.floryt.common.Common;

import java.util.HashMap;

public class IdentityVerificationActivity extends AppCompatActivity {
    HashMap<String, String> data;
    boolean selected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identity_verification);
        //noinspection unchecked
        data = (HashMap<String, String>) getIntent().getSerializableExtra("data");

        final ImageView userImageImageView = (ImageView) findViewById(R.id.userIcon);
        TextView userNameTextView = (TextView) findViewById(R.id.userName);
        TextView userEmailTextView = (TextView) findViewById(R.id.userEmail);
        TextView textLogo = (TextView)findViewById(R.id.text_logo);

        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/CFDiamond.ttf");
        textLogo.setTypeface(custom_font);

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

        Button yesButton = (Button) findViewById(R.id.yesButton);
        Button noButton = (Button) findViewById(R.id.noButton);

        final View.OnClickListener uploadVerification = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean value;
                selected = true;
                switch (v.getId()) {
                    case R.id.yesButton:
                        uploadIdentityAnswer(true);
                        break;
                    case R.id.noButton:
                    default:
                        uploadIdentityAnswer(false);
                }
                finish();
            }
        };

        yesButton.setOnClickListener(uploadVerification);
        noButton.setOnClickListener(uploadVerification);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(selected) return;
        uploadIdentityAnswer(false);
        finish();
    }

    private void uploadIdentityAnswer(boolean isApproved){
        // TODO add timeout
        Common.uploadIdentityAnswer(getApplicationContext(), isApproved, data.get("verificationUID"));
    }
}