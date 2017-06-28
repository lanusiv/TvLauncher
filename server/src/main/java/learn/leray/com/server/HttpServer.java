package learn.leray.com.server;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Handler;
import android.util.Log;

import com.koushikdutta.async.AsyncServer;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.AsyncHttpGet;
import com.koushikdutta.async.http.AsyncHttpPost;
import com.koushikdutta.async.http.AsyncHttpResponse;
import com.koushikdutta.async.http.Multimap;
import com.koushikdutta.async.http.WebSocket;
import com.koushikdutta.async.http.WebSocket.StringCallback;
import com.koushikdutta.async.http.body.MultipartFormDataBody;
import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.koushikdutta.async.http.server.AsyncHttpServer.WebSocketRequestCallback;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;
import com.koushikdutta.async.http.server.HttpServerRequestCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import static learn.leray.com.server.MainActivity.MSG_CHAT;
import static learn.leray.com.server.MainActivity.MSG_UPDATE;


public class HttpServer implements HttpServerRequestCallback {

	private static final String TAG = "HttpServer";

	private static final int PORT_80 = 80;

	private static final int PORT_443 = 8443;

	private AsyncHttpServer server = new AsyncHttpServer();
	private AsyncServer mAsyncServer = new AsyncServer();

	private Context mContext;

	private static HttpServer mHttpServer;

	private static Handler mainHandler;

	public static void start(Context context) {
		start(context, null);
	}

	public static void start(Context context, Handler handler) {
		if (handler != null) {
			mainHandler = handler;
		}
		if (mHttpServer == null) {
			mHttpServer = new HttpServer(context);
		}
		try {
			mHttpServer.startHttpsServer();
		} catch (Exception e) {
			e.printStackTrace();
		}
		logi("Start Http Server");
	}

	public static void stop() {
		if (mHttpServer != null) {
			mHttpServer.stopHttpServer();
		}
		mHttpServer = null;
	}

	private HttpServer(Context context) {
		this.mContext = context;
	}


