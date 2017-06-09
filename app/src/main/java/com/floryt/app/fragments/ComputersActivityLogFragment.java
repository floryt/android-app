package com.floryt.app.fragments;

import android.app.Fragment;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.floryt.app.R;
import com.floryt.common.Common;
import com.floryt.common.Computer;
import com.floryt.common.ComputerActivityLog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.xmlpull.v1.XmlPullParser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Steven on 6/9/2017.
 */

public class ComputersActivityLogFragment extends Fragment {
    private static final ComputersActivityLogFragment ourInstance = new ComputersActivityLogFragment();
    private FirebaseDatabase database;

    public static ComputersActivityLogFragment getInstance() {
        return ourInstance;
    }

    public ComputersActivityLogFragment() {
        database = FirebaseDatabase.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.computer_activity_log_layout, container, false);
        getActivity().setTitle("Computer activity log");
        final Spinner spinner = (Spinner) view.findViewById(R.id.computers_spinner);
        final ListView computerActivityLogListView = (ListView) view.findViewById(R.id.computer_activity_log);

        final Drawable fingerPrintIcon = ContextCompat.getDrawable(getContext(), R.drawable.ic_fingerprint_black_24dp);
        final Drawable openLockIcon = ContextCompat.getDrawable(getContext(), R.drawable.ic_lock_open_black_24dp);
        final Drawable lockIcon = ContextCompat.getDrawable(getContext(), R.drawable.ic_lock_outline_black_24dp);

        final int greenColor = ContextCompat.getColor(getContext(), R.color.material_green_700);
        final int redColor = ContextCompat.getColor(getContext(), R.color.tw__composer_red);

//        computerActivityLogListView.setEmptyView(inflater.inflate(R.layout.a, null));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Pair<String, String> computer = (Pair<String, String>) parent.getItemAtPosition(position);
                computerActivityLogListView.setAdapter(
                        new FirebaseListAdapter<ComputerActivityLog>(getActivity(),
                                ComputerActivityLog.class,
                                R.layout.activity_log_item,
                                database.getReference("Computers").child(computer.second).child("activityLog").orderByChild("negtime")) {
                    @Override
                    protected void populateView(View item, ComputerActivityLog computerActivityLog, int position) {
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
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        database.getReference("Users").child(Common.getUid()).child("computers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Create an ArrayAdapter using the string array and a default spinner layout
                ArrayAdapter<Pair<String, String>> adapter = new ArrayAdapter<Pair<String, String>>(getContext(), android.R.layout.simple_spinner_item){};
                for (DataSnapshot computerDataSnapshot : dataSnapshot.getChildren()){
//                    Computer computer = computerDataSnapshot.getValue(Computer.class);
//                    ComputerReference computerReference = new ComputerReference(computer.getName(), computerDataSnapshot.getKey());
                    Pair<String, String> computer = new Pair<String, String>(computerDataSnapshot.getValue(Computer.class).getName(), computerDataSnapshot.getKey()){
                        @Override
                        public String toString() {
                            return this.first;
                        }
                    };
                    adapter.add(computer);
                }
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                // Apply the adapter to the spinner
                spinner.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return view;
    }
}
