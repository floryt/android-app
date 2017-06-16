package com.floryt.app.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.floryt.app.R;
import com.floryt.common.Common;
import com.floryt.common.Computer;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Steven on 16/04/2017.
 */

public class MyComputersFragment extends android.app.Fragment {
    private static final MyComputersFragment ourInstance = new MyComputersFragment();
    private FirebaseDatabase database;
    private DatabaseReference myComputersRef;

    public static MyComputersFragment getInstance() {
        return ourInstance;
    }

    public MyComputersFragment() {
        database = FirebaseDatabase.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myComputersRef = database.getReference("Users").child(Common.getUid()).child("computers");
        View view = inflater.inflate(R.layout.my_computers_layout, container, false);
        getActivity().setTitle("My Computers");

        ListView computerListView = (ListView) view.findViewById(R.id.my_computers_list_view);

        final FirebaseListAdapter<Computer> myComputersListAdapter = new FirebaseListAdapter<Computer>(getActivity(), Computer.class, R.layout.my_computer_list_item, myComputersRef) {
            @Override
            protected void populateView(View v, final Computer computer, final int position) {
                final String uid = getRef(position).getKey();
                ((TextView) v.findViewById(R.id.computer_name)).setText(computer.getName());
                ((TextView) v.findViewById(R.id.computer_users)).setText(computer.createUsersString());
                ((TextView) v.findViewById(R.id.computer_ip)).setText(computer.getIp());
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putString("computerUid", uid);
                        ComputerProfileFragment computerProfileFragment = new ComputerProfileFragment();
                        computerProfileFragment.setArguments(bundle);
                        getFragmentManager().beginTransaction().replace(R.id.content, computerProfileFragment).commit();
                    }
                });
            }
        };
        computerListView.setAdapter(myComputersListAdapter);

        return view;
    }
}
