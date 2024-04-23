package com.ironygames.unitygooglesignin;

import android.app.Activity;
import android.content.Intent;

public class GoogleLogin {
    public static String webClientId;
    public static String callbackTargetName;
    public static String callbackSuccessFunc;
    public static String callbackFailFunc;

    public static void Login(Activity activity, String clientId, boolean useLegacy, String targetName, String successFunc, String failFunc){
        webClientId = clientId;
        callbackTargetName = targetName;
        callbackSuccessFunc = successFunc;
        callbackFailFunc = failFunc;

        Intent myIntent;
        if (useLegacy){
            myIntent = new Intent(activity, LegacyGoogleLoginActivity.class);
        }
        else {
            myIntent = new Intent(activity, GoogleLoginActivity.class);
        }
        activity.startActivity(myIntent);
    }
}
