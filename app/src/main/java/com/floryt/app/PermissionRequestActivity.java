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

public class PermissionRequestActivity extends AppCompatActivity {

    HashMap<String, String> data;
    boolean selected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission_request);
        //noinspection unchecked
        data = (HashMap<String, String>) getIntent().getSerializableExtra("data");

        final ImageView guestImageImageView = (ImageView) findViewById(R.id.guestIcon);
        TextView guestNameTextView = (TextView) findViewById(R.id.guestName);
        TextView guestEmailTextView = (TextView) findViewById(R.id.guestEmail);
        TextView computerName = (TextView)findViewById(R.id.computerName);
        TextView textLogo = (TextView)findViewById(R.id.text_logo);

        textLogo.setTypeface(Typeface.createFromAsset(getAssets(),  "fonts/CFDiamond.ttf"));

        Glide.with(this)
                .load(data.get("guestPhotoUrl"))
                .asBitmap()
                .centerCrop()
                .into(new BitmapImageViewTarget(guestImageImageView) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(getApplicationContext().getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        guestImageImageView.setImageDrawable(circularBitmapDrawable);
                    }
                });
        guestNameTextView.setText(data.get("guestName"));
        guestEmailTextView.setText(data.get("guestEmail"));

        Button yesButton = (Button) findViewById(R.id.yes_button);
        Button noButton = (Button) findViewById(R.id.noButton);

        final View.OnClickListener uploadPermission = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.yes_button:
                        uploadPermission(true);
                        break;
                    case R.id.noButton:
                    default:
                        uploadPermission(false);
                }
                finish();
            }
        };

        yesButton.setOnClickListener(uploadPermission);
        noButton.setOnClickListener(uploadPermission);

        computerName.setText(data.get("computerName"));
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(selected) return;
        uploadPermission(false);
        finish();
    }

    private void uploadPermission(boolean isApproved){
        // TODO add timeout
        selected = true;
        Common.uploadPermission(getApplicationContext(),
                isApproved,
                data.get("permissionUid"),
                data.get("computerUid"),
                data.get("guestUid"));
    }
}
