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
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.StringTokenizer;

import mushirih.dropoff.R;
import mushirih.dropoff.internal.MyApplication;
import mushirih.dropoff.trans.RecievedRequest;


public class MyFirebaseMessagingService extends FirebaseMessagingService {
private static final String TAG=MyFirebaseMessagingService.class.getSimpleName();
    private NotificationUtils notificationsUtils;

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
        String message= null;
        Context context=getApplicationContext();
        try {
            message = jsonObject.getString("message");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        StringTokenizer stringTokenizer=new StringTokenizer(message, "::::");
        String code="";
        String name="";
        StringTokenizer splits = new StringTokenizer(message, "::::");
        if(null!=splits) {
            code = stringTokenizer.nextToken();
            name = stringTokenizer.nextToken();
        }
        String title = context.getString(R.string.app_name);
        Intent notificationIntent = new Intent(context, RecievedRequest.class);
        //TODO GET TIME OF NOW AND PASS TO INTENT
        notificationIntent.putExtra(MyApplication.TRANSACTION_ID,code);
        notificationIntent.putExtra(MyApplication.FLAG_FRESH_TRANSACTION,"1");
        //  set intent so it does not start a new activity
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent =
                PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder=new NotificationCompat.Builder(context).setSmallIcon(R.drawable.ic)

                .setContentTitle(title).setContentText(name+" requested for transport along your next route\nClick to view\n");
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(name+" requested for transport along your next route\nClick to view\n"/*+message*/));
        builder.setDefaults(Notification.DEFAULT_SOUND);
        builder.setDefaults(Notification.DEFAULT_VIBRATE);
        builder.setAutoCancel(true);
        builder.setContentIntent(intent);
        //notificationManager.notify(0,builder.build());
        notificationManager.notify((int) System.currentTimeMillis(),builder.build());
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


}
