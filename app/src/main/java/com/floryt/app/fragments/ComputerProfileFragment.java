package com.floryt.app.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.MenuRes;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.floryt.app.PhotoViewActivity;
import com.floryt.app.R;
import com.floryt.common.Common;
import com.floryt.common.Computer;
import com.floryt.common.ComputerActivityLog;
import com.floryt.common.Service;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StreamDownloadTask;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class ComputerProfileFragment extends Fragment {
    private MapView map;
    private String computerUid;

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
    public boolean onOptionsItemSelected(MenuItem item) {
        final OnCompleteListener<Void> onCompleteListener = new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                    Toast.makeText(getContext(), "Command was sent successfully", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getContext(), "Failed to sent command", Toast.LENGTH_SHORT).show();
            }
        };
        switch (item.getItemId()){
            case R.id.lock_command:
                Service.sendLockCommand(computerUid).addOnCompleteListener(onCompleteListener);
                break;
            case R.id.screenshot_command:
                Service.screenshotCommand(computerUid).addOnCompleteListener(onCompleteListener);
                break;
            case R.id.shutdown_command:
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                Service.shutdownCommand(computerUid).addOnCompleteListener(onCompleteListener);
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                Toast.makeText(getContext(), "Shutdown aborted", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
                break;
            case R.id.message_command:
                LayoutInflater li = LayoutInflater.from(getContext());
                View promptsView = li.inflate(R.layout.message_input_dialog, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                alertDialogBuilder.setView(promptsView);
                final EditText userInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        // get user input and set it to result
                                        // edit text
                                        String message = String.valueOf((userInput.getText()));
                                        if (message != null && !message.isEmpty()){
                                            Service.messageCommand(computerUid, message).addOnCompleteListener(onCompleteListener);
                                        }
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.cancel();
                                    }
                                });
                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                // show it
                alertDialog.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.commands_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (map != null) {
            map.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onStop() {
        if (getView() != null)
            ((SliderLayout)getView().findViewById(R.id.slider)).stopAutoCycle();
        super.onStop();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.computer_profile_layout, container, false);
        computerUid = getArguments().getString("computerUid");
        assert computerUid != null;
        setHasOptionsMenu(true);
        final SliderLayout sliderShow = (SliderLayout) view.findViewById(R.id.slider);
        populateScreenshots(view, sliderShow);
        sliderShow.setCustomIndicator((PagerIndicator) view.findViewById(R.id.floryt_indicator));

        view.findViewById(R.id.refresh_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                populateScreenshots(view, sliderShow);
            }
        });

        map = (MapView) view.findViewById(R.id.mapView);
        map.onCreate(savedInstanceState);
        FirebaseDatabase.getInstance().getReference("Users").child(Common.getUid()).child("computers").child(computerUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Computer computer = dataSnapshot.getValue(Computer.class);
                if (getActivity() == null){
                    return;
                }
                getActivity().setTitle(computer.getName());
                ((TextView)view.findViewById(R.id.computer_name)).setText(computer.getName());
                ((TextView)view.findViewById(R.id.computer_status)).setText(computer.getStatus() != null ? computer.getStatus().substring(0, 1).toUpperCase() + computer.getStatus().substring(1) : getString(R.string.unknown_status));
                ((TextView)view.findViewById(R.id.computer_ip)).setText(computer.getIp() != null ? computer.getIp() : getString(R.string.missing_ip_address));

                if (computer.getStatus() != null && Objects.equals(computer.getStatus().toLowerCase(), "logged in")){
                    ((TextView)view.findViewById(R.id.computer_status)).setTextColor(ContextCompat.getColor(getContext(), R.color.material_green_700));

                    if (computer.getLastUser() != null){
                        ((TextView)view.findViewById(R.id.computer_current_user)).setText(computer.getLastUser());
                        (view.findViewById(R.id.title_current_user)).setVisibility(View.VISIBLE);
                        (view.findViewById(R.id.computer_current_user)).setVisibility(View.VISIBLE);
                    }
                } else {
                    ((TextView)view.findViewById(R.id.computer_status)).setTextColor(ContextCompat.getColor(getContext(), R.color.tw__composer_red));
                }

                if (computer.getLastSeen() != 0){
                    ((TextView)view.findViewById(R.id.computer_last_seen)).setText(new SimpleDateFormat("EEEE, d MMMM yyyy HH:mm", Locale.UK).format(new Date (computer.getLastSeen()*1000)));
                } else {
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
                }


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
        return view;
    }

    private void populateScreenshots(final View parentView, final SliderLayout sliderShow) {
        final long ONE_MEGABYTE = 1024 * 1024;
        sliderShow.removeAllSliders();
        final StorageReference computerScreenshots = FirebaseStorage.getInstance().getReference().child("Screenshots").child(computerUid);
        computerScreenshots.child("index")
                .getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                int numberOfPictures = Integer.parseInt(new String(bytes, StandardCharsets.UTF_8));
                if (numberOfPictures > 0){
                    parentView.findViewById(R.id.title_screenshots).setVisibility(View.VISIBLE);
                    parentView.findViewById(R.id.screenshots_card).setVisibility(View.VISIBLE);
                    parentView.findViewById(R.id.slider).setVisibility(View.VISIBLE);
                    parentView.findViewById(R.id.floryt_indicator).setVisibility(View.VISIBLE);
                }
                StorageReference currentShot;
                for (int i = 0; i < numberOfPictures ; i++){
                    currentShot = computerScreenshots.child(String.format("%d.png", i));
                    currentShot.getMetadata().addOnCompleteListener(new OnCompleteListener<StorageMetadata>() {
                        @Override
                        public void onComplete(@NonNull Task<StorageMetadata> task) {
                            if (task.isSuccessful()){
                                final StorageMetadata storageMetadata = task.getResult();
                                if (storageMetadata.getDownloadUrl() == null)
                                    return;
                                TextSliderView textSliderView = new TextSliderView(getContext());
                                textSliderView
                                        .description(String.valueOf(new SimpleDateFormat("EEEE, d MMMM yyyy HH:mm", Locale.UK).format(new Date (storageMetadata.getCreationTimeMillis()))))
                                        .image(storageMetadata.getDownloadUrl().toString());
                                textSliderView.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                                    @Override
                                    public void onSliderClick(BaseSliderView slider) {
                                        startActivity(new Intent(getContext(), PhotoViewActivity.class).putExtra("uri", storageMetadata.getDownloadUrl()));
                                    }
                                });
                                sliderShow.addSlider(textSliderView);
                            } else {
                                try {
                                    Toast.makeText(getContext(),task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }catch (Exception e){}
                            }
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // No screenshots
            }
        });

    }
}