package com.floryt.app.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.floryt.app.R;
import com.floryt.common.ActivityLog;
import com.floryt.common.Common;
import com.floryt.common.Computer;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
        myRef = database.getReference("Users").child(Common.getUid());
        activityLogRef = myRef.child("ActivityLog");
        myComputersRef = myRef.child("computers");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dashboard_layout, container, false);
        getActivity().setTitle("Dashboard");
        final LinearLayout activityLogLayout = (LinearLayout) view.findViewById(R.id.activity_log_list);
        final LinearLayout myComputersLayout = (LinearLayout) view.findViewById(R.id.my_computers_list_layout);

        view.findViewById(R.id.activity_log_more_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.content, MyComputersFragment.getInstance()).commit();
            }
        });

        ValueEventListener activityLogValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                activityLogLayout.removeAllViews();
                for(DataSnapshot activityLogData : dataSnapshot.getChildren()){
                    ActivityLog activityLog = activityLogData.getValue(ActivityLog.class);

                    View item = inflater.inflate(R.layout.activity_log_item, null);
                    ((TextView)item.findViewById(R.id.computer_name)).setText(activityLog.getComputerName());
                    ((TextView)item.findViewById(R.id.computer_user)).setText(activityLog.getUser());
                    ((TextView)item.findViewById(R.id.computer_ip)).setText(activityLog.getIp());
                    item.findViewById(R.id.icon_options).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showPopupMenu(v);
                        }
                    });

                    activityLogLayout.addView(item);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //TODO implement on cancel
            }
        };
        activityLogRef.orderByChild("time").limitToFirst(3).addValueEventListener(activityLogValueEventListener);


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
                for(DataSnapshot computerData : dataSnapshot.getChildren()){
                    Computer computer = computerData.getValue(Computer.class);

                    View item = inflater.inflate(R.layout.my_computer_item, null);
                    item.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //TODO add implementation
                            Toast.makeText(DashboardFragment.getInstance().getContext(), ((TextView)v.findViewById(R.id.computer_name)).getText(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    ((TextView)item.findViewById(R.id.computer_name)).setText(computer.getName());

                    myComputersLayout.addView(item);
                }
                myComputersLayout.addView(inflater.inflate(R.layout.my_computer_show_all_item, null));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //TODO add implementation
            }
        };
        myComputersRef.addValueEventListener(myComputersValueEventListener);

        return view;
    }

    private void showPopupMenu(View view) {
        // inflate menu
        PopupMenu popup = new PopupMenu(view.getContext(),view );
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.activity_log_item_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new ActivityLogItemClickListener());
        popup.show();
    }
}

class ActivityLogItemClickListener implements PopupMenu.OnMenuItemClickListener {
    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.show_computer:
                //TODO add implementation
                Toast.makeText(DashboardFragment.getInstance().getContext(), "Show computer", Toast.LENGTH_SHORT).show();
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
