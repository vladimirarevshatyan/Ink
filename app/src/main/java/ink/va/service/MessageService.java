package ink.va.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.ink.va.R;

import org.json.JSONObject;

import java.net.URISyntaxException;

import ink.va.activities.Chat;
import ink.va.interfaces.SocketListener;
import ink.va.utils.Notification;
import ink.va.utils.SharedHelper;

import static com.github.nkzawa.socketio.client.Socket.EVENT_CONNECT;
import static com.github.nkzawa.socketio.client.Socket.EVENT_CONNECT_ERROR;
import static com.github.nkzawa.socketio.client.Socket.EVENT_DISCONNECT;
import static ink.va.utils.Constants.EVENT_ADD_USER;
import static ink.va.utils.Constants.EVENT_NEW_MESSAGE;
import static ink.va.utils.Constants.EVENT_STOPPED_TYPING;
import static ink.va.utils.Constants.EVENT_TYPING;
import static ink.va.utils.Constants.NOTIFICATION_MESSAGE_BUNDLE_KEY;
import static ink.va.utils.Constants.SOCKET_URL;

public class MessageService extends Service {
    private SharedHelper sharedHelper;
    private String currentUserId;
    private final IBinder mBinder = new LocalBinder();
    private SocketListener onSocketListener;

    public MessageService() {
    }

    private com.github.nkzawa.socketio.client.Socket mSocket;

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sharedHelper = new SharedHelper(this);
        currentUserId = sharedHelper.getUserId();
        try {
            mSocket = IO.socket(SOCKET_URL);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        initSocket();
        Toast.makeText(this, "on start command", Toast.LENGTH_SHORT).show();
        return START_STICKY;
    }


    /**
     * Socket Listeners
     */
    private Emitter.Listener onNewMessageReceived = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            final JSONObject messageJson = (JSONObject) args[0];
            if (Notification.get().isSendingRemote()) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        sendGeneralNotification(messageJson);
                    }
                });
            } else {
                if (onSocketListener != null) {
                    onSocketListener.onNewMessageReceived(messageJson);
                }
            }
        }
    };


    private Emitter.Listener onUserTyping = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            JSONObject jsonObject = (JSONObject) args[0];
            if (onSocketListener != null) {
                onSocketListener.onUserTyping(jsonObject);
            }
        }
    };

    private Emitter.Listener onSocketConnected = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            mSocket.emit(EVENT_ADD_USER, currentUserId);
            if (onSocketListener != null) {
                onSocketListener.onSocketConnected();
            }
        }
    };

    private Emitter.Listener onSocketDisconnected = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (onSocketListener != null) {
                onSocketListener.onSocketDisconnected();
            }
        }
    };

    private Emitter.Listener onSocketConnectionError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (onSocketListener != null) {
                onSocketListener.onSocketConnectionError();
            }
        }
    };

    private Emitter.Listener onUserStoppedTyping = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (onSocketListener != null) {
                onSocketListener.onUserStoppedTyping();
            }
        }
    };

    private void initSocket() {
        mSocket.on(EVENT_CONNECT, onSocketConnected);
        mSocket.on(EVENT_CONNECT_ERROR, onSocketConnectionError);
        mSocket.on(EVENT_DISCONNECT, onSocketDisconnected);
        mSocket.on(EVENT_NEW_MESSAGE, onNewMessageReceived);
        mSocket.on(EVENT_STOPPED_TYPING, onUserStoppedTyping);
        mSocket.on(EVENT_TYPING, onUserTyping);
        mSocket.connect();
    }

    public class LocalBinder extends Binder {
        public MessageService getService() {
            return MessageService.this;
        }
    }

    public void setOnSocketListener(SocketListener onSocketListener) {
        this.onSocketListener = onSocketListener;
    }

    public boolean isSocketConnected() {
        return mSocket.connected();
    }

    public void emit(String event, Object... args) {
        mSocket.emit(event, args);
    }

    public void connectSocket() {
        mSocket.connect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        destroySocket();
    }

    private void destroySocket() {
        mSocket.disconnect();
        mSocket.off(EVENT_CONNECT, onSocketConnected);
        mSocket.off(EVENT_CONNECT_ERROR, onSocketConnectionError);
        mSocket.off(EVENT_DISCONNECT, onSocketDisconnected);
        mSocket.off(EVENT_NEW_MESSAGE, onNewMessageReceived);
        mSocket.off(EVENT_STOPPED_TYPING, onUserStoppedTyping);
        mSocket.off(EVENT_TYPING, onUserTyping);
    }

    private void sendGeneralNotification(JSONObject jsonObject) {

        NotificationManager notificationManagerCompat = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        android.support.v7.app.NotificationCompat.Builder builder = new android.support.v7.app.NotificationCompat.Builder(getApplicationContext());
        builder.setSmallIcon(R.drawable.ic_bell_ring_outline_white_24dp);
        builder.setAutoCancel(true);


        builder.setContentTitle("new message");
        builder.setContentText("new message");
        builder.setGroup("faskjflasfas");
        builder.setDefaults(android.app.Notification.DEFAULT_ALL);
        builder.setStyle(new NotificationCompat.BigTextStyle()
                .setSummaryText("new message")
                .setBigContentTitle("new message")
                .bigText("new messagea")
        );

        Intent requestsViewIntent = new Intent(getApplicationContext(), Chat.class);
        requestsViewIntent.putExtra(NOTIFICATION_MESSAGE_BUNDLE_KEY, jsonObject.toString());
        requestsViewIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        requestsViewIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent requestsViewPending = PendingIntent.getActivity(getApplicationContext(),
                Integer.valueOf(jsonObject.optInt("messageId")), requestsViewIntent, 0);
        builder.setContentIntent(requestsViewPending);

        builder.setShowWhen(true);
        android.app.Notification notification = builder.build();
        notificationManagerCompat.notify(Integer.valueOf(jsonObject.optInt("messageId")), notification);
    }
}
