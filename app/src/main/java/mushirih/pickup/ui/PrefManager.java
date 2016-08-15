package mushirih.pickup.ui;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by p-tah on 26/05/2016.
 */
public class PrefManager {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Context context;

    int PRIVATE_MODE=0;

    private static final String PREF_NAME="my_intro";
    private static  final String IS_FIRST_LAUNCH="IS_FIRST_LAUNCH";

    public PrefManager(Context context) {

        this.context = context;

        sharedPreferences=context.getSharedPreferences(PREF_NAME,PRIVATE_MODE);
        editor=sharedPreferences.edit();
    }

    public void setIsFirstLaunch(boolean isFirstTime){
        editor.putBoolean(IS_FIRST_LAUNCH,isFirstTime);
        editor.commit();
    }

    public boolean isFirstLaunch(){
        return sharedPreferences.getBoolean(IS_FIRST_LAUNCH,true);
    }
}
