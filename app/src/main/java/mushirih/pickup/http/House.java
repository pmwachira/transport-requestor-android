package mushirih.pickup.http;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import mushirih.pickup.internal.MyApplication;
import mushirih.pickup.internal.User;

/**
 * Created by p-tah on 10/02/2017.
 */

/**
 * Created by p-tah on 09/08/2016.
 */
public class House {

    //TODO CLASS RECIEVING ALL LOAD/REQUEST PROPERIES
    private static Bitmap IMAGE;
    public static int DAY;
    public static int MONTH;
    public static int YEAR;
    private static int HOUR;
    private static String MINUTE;
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
    private static String CAR_TYPE;

    public static void setDate(int dayOfMonth, int monthOfYear, int year) {
        DAY=dayOfMonth;
        MONTH=monthOfYear;
        YEAR=year;

    }
    public static void setTime(int hourOfDay, String minute) {
        HOUR=hourOfDay;
        MINUTE=minute;
    }
    public static void bulkSet(Context mContext, LatLng location_from, LatLng location_to, String load_char,int distance_between, String car_type) {
        CONTEXT=mContext;
        LOCATION_FROM=location_from;
        LOCATION_TO=location_to;
        LOAD_CHAR=load_char;
        DISTANCE_BETWEEN=distance_between;
        CAR_TYPE=car_type;

    }
    public static void send(){
        requestService(CONTEXT,LOCATION_FROM,LOCATION_TO,LOAD_CHAR,DISTANCE_BETWEEN,CAR_TYPE);
    }
    public static void requestService(final Context current, final LatLng LOCATION_FROM, final LatLng LOCATION_TO, final String load_char, final int DISTANCE_BETWEEN, String carType) {
        mContext=current;

        loading = ProgressDialog.show(mContext,null, "Submitting request.Please wait.",true,false);

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

                    } else {
                        loading.dismiss();
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setTitle("Error").setCancelable(false)
                                .setMessage("Please check your internet settings and try again")
                                .setNeutralButton("Open settings", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent myIntent = new Intent(Settings.ACTION_SETTINGS);
                                        mContext.startActivity(myIntent);
                                    }
                                })
                                .setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        // prevent cost generation again
                                        send();
                                    }
                                });
                        builder.show();
                        //Toast.makeText(mContext, "" + object.getJSONObject("error").getString("message"), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    loading.dismiss();
                    Log.e(TAG, "json parsing error: " + response+"::"+e.getMessage());
                    //Toast.makeText(mContext, "Json parse error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("Error").setCancelable(false)
                            .setMessage("Please check your internet settings and try again")
                            .setNeutralButton("Open settings", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent myIntent = new Intent(Settings.ACTION_SETTINGS);
                                    mContext.startActivity(myIntent);
                                }
                            })
                            .setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    // prevent cost generation again
                                    send();
                                }
                            });
                    builder.show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
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
                params.put("pick_lat", String.valueOf(LOCATION_FROM.latitude));
                params.put("pick_long", String.valueOf(LOCATION_FROM.longitude));
                params.put("load_desc", load_char);
                params.put("est_dist", String.valueOf(DISTANCE_BETWEEN));
                params.put("est_cost",COST);
                params.put("pick_time",HOUR+":"+MINUTE);
                params.put("pick_date",DAY+"/"+MONTH+"/"+YEAR);
                params.put("drop_lat", String.valueOf(LOCATION_TO.latitude));
                params.put("drop_long", String.valueOf(LOCATION_TO.longitude));
                params.put("car_type",CAR_TYPE);


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
        MyApplication.getInstance().getPrefManager().storeTRansactionId(request_id_global );

    }

}

