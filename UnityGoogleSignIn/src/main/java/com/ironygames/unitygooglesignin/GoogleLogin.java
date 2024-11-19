package com.ironygames.unitygooglesignin;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;

import java.security.MessageDigest;

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

        String sha1 = GetSHA1CertificateFingerprint(activity.getPackageManager(), activity.getPackageName());
        if( sha1 != null ) {
            Log.e("Unity", String.format("SHA-1 is %s", sha1));
        }

        Intent myIntent;
        if (useLegacy){
            myIntent = new Intent(activity, LegacyGoogleLoginActivity.class);
        }
        else {
            myIntent = new Intent(activity, GoogleLoginActivity.class);
        }
        activity.startActivity(myIntent);
    }

    private static String GetSHA1CertificateFingerprint(PackageManager packageManager, String packageName){
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
            for (Signature signature : packageInfo.signatures){
                MessageDigest md = MessageDigest.getInstance("SHA-1");
                md.update(signature.toByteArray());
                return FormatSHA1(md.digest());
            }
            Log.e("Unity", "Get SHA-1 Certificate Fingerprint failed, error=no signature in package info");
            return null;
        }
        catch (Exception e){
            Log.e("Unity", "Get SHA-1 Certificate Fingerprint failed, check detail below");
            Log.e("Unity", e.toString());
            return null;
        }
    }

    private static String FormatSHA1(byte[] sha1AsByteArr){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < sha1AsByteArr.length; i++) {
            sb.append(String.format("%02X", sha1AsByteArr[i]));
            if (i < sha1AsByteArr.length - 1) {
                sb.append(":");
            }
        }
        return sb.toString();
    }

    public static void Logout(Activity activity, String clientId, boolean useLegacy){
        webClientId = clientId;

        if (useLegacy){
            GoogleSignInClient client = GoogleSignIn.getClient(activity, LegacyGoogleLoginActivity.BuildGoogleSignInOptions());
            client.signOut();
        }
        else {

        }
    }
}
