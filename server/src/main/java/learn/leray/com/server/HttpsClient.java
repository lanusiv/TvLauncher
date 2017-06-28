package learn.leray.com.server;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import learn.leray.com.server.ssl.PreferredCipherSuiteSSLSocketFactory;
import okhttp3.CertificatePinner;
import okhttp3.CipherSuite;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.TlsVersion;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

/**
 * Created by Administrator on 2017/3/1 0001.
 */

public class HttpsClient {
    private static final String TAG = "HttpsClient";

    private Context context;
    private static HttpsClient httpsClient;
    
    public static HttpsClient getInstance(Context context) {
        if (httpsClient == null) {
            httpsClient = new HttpsClient(context);
        }
        return httpsClient;
    }

    private HttpsClient(Context context) {
        this.context = context;
    }
    
    private Context getApplicationContext() {
        return context;
    }

    public void connect(String urlString, OnConnectListener listener) {
        OkHttpClient client = getOkHttpClient();
        if (client == null) return;
        Request request = new Request.Builder()
                .url(urlString)
                .build();

        Response response;
        String result;
        try {
            response = client.newCall(request).execute();
            result = response.body().string();
            onConnect(listener, result);
        } catch (IOException e) {
            e.printStackTrace();
            onError(listener, e.toString());
        }
    }

    public void initChatServer(String urlString, WebSocketListener webSocketListener) {
        OkHttpClient client = getOkHttpClient();
        Request request = new Request.Builder()
                .url(urlString)
                .build();
        client.newWebSocket(request, webSocketListener);
    }

    private void onConnect(OnConnectListener listener, String response) {
        if (listener != null) {
            listener.onConnect(response);
        }
    }

    private void onError(OnConnectListener listener, String error) {
        if (listener != null) {
            listener.onError(error);
        }
    }

    public interface OnConnectListener {
        void onConnect(String response);

        void onError(String error);
    }

    @Nullable
    private OkHttpClient getOkHttpClient() {
        SSLSocketFactory sslf = HttpsUtils.getSslSocketFactoryByCert(getApplicationContext(), SERVER_CERT_PATH);
        if (sslf == null) {
            return null;
        }
        OkHttpClient client = new OkHttpClient.Builder()
                .sslSocketFactory(sslf, getX509TrustManager()[0])
                .hostnameVerifier(getHostnameVerifier())
//                .connectionSpecs(Collections.singletonList(getConnectionSpec())) // optional
                .build();
        return client;
    }


    public String test(String url) {
        Log.d(TAG, "test() called with: url = [" + url + "]");
//        String result = testHttps(url);
        String result = testOkHttp(url);
        Log.d(TAG, "test() returned: " + result);
        return result;
    }

