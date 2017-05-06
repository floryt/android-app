package com.floryt.app.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.floryt.app.R;

/**
 * Created by Steven on 06/05/2017.
 */

public class SharedComputersFragment extends Fragment {
    private static final SharedComputersFragment ourInstance = new SharedComputersFragment();

    public static SharedComputersFragment getInstance() {
        return ourInstance;
    }

    public SharedComputersFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.shared_computers_layout, container, false);
        getActivity().setTitle("Shared computers");
        return view;
    }
}
