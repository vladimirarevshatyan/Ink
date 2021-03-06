/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kashmirr.social.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.kashmirr.social.R;

import java.util.Map;

import kashmirr.social.activities.HomeActivity;
import kashmirr.social.activities.RequestsView;
import kashmirr.social.activities.SplashScreen;
import kashmirr.social.utils.SharedHelper;

import static kashmirr.social.utils.Constants.NOTIFICAITON_TYPE_GLOBAL_CHAT_MESSAGE;
import static kashmirr.social.utils.Constants.NOTIFICATION_BUNDLE_EXTRA_KEY;
import static kashmirr.social.utils.Constants.NOTIFICATION_TYPE_CHAT_ROULETTE;
import static kashmirr.social.utils.Constants.NOTIFICATION_TYPE_LOCATION_REQUEST_ACCEPTED;
import static kashmirr.social.utils.Constants.NOTIFICATION_TYPE_LOCATION_REQUEST_DECLINED;
import static kashmirr.social.utils.Constants.NOTIFICATION_TYPE_LOCATION_SESSION_ENDED;
import static kashmirr.social.utils.Constants.NOTIFICATION_TYPE_LOCATION_UPDATES;
import static kashmirr.social.utils.Constants.NOTIFICATION_TYPE_REQUESTING_LOCATION;

