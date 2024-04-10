package com.mcfev.fdn;

import static androidx.core.app.NotificationCompat.EXTRA_BIG_TEXT;
import static androidx.core.app.NotificationCompat.EXTRA_INFO_TEXT;
import static androidx.core.app.NotificationCompat.EXTRA_SUB_TEXT;
import static androidx.core.app.NotificationCompat.EXTRA_TEXT;
import static androidx.core.app.NotificationCompat.EXTRA_TITLE;

import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class FDNService extends NotificationListenerService {

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        String packageName = sbn.getPackageName();
        AppInfoService ais = AppInfoService.getInfo(getApplicationContext(), packageName);
        String urlForSend = AppInfoService.getURL(getApplicationContext());
        boolean appExists = ais.isExists();
        final boolean[] appToDelete = {ais.isToDelete()};

        if (appExists) {
            Callback callback = new Callback() {
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful() && response.body().string().equals("ok")) {
                        // Успешный ответ сервера с "ok"
                    } else {
                        // Обработка неуспешного ответа или отсутствия "ok"
                        //appToDelete[0] = false; // Устанавливаем appToDelete в false
                        //так не работает, сначала удаляется потом вызов callback
                    }
                    response.body().close();
                }

                public void onFailure(Call call, IOException iOException) {
                    iOException.printStackTrace();
                    //appToDelete[0] = false; // Устанавливаем appToDelete в false при ошибке
                }
            };

            String title = "";
            String text = "";
            String subtext = "";
            String bigtext = "";
            String infotext = "";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                title = nullToBlank(sbn.getNotification().extras.get(EXTRA_TITLE));
                text = nullToBlank(sbn.getNotification().extras.get(EXTRA_TEXT));
                subtext = nullToBlank(sbn.getNotification().extras.get(EXTRA_SUB_TEXT));
                bigtext = nullToBlank(sbn.getNotification().extras.get(EXTRA_BIG_TEXT));
                infotext = nullToBlank(sbn.getNotification().extras.get(EXTRA_INFO_TEXT));
            }

            String url = String.format(urlForSend,
                    urlencoding(sbn.getPackageName()), urlencoding(title), urlencoding(text),
                    urlencoding(subtext), urlencoding(bigtext), urlencoding(infotext));

            new Http().runGet(url, callback);
        }

        if (appToDelete[0]) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                cancelNotification(sbn.getPackageName(), sbn.getTag(), sbn.getId());
            } else {
                cancelNotification(sbn.getKey());
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn){
        String packageName = sbn.getPackageName();
        AppInfoService ais = AppInfoService.getInfo(getApplicationContext(), packageName);
        boolean appToDelete = ais.isToDelete();

        if (appToDelete) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                cancelNotification(sbn.getPackageName(), sbn.getTag(), sbn.getId());
            } else {
                cancelNotification(sbn.getKey());
            }
        }
    }

    public String nullToBlank(Object obj) {
        return obj == null ? "" : obj.toString();
    }

    public String urlencoding(String str) {
        try {
            return URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }
}
