package com.booxtown.googleAuthSignin;

/**
 * Created by multidots on 6/21/2016.
 */
public interface GoogleAuthResponse {

    void onGoogleAuthSignIn(GoogleAuthUser user);

    void onGoogleAuthSignInFailed();
}
