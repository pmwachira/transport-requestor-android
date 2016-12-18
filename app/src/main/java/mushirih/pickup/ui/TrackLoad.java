package mushirih.pickup.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
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

import mushirih.pickup.R;
import mushirih.pickup.http.HttpConnection;
import mushirih.pickup.internal.MyApplication;
import mushirih.pickup.internal.User;
import mushirih.pickup.mapping.PathJsonParser;

/**
 * Created by p-tah on 18/12/2016.
 */
public class TrackLoad  extends FragmentActivity implements OnMapReadyCallback {

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
            mContext=this;
            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.mapxx);
            mapFragment.getMapAsync(this);
            fetchLocations();
        }

    private void fetchLocations() {

            loading = ProgressDialog.show(mContext,null, "Fetching details.Please wait.",true,false);

            final StringRequest strReq = new StringRequest(Request.Method.POST,
                    MyApplication.TRACK, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    Log.e("LOAD REQUESTOR", "response: " + response);

                    try {
                        JSONObject object = new JSONObject(response);

                        // check for error flag
                        if (object.getString("error").equals("false")) {
                            // user successfully logged in

                            //JSONObject responseObj = obj.getJSONObject("response");
                            String from_lat=object.getString("pick_lat");
                            String from_long=object.getString("pick_long");
                            String to_lat=object.getString("to_lat");
                            String to_long=object.getString("to_long");
                            String current_lat=object.getString("current_lat");
                            String current_long=object.getString("current_long");

                           LOCATION_FROM=new LatLng(Float.parseFloat(from_lat),Float.parseFloat(from_long));
                            LOCATION_TO=new LatLng(Float.parseFloat(to_lat),Float.parseFloat(to_long));
                            LOCATION_NOW=new LatLng(Float.parseFloat(current_lat),Float.parseFloat(current_long));

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
                        Log.e("TrackLoad", "json parsing error: " + response+"::"+e.getMessage());
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
                    Log.e("Fetch Load", "Volley error: " + error.getMessage() + ", code: " + networkResponse+" and "+error.getMessage());
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
                    User user= MyApplication.getInstance().getPrefManager().getUser();
                    //todo trial
                    params.put("eng_id", getIntent().getStringExtra("id"));

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



    }

    @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;

        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(LOCATION_FROM).title("Pick Up Location"));
        MarkerOptions dest=new MarkerOptions().position(LOCATION_TO).title("Drop Location");
        Marker DEST= mMap.addMarker(dest);
        mMap.addMarker(new MarkerOptions().position(LOCATION_NOW).title("Driver position").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));


    }
    private String getMapsApiDirectionsUrl() {
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
                    polyLineOptions.width(10);
                    polyLineOptions.color(Color.BLUE);
                }
            }
            if(null!=polyLineOptions) {
                mMap.addPolyline(polyLineOptions);
                float totalDistance = 0;
                for(int k = 1; k < polyLineOptions.getPoints().size(); k++) {
                    Location currLocation = new Location("this");
                    currLocation.setLatitude(polyLineOptions.getPoints().get(k).latitude);
                    currLocation.setLongitude(polyLineOptions.getPoints().get(k).longitude);
                    Location lastLocation = new Location("that");
                    lastLocation.setLatitude(polyLineOptions.getPoints().get(k-1).latitude);
                    lastLocation.setLongitude(polyLineOptions.getPoints().get(k-1).longitude);
                    totalDistance += lastLocation.distanceTo(currLocation);
                }
               // DISTANCE_BETWEEN= (int) (totalDistance/1000);
            }
        }
    }

}
