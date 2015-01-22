package net.three_headed_monkey.communication.utils;

import android.util.Log;

import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class X509TrustEverythingManager implements X509TrustManager {
    public static final String TAG = "X509TrustEverythingManager";



    @Override
    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {}

    @Override
    public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {}

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return null;
    }

    private static SSLSocketFactory factory = null;

    public synchronized static SSLSocketFactory getTrustEverythingFactory() {
        if(factory != null) return factory;

        TrustManager[] trustManagers = new TrustManager[] { new X509TrustEverythingManager() };
        SSLContext sslContext;
        try {
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagers, new SecureRandom());
        } catch (GeneralSecurityException exception) {
            Log.e(TAG, "Error while creating TrustEverythingSocketFactory", exception);
            return null;
        }
        factory = sslContext.getSocketFactory();
        return factory;

    }

}
