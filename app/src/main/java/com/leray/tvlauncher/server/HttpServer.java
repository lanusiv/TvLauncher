package com.leray.tvlauncher.server;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.koushikdutta.async.AsyncServer;
import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.leray.tvlauncher.server.request.KeyEventRequest;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Created by Administrator on 2017/2/28 0028.
 */

public class HttpServer {

	private static final String TAG = "HttpServer";

	private static final int PORT = 18283;

	private AsyncHttpServer server = new AsyncHttpServer();
	private AsyncServer mAsyncServer = new AsyncServer();

	private WeakReference<Context> mContext;

	private static HttpServer mHttpServer;

	public static String SERVER_ADDRESS = null;


	public static void start(Context context) {
		if (mHttpServer == null) {
			mHttpServer = new HttpServer(context);
		}
		mHttpServer.startHttpServer();
		Log.i(TAG, "Start Http Server");
	}

	public static void stop() {
		if (mHttpServer != null) {
			mHttpServer.stopHttpServer();
		}
		mHttpServer = null;
	}

	private HttpServer(Context context) {
		this.mContext = new WeakReference<>(context);
	}


	private void startHttpServer() {
		server.listen(mAsyncServer, PORT);

		server.setErrorCallback(new CompletedCallback() {

			@Override
			public void onCompleted(Exception e) {
				Log.i(TAG, "server error: " + e);
				startHttpServer();
				Log.i(TAG, "restart http server!");
			}
		});

		new Thread(new Runnable() {

			@Override
			public void run() {
				InetAddress ad = getInetAddress();
				if (ad != null) {
					String ip = ad.getHostName();
					SERVER_ADDRESS = "http://" + ip + ":" + PORT;
					Log.i(TAG, "startHttpServer, server started at " + ip + ":" + PORT);
				}
			}
		}).start();

		Context context = mContext.get();
        /*new RequestFileUpload(context).bindHttpServer(server);
        new RequestDownloadFile(context).bindHttpServer(server);
        new RequestGetImages(context).bindHttpServer(server);
        new RequestRestartTv(context).bindHttpServer(server);
        new RequestGetWifiList(context).bindHttpServer(server);
        new RequestConnectWifi(context).bindHttpServer(server);
        new RequestOpenAp(context).bindHttpServer(server);
        new RequestSendCmd(context).bindHttpServer(server);
        new RequestVoiceCmd(context).bindHttpServer(server);

        new BidirectionalWebUtil().bindHttpServer(server);
        new HeartbeatUtil().bindHttpServer(server);

        new AudioStreamReceiver(context).bindHttpServer(server);*/
		new KeyEventRequest(context).bindServer(server);

        /*HttpRequestFactory factory = new HttpRequestFactory(context, server);
        factory.doGet(HttpInterfaces.REQUEST_DOWNLOAD_DB);*/

        server.directory(context, "/", "html/dpad/");


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
	}

	private static InetAddress getInetAddress() {
		try {
			for (Enumeration en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
				NetworkInterface networkInterface = (NetworkInterface) en.nextElement();

				for (Enumeration enumIpAddr = networkInterface.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
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
		Context context = mContext.get();
		if (context == null) {
			return null;
		}
		AssetManager assetManager = context.getAssets();
		InputStream is = null;
		try {
			is = assetManager.open(fileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new BufferedInputStream(is);
	}

}
