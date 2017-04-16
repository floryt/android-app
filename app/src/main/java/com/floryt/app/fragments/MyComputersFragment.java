package com.floryt.app.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.floryt.app.R;
import com.floryt.common.Computer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Created by StevenD on 16/04/2017.
 */

public class MyComputersFragment extends android.app.Fragment {
    private static final MyComputersFragment ourInstance = new MyComputersFragment();
    private FirebaseDatabase database;
    private DatabaseReference myComputers;



    public static MyComputersFragment getInstance() {
        return ourInstance;
    }

    public MyComputersFragment() {
        database = FirebaseDatabase.getInstance();
        myComputers = database.getReference("Computes");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_computers_layout, container, false);
        Button saveButton = (Button) view.findViewById(R.id.save_computer_button);
        final EditText nameEditText = (EditText) view.findViewById(R.id.computer_name_edit_text);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameEditText.getText().toString();
                if (name.isEmpty()) {
                    nameEditText.setError("Text is empty");
                    return;
                }
                myComputers.child(new BigInteger(130, new SecureRandom()).toString(32)).setValue(new Computer(FirebaseAuth.getInstance().getCurrentUser().getUid(),name));
            }
        });

        FirebaseListAdapter<Computer> firebaseListAdapter = new FirebaseListAdapter<Computer>(getActivity(), Computer.class, R.layout.computer_item, myComputers){
            @Override
            protected void populateView(View v, Computer computer, int position) {
                ((TextView) v.findViewById(R.id.computer_name)).setText(computer.getName());
                ((TextView) v.findViewById(R.id.computer_users)).setText(computer.getUsersString());
            }
        };

        ListView computerListView = (ListView) view.findViewById(R.id.my_computes_list_view);
        computerListView.setAdapter(firebaseListAdapter);

        return view;
    }
}
