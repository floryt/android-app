package com.floryt.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class ComputerInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_computer_info);
        String computerName = getIntent().getStringExtra("Computer");
        ArrayList<String> users = getIntent().getStringArrayListExtra("Users");

        ListView userListView = (ListView) findViewById(R.id.users_list_view);
        ArrayAdapter<String> itemsAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, users);
        userListView.setAdapter(itemsAdapter);


        TextView computerNameTextView = (TextView) findViewById(R.id.computer_name);
        computerNameTextView.setText(computerName);

    }
}
