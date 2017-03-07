package mushirih.pickup.firebase;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.text.TextUtils;
import android.util.Patterns;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import mushirih.pickup.R;


/**
 * Created by p-tah on 05/03/2017.
 */
public class NotificationUtils {
    private static String TAG=NotificationUtils.class.getSimpleName();
    private Context mContext;
    public NotificationUtils(Context context){
        mContext=context;
    }
    public void showNotificationMessage(String title, String message, final String timestamp, Intent intent,String imageUrl){
        if (TextUtils.isEmpty(message)){
            return;
        }
        final int icon= R.mipmap.ic_launcher;
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        final PendingIntent resultPendingIntent=PendingIntent.getActivity(
                mContext,
                0,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        final NotificationCompat.Builder builder=new NotificationCompat.Builder(mContext);
        final Uri alarmSound= Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE+"://"+mContext.getPackageName()+"/raw/notification");
        if(!TextUtils.isEmpty(imageUrl)){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
                if(imageUrl!=null && imageUrl.length()>4&& Patterns.WEB_URL.matcher(imageUrl).matches()){
                    Bitmap bitmap=getBitmapFromURL(imageUrl);
                    if(bitmap!=null){
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                            showBigNotificationMessage(bitmap,builder,icon,title,message,timestamp,resultPendingIntent,alarmSound);
                        }
                    }else{
                        showSmallNotification(builder,icon,title,message,timestamp,resultPendingIntent,alarmSound);
                        playNotificationSound();
                    }
                }
            }
        }
    }

    public void showSmallNotification(NotificationCompat.Builder builder, int icon, String title, String message, String timestamp, PendingIntent resultPendingIntent, Uri alarmSound) {
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

        inboxStyle.addLine(message);

        Notification notification;
        notification = builder.setSmallIcon(icon).setTicker(title).setWhen(0)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentIntent(resultPendingIntent)
                .setSound(alarmSound)
                .setStyle(inboxStyle)
                .setWhen(getTimeMilliSec(timestamp))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon))
                .setContentText(message)
                .build();

        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(Config.NOTIFICATION_ID, notification);
    }
    private Bitmap getBitmapFromURL(String imageUrl) {
        try {
            URL url=new URL(imageUrl);
            HttpURLConnection httpURLConnection= (HttpURLConnection) url.openConnection();
            httpURLConnection.setDoInput(true);
            InputStream inputStream=httpURLConnection.getInputStream();
            Bitmap bitmap=BitmapFactory.decodeStream(inputStream);
            return bitmap;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;

        }
    }
    public void playNotificationSound() {
        Uri uri=Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE+"://"+mContext.getPackageName()+"/raw/notification");
        Ringtone ringtone= RingtoneManager.getRingtone(mContext,uri);
        ringtone.play();
    }
    private long getTimeMilliSec(String timestamp) {
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date=format.parse(timestamp);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;

    }

    private void showBigNotificationMessage(Bitmap bitmap, NotificationCompat.Builder builder, int icon, String title, String message, String timestamp, PendingIntent resultPendingIntent, Uri alarmSound) {
        NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
        bigPictureStyle.setBigContentTitle(title);
        bigPictureStyle.setSummaryText(Html.fromHtml(message).toString());
        bigPictureStyle.bigPicture(bitmap);
        Notification notification;
        notification = builder.setSmallIcon(icon).setTicker(title).setWhen(0)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentIntent(resultPendingIntent)
                .setSound(alarmSound)
                .setStyle(bigPictureStyle)
                .setWhen(getTimeMilliSec(timestamp))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon))
                .setContentText(message)
                .build();

        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(Config.NOTIFICATION_ID_BIG_IMAGE, notification);
    }
    /**
     * Method checks if the app is in background or not
     */
    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }

    // Clears notification tray messages
    public static void clearNotifications(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }



}
