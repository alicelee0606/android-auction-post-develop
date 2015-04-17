package com.yahoo.mobile.android.auctionpost;

import android.app.Activity;
import android.content.Intent;

public class SendMail {
    public static void sendMail(Activity activity, String address, String subject, String text) {
        final Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{address});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, text);
        activity.startActivity(Intent.createChooser(emailIntent, "Send Email"));
    }
}
