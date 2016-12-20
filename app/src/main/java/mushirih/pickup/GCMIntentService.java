package mushirih.pickup;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;

import java.util.StringTokenizer;

import mushirih.pickup.cm.ServerUtilities;
import mushirih.pickup.internal.MyPreferenceManager;
import mushirih.pickup.internal.User;
import mushirih.pickup.ui.RateTransaction;
import mushirih.pickup.ui.TrackLoad;

import static mushirih.pickup.cm.CommonUtilities.SENDER_ID;
import static mushirih.pickup.cm.CommonUtilities.displayMessage;

/**
 * Created by p-tah on 28/09/2016.
 */

    public class GCMIntentService extends GCMBaseIntentService {

        private static final String TAG = "GCMIntentService";
    private static NotificationManager notificationManager;


        public GCMIntentService() {
            super(SENDER_ID);
        }

        /**
         * Method called on device registered
         **/
        @Override
        protected void onRegistered(Context context, String registrationId) {
            Log.i(TAG, "Device registered: regId = " + registrationId);
            displayMessage(context, "Your device registred with GCM");
//            Log.d("NAME", User.name);
            ServerUtilities.register(context, User.name, User.email, registrationId);
        }

        /**
         * Method called on device un registred
         * */
        @Override
        protected void onUnregistered(Context context, String registrationId) {
            Log.i(TAG, "Device unregistered");
            displayMessage(context, getString(R.string.gcm_unregistered));
            ServerUtilities.unregister(context, registrationId);
        }

        /**
         * Method called on Receiving a new message
         * */
        @Override
        protected void onMessage(Context context, Intent intent) {
            Log.i(TAG, "Received message");
            String message = intent.getExtras().getString("messages");

            displayMessage(context, message);
            // notifies user
            generateNotification(context, message);
        }

        /**
         * Method called on receiving a deleted message
         * */
        @Override
        protected void onDeletedMessages(Context context, int total) {
            Log.i(TAG, "Received deleted messages notification");
            String message = getString(R.string.gcm_deleted, total);
            displayMessage(context, message);
            // notifies user
            generateNotification(context, message);
        }

        /**
         * Method called on Error
         * */
        @Override
        public void onError(Context context, String errorId) {
            Log.i(TAG, "Received error: " + errorId);
            displayMessage(context, getString(R.string.gcm_error, errorId));
        }

        @Override
        protected boolean onRecoverableError(Context context, String errorId) {
            // log message
            Log.i(TAG, "Received recoverable error: " + errorId);
            displayMessage(context, getString(R.string.gcm_recoverable_error,
                    errorId));
            return super.onRecoverableError(context, errorId);
        }

        /**
         * Issues a notification to inform the user that server has sent a message.
         */
        private static void generateNotification(Context context, String message) {
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
            }else{
            String title = context.getString(R.string.app_name);
                String number="00";
                String id="0000";
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
                Intent notificationIntent2 = new Intent(context,TrackLoad.class);
                // set intent so it does not start a new activity
                notificationIntent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_SINGLE_TOP);
                notificationIntent2.putExtra("id",id);
                notificationIntent2.putExtra("num",number);
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
                builder.addAction(R.drawable.call,"Call",intent);
                builder.addAction(R.drawable.track,"Track",intentTrack);
//            notificationManager.notify(0,builder.build());
            notificationManager.notify((int) System.currentTimeMillis(), builder.build());
        }
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
;
