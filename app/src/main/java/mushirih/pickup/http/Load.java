package mushirih.pickup.http;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import mushirih.pickup.internal.MyApplication;
import mushirih.pickup.ui.MainActivity;

/**
 * Created by p-tah on 09/08/2016.
 */
public class Load {
    //TODO CLASS RECIEVING ALL LOAD/REQUEST PROPERIES
    private static Bitmap IMAGE;
    private static int DAY;
    private static int MONTH;
    private static int YEAR;
    private static int HOUR;
    private static int MINUTE;
   private static Context mContext;
    private static String TAG="LOAD ALPHA REQUEST";
    private static String request_id_global="A";
    static ProgressDialog loading;
    private static Context CONTEXT;
    private static LatLng LOCATION_FROM;
    private static LatLng LOCATION_TO;
    private static String WEIGHT;
    private static ArrayList LOAD_CHAR;
   private static String NAMEE, IDD, NUMM;
   private static int DISTANCE_BETWEEN;


//DONE
    public static void setImage(Bitmap image) {
        IMAGE = image;
    }

    public static void setDate(int dayOfMonth, int monthOfYear, int year) {
        DAY=dayOfMonth;
        MONTH=monthOfYear;
        YEAR=year;
        
    }

    public static void setTime(int hourOfDay, int minute) {
        HOUR=hourOfDay;
        MINUTE=minute;
    }
    public static void bulkSet(Context mContext, LatLng location_from, LatLng location_to, String weight, ArrayList load_char, String namee, String idd, String numm, int distance_between) {
        CONTEXT=mContext;
        LOCATION_FROM=location_from;
        LOCATION_TO=location_to;
        WEIGHT=weight;
        LOAD_CHAR=load_char;
        NAMEE=namee;
        IDD=idd;
        NUMM=numm;
        DISTANCE_BETWEEN=distance_between;

    }

    public static void send(){
        requestService(CONTEXT,LOCATION_FROM,LOCATION_TO,WEIGHT,LOAD_CHAR,NAMEE,IDD,NUMM,IMAGE,DISTANCE_BETWEEN);
    }

    public static void requestService(final Context current, final LatLng LOCATION_FROM, final LatLng LOCATION_TO, final String weight, final ArrayList load_char, final String name, final String id, final String num, final Bitmap image, final int DISTANCE_BETWEEN) {
        mContext=current;
        loading = ProgressDialog.show(mContext, "Submitting your request", "Please wait...",true,true);

        final StringRequest strReq = new StringRequest(Request.Method.POST,
                MyApplication.ONLINE_ALPHA_REQUEST, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("LOAD REQUESTOR", "response: " + response);

                try {
                    JSONObject obj = new JSONObject(response);

                    // check for error flag
                    if (obj.getString("error") == "false") {
                        // user successfully logged in

                        JSONObject responseObj = obj.getJSONObject("response");
                         String request_id=responseObj.getString("request_id");
                        request_id_global =request_id;
//                        User user = new User(userObj.getString("user_id"),
//                                userObj.getString("name"),
//                                userObj.getString("email"));

                        // storing user in shared preferences
                       // MyApplication.getInstance().getPrefManager().storeUser(user);

                        // start main activity
                        current.startActivity(new Intent(current, MainActivity.class));
                       // current.finish();

                    } else {
                        // login error - simply toast the message
                        Toast.makeText(mContext, "" + obj.getJSONObject("error").getString("message"), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    Log.e(TAG, "json parsing error: " + e.getMessage());
                    Toast.makeText(mContext, "Json parse error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                NetworkResponse networkResponse = error.networkResponse;
                Log.e(TAG, "Volley error: " + error.getMessage() + ", code: " + networkResponse+" and "+error.getMessage());
                Toast.makeText(mContext, "Volley error: " + error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }) {

            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("drop_id", LOCATION_TO.toString());
                params.put("drop_num","");
                params.put("requestor_id","");
                params.put("pick_num",num.toString());
                params.put("pick_coords", LOCATION_FROM.toString());
                params.put("load_desc", "weight: "+weight.toString()+"Load Description: "+load_char.toString());
                params.put("image","");
                params.put("est_dist", String.valueOf(DISTANCE_BETWEEN));
                params.put("est_cost","");
                params.put("pick_time","");
                params.put("drop_coords","");

                Log.e(TAG, "params: " + params.toString());
                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }

        };
        //TODO UPLOAD IMAGE to
        //request_id
        // Bitmap image
        //TODO FETCH REQUEST_ID FROM ABOVE RESPONSEE
        String requesting_id= request_id_global;
        uploadImage(image,requesting_id);

        //Adding request to request queue
      //  MyApplication.getInstance().addToRequestQueue(strReq);
    }

    private static void uploadImage(Bitmap image, final String requesting_id) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        final String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, MyApplication.IMAGE_UPLOAD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Disimissing the progress dialog
                        loading.dismiss();
                        //Showing toast message of the response
                        Toast.makeText(mContext, s , Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        loading.dismiss();

                        //Showing toast
                        Toast.makeText(mContext, volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Converting Bitmap to String
                String image = encodedImage;


                //Creating parameters
                Map<String,String> params = new Hashtable<String, String>();

                //Adding parameters
                params.put("KEY_IMAGE", image);
                params.put("KEY_ID", requesting_id+"");

                //returning parameters
                return params;
            }
        };

        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }



}
