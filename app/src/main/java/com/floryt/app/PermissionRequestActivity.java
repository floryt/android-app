package com.floryt.app;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.floryt.common.Common;

import java.util.HashMap;
import java.util.Locale;

public class PermissionRequestActivity extends AppCompatActivity {

    HashMap<String, String> data;
    boolean selected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission_request);
        //noinspection unchecked
        data = (HashMap<String, String>) getIntent().getSerializableExtra("data");

        RelativeLayout user = (RelativeLayout) findViewById(R.id.user);
        RelativeLayout computer = (RelativeLayout) findViewById(R.id.computer);


        final ImageView guestImageImageView = (ImageView) user.findViewById(R.id.icon);
        TextView guestNameTextView = (TextView) findViewById(R.id.text_primary);
        TextView guestEmailTextView = (TextView) findViewById(R.id.text_secondary);

        final ImageView computerIconImageView = (ImageView) computer.findViewById(R.id.icon);
        TextView computerNameTextView = (TextView) computer.findViewById(R.id.text_primary);
        TextView computerIpTextView = (TextView) computer.findViewById(R.id.text_secondary);

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

        computerIconImageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_desktop_windows_white_24dp));
        computerNameTextView.setText(data.get("computerName"));
        computerIpTextView.setText(data.get("computerIp").isEmpty() ? getString(R.string.ip_missing_message) : data.get("computerIp"));

        LinearLayout yesButton = (LinearLayout) findViewById(R.id.approve_button);
        LinearLayout noButton = (LinearLayout) findViewById(R.id.reject_button);

        final View.OnClickListener uploadPermission = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.approve_button:
                        uploadPermission(true);
                        break;
                    case R.id.reject_button:
                        uploadPermission(false);
                }
                finish();
            }
        };

        yesButton.setOnClickListener(uploadPermission);
        noButton.setOnClickListener(uploadPermission);

        LinearLayout countdown = (LinearLayout) findViewById(R.id.count_down);
        final TextView counter = (TextView) countdown.findViewById(R.id.counter);
        final LinearLayout progressBar = (LinearLayout) countdown.findViewById(R.id.progress_bar);
        final long timeLeft = (Long.parseLong(data.get("deadline"))*1000) - System.currentTimeMillis();

        progressBar.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                progressBar.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                final int totalWidth = progressBar.getMeasuredWidth();

                CountDownTimer Count = new CountDownTimer(timeLeft, 200) {
                    public void onTick(long millisUntilFinished) {
                        double percentage = (millisUntilFinished/1000) / 120.0;
                        ViewGroup.LayoutParams progressBarLayoutParams = progressBar.getLayoutParams();
                        progressBarLayoutParams.width = (int) ((double)totalWidth * percentage);
                        progressBar.setLayoutParams(progressBarLayoutParams);

                        progressBar.setBackgroundColor(Color.rgb((int) (255.0 * (1 - percentage)), (int) (255.0 * percentage), 0));

                        counter.setText(String.format(Locale.UK, "%d seconds remaining", millisUntilFinished / 1000));
                    }

                    public void onFinish() {
                        counter.setText(R.string.timeout_message);
                        finish();
                    }
                };
                Count.start();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(selected) return;
        uploadPermission(false);
        finish();
    }

    private void uploadPermission(boolean isApproved){
        selected = true;
        if (data.get("permissionUid") == null) return;
        Common.uploadPermission(getApplicationContext(),
                isApproved,
                data.get("permissionUid"),
                data.get("computerUid"),
                data.get("guestUid"));
    }
}
