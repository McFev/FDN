package com.mcfev.fdn;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
    private static final String ACTION_NOTIFICATION_LISTENER_SETTINGS = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";
    private EditText urlEditText;
    private Button updateButton;
    private TextView urlSendText;
    private TableLayout dataTable;
    private SharedPreferences sharedPreferences;
    private Gson gson;
    private AlertDialog enableNotificationListenerAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //из примера https://github.com/Chagall/notification-listener-service-example

        // If the user did not turn the notification listener service on we prompt him to do so
        if(!isNotificationServiceEnabled()){
            enableNotificationListenerAlertDialog = buildNotificationServiceAlertDialog();
            enableNotificationListenerAlertDialog.show();
        }

        urlEditText = findViewById(R.id.urlEditText);
        updateButton = findViewById(R.id.updateButton);
        urlSendText = findViewById(R.id.urlSendText);
        dataTable = findViewById(R.id.dataTable);
        sharedPreferences = getSharedPreferences("AppData", Context.MODE_PRIVATE);
        gson = new Gson();

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = urlEditText.getText().toString();
                if (!url.isEmpty()) {
                    fetchDataAndUpdateTable(url);
                } else {
                    Toast.makeText(MainActivity.this, R.string.enter_URL_first, Toast.LENGTH_SHORT).show();
                }
            }
        });

        loadDataAndUpdateTable();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void fetchDataAndUpdateTable(final String url) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, R.string.error_fetch_data, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String responseData = response.body().string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateTable(responseData);
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, R.string.failed_fetch_data, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void updateTable(String jsonData) {
        // Используйте Gson для преобразования JSON в список объектов AppInfo
        // После этого обновите таблицу с данными из списка
        try {
            JSONObject jsonObject = new JSONObject(jsonData);

            String urlValue = jsonObject.getString("url");
            sharedPreferences.edit().putString("url", urlValue).apply();

            JSONArray jsonArray = jsonObject.getJSONArray("applications");

            List<AppInfo> appList = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject appJson = jsonArray.getJSONObject(i);
                String appName = appJson.getString("appName");
                boolean toDelete = appJson.getBoolean("toDelete");
                appList.add(new AppInfo(appName, toDelete));
            }

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("appData", jsonData);
            editor.apply();

            urlSendText.setText(urlValue);

            dataTable.removeAllViews();
            for (AppInfo appInfo : appList) {
                addRowToTable(appInfo);
            }

            Toast.makeText(this, R.string.table_updated_successfully, Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, R.string.failed_fetch_data, Toast.LENGTH_SHORT).show();
        }
    }

    private void loadDataAndUpdateTable() {
        String jsonData = sharedPreferences.getString("appData", "");
        if (!jsonData.isEmpty()) {
            updateTable(jsonData);
        }
        String urlValue = sharedPreferences.getString("url", "");
        urlSendText.setText(urlValue);
    }

    private void addRowToTable(AppInfo appInfo) {
        TableRow row = new TableRow(this);

        // Get application icon using PackageManager
        PackageManager packageManager = getPackageManager();
        Drawable appIcon = null;
        try {
            ApplicationInfo aInfo = packageManager.getApplicationInfo(appInfo.getAppName(), 0);
            appIcon = packageManager.getApplicationIcon(aInfo);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            // If icon not found, set a default icon
            appIcon = getResources().getDrawable(R.drawable.no_img);
        }

        if (appIcon != null) {
            ImageView logoImageView = new ImageView(this);
            logoImageView.setImageDrawable(appIcon);
            TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.weight = 0.1f; // 10% of the screen width
            layoutParams.height = 100;
            logoImageView.setLayoutParams(layoutParams);
            row.addView(logoImageView);

            // Set gravity for row to center vertical
            row.setGravity(Gravity.CENTER_VERTICAL);
        }

        TextView deleteTextView = new TextView(this);
        deleteTextView.setText("  " + (appInfo.isToDelete() == true ? "📵" : "📱"));
        TableRow.LayoutParams deleteLayoutParams = new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        deleteLayoutParams.weight = 0.2f; // 40% of the screen width
        deleteTextView.setLayoutParams(deleteLayoutParams);
        row.addView(deleteTextView);

        TextView appNameTextView = new TextView(this);
        appNameTextView.setText(appInfo.getAppName());
        TableRow.LayoutParams appNameLayoutParams = new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        appNameLayoutParams.weight = 0.7f; // 50% of the screen width
        appNameTextView.setLayoutParams(appNameLayoutParams);
        row.addView(appNameTextView);

        dataTable.addView(row);
    }

    /**
     * Is Notification Service Enabled.
     * Verifies if the notification listener service is enabled.
     * Got it from: https://github.com/kpbird/NotificationListenerService-Example/blob/master/NLSExample/src/main/java/com/kpbird/nlsexample/NLService.java
     * @return True if enabled, false otherwise.
     */
    private boolean isNotificationServiceEnabled(){
        String pkgName = getPackageName();
        final String flat = Settings.Secure.getString(getContentResolver(),
                ENABLED_NOTIFICATION_LISTENERS);
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (int i = 0; i < names.length; i++) {
                final ComponentName cn = ComponentName.unflattenFromString(names[i]);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Build Notification Listener Alert Dialog.
     * Builds the alert dialog that pops up if the user has not turned
     * the Notification Listener Service on yet.
     * @return An alert dialog which leads to the notification enabling screen
     */
    private AlertDialog buildNotificationServiceAlertDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(R.string.notification_listener_service);
        alertDialogBuilder.setMessage(R.string.notification_listener_service_explanation);
        alertDialogBuilder.setPositiveButton(R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS));
                    }
                });
        alertDialogBuilder.setNegativeButton(R.string.no,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // If you choose to not enable the notification listener
                        // the app. will not work as expected
                    }
                });
        return(alertDialogBuilder.create());
    }
}
