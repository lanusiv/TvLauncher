package com.leray.tvlauncher.server.request;

import android.app.Instrumentation;
import android.content.Context;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;

import com.koushikdutta.async.http.Multimap;
import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;
import com.leray.tvlauncher.server.RequestInerfaces;

import static com.leray.tvlauncher.server.HttpServer.SERVER_ADDRESS;

/**
 * Created by leray on 2017/4/6.
 */

public class KeyEventRequest extends BaseRequest {

    public KeyEventRequest(Context context) {
        super(context);
    }

    @Override
    public void bindServer(AsyncHttpServer server) {
        server.get(RequestInerfaces.REQUEST_DPAD, this);
        Log.i(TAG, "bindServer: " + SERVER_ADDRESS + RequestInerfaces.REQUEST_DPAD);
    }

    @Override
    public boolean handlerRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
        Multimap query = request.getQuery();
        if (query == null) {
            response.send("wrong params");
        }
        String key = query.getString("key_code");
        String longPress = query.getString("long_press");
        int keyCode = Integer.valueOf(key);
        boolean isLongPress = Boolean.valueOf(longPress);
        if (isLongPress) {
            sendLongKeyEvent(keyCode);
        } else {
            sendKeyEvent(keyCode);
        }
        response.getHeaders().add("Access-Control-Allow-Origin", "*");
        response.send("Success");
        return true;
    }

    private void sendKeyEvent(int keyCode) {
        Instrumentation it = new Instrumentation();
        it.sendKeyDownUpSync(keyCode);
    }

    private void sendLongKeyEvent(int keyCode) {
        Instrumentation it = new Instrumentation();
        long downTime = SystemClock.uptimeMillis();
        long eventTime = downTime;
        KeyEvent keyEvent = new KeyEvent(downTime, eventTime, KeyEvent.ACTION_DOWN, keyCode, 1);
        it.sendKeySync(keyEvent);
        downTime += 100;
        keyEvent = new KeyEvent(downTime, eventTime, KeyEvent.ACTION_UP, keyCode, 0);
        it.sendKeySync(keyEvent);
    }
}