public class NotificationService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    private static final String GROUP_KEY_MESSAGES = "Messages_Group";
    private SharedHelper mSharedHelper;

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */


    // [START receive_message]
    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {
        // Build the notification and add the action
        mSharedHelper = new SharedHelper(this);


        final Map<String, String> response = remoteMessage.getData();
        if (response.get("type") == null) {
            return;
        }
        String type = response.get("type");

        switch (type) {

            case NOTIFICATION_TYPE_CHAT_ROULETTE:
                Intent intent = new Intent(getPackageName() + "WaitRoom");
                intent.putExtra("currentUserId", response.get("currentUserId"));
                intent.putExtra("opponentId", response.get("opponentId"));
                intent.putExtra("message", response.get("message"));
                intent.putExtra("isDisconnected", response.get("isDisconnected"));
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                break;

            case NOTIFICAITON_TYPE_GLOBAL_CHAT_MESSAGE:
                intent = new Intent(getPackageName() + ".GlobalVipChat");
                intent.putExtra("data", remoteMessage);
                LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
                localBroadcastManager.sendBroadcast(intent);
                break;

            case NOTIFICATION_TYPE_REQUESTING_LOCATION:
                String requesterName = response.get("requesterName");
                String requesterId = response.get("requesterId");
                sendLocationRequestNotification(getApplicationContext(), requesterId, requesterName);
                break;

            case NOTIFICATION_TYPE_LOCATION_SESSION_ENDED:
                requesterName = response.get("requesterName");
                intent = new Intent(getPackageName() + ".Chat");
                intent.putExtra("requesterName", requesterName);
                intent.putExtra("type", NOTIFICATION_TYPE_LOCATION_SESSION_ENDED);
                localBroadcastManager = LocalBroadcastManager.getInstance(this);
                localBroadcastManager.sendBroadcast(intent);
                break;


            case NOTIFICATION_TYPE_LOCATION_REQUEST_DECLINED:
                requesterName = response.get("requesterName");
                intent = new Intent(getPackageName() + ".Chat");
                intent.putExtra("requesterName", requesterName);
                intent.putExtra("type", "locationSession");
                intent.putExtra("hasAccepted", false);
                localBroadcastManager = LocalBroadcastManager.getInstance(this);
                localBroadcastManager.sendBroadcast(intent);
                break;

            case NOTIFICATION_TYPE_LOCATION_REQUEST_ACCEPTED:
                requesterName = response.get("requesterName");
                intent = new Intent(getPackageName() + ".Chat");
                intent.putExtra("requesterName", requesterName);
                intent.putExtra("type", "locationSession");
                intent.putExtra("hasAccepted", true);
                localBroadcastManager = LocalBroadcastManager.getInstance(this);
                localBroadcastManager.sendBroadcast(intent);
                break;

            case NOTIFICATION_TYPE_LOCATION_UPDATES:
                String latitude = response.get("latitude");
                String longitude = response.get("longitude");

                intent = new Intent(getPackageName() + ".Chat");
                intent.putExtra("latitude", latitude);
                intent.putExtra("longitude", longitude);
                intent.putExtra("type", NOTIFICATION_TYPE_LOCATION_UPDATES);
                localBroadcastManager = LocalBroadcastManager.getInstance(this);
                localBroadcastManager.sendBroadcast(intent);
                break;

            default:
                sendGeneralNotification(getApplicationContext(),
                        response.get("uniqueId"), response.get("title"),
                        response.get("content"), null);
        }

    }

    private void sendLikeNotification(Context context, String requesterName, String requestId) {

        NotificationManager notificationManagerCompat = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        Intent requestsViewIntent = new Intent(context, mSharedHelper.isLoggedIn() ? HomeActivity.class : SplashScreen.class);
        PendingIntent likeNotification = PendingIntent.getActivity(context, Integer.valueOf(requestId), requestsViewIntent, 0);

        android.support.v7.app.NotificationCompat.Builder builder = new android.support.v7.app.NotificationCompat.Builder(context);
        builder.setSmallIcon(R.drawable.ic_account_multiple_plus_white_24dp);
        builder.setAutoCancel(true);
        builder.setContentIntent(likeNotification);
        builder.setContentTitle(requesterName + getString(R.string.likedPostText));
        builder.setGroup(GROUP_KEY_MESSAGES);
        builder.setDefaults(android.app.Notification.DEFAULT_ALL);
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(getString(R.string.toViewTheRequest)));
        builder.setShowWhen(true);
        android.app.Notification notification = builder.build();
        notificationManagerCompat.notify(Integer.valueOf(requestId), notification);
    }


    private void sendLocationRequestNotification(Context context, String requestId, String requesterName) {

        NotificationManager notificationManagerCompat = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        Intent requestsViewIntent = new Intent(context, RequestsView.class);
        requestsViewIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);


        PendingIntent requestsViewPending = PendingIntent.getActivity(context, Integer.valueOf(requestId), requestsViewIntent, 0);
        android.support.v7.app.NotificationCompat.Builder builder = new android.support.v7.app.NotificationCompat.Builder(context);
        builder.setSmallIcon(R.drawable.location_vector);
        builder.setAutoCancel(true);


        builder.setContentTitle(requesterName + " " + getString(R.string.requestedShareLocation));
        builder.setContentText(getString(R.string.requestedTextBody));
        builder.setGroup(GROUP_KEY_MESSAGES);
        builder.setDefaults(android.app.Notification.DEFAULT_ALL);
        builder.setContentIntent(requestsViewPending);
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(getString(R.string.toViewTheRequest)));
        builder.setShowWhen(true);
        android.app.Notification notification = builder.build();
        notificationManagerCompat.notify(Integer.valueOf(requestId), notification);
    }

    public void sendGroupRequestNotification(Context context, String requesterName, String requestedGroup,
                                             String requestId) {

        NotificationManager notificationManagerCompat = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        Intent requestsViewIntent = new Intent(context, RequestsView.class);
        requestsViewIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);


        PendingIntent requestsViewPending = PendingIntent.getActivity(context, Integer.valueOf(requestId), requestsViewIntent, 0);
        android.support.v7.app.NotificationCompat.Builder builder = new android.support.v7.app.NotificationCompat.Builder(context);
        builder.setSmallIcon(R.drawable.ic_account_switch_white_24dp);
        builder.setAutoCancel(true);


        builder.setContentTitle(requesterName + " " + getString(R.string.requestedText) + " " +
                "'" + requestedGroup + "'");
        builder.setContentText(getString(R.string.requestedTextBody));
        builder.setGroup(GROUP_KEY_MESSAGES);
        builder.setDefaults(android.app.Notification.DEFAULT_ALL);
        builder.setContentIntent(requestsViewPending);
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(getString(R.string.requestedTextBody)));
        builder.setShowWhen(true);
        android.app.Notification notification = builder.build();
        notificationManagerCompat.notify(Integer.valueOf(requestId), notification);
    }

    public void sendFriendRequestNotification(Context context, String requesterName,
                                              String requestId) {

        NotificationManager notificationManagerCompat = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        Intent requestsViewIntent = new Intent(context, RequestsView.class);
        requestsViewIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);


        PendingIntent requestsViewPending = PendingIntent.getActivity(context, Integer.valueOf(requestId), requestsViewIntent, 0);
        android.support.v7.app.NotificationCompat.Builder builder = new android.support.v7.app.NotificationCompat.Builder(context);
        builder.setSmallIcon(R.drawable.ic_account_multiple_plus_white_24dp);
        builder.setAutoCancel(true);


        builder.setContentTitle(requesterName + " " + getString(R.string.tobeFriend));
        builder.setContentText(getString(R.string.toViewTheRequest));
        builder.setGroup(GROUP_KEY_MESSAGES);
        builder.setDefaults(android.app.Notification.DEFAULT_ALL);
        builder.setContentIntent(requestsViewPending);
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(getString(R.string.toViewTheRequest)));
        builder.setShowWhen(true);
        android.app.Notification notification = builder.build();
        notificationManagerCompat.notify(Integer.valueOf(requestId), notification);
    }

    private void sendGeneralNotification(Context context, String uniqueId,
                                         String title, String contentText,
                                         @Nullable Bundle extra) {

        NotificationManager notificationManagerCompat = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        android.support.v4.app.NotificationCompat.Builder builder = new android.support.v4.app.NotificationCompat.Builder(context);
        builder.setSmallIcon(R.drawable.ic_bell_ring_outline_white_24dp);
        builder.setAutoCancel(true);


        builder.setContentTitle(title);
        builder.setContentText(contentText);
        builder.setGroup(GROUP_KEY_MESSAGES);
        builder.setDefaults(android.app.Notification.DEFAULT_ALL);
        builder.setStyle(new NotificationCompat.BigTextStyle()
                .bigText(contentText)
        );

        Intent requestsViewIntent = new Intent(context, mSharedHelper.isLoggedIn() ? HomeActivity.class : SplashScreen.class);
        if (extra != null) {
            requestsViewIntent.putExtra(NOTIFICATION_BUNDLE_EXTRA_KEY, extra);
        }
        requestsViewIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        requestsViewIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent requestsViewPending = PendingIntent.getActivity(context, Integer.valueOf(uniqueId), requestsViewIntent, 0);
        builder.setContentIntent(requestsViewPending);

        builder.setShowWhen(true);
        android.app.Notification notification = builder.build();
        notificationManagerCompat.notify(Integer.valueOf(uniqueId), notification);
    }
}