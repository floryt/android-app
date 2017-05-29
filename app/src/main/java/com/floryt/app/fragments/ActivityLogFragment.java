package com.floryt.app.fragments;

import android.app.Fragment;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.floryt.app.R;
import com.floryt.common.Common;
import com.floryt.common.Computer;
import com.floryt.common.PersonalActivityLog;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Objects;

/**
 * Created by Steven on 5/26/2017.
 */

public class ActivityLogFragment extends Fragment{
    private static final ActivityLogFragment ourInstance = new ActivityLogFragment();
    private FirebaseDatabase database;
    private Query activityLogRef;

    public static ActivityLogFragment getInstance() {
        return ourInstance;
    }

    public ActivityLogFragment() {
        database = FirebaseDatabase.getInstance();
        activityLogRef = database.getReference("Users").child(Common.getUid()).child("activityLog").orderByChild("negtime");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_log_layout, container, false);
        getActivity().setTitle("Activity log");

        ListView activityLogListView = (ListView) view.findViewById(R.id.activity_log_list_view);
//        activityLogListView.setStackFromBottom(true);

        final Drawable fingerPrintIcon = ContextCompat.getDrawable(getContext(), R.drawable.ic_fingerprint_black_24dp);
        final Drawable openLockIcon = ContextCompat.getDrawable(getContext(), R.drawable.ic_lock_open_black_24dp);
        final Drawable lockIcon = ContextCompat.getDrawable(getContext(), R.drawable.ic_lock_outline_black_24dp);

        final int greenColor = ContextCompat.getColor(getContext(), R.color.material_green_700);
        final int redColor = ContextCompat.getColor(getContext(), R.color.tw__composer_red);


        final FirebaseListAdapter<PersonalActivityLog> activityLogListAdapter = new FirebaseListAdapter<PersonalActivityLog>(getActivity(), PersonalActivityLog.class, R.layout.activity_log_item, activityLogRef) {
            @Override
            protected void populateView(View v, final PersonalActivityLog personalActivityLog, final int position) {
                Drawable icon = null;
                int textColor = Integer.MAX_VALUE;
                switch (personalActivityLog.getType()){
                    case "Identity verification":
                        icon = fingerPrintIcon;
                        switch (personalActivityLog.getResult()) {
                            case "Verified":
                                textColor = greenColor;
                                break;
                            case "Not verified":
                                textColor = redColor;
                                break;
                        }
                        break;
                    case "Permission request":
                        switch (personalActivityLog.getResult()) {
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

                ((ImageView) v.findViewById(R.id.activity_log_icon)).setImageDrawable(icon);
                ((TextView) v.findViewById(R.id.type)).setText(personalActivityLog.getType());
                ((TextView) v.findViewById(R.id.result)).setText(personalActivityLog.getResult());
                ((TextView) v.findViewById(R.id.result)).setTextColor(textColor);
                if (personalActivityLog.getMessage() == null){
                    ((TextView) v.findViewById(R.id.message)).setVisibility(View.GONE);
                }else {
                    ((TextView) v.findViewById(R.id.message)).setVisibility(View.VISIBLE);
                    ((TextView) v.findViewById(R.id.message)).setText(personalActivityLog.getMessage());
                }
                if (personalActivityLog.getComputerName() == null){
                    ((TextView) v.findViewById(R.id.computer_name)).setVisibility(View.GONE);
                }else {
                    ((TextView) v.findViewById(R.id.computer_name)).setVisibility(View.VISIBLE);
                    ((TextView) v.findViewById(R.id.computer_name)).setText(String.format("On computer: %s", personalActivityLog.getComputerName()));
                }
                ((TextView) v.findViewById(R.id.time)).setText(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.UK).format(new java.util.Date (personalActivityLog.getTime()*1000)));
            }
        };
        activityLogListView.setAdapter(activityLogListAdapter);
        return view;
    }

}
