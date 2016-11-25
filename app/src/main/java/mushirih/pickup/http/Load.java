package mushirih.pickup.http;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
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
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import mushirih.pickup.internal.MyApplication;
import mushirih.pickup.internal.User;
import mushirih.pickup.mapping.MapsActivity;

/**
 * Created by p-tah on 09/08/2016.
 */
public class Load {
    //TODO CLASS RECIEVING ALL LOAD/REQUEST PROPERIES
    private static Bitmap IMAGE;
    public static int DAY;
    public static int MONTH;
    public static int YEAR;
    private static int HOUR;
    private static int MINUTE;
   private static Context mContext;
    private static String TAG="LOAD ALPHA REQUEST";
    private static String request_id_global;
    static ProgressDialog loading;
    private static Context CONTEXT;
    private static LatLng LOCATION_FROM;
    private static LatLng LOCATION_TO;
    private static String WEIGHT;
    private static String LOAD_CHAR;
   private static String NAMEE, IDD, NUMM;
   private static int DISTANCE_BETWEEN;
    private static String COST;


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
    public static void bulkSet(Context mContext, LatLng location_from, LatLng location_to, String weight, String load_char, String namee, String idd, String numm, int distance_between) {
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
    public static void requestService(final Context current, final LatLng LOCATION_FROM, final LatLng LOCATION_TO, final String weight, final String load_char, final String name, final String id, final String num, final Bitmap image, final int DISTANCE_BETWEEN) {
        mContext=current;

        loading = ProgressDialog.show(mContext, "Submitting your request", "Please wait...",true,false);

        final StringRequest strReq = new StringRequest(Request.Method.POST,
                MyApplication.ONLINE_ALPHA_REQUEST, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("LOAD REQUESTOR", "response: " + response);

                try {
                    JSONObject object = new JSONObject(response);

                    // check for error flag
                    if (object.getString("error").equals("false")) {
                        // user successfully logged in

                        //JSONObject responseObj = obj.getJSONObject("response");
                          String request_id=object.getString("request_id");

                        request_id_global=request_id;
                        if(null==request_id_global){
                            request_id_global=System.currentTimeMillis()+"";
                            Log.e("IMAGE UPLOAD","NULL REQUEST ID");
                        }else{
                            uploader(image,request_id_global);
                        }

                    } else {
                        loading.dismiss();
                        // login error - simply toast the message
                        Toast.makeText(mContext, "" + object.getJSONObject("error").getString("message"), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    loading.dismiss();
                    Log.e(TAG, "json parsing error: " + response+"::"+e.getMessage());
                    Toast.makeText(mContext, "Json parse error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                NetworkResponse networkResponse = error.networkResponse;
                Log.e(TAG, "Volley error: " + error.getMessage() + ", code: " + networkResponse+" and "+error.getMessage());
                Toast.makeText(mContext, "Volley error: " + error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }) {
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                String userid;
                User user= MyApplication.getInstance().getPrefManager().getUser();
                //todo trial
                if(user.getId()==null){
                    userid="0000";
                }else{
                    userid=user.getId();
                }

                params.put("drop_id",userid);
                params.put("drop_num","0000");
                params.put("requestor_id", userid);
                params.put("pick_num",num.toString());
                params.put("pick_lat", String.valueOf(LOCATION_FROM.latitude));
                params.put("pick_long", String.valueOf(LOCATION_FROM.longitude));
                params.put("load_desc", "weight: "+weight.toString()+"Load Description: "+load_char.toString());
                params.put("image","0000");
                params.put("est_dist", String.valueOf(DISTANCE_BETWEEN));
                params.put("est_cost",COST);
                params.put("pick_time",HOUR+":"+MINUTE);
                params.put("pick_date",DAY+"/"+MONTH+"/"+YEAR);
                params.put("drop_lat", String.valueOf(LOCATION_TO.latitude));
                params.put("drop_long", String.valueOf(LOCATION_TO.longitude));

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
        //Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);
        //store transaction id+state to sp
        MyApplication.getInstance().getPrefManager().storeTRansactionId(request_id_global,"ALPHA");

    }
    private static void uploader(Bitmap image, final String request_id_global) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        final String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
        //
        StringRequest stringRequest = new StringRequest(Request.Method.POST, MyApplication.IMAGE_UPLOAD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Disimissing the progress dialog
                        loading.dismiss();
                        //Showing toast message of the response
                        Toast.makeText(mContext, s, Toast.LENGTH_LONG).show();
                        //TODO Tell user something
                        AlertDialog.Builder builder=new AlertDialog.Builder(mContext);
                        builder.setTitle("Request successful").setCancelable(false)
                                .setMessage("A driver going the direction of your load will get back to you")
                                .setPositiveButton("Okay",new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        MapsActivity.requestSuccessful();
                                    }
                                });
                        builder.show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        loading.dismiss();

                        //Showing toast
                        Toast.makeText(mContext, volleyError.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                //Creating parameters
                Map<String, String> params = new Hashtable<String, String>();

                //Adding parameters
                params.put("image", encodedImage);
                params.put("name", request_id_global);

                //returning parameters
                return params;
            }
        };

        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }

    public static void setCost(String cost) {
        COST=cost;
    }
}

