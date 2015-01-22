package net.three_headed_monkey.test_utils;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.SecureRandom;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

public class SSLUtils {

    private static final String TEST_CERT_BASEPATH = "/certificates/";
    private static final String KEYSTORE_PW = "testing";
    private static final String SSL_PROTOCOL = "TLSv1.2";
    public static final String TEST_CERT1_HASH = "7d528273d030c0e5195d2adf6ef90bce3f1fcb1c664ac8383fa43b0b80139238";


    KeyStore ks_testCert1_localhost = null;

    public SSLUtils() {
    }

    public SSLContext getSSLContext(KeyStore keyStore) throws Exception {
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(keyStore);
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, KEYSTORE_PW.toCharArray());
        SSLContext sslContext = SSLContext.getInstance(SSL_PROTOCOL);
        sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());
        return sslContext;
    }

    public KeyStore getTestCert1() throws Exception {
        if (ks_testCert1_localhost != null)
            return ks_testCert1_localhost;

        ks_testCert1_localhost = loadKeystoreFile("ks_testCert1_localhost.p12");
        return ks_testCert1_localhost;
    }

    private KeyStore loadKeystoreFile(String name) throws Exception {
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        FileInputStream f = new FileInputStream(getClass().getResource(TEST_CERT_BASEPATH + name).getPath());
        keyStore.load(f, KEYSTORE_PW.toCharArray());
        f.close();
        return keyStore;
    }

}
