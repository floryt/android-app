package com.floryt.app.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.floryt.app.R;

/**
 * Created by Steven on 07/05/2017.
 */

public class DashboardFragment extends Fragment {
    private static final DashboardFragment ourInstance = new DashboardFragment();

    public static DashboardFragment getInstance() {
        return ourInstance;
    }

    public DashboardFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dashboard_layout, container, false);
        getActivity().setTitle("Dashboard");
        return view;
    }
}
