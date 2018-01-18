package com.shalbert.hobby.gamecollector.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.shalbert.hobby.gamecollector.activities.AuthenticationActivity;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by shalom on 2018-01-18.
 */

public class MenuUtils {

    /*Runs an implicit intent which shows user all email clients, and sets the To and Subject lines*/
    public static void ImplicitEmailIntent(Context context) {
        /*Get list of email clients*/
        Intent send = new Intent(Intent.ACTION_SENDTO);
        /*Set recipient email address and subject*/
        String uriText = "mailto:" + Uri.encode("shalomhalbert@gmail.com") +
                "?subject=" + Uri.encode("Game Collector Application");
        /*Parse String into Uri*/
        Uri uri = Uri.parse(uriText);
        send.setData(uri);
        context.startActivity(Intent.createChooser(send, "Send mail..."));
    }

    /*Logs out the current user from Firebase, and opens the AuthenticationActivity screen*/
    public static void LogoutUser(Context context) {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(context, AuthenticationActivity.class);
        context.startActivity(intent);
    }
}
