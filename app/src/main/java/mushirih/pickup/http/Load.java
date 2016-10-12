package mushirih.pickup.http;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.model.LatLng;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import mushirih.pickup.internal.MyApplication;

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
    private static String request_id_global;
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
                    JSONObject object = new JSONObject(response);

                    // check for error flag
                    if (object.getString("error").equals("false")) {
                        // user successfully logged in

                        //JSONObject responseObj = obj.getJSONObject("response");
                          String request_id=object.getString("request_id");

                        request_id_global=request_id;
//                        User user = new User(userObj.getString("user_id"),
//                                userObj.getString("name"),
//                                userObj.getString("email"));

                        // storing user in shared preferences
                       // MyApplication.getInstance().getPrefManager().storeUser(user);

                        // start main activity
                        //current.startActivity(new Intent(current, MainActivity.class));
                       // current.finish();

                    } else {
                        // login error - simply toast the message
                        Toast.makeText(mContext, "" + object.getJSONObject("error").getString("message"), Toast.LENGTH_LONG).show();
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
//                params.put("drop_id", LOCATION_TO.toString());
                params.put("drop_id","0000");
                params.put("drop_num","0000");
                params.put("requestor_id","0000");
//                params.put("pick_num",num.toString());
                params.put("pick_num","0000");
//                params.put("pick_coords", LOCATION_FROM.toString());
                params.put("pick_coords","0,0");
//                params.put("load_desc", "weight: "+weight.toString()+"Load Description: "+load_char.toString());
                params.put("load_desc", "0000");
                params.put("image","0000");
//                params.put("est_dist", String.valueOf(DISTANCE_BETWEEN));
                params.put("est_dist","0000");
                params.put("est_cost","0000");
                params.put("pick_time","0000");
                params.put("drop_coords","0,0");

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
        new Uploader(image,request_id_global).execute();

    }
    private static class Uploader extends AsyncTask<Void, Void, Void>{
        Bitmap image;
        String name;
        public Uploader(Bitmap image,String name) {
            this.image=image;
            this.name=name;
        }

        @Override
        protected Void doInBackground(Void... params) {
            ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
            String encodedImage=Base64.encodeToString(byteArrayOutputStream.toByteArray(),Base64.DEFAULT);

            ArrayList<NameValuePair> dataToSend=new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("image",encodedImage));
            dataToSend.add(new BasicNameValuePair("name",name));

            HttpParams httpRequestParams=getHttpRequestParams();

            HttpClient httpClient=new DefaultHttpClient(httpRequestParams);
            HttpPost httpPost=new HttpPost(MyApplication.IMAGE_UPLOAD_URL);

            try{
                httpPost.setEntity(new UrlEncodedFormEntity(dataToSend));
                httpClient.execute(httpPost);
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            loading.dismiss();
        }
    }
    private static HttpParams getHttpRequestParams(){
        HttpParams httpRequestParams=new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpRequestParams,30*1000);
        HttpConnectionParams.setSoTimeout(httpRequestParams,30*1000);
        return httpRequestParams;
    }
}
