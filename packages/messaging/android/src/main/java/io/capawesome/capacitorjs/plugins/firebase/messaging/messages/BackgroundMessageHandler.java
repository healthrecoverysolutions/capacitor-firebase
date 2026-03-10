package io.capawesome.capacitorjs.plugins.firebase.messaging.messages;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Objects;

import io.capawesome.capacitorjs.plugins.firebase.messaging.messages.incomingcall.IncomingCall;
import timber.log.Timber;

public class BackgroundMessageHandler {
    public static void handle(Context context, RemoteMessage remoteMessage) {
        Timber.d("handle()");
        HashMap<String, Object> data = new HashMap<String, Object>();

        for (String key : remoteMessage.getData().keySet()) {
            Object value = remoteMessage.getData().get(key);
            Timber.d("\tKey: " + key + " Value: " + value);
            data.put(key, value);
        }

        JSONObject jsonData = null;
        try {
            jsonData = new JSONObject((String) Objects.requireNonNull(data.get("jsonData")));
        } catch (JSONException e) {
            Timber.e(e, "Error decoding jsonData from push notification");
            return;
        }

        if (jsonData.optString("action").equals("incoming_call")) {
            handleIncomingCall(context, jsonData);
        } else if (jsonData.optString("action").equals("call_left")) {
            IncomingCall.callLeft(context);
//            FCMPlugin.sendPushPayload(data);
        } else if (!jsonData.optString("title").isEmpty()) {
            handleGenericNotification(context, jsonData);
            // Store these to shared preferences to handle multiple other messages when one is tapped
            try {
                SharedPreferencesManager.getInstance(context).storeNotification(jsonData.getString("id"), jsonData);
            } catch (JSONException e) {
                Timber.e("Error getting storing notification in shared preferences: %s", e.getMessage());
            }

            if (jsonData.optString("status").equals("deactivate")) {
                Timber.d("Incoming deactivate patient notification. Deleting token.");
                try {
                    com.google.firebase.messaging.FirebaseMessaging.getInstance().deleteToken();
                } catch (Exception e) {
                    Timber.e("Patient deactivated. Error deleting Firebase instance: %s", e.getMessage());
                }
            }
        }

        Timber.d("Notification Data: %s", jsonData.toString());
    }

    private static void handleGenericNotification(Context context, JSONObject jsonData) {
        try {
            new GenericNotification().show(context, jsonData);
        } catch (JSONException e) {
            Timber.e("Failed to generate generic notification  %s", e.getMessage());
        } catch (PackageManager.NameNotFoundException e) {
            Timber.e("Failed to find package name/icon for generic notification  %s", e.getMessage());
        }
    }


    private static void handleIncomingCall(Context context, JSONObject jsonData) {
        String name = "";
        String title = "";
        String type = jsonData.optString("type");
        JSONObject data = jsonData.optJSONObject("data");
        if ("video".equals(type) || "video-zoom".equals(type)) {
            JSONObject caller = jsonData.optJSONObject("caller");
            if (caller != null) {
                name = caller.optString("name", "Clinician");
            }
            title = "Incoming Video Call";
        } else if ("voice".equals(type) || "voicecall".equals(type)) {
            if (data != null) {
                name = data.optString("from");
            }
            title = "Incoming Voice Call";
        }

        try {
            Bundle bundle = MessageUtils.jsonToBundle(jsonData);
            bundle.putString("jsonData", jsonData.toString());
            IncomingCall.showNotification(context, name, title, bundle);
        } catch (JSONException e) {
            Timber.e("Failed to show incoming call notification%s", e.getMessage());
        }
    }
}
