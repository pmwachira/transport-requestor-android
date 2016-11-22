package mushirih.pickup.internal;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.HashSet;

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

    public void storeTRansactionId(String request_id_global, String alpha) {
        HashSet<String> trans=new HashSet<>();
        trans.add(request_id_global);
        trans.add(alpha);
        editor.putStringSet(KEY_MY_TRANSATION,trans);
        editor.commit();
    }
    public String[] getTransaction(){
        String[] trans = new String[0];
        if(pref.getStringSet(KEY_MY_TRANSATION,null)!=null){
             trans=pref.getStringSet(KEY_MY_TRANSATION,null).toArray(new String[pref.getStringSet(KEY_MY_TRANSATION,null).size()]);
        }
        return trans;
    }
    public void setIsFirstLaunch(boolean isFirstTime){
        editor.putBoolean(IS_FIRST_LAUNCH,isFirstTime);
        editor.commit();
    }

    public boolean isFirstLaunch(){
        return pref.getBoolean(IS_FIRST_LAUNCH,true);
    }
}