package com.floryt.app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.annotation.StyleRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.ResultCodes;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.ArrayList;
import java.util.List;

// TODO use https://github.com/firebase/FirebaseUI-Android/tree/master/auth
public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "SignInActivity";
    private static final String GOOGLE_TOS_URL = "https://www.google.com/policies/terms/";
    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001;
    private ProgressDialog mProgressDialog;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

//        mAuth = FirebaseAuth.getInstance();
//        mAuthListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                FirebaseUser user = firebaseAuth.getCurrentUser();
//                if (user != null) {
//                    // User is signed in
//                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
//                    Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
//                    startActivity(mainActivity);
//                    finish();
//                } else {
//                    // User is signed out
//                    Log.d(TAG, "onAuthStateChanged:signed_out");
//                }
//            }
//        };
//
//        findViewById(R.id.button_google_login).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showProgressDialog("Signing in...");
//                mGoogleApiClient = new GoogleApiClient.Builder(LoginActivity.this)
//                        .enableAutoManage(
//                                LoginActivity.this /* FragmentActivity */,
//                                new GoogleApiClient.OnConnectionFailedListener(){
//                                    @Override
//                                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//                                        Log.w(TAG, "onConnectionFailed:" + connectionResult.getErrorMessage());
//                                        setStatusMessage("Connection failed");
//                                    }
//                                })
//                        .addApi(Auth.GOOGLE_SIGN_IN_API,
//                                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                                        .requestIdToken(getString(R.string.default_web_client_id))
//                                        .requestEmail()
//                                        .build())
//                        .build();
//                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
//                startActivityForResult(signInIntent, RC_SIGN_IN);
//            }
//        });
        findViewById(R.id.button_google_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(
                        AuthUI.getInstance().createSignInIntentBuilder()
                                .setTheme(getSelectedTheme())
                                .setLogo(getLogo())
                                .setProviders(getProviders())
                                .setTosUrl(getTosUrl())
                                .setIsSmartLockEnabled(false)
                                .setAllowNewEmailAccounts(false)
                                .build(),
                        RC_SIGN_IN);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // TODO add more conditions
        if (requestCode == RC_SIGN_IN) {
//            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
//            handleSignInResult(result);
            handleSignInResponse(resultCode, data);
        }
    }

    @MainThread
    private void handleSignInResponse(int resultCode, Intent data) {
        IdpResponse response = IdpResponse.fromResultIntent(data);

        // Successfully signed in
        if (resultCode == ResultCodes.OK) {
            startSignedInActivity(response);
            finish();
            return;
        } else {
            // Sign in failed
            if (response == null) {
                // User pressed back button
                showSnackbar(R.string.sign_in_cancelled);
                return;
            }

            if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                showSnackbar(R.string.no_internet_connection);
                return;
            }

            if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                showSnackbar(R.string.unknown_error);
                return;
            }
        }

        showSnackbar(R.string.unknown_sign_in_response);
    }

    private void startSignedInActivity(IdpResponse response) {
        startActivity(new Intent(this, MainActivity.class));
//                SignedInActivity.createIntent(
//                        this,
//                        response,
//                        new SignedInActivity.SignedInConfig(
//                                getLogo(),
//                                getSelectedTheme(),
//                                getProviders(),
//                                getTosUrl(),
//                                true)));
    }

    @MainThread
    @DrawableRes
    private int getLogo() {
        return R.mipmap.floryt;
    }

    @MainThread
    private String getTosUrl() {
        return GOOGLE_TOS_URL;
    }

    @MainThread
    @StyleRes
    private int getSelectedTheme() {
        return R.style.PurpleTheme;
    }

    @MainThread
    private List<AuthUI.IdpConfig> getProviders() {
        List<AuthUI.IdpConfig> selectedProviders = new ArrayList<>();
        selectedProviders.add(
                new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER)
                        .setPermissions(getGooglePermissions())
                        .build());
        selectedProviders.add(
                new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER)
                        .setPermissions(getFacebookPermissions())
                        .build());
        selectedProviders.add(new AuthUI.IdpConfig.Builder(AuthUI.TWITTER_PROVIDER).build());
        selectedProviders.add(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build());

        return selectedProviders;
    }

    @MainThread
    private List<String> getFacebookPermissions() {
        List<String> result = new ArrayList<>();
        result.add("user_friends");
        result.add("user_photos");
        return result;
    }

    @MainThread
    private List<String> getGooglePermissions() {
        List<String> result = new ArrayList<>();
        //result.add(Scopes.GAMES);
        //result.add(Scopes.DRIVE_FILE);
        return result;
    }

    @MainThread
    private void showSnackbar(@StringRes int errorMessageRes) {
        Snackbar.make(findViewById(R.id.login_root), errorMessageRes, Snackbar.LENGTH_LONG).show();
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount account = result.getSignInAccount();
            assert account != null;
            firebaseAuthWithCredential(GoogleAuthProvider.getCredential(account.getIdToken(), null));
            Log.d(TAG, "handleSignInResult:Sign in succeeded");
        } else {
            // Signed out, show unauthenticated UI.
            hideProgressDialog();
            Log.d(TAG, "handleSignInResult:Sign in failed");
            setStatusMessage("Authentication failed");
        }
    }

    private void firebaseAuthWithCredential(AuthCredential credential) {
        Log.d(TAG, "firebaseAuthWithCredential:" + credential.getProvider());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        hideProgressDialog();
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        if (task.isSuccessful()) {
                            // Login Success
                            Log.w(TAG, "signInWithCredential:success");
                        } else {
                            // Login Failed
                            Log.w(TAG, "signInWithCredential:", task.getException());
                            setStatusMessage("Authentication failed");
                        }
                    }
                });
    }

    private void setStatusMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void showProgressDialog(String message) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(message);
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

}

