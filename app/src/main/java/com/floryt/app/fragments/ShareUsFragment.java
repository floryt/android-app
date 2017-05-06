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

public class ShareUsFragment extends Fragment {
    private static final ShareUsFragment ourInstance = new ShareUsFragment();

    public static ShareUsFragment getInstance() {
        return ourInstance;
    }

    public ShareUsFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.shared_us_layout, container, false);
        getActivity().setTitle("Share us");
        return view;
    }
}
