package com.mcfev.fdn;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class Http {
    public void runPost(String str, RequestBody requestBody, Callback callback) {
        try {
            getClient().newCall(new Request.Builder().url(str).header("User-Agent", "android").post(requestBody).build()).enqueue(callback);
        } catch (Exception e) {
            /*Utils utils = this.f81_u;
            utils.log("POST =>" + str + "<=" + e);*/
            e.printStackTrace();
        }
    }

    public void runGet(String str, Callback callback) {
        try {
            getClient().newCall(new Request.Builder().header("User-Agent", "android").url(str).get().build()).enqueue(callback);
        } catch (Exception e) {
            /*Utils utils = this.f81_u;
            utils.log("GET =>" + str + "<=" + e);*/
            e.printStackTrace();
        }
    }

    public OkHttpClient getClient() throws Exception {
        SSLContext instance = SSLContext.getInstance("SSL");
        instance.init((KeyManager[]) null, new TrustManager[]{new MyX509TrustManager()}, new SecureRandom());
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.sslSocketFactory(instance.getSocketFactory(), new MyX509TrustManager());
        builder.hostnameVerifier(new HostnameVerifier() {
            public boolean verify(String str, SSLSession sSLSession) {
                return true;
            }
        });
        return builder.build();
    }

    static class MyX509TrustManager implements X509TrustManager {
        public void checkClientTrusted(X509Certificate[] x509CertificateArr, String str) {
        }

        public void checkServerTrusted(X509Certificate[] x509CertificateArr, String str) {
        }

        MyX509TrustManager() {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }
}
