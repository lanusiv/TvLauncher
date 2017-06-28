package com.leray.tvlauncher.server.ssl;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

/**
 * Created by Administrator on 2017/2/24 0024.
 */

public class HttpsUtils {

    private static final String TAG = "HttpsUtils";


    public static SSLSocketFactory getSslSocketFactoryByCert(Context context, String certPath) {
        SSLContext sslContext = null;
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
//        InputStream caInput = new BufferedInputStream(new FileInputStream("load-der.crt"));
            InputStream caInput = context.getResources().getAssets().open(certPath);
            Certificate ca;
            try {
                ca = cf.generateCertificate(caInput);
                System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
            } finally {
                caInput.close();
            }

            // Create a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            Log.i(TAG, "getSslSocketFactory2, keyStoreType: " + keyStoreType);
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            // Create a TrustManager that trusts the CAs in our KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

            // Create an SSLContext that uses our TrustManager
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), null);

            return sslContext.getSocketFactory();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static SSLSocketFactory getSslSocketFactoryByStore(Context context, String keystorePath, String password) {
        try {
            KeyStore trusted = KeyStore.getInstance("BKS");
            InputStream in = context.getResources().getAssets().open(keystorePath);
            trusted.load(in, password.toCharArray());
            SSLContext sslContext = SSLContext.getInstance("TLS");
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(trusted);
            sslContext.init(null, trustManagerFactory.getTrustManagers(), null);
            return sslContext.getSocketFactory();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String getString(InputStream in) {
        String strContent;
        /*
         * There are several way to convert InputStream to String. First is using
         * BufferedReader as given below.
         */
        //Create BufferedReader object
        BufferedReader bReader = new BufferedReader(new InputStreamReader(in));
        StringBuffer sbfFileContents = new StringBuffer();
        String line = null;

        try {
            //read file line by line
            while ((line = bReader.readLine()) != null) {
                sbfFileContents.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //finally convert StringBuffer object to String!
        strContent = sbfFileContents.toString();

        return strContent;
    }

}
