package mushirih.pickup.http;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
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
    private static int request_id_global=10;
    static ProgressDialog loading;



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

    public static void requestService(final Context current, final LatLng LOCATION_FROM, final LatLng LOCATION_TO, final String weight, final ArrayList load_char, final EditText name, final EditText id, final EditText num, final Bitmap image) {
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

                        JSONObject userObj = obj.getJSONObject("response");
                         int request_id=userObj.getInt("request_id");
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
                params.put("from", LOCATION_FROM.toString());
                params.put("to", LOCATION_TO.toString());
                params.put("weight", weight.toString());
                params.put("load_char",load_char.toString());
                params.put("name", name.toString());
                params.put("id", id.toString());
                params.put("num",num.toString());

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
        int requesting_id= request_id_global;
        uploadImage(image,requesting_id);

        //Adding request to request queue
      //  MyApplication.getInstance().addToRequestQueue(strReq);
    }

    private static void uploadImage(final Bitmap image, final int requested_id) {


            class UploadImage extends AsyncTask<Bitmap,Void,String> {



                ImageRequestHandler rh = new ImageRequestHandler();

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();

                }

                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);
                    loading.dismiss();
                    //Toast.makeText(mContext,s,Toast.LENGTH_LONG).show();
                }

                @Override
                protected String doInBackground(Bitmap... params) {
                    Bitmap bitmap = params[0];
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] imageBytes = baos.toByteArray();
                    String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

                    HashMap<String,String> data = new HashMap<>();
//                    data.put("UPLOAD_KEY FOR "+requested_id, encodedImage);
                    data.put("image", encodedImage);

                    String result = rh.sendPostRequest(MyApplication.UPLOAD_URL,data);

                    return result;
                }
            }
        UploadImage ui = new UploadImage();
        ui.execute(image);




    }
   /* private static void getImage() {
        String id = editTextId.getText().toString().trim();
        class GetImage extends AsyncTask<String,Void,Bitmap>{
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(ViewImage.this, "Uploading...", null,true,true);
            }

            @Override
            protected void onPostExecute(Bitmap b) {
                super.onPostExecute(b);
                loading.dismiss();
                imageView.setImageBitmap(b);
            }

            @Override
            protected Bitmap doInBackground(String... params) {
                String id = params[0];
                String add = "http://simplifiedcoding.16mb.com/ImageUpload/getImage.php?id="+id;
                URL url = null;
                Bitmap image = null;
                try {
                    url = new URL(add);
                    image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return image;
            }
        }

        GetImage gi = new GetImage();
        gi.execute(id);
    }
    */
}
