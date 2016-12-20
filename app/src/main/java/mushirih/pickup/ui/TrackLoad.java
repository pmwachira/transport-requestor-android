package mushirih.pickup.ui;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import mushirih.pickup.R;
import mushirih.pickup.http.HttpConnection;
import mushirih.pickup.internal.MyApplication;
import mushirih.pickup.internal.MyPreferenceManager;
import mushirih.pickup.internal.User;
import mushirih.pickup.mapping.PathJsonParser;

/**
 * Created by p-tah on 18/12/2016.
 */
public class TrackLoad extends ActionBarActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Context mContext;
    ProgressDialog loading;
    LatLng LOCATION_FROM;
    LatLng LOCATION_TO;
    LatLng LOCATION_NOW;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.track_maps);
        mContext = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Tracking assigned person");
        setSupportActionBar(toolbar);

        new MyPreferenceManager(mContext).setTracking(true);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapxx);

        mapFragment.getMapAsync(this);
        showTrackingNotification();

        Timer t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                TrackLoad.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fetchLocations();
                    }
                });
            }

        }, 0, 10000);

    }

    private void fetchLocations() {

        loading = ProgressDialog.show(mContext, null, "Fetching driver location.", true, false);

        final StringRequest strReq = new StringRequest(Request.Method.POST,
                MyApplication.TRACK, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("LOAD REQUESTOR", "response: " + response);

                try {
                    JSONObject object = new JSONObject(response);

                    // check for error flag
                    if (object.getString("error").equals("false")) {
                        loading.dismiss();

                        //JSONObject responseObj = obj.getJSONObject("response");
                        String from_lat = object.getString("pick_lat");
                        String from_long = object.getString("pick_long");
                        String to_lat = object.getString("to_lat");
                        String to_long = object.getString("to_long");
                        String current_lat = object.getString("current_lat");
                        String current_long = object.getString("current_long");

                        LOCATION_FROM = new LatLng(Float.parseFloat(from_lat), Float.parseFloat(from_long));
                        LOCATION_TO = new LatLng(Float.parseFloat(to_lat), Float.parseFloat(to_long));
                        LOCATION_NOW = new LatLng(Float.parseFloat(current_lat), Float.parseFloat(current_long));
                        //Load Journey
                        String url = getMapsApiDirectionsUrl();
                        ReadTask readTask = new ReadTask();
                        readTask.execute(url);
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
                                        fetchLocations();
                                    }
                                });
                        builder.show();
                        //Toast.makeText(mContext, "" + object.getJSONObject("error").getString("message"), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    loading.dismiss();
                    Log.e("TrackLoad", "json parsing error: " + response + "::" + e.getMessage());
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
                                    fetchLocations();
                                }
                            });
                    builder.show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                NetworkResponse networkResponse = error.networkResponse;
                Log.e("Fetch Load", "Volley error: " + error.getMessage() + ", code: " + networkResponse + " and " + error.getMessage());
                // Toast.makeText(mContext, "Volley error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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
                                fetchLocations();
                            }
                        });
                builder.show();


            }
        }) {
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                String userid;
                User user = MyApplication.getInstance().getPrefManager().getUser();
                //todo trial
                String id = "0000";
                if (null == getIntent().getStringExtra("id")) {
                    id = MyApplication.getInstance().getPrefManager().getTransaction();
                } else {
                    id = getIntent().getStringExtra("id");
                }
                params.put("eng_id", id);

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        //Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);

    }
    private String getMapsApiDirectionsUrl() {
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(LOCATION_FROM).title("Pick Up Location"));
        MarkerOptions dest=new MarkerOptions().position(LOCATION_TO).title("Drop Location");
        Marker DEST= mMap.addMarker(dest);
        mMap.addMarker(new MarkerOptions().position(LOCATION_NOW).title("Driver position").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(LOCATION_NOW,18);
        mMap.animateCamera(cameraUpdate);

        String waypoints="&waypoints=optimize:true|"+
                LOCATION_FROM.latitude+","+LOCATION_FROM.longitude+
                "|"+LOCATION_TO.latitude+","+LOCATION_TO.longitude;
        String sensor="sensor=false";
        String params=waypoints+"&"+sensor;
        String output="json";
        String url="https://maps.googleapis.com/maps/api/directions/" + output + "?"+"origin="
                +LOCATION_FROM.latitude+","+LOCATION_FROM.longitude+"&destination="
                +LOCATION_TO.latitude+","+LOCATION_TO.longitude +  params;
/*Chnage route

&waypoints=via:"+LOCATION_OCHA2.latitude+*/
        return url;
    }
    private String getMapsApiDirectionsUrlDriver() {
       String waypoints="&waypoints=optimize:true|"+
                LOCATION_NOW.latitude+","+LOCATION_NOW.longitude+
                "|"+LOCATION_FROM.latitude+","+LOCATION_FROM.longitude;
        String sensor="sensor=false";
        String params=waypoints+"&"+sensor;
        String output="json";
        String url="https://maps.googleapis.com/maps/api/directions/" + output + "?"+"origin="
                +LOCATION_NOW.latitude+","+LOCATION_NOW.longitude+"&destination="
                +LOCATION_FROM.latitude+","+LOCATION_FROM.longitude +  params;
/*Chnage route

&waypoints=via:"+LOCATION_OCHA2.latitude+*/

        return url;
    }
    private class ReadTask extends AsyncTask<String,Void,String> {


        @Override
        protected String doInBackground(String... url) {
            String data="";

            HttpConnection httpConnection= new HttpConnection();
            try {
                data=httpConnection.readUrl(url[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            new ParserTask().execute(s);
        }
    }
    private class ParserTask extends AsyncTask<String,Integer,List<List<HashMap<String,String>>>>{
        JSONObject jsonObject;
        List<List<HashMap<String,String>>> routes=null;

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
            try {
                jsonObject=new JSONObject(jsonData[0]);
                PathJsonParser pathJsonParser=new PathJsonParser();
                routes=pathJsonParser.parse(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            ArrayList<LatLng> points = null;
            PolylineOptions polyLineOptions = null;
            if(routes.size()>0){
                // traversing through routes
                for (int i = 0; i < routes.size(); i++) {
                    points = new ArrayList<LatLng>();
                    polyLineOptions = new PolylineOptions();
                    List<HashMap<String, String>> path = routes.get(i);

                    for (int j = 0; j < path.size(); j++) {

                        HashMap<String, String> point = path.get(j);

                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);
                        points.add(position);
                    }

                        polyLineOptions.addAll(points);
                        polyLineOptions.width(20);
                        polyLineOptions.color(Color.BLUE);

                        mMap.addPolyline(polyLineOptions);



                }
            }

        }
    }

    private void showTrackingNotification() {
        NotificationManager notificationManager = (NotificationManager)
                mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            int icon = R.mipmap.ic_launcher;
            long when = System.currentTimeMillis();


            String title = mContext.getString(R.string.app_name);

            Intent notificationIntent = new Intent(mContext,TrackLoad.class);
            // set intent so it does not start a new activity
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent intent =
                    PendingIntent.getActivity(mContext, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder builder=new NotificationCompat.Builder(mContext).setSmallIcon(R.drawable.ic)
                    .setContentTitle(title).setContentText("");
            builder.setStyle(new NotificationCompat.BigTextStyle().bigText("Tracking your Request\nClick to view"));
            builder.setDefaults(Notification.DEFAULT_SOUND);
            builder.setDefaults(Notification.DEFAULT_VIBRATE);
            builder.setAutoCancel(true);
            builder.setContentIntent(intent);
            notificationManager.notify(0,builder.build());


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate your Menu
        getMenuInflater().inflate(R.menu.menu_track, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id==R.id.call){

            String num="0712613052";
            if(null== getIntent().getStringExtra("num")){
                num=MyApplication.getInstance().getPrefManager().getnumber();
            }else{
                num = getIntent().getStringExtra("num");
            }
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + num)));


        }

        return super.onOptionsItemSelected(item);
    }


}
