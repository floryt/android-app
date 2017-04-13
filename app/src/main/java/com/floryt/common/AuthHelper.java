package com.floryt.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.floryt.app.LoginActivity;
import com.floryt.app.MainActivity;
import com.floryt.app.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by user on 13/04/2017.
 */
// TODO https://github.com/firebase/quickstart-android/tree/master/auth/app/src/main/java/com/google/firebase/quickstart/auth
public class AuthHelper {
    private static final String TAG = "AuthHelper";
    private static final AuthHelper ourInstance = new AuthHelper();

    private static GoogleApiClient mGoogleApiClient;

    public static AuthHelper getInstance() {
        return ourInstance;
    }

    private AuthHelper() {
    }

    public static GoogleApiClient getGoogleApiClient(final Activity activity) {
        if (mGoogleApiClient == null){
            // Configure Google Sign In
            GoogleSignInOptions mGoogleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(activity.getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();

            GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener = new GoogleApiClient.OnConnectionFailedListener(){
                @Override
                public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                    Log.w(TAG, "onConnectionFailed:" + connectionResult.getErrorMessage());
                    Toast.makeText(activity.getApplicationContext(), "Connection failed", Toast.LENGTH_SHORT).show();
                }
            };

            mGoogleApiClient = new GoogleApiClient.Builder(activity.getApplicationContext())
                    .enableAutoManage((FragmentActivity) activity, onConnectionFailedListener)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, mGoogleSignInOptions)
                    .build();
        }
        return mGoogleApiClient;
    }

    public static void googleSignOut() {

    }
}
