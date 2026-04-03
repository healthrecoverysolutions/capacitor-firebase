package io.capawesome.capacitorjs.plugins.firebase.messaging.messages;

import android.os.Bundle;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class MessageUtils {

    public static Bundle jsonToBundle(JSONObject jsonObject) throws JSONException {
        Bundle bundle = new Bundle();

        for (Iterator<String> it = jsonObject.keys(); it.hasNext();) {
            String key = it.next();
            Object value = jsonObject.get(key);

            if (value instanceof String) {
                bundle.putString(key, (String) value);
            } else if (value instanceof Integer) {
                bundle.putInt(key, (Integer) value);
            } else if (value instanceof Boolean) {
                bundle.putBoolean(key, (Boolean) value);
            } else if (value instanceof Double) {
                bundle.putDouble(key, (Double) value);
            } else if (value instanceof Long) {
                bundle.putLong(key, (Long) value);
            } else if (value instanceof JSONObject) {
                bundle.putBundle(key, jsonToBundle((JSONObject) value));
            } else if (value instanceof JSONArray) {
                bundle.putSerializable(key, jsonArrayToArrayList((JSONArray) value));
            }
        }

        return bundle;
    }

    private static ArrayList<Object> jsonArrayToArrayList(JSONArray jsonArray) throws JSONException {
        ArrayList<Object> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            Object value = jsonArray.get(i);
            if (value instanceof JSONObject) {
                list.add(jsonToBundle((JSONObject) value));
            } else if (value instanceof JSONArray) {
                list.add(jsonArrayToArrayList((JSONArray) value));
            } else {
                list.add(value);
            }
        }
        return list;
    }

    public static JSONArray convertToJsonArray(JSONObject jsonObject) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        Iterator<String> keys = jsonObject.keys();

        while (keys.hasNext()) {
            String key = keys.next();
            jsonArray.put(jsonObject.get(key));
        }

        return jsonArray;
    }

    /**
     * Converts HRS Unique Notification ID included in Notifications to int
     * Can be used to consistently reference the same unique id passed to the OS from the id included in the notification from our backend
     * @return int
     */
    public static int createNotificationId(String id) {
        return Math.abs(id.hashCode());
    }

    public static JSONObject bundleToJSONObject(@NonNull Bundle bundle) throws JSONException {
        JSONObject jsonObject = new JSONObject();

        for (String key : bundle.keySet()) {
            Object value = bundle.get(key);

            if (value == null) {
                jsonObject.put(key, JSONObject.NULL);
            } else if (value instanceof Bundle) {
                jsonObject.put(key, bundleToJSONObject((Bundle) value));
            } else if (value instanceof ArrayList) {
                jsonObject.put(key, arrayListToJsonArray((ArrayList<?>) value));
            } else if (value instanceof String || value instanceof Integer ||
                       value instanceof Boolean || value instanceof Double ||
                       value instanceof Long) {
                jsonObject.put(key, value);
            } else {
                // Fallback for other types - convert to string
                jsonObject.put(key, value.toString());
            }
        }

        return jsonObject;
    }

    private static JSONArray arrayListToJsonArray(ArrayList<?> list) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        for (Object value : list) {
            if (value instanceof Bundle) {
                jsonArray.put(bundleToJSONObject((Bundle) value));
            } else if (value instanceof ArrayList) {
                jsonArray.put(arrayListToJsonArray((ArrayList<?>) value));
            } else {
                jsonArray.put(value);
            }
        }
        return jsonArray;
    }
}
