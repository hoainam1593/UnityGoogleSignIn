package com.ironygames.unitygooglesignin;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.unity3d.player.UnityPlayer;

public class GoogleLoginActivity extends Activity {

    //region data members

    private static final int REQ_ONE_TAP = 2;
    private SignInClient oneTapClient;

    //endregion

    //region activity functions

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        oneTapClient = Identity.getSignInClient(this);
        BeginSignInRequest request = BuildBeginSignInRequest();
        oneTapClient.beginSignIn(request).addOnSuccessListener(this, this::onSignInSuccess).addOnFailureListener(this, this::onSignInFail);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_ONE_TAP) {
            try{
                SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(data);
                String idToken = credential.getGoogleIdToken();
                FinishSuccess(idToken);
            } catch (ApiException e){
                String msg = "cannot get token, reason=" + e.getLocalizedMessage();
                FinishFailed(msg);
            }
        }
    }

    //endregion

    //region sign in callback

    private void onSignInSuccess(BeginSignInResult result){
        try{
            startIntentSenderForResult(result.getPendingIntent().getIntentSender(), REQ_ONE_TAP, null, 0, 0, 0);
        } catch (IntentSender.SendIntentException e){
            String msg = "cannot start login UI, reason=" + e.getLocalizedMessage();
            FinishFailed(msg);
        }
    }

    private void onSignInFail(Exception e){
        String msg = "sign in google fail, reason=" + e.getLocalizedMessage();
        FinishFailed(msg);
    }

    //endregion

    //region finish activity

    private void FinishSuccess(String token){
        oneTapClient.signOut();
        finish();
        if (GoogleLogin.callbackTargetName != null){
            UnityPlayer.UnitySendMessage(GoogleLogin.callbackTargetName, GoogleLogin.callbackSuccessFunc, token);
        }else {
            Log.d("GoogleLogin", "login success token=" + token);
        }
    }

    private void FinishFailed(String errMsg){
        oneTapClient.signOut();
        finish();
        if (GoogleLogin.callbackTargetName != null) {
            UnityPlayer.UnitySendMessage(GoogleLogin.callbackTargetName, GoogleLogin.callbackFailFunc, errMsg);
        }else {
            Log.e("GoogleLogin", "login fail err=" + errMsg);
        }
    }

    //endregion

    //region utils

    private static BeginSignInRequest BuildBeginSignInRequest(){
        String clientId = GoogleLogin.webClientId;
        BeginSignInRequest.PasswordRequestOptions password = BeginSignInRequest.PasswordRequestOptions.builder().setSupported(true).build();
        BeginSignInRequest.GoogleIdTokenRequestOptions token = BeginSignInRequest.GoogleIdTokenRequestOptions.builder().setSupported(true).setServerClientId(clientId).setFilterByAuthorizedAccounts(false).build();
        return BeginSignInRequest.builder().setPasswordRequestOptions(password).setGoogleIdTokenRequestOptions(token).setAutoSelectEnabled(true).build();
    }

    //endregion
}