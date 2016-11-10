package mushirih.pickup.internal;

import android.app.Application;
import android.content.Intent;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import mushirih.pickup.ui.MainActivity;

/**
 * Created by p-tah on 13/08/2016.
 */
public class MyApplication extends Application {

    public static final String TAG = MyApplication.class
            .getSimpleName();




    private static MyApplication mInstance;

    private RequestQueue requestQueue;

    private MyPreferenceManager pref;
    public static final String ADD_USER = "http://noshybakery.co.ke/PICKUP/v1/user/add";
    public static String Online_Login="http://noshybakery.co.ke/PICKUP/v1/user/login";
    public static final String ONLINE_ALPHA_REQUEST = "http://noshybakery.co.ke/PICKUP/v1/init_request";
    public static final String ONLINE_CALCULATE_COST = "http://noshybakery.co.ke/PICKUP/v1/calculate_cost";
    public static final String IMAGE_UPLOAD_URL = "http://noshybakery.co.ke/PICKUP/include/image_upload.php";

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }
    public static synchronized MyApplication getInstance() {
        return mInstance;
    }
    public RequestQueue getRequestQueue(){
        if (requestQueue==null){
            requestQueue= Volley.newRequestQueue(getApplicationContext());
        }
return requestQueue;
    }
    public MyPreferenceManager getPrefManager() {
        if (pref == null) {
            pref = new MyPreferenceManager(this);
        }
        return pref;
    }
    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }
    public <T> void addToRequestQueue(Request<T> req) {
//        req.setTag(TAG);
        req.setShouldCache(false);
        getRequestQueue().add(req);
    }
    public void cancelPendingRequests(Object tag) {
        if (requestQueue != null) {
            requestQueue.cancelAll(tag);
        }
    }
    public void logout() {
        pref.clear();
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
