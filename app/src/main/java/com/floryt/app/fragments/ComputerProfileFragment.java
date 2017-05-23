package com.floryt.app.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.MenuRes;
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
import com.floryt.common.Computer;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ComputerProfileFragment extends Fragment {
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.computer_profile_layout, container, false);
        final String computerUid = getArguments().getString("computerUid");
        assert computerUid != null;
        FirebaseDatabase.getInstance().getReference("Computers").child(computerUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Computer computer = dataSnapshot.getValue(Computer.class);

                getActivity().setTitle(computer.getName());
                ((TextView)view.findViewById(R.id.computer_name)).setText(computer.getName());

                LinearLayout administratorsList = (LinearLayout) view.findViewById(R.id.administrators_list);
                for(String user : computer.getUsers()){
                    View item = inflater.inflate(R.layout.administrator_card_item, null);
                    item.findViewById(R.id.icon_options).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showPopupMenu(v, new AdministratorItemClickListener(), R.menu.administrator_item_menu);
                        }
                    });
                    ((TextView)item.findViewById(R.id.name)).setText(user);
                    ((TextView)item.findViewById(R.id.email)).setText(user+"@gmail.com");

                    administratorsList.addView(item);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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