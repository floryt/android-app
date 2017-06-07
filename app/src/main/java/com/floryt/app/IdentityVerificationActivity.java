package com.floryt.app;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.floryt.common.Common;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Locale;

import static com.floryt.app.R.id.dark;
import static com.floryt.app.R.id.mapView;

public class IdentityVerificationActivity extends AppCompatActivity implements OnMapReadyCallback  {
    HashMap<String, String> data;
    boolean selected;
    protected MapView map;

    @Override
    public void onResume() {
        super.onResume();
        if (map != null) {
            map.onResume();
        }
    }

    @Override
    public void onPause() {
        if (map != null) {
            map.onPause();
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (map != null) {
            try {
                map.onDestroy();
            } catch (NullPointerException e) {
                Log.e("IdentityVerification", "Error while attempting MapView.onDestroy(), ignoring exception", e);
            }
        }
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (map != null) {
            map.onLowMemory();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (map != null) {
            map.onSaveInstanceState(outState);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identity_verification);

        //noinspection unchecked
        data = (HashMap<String, String>) getIntent().getSerializableExtra("data");

        RelativeLayout computer = (RelativeLayout) findViewById(R.id.computer);
        ImageView computerIcon = (ImageView) computer.findViewById(R.id.icon);
        TextView computerName = (TextView) computer.findViewById(R.id.text_primary);
        TextView computerLocation = (TextView) computer.findViewById(R.id.text_secondary);

        computerIcon.setImageResource(R.drawable.ic_desktop_windows_white_24dp);
        computerName.setText(data.get("computerName"));
        computerLocation.setText(data.get("computerIp"));

        map = (MapView) findViewById(R.id.mapView);
        map.onCreate(savedInstanceState);
        map.getMapAsync(this);

        LinearLayout yesButton = (LinearLayout) findViewById(R.id.approve_button);
        LinearLayout noButton = (LinearLayout) findViewById(R.id.reject_button);

        final View.OnClickListener uploadPermission = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.approve_button:
                        uploadIdentityAnswer(true);
                        break;
                    case R.id.reject_button:
                        uploadIdentityAnswer(false);
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
        uploadIdentityAnswer(false);
        finish();
    }

    private void uploadIdentityAnswer(boolean isApproved){
        selected = true;
        Common.uploadIdentityAnswer(getApplicationContext(), isApproved, data.get("verificationUid"));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng location = new LatLng(Double.parseDouble(data.get("computerLatitude")), Double.parseDouble(data.get("computerLongitude")));

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 10));

        googleMap.addMarker(new MarkerOptions()
                .title("Computer location")
                .position(location));
    }
}