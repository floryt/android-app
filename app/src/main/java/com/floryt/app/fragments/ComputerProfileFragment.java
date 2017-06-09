package com.floryt.app.fragments;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.MenuRes;
import android.support.transition.Visibility;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.floryt.app.R;
import com.floryt.common.Common;
import com.floryt.common.Computer;
import com.floryt.common.ComputerActivityLog;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class ComputerProfileFragment extends Fragment {
    private MapView map;

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
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.computer_profile_layout, container, false);
        final String computerUid = getArguments().getString("computerUid");
        assert computerUid != null;

        map = (MapView) view.findViewById(R.id.mapView);
        map.onCreate(savedInstanceState);
        FirebaseDatabase.getInstance().getReference("Users").child(Common.getUid()).child("computers").child(computerUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Computer computer = dataSnapshot.getValue(Computer.class);
                getActivity().setTitle(computer.getName());
                ((TextView)view.findViewById(R.id.computer_name)).setText(computer.getName());
                ((TextView)view.findViewById(R.id.computer_ip)).setText(computer.getIp() != null ? computer.getIp() : getString(R.string.missing_ip_address));

                if (computer.getLatitude() != null && computer.getLongitude() != null && !(computer.getLatitude().isEmpty() || computer.getLatitude().isEmpty())){
                    map.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            LatLng location = new LatLng(Double.parseDouble(computer.getLatitude()), Double.parseDouble(computer.getLongitude()));

                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 10));

                            googleMap.addMarker(new MarkerOptions()
                                    .title("Computer location")
                                    .position(location));
                        }
                    });
                }else{
                    view.findViewById(R.id.title_location).setVisibility(View.GONE);
                    view.findViewById(R.id.mapViewCard).setVisibility(View.GONE);
                }

                LinearLayout ownersList = (LinearLayout) view.findViewById(R.id.administrators_list);
                ownersList.removeAllViews();
                View item = inflater.inflate(R.layout.administrator_card_item, null);
                item.findViewById(R.id.icon_options).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    showPopupMenu(v, new AdministratorItemClickListener(), R.menu.administrator_item_menu);
                    }
                });

                final ImageView image = ((ImageView)item.findViewById(R.id.image));
                Glide.with(getContext())
                    .load(Common.getCurrentUser().getPhotoUrl())
                    .asBitmap()
                    .centerCrop()
                    .into(new BitmapImageViewTarget(image) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(getContext().getResources(), resource);
                            circularBitmapDrawable.setCircular(true);
                            image.setImageDrawable(circularBitmapDrawable);
                        }
                    });
                ((TextView)item.findViewById(R.id.name)).setText(Common.getCurrentUser().getDisplayName());
                ((TextView)item.findViewById(R.id.email)).setText(Common.getCurrentUser().getEmail());
                ownersList.addView(item);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load computer profile: " + databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

        final Drawable fingerPrintIcon = ContextCompat.getDrawable(getContext(), R.drawable.ic_fingerprint_black_24dp);
        final Drawable openLockIcon = ContextCompat.getDrawable(getContext(), R.drawable.ic_lock_open_black_24dp);
        final Drawable lockIcon = ContextCompat.getDrawable(getContext(), R.drawable.ic_lock_outline_black_24dp);

        final int greenColor = ContextCompat.getColor(getContext(), R.color.material_green_700);
        final int redColor = ContextCompat.getColor(getContext(), R.color.tw__composer_red);
        FirebaseDatabase.getInstance().getReference("Users").child(Common.getUid()).child("computers").child(computerUid).child("activityLog").orderByChild("negtime").limitToFirst(3).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                LinearLayout ownersList = (LinearLayout) view.findViewById(R.id.activity_log_list);
                ownersList.removeAllViews();
                for (DataSnapshot activityLogData: dataSnapshot.getChildren()){
                    ComputerActivityLog computerActivityLog = activityLogData.getValue(ComputerActivityLog.class);
                    View item = inflater.inflate(R.layout.activity_log_item, null);
                    Drawable icon = null;
                    int textColor = Integer.MAX_VALUE;
                    switch (computerActivityLog.getType()){
                        case "Identity verification":
                            icon = fingerPrintIcon;
                            switch (computerActivityLog.getResult()) {
                                case "Verified":
                                    textColor = greenColor;
                                    break;
                                case "Not verified":
                                    textColor = redColor;
                                    break;
                            }
                            break;
                        case "Permission request":
                            switch (computerActivityLog.getResult()) {
                                case "Permitted":
                                    icon = openLockIcon;
                                    textColor = greenColor;
                                    break;
                                case "Denied":
                                    icon = lockIcon;
                                    textColor = redColor;
                                    break;
                            }
                            break;
                    }

                    ((ImageView) item.findViewById(R.id.activity_log_icon)).setImageDrawable(icon);
                    ((TextView) item.findViewById(R.id.type)).setText(computerActivityLog.getType());
                    ((TextView) item.findViewById(R.id.result)).setText(computerActivityLog.getResult());
                    ((TextView) item.findViewById(R.id.result)).setTextColor(textColor);
                    if (computerActivityLog.getMessage() == null){
                        item.findViewById(R.id.message).setVisibility(View.GONE);
                    }else {
                        item.findViewById(R.id.message).setVisibility(View.VISIBLE);
                        ((TextView) item.findViewById(R.id.message)).setText(computerActivityLog.getMessage());
                    }
                    item.findViewById(R.id.computer_name).setVisibility(View.GONE);
                    ((TextView) item.findViewById(R.id.time)).setText(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.UK).format(new Date (computerActivityLog.getTime()*1000)));
                    ownersList.addView(item);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load computer activity log: " + databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

        FirebaseDatabase.getInstance().getReference("Users").child(Common.getUid()).child("computers").child(computerUid).child("activityLog").orderByChild("negtime").limitToFirst(1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildren().iterator().hasNext()){
                    ComputerActivityLog computerActivityLog = dataSnapshot.getChildren().iterator().next().getValue(ComputerActivityLog.class);
                    ((TextView)view.findViewById(R.id.computer_last_seen)).setText(new SimpleDateFormat("EEEE, d MMMM yyyy HH:mm", Locale.UK).format(new Date (computerActivityLog.getTime()*1000)));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load last seen data: " + databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }



    private void showPopupMenu(View view, PopupMenu.OnMenuItemClickListener onMenuItemClickListener, @MenuRes int menuRes) {
        // inflate menu
        PopupMenu popup = new PopupMenu(view.getContext(),view );
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(menuRes, popup.getMenu());
        popup.setOnMenuItemClickListener(onMenuItemClickListener);
        popup.show();
    }

    private class AdministratorItemClickListener implements PopupMenu.OnMenuItemClickListener {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.remove:
                    //TODO add implementation
                    Toast.makeText(DashboardFragment.getInstance().getContext(), "Remove", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.show_user:
                    //TODO add implementation
                    Toast.makeText(DashboardFragment.getInstance().getContext(), "Show user", Toast.LENGTH_SHORT).show();
                    return true;
                default:
            }
            return false;
        }
    }
}