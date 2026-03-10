package io.capawesome.capacitorjs.plugins.firebase.messaging.messages;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import timber.log.Timber;

public class SharedPreferencesManager {
    private static final String PREF_NAME = "PendingNotifications";
    private static final String KEY_NOTIFICATIONS = "notifications";

    private static SharedPreferencesManager instance;
    private final SharedPreferences sharedPreferences;

    private SharedPreferencesManager(Context context) throws JSONException {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized SharedPreferencesManager getInstance(Context context) throws JSONException {
        if (instance == null) {
            instance = new SharedPreferencesManager(context.getApplicationContext());
        }
        return instance;
    }


    public void removeNotification(int notificationId) {
        String id = String.valueOf(notificationId);
        removeNotification(id);
    }

    public void removeNotification(String notificationId) {
        try {
            String notificationsString = sharedPreferences.getString(KEY_NOTIFICATIONS, "");
            if (notificationsString.isEmpty()) return;

            JSONObject notifications = new JSONObject(notificationsString);

            // remove directly by key
            notifications.remove(String.valueOf(MessageUtils.createNotificationId(notificationId)));
            sharedPreferences.edit()
                .putString(KEY_NOTIFICATIONS, notifications.toString())
                .apply();

        } catch (JSONException e) {
            Timber.e(e, "removeNotification JSONException");
        }
    }

    public void storeNotification(String id, JSONObject jsonData) {
        try {
            String notificationsString = sharedPreferences.getString(KEY_NOTIFICATIONS, "");
            JSONObject notifications = notificationsString.isEmpty() ? new JSONObject() : new JSONObject(notificationsString);
            notifications.put(String.valueOf(MessageUtils.createNotificationId(id)), jsonData);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(KEY_NOTIFICATIONS, notifications.toString());
            editor.apply();
        } catch (JSONException e) {
            Timber.e("Failed to store notification: %s", e.getMessage());
        }
    }

    public JSONObject getNotifications() throws JSONException {
        String notificationsString = sharedPreferences.getString(KEY_NOTIFICATIONS, "");
        return notificationsString.isEmpty()
            ? new JSONObject()
            : new JSONObject(notificationsString);
    }

    public void clearNotifications() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("notifications");
        editor.apply();
    }
}
