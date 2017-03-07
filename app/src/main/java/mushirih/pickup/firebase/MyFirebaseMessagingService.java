package mushirih.pickup.firebase;

/**
 * Created by p-tah on 06/03/2017.
 *
 *
 */


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.StringTokenizer;

import mushirih.pickup.R;
import mushirih.pickup.internal.MyPreferenceManager;
import mushirih.pickup.ui.RateTransaction;
import mushirih.pickup.ui.TrackLoad;


public class MyFirebaseMessagingService extends FirebaseMessagingService {
private static final String TAG=MyFirebaseMessagingService.class.getSimpleName();
    private NotificationUtils notificationsUtils;
    private static NotificationManager notificationManager;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        JSONObject jsonObject=null;
        Log.e(TAG,"From:" +remoteMessage.getFrom());
         if (remoteMessage==null){
             return;
         }
        if(remoteMessage.getNotification()!=null){
            Log.e(TAG,"Notification Body: "+remoteMessage.getNotification().getBody());
            handleNotification(remoteMessage.getNotification().getBody());
        }
        if(remoteMessage.getData().size()>0){
            Log.e(TAG,"Data PayLoad: "+remoteMessage.getData().toString());
            try {
                jsonObject=new JSONObject(remoteMessage.getData().toString());
                handleDataMessage(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleNotification(String body) {
        if(!NotificationUtils.isAppIsInBackground(getApplicationContext())){
            Intent pushNotification=new Intent(Config.PUSH_NOTIFICATION);
            pushNotification.putExtra("message",body);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

            NotificationUtils notificationUtils=new NotificationUtils(getApplicationContext());
            notificationUtils.playNotificationSound();
        }else{
            Log.e(TAG,"Error: App in Background");
        }
    }
    private void handleDataMessage(JSONObject jsonObject) {
        /*
        Log.e(TAG,"push json " +jsonObject.toString());

        try {
            JSONObject data=jsonObject.getJSONObject("data");
            String title=data.getString("title");
            String message=data.getString("message");
            boolean isBackground=data.getBoolean("is_background");
            String imageUrl=data.getString("image");
            String timestamp=data.getString("timestamp");
            JSONObject payload= data.getJSONObject("payload");

            Log.e(TAG, "title: " + title);
            Log.e(TAG, "message: " + message);
            Log.e(TAG, "isBackground: " + isBackground);
            Log.e(TAG, "payload: " + payload.toString());
            Log.e(TAG, "imageUrl: " + imageUrl);
            Log.e(TAG, "timestamp: " + timestamp);

            if(!NotificationUtils.isAppIsInBackground(getApplicationContext())){
                Intent pushNotification=new Intent(Config.PUSH_NOTIFICATION);
                pushNotification.putExtra("message",message);
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                NotificationUtils notificationUtils=new NotificationUtils(getApplicationContext());
                notificationUtils.playNotificationSound();
            }else{
                Intent resultIntent=new Intent(getApplicationContext(), MainActivity.class);
                resultIntent.putExtra("message",message);

                if(TextUtils.isEmpty(imageUrl)){
                    showNotificationMessage(getApplicationContext(),title,message,timestamp,resultIntent);
                }else{
                    showNotificationMessageWithBigImage(getApplicationContext(),title,message,timestamp,resultIntent,imageUrl);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        */
        String message = null;
        Context context = getApplicationContext();
        try {
            message = jsonObject.getString("message");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        int icon = R.mipmap.ic_launcher;
        long when = System.currentTimeMillis();

        notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(icon, message, when);
        String firstMessage = "";
        String secondMessage = "";

        StringTokenizer splits = new StringTokenizer(message, "::::");
        if (null != splits) {
            firstMessage = splits.nextToken();
            secondMessage = splits.nextToken();
        }
        //TODO notification for successful transaction
        if (firstMessage.equals("COMPLETE")) {
            transactionCompete(context, secondMessage);
        } else {
            String title = context.getString(R.string.app_name);
            String number = "00";
            String id = "0000";
            StringTokenizer splits2 = new StringTokenizer(secondMessage, ">>>>");
            if (null != splits2) {
                number = splits2.nextToken();
                id = splits2.nextToken();
            }
            Intent notificationIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
            // set intent so it does not start a new activity
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent intent =
                    PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            Intent notificationIntent2 = new Intent(context, TrackLoad.class);
            // set intent so it does not start a new activity
            notificationIntent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_SINGLE_TOP);
            notificationIntent2.putExtra("id", id);
            notificationIntent2.putExtra("num", number);
            new MyPreferenceManager(context).storeTRansactionId(id);
            new MyPreferenceManager(context).storenumber(number);
            PendingIntent intentTrack =
                    PendingIntent.getActivity(context, 0, notificationIntent2, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context).setSmallIcon(R.drawable.ic)
                    .setContentTitle(title).setContentText(firstMessage + ": I will be transporting your load\nClick to call me.");
            builder.setDefaults(Notification.DEFAULT_SOUND);
            builder.setDefaults(Notification.DEFAULT_VIBRATE);
            builder.setStyle(new NotificationCompat.BigTextStyle().bigText(firstMessage + ": I will be transporting your load\nClick to call me."));
            builder.setAutoCancel(true);
            //builder.setContentIntent(intent);
            builder.addAction(R.drawable.call, "Call", intent);
            builder.addAction(R.drawable.track, "Track", intentTrack);
//            notificationManager.notify(0,builder.build());
            notificationManager.notify((int) System.currentTimeMillis(), builder.build());
        }
    }

    private void showNotificationMessageWithBigImage(Context applicationContext, String title, String message, String timestamp, Intent resultIntent, String imageUrl) {
        notificationsUtils = new NotificationUtils(applicationContext);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationsUtils.showNotificationMessage(title, message, timestamp, resultIntent, imageUrl);
    }

    private void showNotificationMessage(Context applicationContext, String title, String message, String timestamp, Intent resultIntent) {
       notificationsUtils = new NotificationUtils(applicationContext);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationsUtils.showNotificationMessage(title, message, timestamp, resultIntent,null);
    }
    private static void transactionCompete(Context context, String secondMessage) {
        String title = context.getString(R.string.app_name);
        //TODO THIS INTENT SHOULD SHOW COMPLETED AND ASK FOR RATING
        Intent notificationIntent = new Intent(context,RateTransaction.class);
        notificationIntent.putExtra("ID",secondMessage);
        // set intent so it does not start a new activity
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent =
                PendingIntent.getActivity(context, 0, notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder=new NotificationCompat.Builder(context).setSmallIcon(R.drawable.ic)
                .setContentTitle(title).setContentText("Your delivery is complete");
        builder.setDefaults(Notification.DEFAULT_SOUND);
        builder.setDefaults(Notification.DEFAULT_VIBRATE);
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText("Your delivery is complete"));
        builder.setAutoCancel(true);
        builder.setContentIntent(intent);
//            notificationManager.notify(0,builder.build());
        notificationManager.notify((int) System.currentTimeMillis(),builder.build());
    }

}
