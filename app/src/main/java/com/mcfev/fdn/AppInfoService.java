package com.mcfev.fdn;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class AppInfoService {
    private boolean exists;
    private boolean toDelete;

    public boolean isExists() {
        return exists;
    }

    public boolean isToDelete() {
        return toDelete;
    }

    public static AppInfoService getInfo(Context context, String appName) {
        AppInfoService ais = new AppInfoService();
        ais.exists = false;
        ais.toDelete = false;
        Gson gson = new Gson();
        SharedPreferences sharedPreferences = context.getSharedPreferences("AppData", Context.MODE_PRIVATE);
        String jsonData = sharedPreferences.getString("appData", "");
        if (!jsonData.isEmpty()) {
            try {
                JSONObject jsonObject = new JSONObject(jsonData);
                JSONArray jsonArray = jsonObject.getJSONArray("applications");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject appJson = jsonArray.getJSONObject(i);
                    String appNameFromJson = appJson.getString("appName");
                    boolean toDeleteFromJson = appJson.getBoolean("toDelete");
                    if (Objects.equals(appNameFromJson, appName)) {
                        ais.exists = true;
                        ais.toDelete = toDeleteFromJson;
                        return ais;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return ais;
    }

    public static String getURL(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("AppData", Context.MODE_PRIVATE);
        return sharedPreferences.getString("url", "");
    }
}
