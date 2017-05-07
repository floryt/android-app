package com.floryt.auth.ui;

import android.app.Activity;

import com.floryt.auth.AuthUI;

/**
 * Result codes returned when using {@link AuthUI.SignInIntentBuilder#build()} with
 * {@code startActivityForResult}.
 */
public class ResultCodes {
    /**
     * Sign in succeeded
     **/
    public static final int OK = Activity.RESULT_OK;

    /**
     * Sign in canceled by user
     **/
    public static final int CANCELED = Activity.RESULT_CANCELED;
    /**
     * Sign in failed due to lack of network connection
     *
     * @deprecated Please use {@link com.floryt.auth.ErrorCodes#NO_NETWORK}
     **/
    @Deprecated
    public static final int RESULT_NO_NETWORK = com.floryt.auth.ErrorCodes.NO_NETWORK;

}
