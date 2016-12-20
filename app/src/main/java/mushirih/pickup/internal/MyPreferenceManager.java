package mushirih.pickup.internal;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by p-tah on 13/08/2016.
 */

public class MyPreferenceManager {



    private String TAG = MyPreferenceManager.class.getSimpleName();

    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "mushirihx";

    // All Shared Preferences Keys
    private static final String KEY_MY_DRIVNUM = "drivers_number";
    private static final String TRACK_DRIVER_FOR_ME ="track driver for me" ;
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_NOTIFICATIONS = "notifications";
    private static final String KEY_MY_TRANSATION = "my_transaction";
    private static  final String IS_FIRST_LAUNCH="IS_FIRST_LAUNCH";

    // Constructor
    public MyPreferenceManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }
    public void storeUser(User user) {
        editor.putString(KEY_USER_ID, user.getId());
        editor.putString(KEY_USER_NAME, user.getName());
        editor.putString(KEY_USER_EMAIL, user.getEmail());
        editor.commit();

        Log.e(TAG, "User is stored in shared preferences. " + user.getName() + ", " + user.getEmail());
    }
    public void logOutUser() {
        editor.putString(KEY_USER_ID,null);
        editor.putString(KEY_USER_NAME, null);
        editor.putString(KEY_USER_EMAIL, null);
        editor.commit();

        Log.e(TAG, "User Logged Out");
    }

    public User getUser() {
        if (pref.getString(KEY_USER_ID, null) != null) {
            String id, name, email;
            id = pref.getString(KEY_USER_ID, null);
            name = pref.getString(KEY_USER_NAME, null);
            email = pref.getString(KEY_USER_EMAIL, null);

            User user = new User(id, name, email);
            return user;
        }
        return null;
    }

    public void addNotification(String notification) {

        // get old notifications
        String oldNotifications = getNotifications();

        if (oldNotifications != null) {
            oldNotifications += "|" + notification;
        } else {
            oldNotifications = notification;
        }

        editor.putString(KEY_NOTIFICATIONS, oldNotifications);
        editor.commit();
    }

    public String getNotifications() {
        return pref.getString(KEY_NOTIFICATIONS, null);
    }

    public void clear() {
        editor.clear();
        editor.commit();
    }

    public void storeTRansactionId(String request_id_global) {

        editor.putString(KEY_MY_TRANSATION,request_id_global);
        editor.commit();
    }
    public String getTransaction(){
       String trans="0000";
        if(pref.getString(KEY_MY_TRANSATION,null)!=null){
             trans=pref.getString(KEY_MY_TRANSATION,null);
        }
        return trans;
    }
    public void storenumber(String num) {

        editor.putString(KEY_MY_DRIVNUM,num);
        editor.commit();
    }
    public String getnumber(){
        String num="0000";
        if(pref.getString(KEY_MY_DRIVNUM,null)!=null){
            num=pref.getString(KEY_MY_DRIVNUM,null);
        }
        return num;
    }
    public void setIsFirstLaunch(boolean isFirstTime){
        editor.putBoolean(IS_FIRST_LAUNCH,isFirstTime);
        editor.commit();
    }

    public boolean isFirstLaunch(){
        return pref.getBoolean(IS_FIRST_LAUNCH,true);
    }

    public void setTracking(boolean b) {
        editor.putBoolean(TRACK_DRIVER_FOR_ME,b);
        editor.commit();
    }
    public boolean getTracking(){
        return pref.getBoolean(TRACK_DRIVER_FOR_ME,false);
    }
}