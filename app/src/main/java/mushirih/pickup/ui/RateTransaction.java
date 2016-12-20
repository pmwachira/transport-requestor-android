package mushirih.pickup.ui;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import mushirih.pickup.R;
import mushirih.pickup.internal.MyApplication;
import mushirih.pickup.internal.MyPreferenceManager;

/**
 * Created by p-tah on 09/12/2016.
 */
public class RateTransaction extends AppCompatActivity {
    RatingBar ratingBar;
    ProgressDialog loading;
    Context contextt;
    String TAG = "RateTransaction.class";
    String trans_id = "";
    String comment = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null != getIntent() && null != getIntent().getStringExtra("ID")) {
            trans_id = getIntent().getStringExtra("ID");
        }

        setContentView(R.layout.ratetrans);
        contextt = this;
        new MyPreferenceManager(contextt).setTracking(false);
        final TextView preview = (TextView) findViewById(R.id.ratingPreview);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        Button submit = (Button) findViewById(R.id.submitReview);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                preview.setText("" + v);
            }
        });
        final EditText suggest= (EditText) findViewById(R.id.suggest);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                comment=suggest.getText().toString().trim();
                sendReview("trans", ratingBar.getRating(),comment);
            }
        });
    }

    private void sendReview(final String trans, final float rating, final String comment) {
        loading = ProgressDialog.show(contextt, null, "Submitting review...", true, false);
        StringRequest strRating = new StringRequest(Request.Method.POST,
                MyApplication.RequestorReview, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "response: " + response);
                try {
                    JSONObject obj = new JSONObject(response);
                    // check for error flag
                    if (obj.getString("error").equals("false")) {
                        // user successfully logged in
                        final String cost = obj.getString("cost");
                        final String pick_num = obj.getString("pick_num");
                        loading.dismiss();
                        AlertDialog.Builder builder = new AlertDialog.Builder(contextt);
                        builder.setTitle("Delivery Complete").setCancelable(false)
                                .setMessage("Thank you for using pickUp service. Please pay " + cost + " Ksh")
                                .setPositiveButton("Pay", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent cst=new Intent(RateTransaction.this, Payments.class);
                                        cst.putExtra("cost",cost);
                                        startActivity(cst);
                                        finish();
                                    }
                                })
                                .setNeutralButton("Ask collector to pay", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if (ActivityCompat.checkSelfPermission(contextt, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                            // TODO: Consider calling
                                            //    ActivityCompat#requestPermissions
                                            // here to request the missing permissions, and then overriding
                                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                            //                                          int[] grantResults)
                                            // to handle the case where the user grants the permission. See the documentation
                                            // for ActivityCompat#requestPermissions for more details.
                                            return;
                                        }
                                        startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + pick_num)));
                                        finish();
                            }
                        });

                        builder.show();

                    } else {
                        // login error - simply toast the message
                        loading.dismiss();
                        Toast.makeText(getApplicationContext(), "" + obj.getJSONObject("error").getString("message"), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    Log.e(TAG, "json parsing error: " + e.getMessage());
                    Toast.makeText(getApplicationContext(), "Json parse error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                loading.dismiss();
                Log.e(TAG, "Volley error: " + error.getMessage() + ", code: " + networkResponse+" and "+error.getMessage());
                AlertDialog.Builder builder = new AlertDialog.Builder(contextt);
                builder.setTitle("Error").setCancelable(false)
                        .setMessage("Please check your internet settings and try again")
                        .setNeutralButton("Open settings", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent myIntent = new Intent(Settings.ACTION_SETTINGS);
                                startActivity(myIntent);
                            }
                        })
                        .setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //retry
                                sendReview("trans",ratingBar.getRating(), comment);
                            }
                        });
                builder.show();
                //Toast.makeText(getApplicationContext(), "Volley error: " + error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }) {

            @Override

            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("eng_id", trans_id);
                params.put("eng_rating", String.valueOf(rating));
                params.put("comment", comment);

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
        MyApplication.getInstance().addToRequestQueue(strRating);
    }
}
