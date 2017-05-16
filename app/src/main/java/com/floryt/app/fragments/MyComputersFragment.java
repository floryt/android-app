package com.floryt.app.fragments;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.floryt.app.R;
import com.floryt.common.Common;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

/**
 * Created by Steven on 16/04/2017.
 */

public class MyComputersFragment extends android.app.Fragment {
    private static final MyComputersFragment ourInstance = new MyComputersFragment();
    private FirebaseDatabase database;
    private DatabaseReference myComputersRef;
    private TreeSet<String> computersList;

    public static MyComputersFragment getInstance() {
        return ourInstance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        computersList = new TreeSet<>();
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
            case R.id.action_add_computer:
                addComputer();
            default:
                break;
        }
        return false;
    }

    private void addComputer() {
        DialogFragment addComputerDialog = new AddComputerDialogFragment();
        addComputerDialog.show(getFragmentManager(), "AddComputerDialogFragment");
    }

    public MyComputersFragment() {
        database = FirebaseDatabase.getInstance();
        myComputersRef = database.getReference("Users").child(Common.getUid()).child("computers");
        myComputersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    List<String> remoteList = new ArrayList<String>();
                    try {
                        remoteList = (List<String>) dataSnapshot.getValue();
                    }catch (Exception e){
                        Log.e("myComputersRef", e.getMessage());
                    }finally {
                        computersList.retainAll(remoteList); // remove all the absent values
                        computersList.addAll(remoteList); // add all the new values
                    }
                } else {
                    computersList.clear();
                }
                Toast.makeText(getContext(), computersList.toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_computers_layout, container, false);
        getActivity().setTitle("My computers");
        final ListView computerListView = (ListView) view.findViewById(R.id.my_computes_list_view);
        //TODO add message when listview is empty
//        FirebaseListAdapter<Computer> firebaseListAdapter = new FirebaseListAdapter<Computer>(getActivity(), Computer.class, R.layout.computer_item, myComputers) {
//            @Override
//            protected void populateView(View v, final Computer computer, int position) {
//                ((TextView) v.findViewById(R.id.computer_name)).setText(computer.getName());
//                ((TextView) v.findViewById(R.id.computer_users)).setText(computer.createUsersString());
//                v.findViewById(R.id.computer_item_button).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Bundle bundle = new Bundle();
//                        bundle.putStringArrayList("Users",computer.getUsers());
//                        Intent computerInfoActivity = new Intent(getActivity(), ComputerInfoActivity.class);
//                        computerInfoActivity.putExtra("Computer", computer.getName());
//                        computerInfoActivity.putExtras(bundle);
//                        startActivity(computerInfoActivity);
//                    }
//                });
//            }
//        };
//        MergeAdapter margeAdapter = new MergeAdapter();
//        margeAdapter.addAdapter(firebaseListAdapter);
////        margeAdapter.addAdapter(firebaseListAdapter);
//        computerListView.setAdapter(margeAdapter);

        return view;
    }
}
