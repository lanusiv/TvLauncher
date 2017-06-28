package learn.leray.com.server;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.concurrent.Executors;

import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private TextView serverInfoTv;
    private Button btnRestartSever;
    private TextView msgTv;
    private EditText serverAddressInput;
    private EditText msgInput;
    private Button btnConnect;
    private Button btnSend;
    private ScrollView mScrollView;

    private ScrollView mScrollView0;
    private TextView msgTv0;

    private HttpsClient httpsClient;

    private WebSocket webSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        serverInfoTv = (TextView) findViewById(R.id.textView);
        serverInfoTv.setText("");
        btnRestartSever = (Button) findViewById(R.id.button);
        msgTv = (TextView) findViewById(R.id.tv_msg);
        serverAddressInput = (EditText) findViewById(R.id.server_address);
        msgInput = (EditText) findViewById(R.id.message_input);
        btnConnect = (Button) findViewById(R.id.connect);
        btnSend = (Button) findViewById(R.id.send_msg);
        mScrollView = (ScrollView) findViewById(R.id.scrollView);

        mScrollView0 = (ScrollView) findViewById(R.id.scrollView0);
        msgTv0 = (TextView) findViewById(R.id.tv_msg0);

        HttpServer.start(getApplicationContext(), mHandler);

        httpsClient = HttpsClient.getInstance(getApplicationContext());
    }

    public void restartServer(View view) {
        btnRestartSever.setEnabled(false);
        HttpServer.stop();
        HttpServer.start(getApplicationContext(), mHandler);
        Log.i(TAG, "restartServer, server restarted");
    }

    public void connectServer(View view) {
        final String url = serverAddressInput.getText().toString().trim();

        execute(new Runnable() {
            @Override
            public void run() {
                httpsClient.connect(url, new HttpsClient.OnConnectListener() {
                    @Override
                    public void onConnect(String response) {
                        showLog("response from " + url + ": " + response);
                        httpsClient.initChatServer(url + "/wss", webSocketListener);
                    }

                    @Override
                    public void onError(String error) {
                        showLog("request failed, error: " + error);
                    }
                });
            }
        });
    }

    public void sendMessage(View view) {
        if (webSocket == null) {
            showLog("Connection not established!");
            return;
        }
        String content = msgInput.getText().toString();
        if (!TextUtils.isEmpty(content)) {
            msgInput.setText("");
            webSocket.send(content);
            showLog("Sent Message --> " + content);
        }
    }

    private WebSocketListener webSocketListener = new WebSocketListener() {
        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            Log.d(TAG, "onOpen() called with: webSocket = [" + webSocket + "], response = [" + response + "]");
            MainActivity.this.webSocket = webSocket;
            showLog("Chat session opened, " + response, true);
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            Log.d(TAG, "onMessage() called with: webSocket = [" + webSocket + "], text = [" + text + "]");
            showLog("Received Message --> " + text);
        }

        @Override
        public void onMessage(WebSocket webSocket, ByteString bytes) {
            Log.d(TAG, "onMessage() called with: webSocket = [" + webSocket + "], bytes = [" + bytes + "]");
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            Log.d(TAG, "onClosing() called with: webSocket = [" + webSocket + "], code = [" + code + "], reason = [" + reason + "]");
        }

        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {
            Log.d(TAG, "onClosed() called with: webSocket = [" + webSocket + "], code = [" + code + "], reason = [" + reason + "]");
            showLog("websocket is closed");
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            Log.d(TAG, "onFailure() called with: webSocket = [" + webSocket + "], t = [" + t + "], response = [" + response + "]");
            showLog("failed to connect, " + response);
        }
    };

    private void showLog(final String text) {
        showLog(text, false);
    }

    private void showLog(final String text, final boolean clear) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (clear) {
                    msgTv.setText("");
                }
                msgTv.append(text + "\n");
                mScrollView.arrowScroll(View.FOCUS_DOWN);
            }
        });
    }

    private void showLog0(final String text, final boolean clear) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (clear) {
                    msgTv0.setText("");
                }
                msgTv0.append(text + "\n");
                mScrollView0.arrowScroll(View.FOCUS_DOWN);
            }
        });
    }

    private void execute(Runnable task) {
        Executors.newSingleThreadExecutor().execute(task);
    }

    public static final int MSG_UPDATE = 1;
    public static final int MSG_CHAT = 2;
    public static final int SERVER_STATUS_SUCCEED = 10;
    public static final int SERVER_STATUS_FAILED = 11;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_UPDATE:
                    String text = msg.obj.toString();
                    serverInfoTv.setText(text);
                    showLog0("", true);
                    serverAddressInput.setText(text);
                    serverAddressInput.setSelection(text.length());
                    btnRestartSever.setEnabled(true);
                    break;
                case MSG_CHAT:
                    text = msg.obj.toString();
                    showLog0(text, false);
                    break;
            }
        }
    };
}
