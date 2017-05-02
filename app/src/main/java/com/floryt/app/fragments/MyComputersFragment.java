package com.floryt.app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.floryt.app.ComputerInfoActivity;
import com.floryt.app.R;
import com.floryt.common.Computer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Created by Steven on 16/04/2017.
 */

public class MyComputersFragment extends android.app.Fragment {
    private static final MyComputersFragment ourInstance = new MyComputersFragment();
    private FirebaseDatabase database;
    private DatabaseReference myComputers;



    public static MyComputersFragment getInstance() {
        return ourInstance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO add menu
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            default:
                break;
        }
        return false;
    }

    public MyComputersFragment() {
        database = FirebaseDatabase.getInstance();
        myComputers = database.getReference("Computers");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_computers_layout, container, false);
        getActivity().setTitle("My computers");
        Button saveButton = (Button) view.findViewById(R.id.save_computer_button);
        final EditText nameEditText = (EditText) view.findViewById(R.id.computer_name_edit_text);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameEditText.getText().toString();
                if (name.isEmpty()) {
                    nameEditText.setError("Name is empty");
                    return;
                }else if (false/*computerNameExists*/){
                    nameEditText.setError("Name is taken");
                }

                String randomUID = new BigInteger(130, new SecureRandom()).toString(32);
                myComputers.child(randomUID).setValue(new Computer(FirebaseAuth.getInstance().getCurrentUser().getUid(),name));
            }
        });

        final ListView computerListView = (ListView) view.findViewById(R.id.my_computes_list_view);
        computerListView.setAdapter(new FirebaseListAdapter<Computer>(getActivity(), Computer.class, R.layout.computer_item, myComputers){
            @Override
            protected void populateView(View v, final Computer computer, int position) {
                ((TextView) v.findViewById(R.id.computer_name)).setText(computer.getName());
                ((TextView) v.findViewById(R.id.computer_users)).setText(computer.createUsersString());
                ((ImageButton) v.findViewById(R.id.computer_item_button)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putStringArrayList("Users",computer.getUsers());
                        Intent computerInfoActivity = new Intent(getActivity(), ComputerInfoActivity.class);
                        computerInfoActivity.putExtra("Computer", computer.getName());
                        computerInfoActivity.putExtras(bundle);
                        startActivity(computerInfoActivity);
                    }
                });
            }
        });

        return view;
    }
}
