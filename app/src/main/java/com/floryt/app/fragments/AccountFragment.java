package com.floryt.app.fragments;

import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.firebase.ui.auth.AuthUI;
import com.floryt.app.LoginActivity;
import com.floryt.app.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by Steven on 07/05/2017.
 */

public class AccountFragment extends Fragment {
    private static final AccountFragment ourInstance = new AccountFragment();

    public static AccountFragment getInstance() {
        return ourInstance;
    }

    public AccountFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.account_setting_layout, container, false);
        getActivity().setTitle("Account settings");
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        final ImageView userImage = (ImageView) view.findViewById(R.id.user_icon);
        TextView userDisplayName = (TextView) view.findViewById(R.id.user_name);
        TextView userEmail = (TextView) view.findViewById(R.id.user_email);

        Glide.with(this)
                .load(currentUser.getPhotoUrl())
                .asBitmap()
                .centerCrop()
                .into(new BitmapImageViewTarget(userImage) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(getContext().getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        userImage.setImageDrawable(circularBitmapDrawable);
                    }
                });
        userDisplayName.setText(currentUser.getDisplayName());
        userEmail.setText(currentUser.getEmail());
        view.findViewById(R.id.delete_account).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAccountClicked();
            }
        });
        return view;
    }

    public void deleteAccountClicked() {

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setMessage("Are you sure you want to delete this account?")
                .setPositiveButton("Yes, nuke it!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteAccount();
                    }
                })
                .setNegativeButton("No", null)
                .create();

        dialog.show();
    }

    private void deleteAccount() {
        AuthUI.getInstance()
                .delete(getActivity())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(getContext(), LoginActivity.class));
                            getActivity().finish();
                        } else {
                            Toast.makeText(getContext(), "Delete account failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}