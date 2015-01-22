package net.three_headed_monkey.communication.utils;

import android.util.Log;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class X509TrustSingleManager implements X509TrustManager {
    public static final String TAG = "X509TrustSingleManager";

    private String certHash;

    public X509TrustSingleManager(String certHash) {
        this.certHash = certHash;
    }

    @Override
    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {}

    @Override
    public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
        String hash = new String(Hex.encodeHex(DigestUtils.sha256(x509Certificates[0].getEncoded())));
        if(!hash.equalsIgnoreCase(certHash))
            throw new CertificateException(hash + " not equal to " + certHash + " and therefore not trusted");
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return null;
    }

    public synchronized static SSLSocketFactory getTrustSingleFactory(String certHash) {
        TrustManager[] trustManagers = new TrustManager[] { new X509TrustSingleManager(certHash) };
        SSLContext sslContext;
        try {
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagers, new SecureRandom());
        } catch (GeneralSecurityException exception) {
            Log.e(TAG, "Error while creating TrustSingleSocketFactory", exception);
            return null;
        }
        return sslContext.getSocketFactory();

    }

}