	private static InetAddress getInetAddress() {
		try {
			for (Enumeration en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface networkInterface = (NetworkInterface) en.nextElement();

				for (Enumeration enumIpAddr = networkInterface.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = (InetAddress) enumIpAddr.nextElement();

					if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
						return inetAddress;
					}
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
			Log.e(TAG, "Error getting the network interface information");
		}

		return null;
	}

	private InputStream getAssetInputStream(String fileName) {
		AssetManager assetManager = mContext.getAssets();
		InputStream is = null;
		try {
			is = assetManager.open(fileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new BufferedInputStream(is);
	}

	@Override
	public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
		Log.i(TAG, "request.getPath(): " + request.getPath());
		String path = request.getPath();
		Multimap params = request.getQuery();
		if (path.equals("/hello")) {
			response.send("<h1>Hello!</h1>");
		} else {
			response.send("Welcome!");
		}

	}

	private void stopHttpServer() {
		if (server != null) {
			server.stop();
		}
		if (mAsyncServer != null) {
			mAsyncServer.stop();
		}
		server = null;
		mAsyncServer = null;
		notifyUi("server stopped");
	}

	private void startHttpsServer() throws Exception {
		Log.d(TAG, "startHttpsServer() called");
		notifyUi("starting https server...");
//		final int port = 8898;
		final String keystorePath = "";
		final String keystorePassword = "abc123";  //storepass
		final String keystoreType = KeyStore.getDefaultType();//"jks";
		Log.i(TAG, "startHttpsServer, KeyStore.getDefaultType(): " + KeyStore.getDefaultType());

		KeyManagerFactory kmf = KeyManagerFactory.getInstance("X509");
		KeyStore ks = KeyStore.getInstance(keystoreType);

		ks.load(mContext.getResources().openRawResource(R.raw.server), keystorePassword.toCharArray());
		kmf.init(ks, keystorePassword.toCharArray());


		TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		KeyStore ts = KeyStore.getInstance(keystoreType);
		ts.load(mContext.getResources().openRawResource(R.raw.server), keystorePassword.toCharArray());
		tmf.init(ts);

		SSLContext sslContext = SSLContext.getInstance("TLS");
		sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

		AsyncHttpServer httpServer = new AsyncHttpServer();
		httpServer.listenSecure(PORT_443, sslContext);
		httpServer.get("/", new HttpServerRequestCallback() {
			@Override
			public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
				logi("path: " + request.getPath());
				response.setContentType("text/html");
				response.send("<h1>hello!</h1>");
			}
		});

		httpServer.websocket("/wss", null, new WebSocketRequestCallback() {
			@Override
			public void onConnected(final WebSocket webSocket, AsyncHttpServerRequest request) {
				Log.d(TAG, "onConnected() called with: webSocket = [" + webSocket + "], request = [" + request + "]");
				webSocket.setStringCallback(new StringCallback() {
					@Override
					public void onStringAvailable(String s) {
						notifyUiOnMessage(s);
						String reply = new StringBuffer(s).reverse().toString();
						webSocket.send(reply);
					}
				});
			}
		});

		new Thread(new Runnable() {

			@Override
			public void run() {
				InetAddress ad = getInetAddress();
				if (ad != null) {
					String ip = ad.getHostName();
					String serverInfo = "https://" + ip + ":" + PORT_443;
					Log.d(TAG, "https server started, address: " + serverInfo);
					notifyUi(serverInfo);
				}
			}
		}).start();
		Thread.sleep(1000);

		AsyncHttpClient.getDefaultInstance().getSSLSocketMiddleware().setSSLContext(sslContext);
		AsyncHttpClient.getDefaultInstance().getSSLSocketMiddleware().setTrustManagers(tmf.getTrustManagers());
		AsyncHttpClient.getDefaultInstance().executeString(new AsyncHttpGet("https://localhost/"), null).get();
	}

	private void notifyUi(String msg) {
		Log.d(TAG, "notify() called with: msg = [" + msg + "]");
		if (mainHandler != null) {
			mainHandler.obtainMessage(MSG_UPDATE, msg).sendToTarget();
		}
	}

	private void notifyUiOnMessage(String msg) {
		Log.d(TAG, "notify() called with: msg = [" + msg + "]");
		if (mainHandler != null) {
			mainHandler.obtainMessage(MSG_CHAT, msg).sendToTarget();
		}
	}


	public void testUpload() throws Exception {
		Log.d(TAG, "testUpload() called");
		File dummy = mContext.getFileStreamPath("dummy.txt");
		final String FIELD_VAL = "bar";
		dummy.getParentFile().mkdirs();
		FileOutputStream fout = new FileOutputStream(dummy);
		byte[] zeroes = new byte[100000];
		for (int i = 0; i < 10; i++) {
			fout.write(zeroes);
		}
		fout.close();
//        StreamUtility.writeFile(dummy, DUMMY_VAL);

		AsyncHttpPost post = new AsyncHttpPost("http://localhost:18283");
		MultipartFormDataBody body = new MultipartFormDataBody();
		body.addStringPart("foo", FIELD_VAL);
		body.addFilePart("my-file", dummy);
		body.addStringPart("baz", FIELD_VAL);
		body.addStringPart("name", "tom&jerry");
		post.setBody(body);

		Future<String> ret = AsyncHttpClient.getDefaultInstance().executeString(post, new AsyncHttpClient.StringCallback() {
			@Override
			public void onCompleted(Exception e, AsyncHttpResponse source, String result) {
			}
		});

		String data = ret.get(10000, TimeUnit.MILLISECONDS);
		Log.d(TAG, "testUpload() returned: " + data);
	}

	private String toJsonString(int code, String msg) {
		JSONObject json = new JSONObject();
		try {
			json.put("code", code);
			json.put("msg", msg);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json.toString();
	}

	private static void logi(String msg) {
		Log.i(TAG, msg);
	}
}
