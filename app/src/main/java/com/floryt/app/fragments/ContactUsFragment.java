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

public class ContactUsFragment extends Fragment {
    private static final ContactUsFragment ourInstance = new ContactUsFragment();

    public static ContactUsFragment getInstance() {
        return ourInstance;
    }

    public ContactUsFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.contact_us_layout, container, false);
        getActivity().setTitle("Contact us");
        return view;
    }
}
