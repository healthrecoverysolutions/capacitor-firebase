package io.capawesome.capacitorjs.plugins.firebase.messaging.messages.incomingcall;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class IncomingCall {
    public static void showNotification(Context context, String callerName, String callTitle, Bundle data) {
        Intent intent = new Intent(context, IncomingCallService.class);
        intent.putExtra(Constants.EXTRA_CALLER_NAME, callerName);
        intent.putExtra(Constants.EXTRA_CALL_TITLE, callTitle);
        intent.putExtra(Constants.EXTRA_CALL_DATA, data);

        context.startForegroundService(intent);
    }

    public static void callLeft(Context context) {
        Intent callLeftIntent = new Intent(context, IncomingCallService.class);
        callLeftIntent.setAction(Constants.ACTION_CALL_LEFT);
        context.startService(callLeftIntent);
    }

    /**
     * Tears down the native ringing UI - ringtone, notification and full-screen activity - once
     * the app has taken the call over and is showing its own ringing screen.
     */
    public static void handOff(Context context) {
        Intent handOffIntent = new Intent(context, IncomingCallService.class);
        handOffIntent.setAction(Constants.ACTION_CALL_HANDED_OFF);
        context.startService(handOffIntent);
    }
}