    private String testHttps(String urlString) {
        String response = null;
        try {
            URL url = new URL(urlString);

            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
//            HttpsURLConnection.setDefaultSSLSocketFactory(getSslSocketFactory());  // no use
            httpsURLConnection.setSSLSocketFactory(getSslSocketFactory());

            httpsURLConnection.setHostnameVerifier(getHostnameVerifier());

//            httpsURLConnection.setRequestMethod("GET");//"POST" "GET"
//            httpsURLConnection.setDoOutput(true);
//            httpsURLConnection.setDoInput(true);
//            httpsURLConnection.setUseCaches(false);
//            httpsURLConnection.setRequestProperty("Content-Type", "binary/octet-stream");
//            httpsURLConnection.setConnectTimeout(30000);

            InputStream result = httpsURLConnection.getInputStream();
            response = HttpsUtils.getString(result);
            Log.i(TAG, "testHttps, result: " + response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }


    private String testOkHttp(String urlString) {
        SSLSocketFactory sslf = HttpsUtils.getSslSocketFactoryByCert(getApplicationContext(), SERVER_CERT_PATH);
        sslf = HttpsUtils.getSslSocketFactoryByStore(getApplicationContext(), KEY_STORE_TRUST_PATH, KEY_STORE_TRUST_PASSWORD);
        if (sslf == null) {
            return null;
        }
        OkHttpClient client = new OkHttpClient.Builder()
                .sslSocketFactory(sslf, getX509TrustManager()[0])
                .hostnameVerifier(getHostnameVerifier())
//                .connectionSpecs(Collections.singletonList(getConnectionSpec())) // optional
                .build();
        Request request = new Request.Builder()
                .url(urlString + "ws")
//                .url("wss://192.168.111.39:8898/ws")
                .build();


        /*Response response = null;
        String result = null;
        try {
            response = client.newCall(request).execute();
            result = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        WebSocket webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                super.onOpen(webSocket, response);
                Log.d(TAG, "onOpen() called with: webSocket = [" + webSocket + "], response = [" + response + "]");
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                super.onMessage(webSocket, text);
                Log.d(TAG, "onMessage() called with: webSocket = [" + webSocket + "], text = [" + text + "]");
            }

            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {
                super.onMessage(webSocket, bytes);
                Log.d(TAG, "onMessage() called with: webSocket = [" + webSocket + "], bytes = [" + bytes + "]");
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                super.onClosing(webSocket, code, reason);
                Log.d(TAG, "onClosing() called with: webSocket = [" + webSocket + "], code = [" + code + "], reason = [" + reason + "]");
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                super.onClosed(webSocket, code, reason);
                Log.d(TAG, "onClosed() called with: webSocket = [" + webSocket + "], code = [" + code + "], reason = [" + reason + "]");
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                super.onFailure(webSocket, t, response);
                Log.d(TAG, "onFailure() called with: webSocket = [" + webSocket + "], t = [" + t + "], response = [" + response + "]");
            }
        });
        webSocket.send("hi ~~ hello");
        return webSocket.toString();
    }

    private static ConnectionSpec getConnectionSpec() {
        ConnectionSpec spec = new ConnectionSpec
                .Builder(ConnectionSpec.MODERN_TLS)
                .tlsVersions(TlsVersion.TLS_1_0)
                .cipherSuites(CipherSuite.TLS_RSA_WITH_AES_128_CBC_SHA256,
                        CipherSuite.TLS_RSA_WITH_AES_128_CBC_SHA,
                        CipherSuite.TLS_RSA_WITH_AES_256_CBC_SHA256,
                        CipherSuite.TLS_RSA_WITH_AES_256_CBC_SHA,
                        CipherSuite.TLS_RSA_WITH_3DES_EDE_CBC_SHA)
                .build();
        return spec;
    }

    @NonNull
    private HostnameVerifier getHostnameVerifier() {
        return new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
    }


    private SSLSocketFactory getSslSocketFactory() {
//        return new PreferredCipherSuiteSSLSocketFactory(getSSLContext(getApplicationContext()).getSocketFactory());
        return new PreferredCipherSuiteSSLSocketFactory(HttpsUtils.getSslSocketFactoryByCert(getApplicationContext(), SERVER_CERT_PATH));
    }


    private static final String KEY_STORE_TYPE_BKS = "bks";//证书类型 固定值
    private static final String KEY_STORE_TYPE_P12 = "PKCS12";//证书类型 固定值

    private static final String KEY_STORE_CLIENT_PATH = "client.p12";//客户端要给服务器端认证的证书
    private static final String SERVER_CERT_PATH = "server.cer";//服务器端的证书
    private static final String KEY_STORE_TRUST_PATH = "client.truststore";//客户端验证服务器端的证书库
    private static final String KEY_STORE_PASSWORD = "abc123";// 客户端证书密码
    private static final String KEY_STORE_TRUST_PASSWORD = "abc123";//客户端证书库密码

    /**
     * 获取SSLContext
     *
     * @param context 上下文
     * @return SSLContext
     */
    private static SSLContext getSSLContext(Context context) {
        try {
            // 服务器端需要验证的客户端证书
            KeyStore keyStore = KeyStore.getInstance(KEY_STORE_TYPE_P12);
            // 客户端信任的服务器端证书
            KeyStore trustStore = KeyStore.getInstance(KEY_STORE_TYPE_BKS);

            InputStream ksIn = context.getResources().getAssets().open(KEY_STORE_CLIENT_PATH);
            InputStream tsIn = context.getResources().getAssets().open(KEY_STORE_TRUST_PATH);
            try {
                keyStore.load(ksIn, KEY_STORE_PASSWORD.toCharArray());
                trustStore.load(tsIn, KEY_STORE_TRUST_PASSWORD.toCharArray());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    ksIn.close();
                } catch (Exception ignore) {
                }
                try {
                    tsIn.close();
                } catch (Exception ignore) {
                }
            }
            SSLContext sslContext = SSLContext.getInstance("TLS");
//            SSLContext sslContext = SSLContext.getInstance("SSL");
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(trustStore);
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("X509");
            keyManagerFactory.init(keyStore, KEY_STORE_PASSWORD.toCharArray());
            /*sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);*/

            sslContext.init(null, getX509TrustManager(), new SecureRandom());

//            SSLEngine engine = sslContext.createSSLEngine();
//            engine.setEnabledProtocols(new String[] { "TLSv1.2", "TLSv1", "SSLv3" });

            return sslContext;
        } catch (Exception e) {
            Log.e("tag", e.getMessage(), e);
        }
        return null;
    }

    private static X509TrustManager[] getX509TrustManager() {
        return new X509TrustManager[]{new X509TrustManager() {
            public void checkClientTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }

            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        }};
    }

    public CertificatePinner getPinnedCerts() {
        return new CertificatePinner.Builder()
                .add("domain.com", "sha1/theSha=")
                .build();
    }


}
