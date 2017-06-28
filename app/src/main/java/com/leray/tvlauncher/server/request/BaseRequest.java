package com.leray.tvlauncher.server.request;

import android.content.Context;
import android.util.Log;

import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;
import com.koushikdutta.async.http.server.HttpServerRequestCallback;
import com.leray.tvlauncher.server.RequestInerfaces;

/**
 * Created by leray on 2017/4/6.
 */

public abstract class BaseRequest implements HttpServerRequestCallback {

    protected static final String TAG = BaseRequest.class.getSimpleName();

    private Context context;

    public BaseRequest(Context context) {
        this.context = context;
    }

    public abstract void bindServer(AsyncHttpServer server);

    @Override
    public final void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
        Log.d(TAG, "onRequest() called with: request = [" + request + "], response = [" + response + "]");
        Log.d(TAG, "onRequest: path: " + request.getPath());
        boolean handled = handlerRequest(request, response);
        if (!handled) {
            response.send("Not Implemented yet");
        }
    }

    public Context getContext() {
        return context;
    }

    public abstract boolean handlerRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response);
}
