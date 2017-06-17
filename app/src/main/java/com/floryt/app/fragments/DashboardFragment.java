package com.floryt.app.fragments;

import android.app.Fragment;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.floryt.app.R;
import com.floryt.common.ActivityLog;
import com.floryt.common.Common;
import com.floryt.common.Computer;
import com.floryt.common.ComputerActivityLog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

/**
 * Created by Steven on 07/05/2017.
 */

public class DashboardFragment extends Fragment {
    private static final DashboardFragment ourInstance = new DashboardFragment();
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private DatabaseReference activityLogRef;
    private DatabaseReference myComputersRef;

    public static DashboardFragment getInstance() {
        return ourInstance;
    }

    public DashboardFragment() {
        database = FirebaseDatabase.getInstance();
    }


    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myRef = database.getReference("Users").child(Common.getUid());
        activityLogRef = myRef.child("activityLog");
        myComputersRef = myRef.child("computers");
        final View view = inflater.inflate(R.layout.dashboard_layout, container, false);
        getActivity().setTitle("Dashboard");
        final LinearLayout activityLogLayout = (LinearLayout) view.findViewById(R.id.activity_log_list);
        final LinearLayout myComputersLayout = (LinearLayout) view.findViewById(R.id.my_computers_list_layout);

        view.findViewById(R.id.activity_log_more_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.content, ActivityLogFragment.getInstance()).commit();
            }
        });

        final Drawable fingerPrintIcon = ContextCompat.getDrawable(getContext(), R.drawable.ic_fingerprint_black_24dp);
        final Drawable openLockIcon = ContextCompat.getDrawable(getContext(), R.drawable.ic_lock_open_black_24dp);
        final Drawable lockIcon = ContextCompat.getDrawable(getContext(), R.drawable.ic_lock_outline_black_24dp);

        final int greenColor = ContextCompat.getColor(getContext(), R.color.material_green_700);
        final int redColor = ContextCompat.getColor(getContext(), R.color.tw__composer_red);

        ValueEventListener activityLogValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                activityLogLayout.removeAllViews();
                for(DataSnapshot activityLogData : dataSnapshot.getChildren()){
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
                    ((TextView) item.findViewById(R.id.time)).setText(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.UK).format(new Date(computerActivityLog.getTime()*1000)));
                    activityLogLayout.addView(item);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), String.format("Failed to connect to internet: %s", databaseError.getMessage()), Toast.LENGTH_SHORT).show();
            }
        };
        activityLogRef.orderByChild("negtime").limitToFirst(3).addValueEventListener(activityLogValueEventListener);


        view.findViewById(R.id.my_computers_more_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.content, MyComputersFragment.getInstance()).commit();
            }
        });

        ValueEventListener myComputersValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                myComputersLayout.removeAllViews();
                View progressBar = view.findViewById(R.id.progress_bar_computers);
                if (progressBar != null){
                    ((ViewManager)progressBar.getParent()).removeView(progressBar);
                }
                for(final DataSnapshot computerData : dataSnapshot.getChildren()){
                    final Computer computer = computerData.getValue(Computer.class);

                    View item = inflater.inflate(R.layout.my_computer_card_item, null);
                    item.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Bundle bundle = new Bundle();
                            bundle.putString("computerUid", computerData.getKey());
                            ComputerProfileFragment computerProfileFragment = new ComputerProfileFragment();
                            computerProfileFragment.setArguments(bundle);
                            getFragmentManager().beginTransaction().replace(R.id.content, computerProfileFragment).commit();
                        }
                    });
                    ((TextView)item.findViewById(R.id.computer_name)).setText(computer.getName());
                    if (computer.getStatus() != null && Objects.equals(computer.getStatus().toLowerCase(), "logged in")){
                       (item.findViewById(R.id.status_circle)).setBackground(ContextCompat.getDrawable(getContext(), R.drawable.circle_green));
                    } else {
                        (item.findViewById(R.id.status_circle)).setBackground(ContextCompat.getDrawable(getContext(), R.drawable.circle_red));
                    }

                    myComputersLayout.addView(item);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to get computers", Toast.LENGTH_SHORT).show();
            }
        };
        myComputersRef.addValueEventListener(myComputersValueEventListener);

        return view;
    }
}

