package mushirih.pickup.cm;

import android.content.Context;
import android.content.Intent;

/**
 * Created by p-tah on 28/09/2016.
 */
public class CommonUtilities {
    // give your server registration url here
    public static final String SERVER_URL = "http://noshybakery.co.ke/PICKUP/v1/user/gcm";

    // Google project id

//    public static final String SENDER_ID = "495371649755";
  public static final String SENDER_ID = "710981012625";

    /**
     * Tag used on log messages.
     */
    static final String TAG = "Mushhirih Push";

    public static final String DISPLAY_MESSAGE_ACTION =
            "mushirih.PICKUP.pushnotifications.DISPLAY_MESSAGE";

    public static final String EXTRA_MESSAGE = "messages";

    /**
     * Notifies UI to display a message.
     * <p>
     * This method is defined in the common helper because it's used both by
     * the UI and the background service.
     *
     * @param context application's context.
     * @param message message to be displayed.
     */
    public static void displayMessage(Context context, String message) {
        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtra(EXTRA_MESSAGE, message);
        context.sendBroadcast(intent);
    }
}
