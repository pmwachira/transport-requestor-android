package mushirih.pickup.mapping;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mushirih.pickup.R;
import mushirih.pickup.http.HttpConnection;
import mushirih.pickup.http.Load;
import mushirih.pickup.pdf.PDF;
import mushirih.pickup.ui.DatePickerFragment;
import mushirih.pickup.ui.TimePickerFragment;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener  {
   static Context mContext;
    LinearLayout searchloc;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    PDF pdf;
    TextView mLocationText,pich_loc,drop_loc,loc_rep;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    ToggleButton zero,one, two, three;
    LinearLayout l0,l1, l2, l3,request_pane,request_time,describe_load,destination_pane,location_pick_graphic,top_dest;
    Button confirm;
    private AddressResultReceiver mResultReceiver;
    String TAG="MAPSACTIVITY LOG";
    private LatLng mCenterLatLong;
    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;
    int PLACE_PICKER_REQUEST=2;
    int PLACE_PICKER_DEST_REQUEST=02022;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 20;
    protected String mAddressOutput;
    protected String mAreaOutput;
    protected String mCityOutput;
    protected String mStateOutput;
    Activity activity;
   Location mLastLocation;
    CardView hide,go;
  TextView VIEW_TO_CHANGE;
    EditText EDIT_TEXT_TO_EDIT;
    int CONTACT_PICKER_RESULT=999;
    LatLng LOCATION_FROM;
    LatLng LOCATION_TO;
    int PICK_FLAG=44;
    int DROP_FLAG=55;
    int MARKER_TYPE;
    static final int REQUEST_IMAGE_CAPTURE=802;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context = this;

        //pdf=new PDF(context, mBitmap);
        mContext=this;
        activity=this;
        buildGoogleApiClient();
        setContentView(R.layout.activity_maps);

        mLocationText= (TextView) findViewById(R.id.locationtext);

        zero=(ToggleButton) findViewById(R.id.toggleButton0);
        one = (ToggleButton) findViewById(R.id.toggleButton1);
        two = (ToggleButton) findViewById(R.id.toggleButton2);
        three = (ToggleButton) findViewById(R.id.toggleButton3);

        l0 = (LinearLayout) findViewById(R.id.select_individual);
        l1 = (LinearLayout) findViewById(R.id.select_bike);
        l2 = (LinearLayout) findViewById(R.id.select_pick_up);
        l3 = (LinearLayout) findViewById(R.id.select_lorry);

        l1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                one.setChecked(true);
                two.setChecked(false);
                three.setChecked(false);
            }
        });
        l2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                one.setChecked(false);
                two.setChecked(true);
                three.setChecked(false);
            }
        });
        l3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                one.setChecked(false);
                two.setChecked(false);
                three.setChecked(true);
            }
        });
        pich_loc=(TextView) findViewById(R.id.top_bar_location);

        searchloc= (LinearLayout) findViewById(R.id.search_loc);
        loc_rep= (TextView) findViewById(R.id.top_bar_location_rep);
        searchloc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder()/*.setLatLngBounds(LatLngBounds.builder().include(mCenterLatLong).build()*/;
                try {
                    VIEW_TO_CHANGE=pich_loc;
                    MARKER_TYPE=PICK_FLAG;
                    startActivityForResult(builder.build(activity), PLACE_PICKER_DEST_REQUEST);

                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
//                try {
//                    Intent intent=new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).build(activity);
//                    startActivityForResult(intent,REQUEST_CODE_AUTOCOMPLETE);
//                } catch (GooglePlayServicesRepairableException e) {
//                    e.printStackTrace();
//                } catch (GooglePlayServicesNotAvailableException e) {
//                    e.printStackTrace();
//                }
            }
        });
        request_pane= (LinearLayout) findViewById(R.id.request_pane);
        request_time= (LinearLayout) findViewById(R.id.set_time);
        request_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //time pick
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getSupportFragmentManager(), "timePicker");
                //datePicker
                DialogFragment newfragment = new DatePickerFragment();
                newfragment.show(getSupportFragmentManager(), "datePicker");

            }
        });

        describe_load= (LinearLayout) findViewById(R.id.describe_load);
        describe_load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                describe_load();
            }
        });



        destination_pane= (LinearLayout) findViewById(R.id.dest_loc);
        destination_pane.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pich_loc.getText().equals("Select pick up location")) {
                    pich_loc.setTextColor(Color.RED);
                    pich_loc.setTextSize(20);
                }else{
                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder()/*.setLatLngBounds(LatLngBounds.builder().include(mCenterLatLong).build()*/;
                    try {
                        VIEW_TO_CHANGE=loc_rep;
                        MARKER_TYPE=DROP_FLAG;
                        startActivityForResult(builder.build(activity), PLACE_PICKER_DEST_REQUEST);

                    } catch (GooglePlayServicesRepairableException e) {
                        e.printStackTrace();
                    } catch (GooglePlayServicesNotAvailableException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        // Toast.makeText(context,"Call PDF ? ",Toast.LENGTH_LONG).show();
        drop_loc= (TextView) findViewById(R.id.bottom_bar_location);
        hide= (CardView) findViewById(R.id.second_hide);
        hide.setVisibility(View.GONE);
        go= (CardView) findViewById(R.id.go);


        location_pick_graphic= (LinearLayout) findViewById(R.id.locationMarker);
        top_dest= (LinearLayout) findViewById(R.id.top_dest_holder);
        top_dest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder()/*.setLatLngBounds(LatLngBounds.builder().include(mCenterLatLong).build()*/;
                try {
                    VIEW_TO_CHANGE=loc_rep;
                    MARKER_TYPE=DROP_FLAG;
                    startActivityForResult(builder.build(activity), PLACE_PICKER_DEST_REQUEST);

                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });
        confirm= (Button) findViewById(R.id.confirm);
        confirm.setText("Describe load");
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                describe_load();
            }
        });

        if(confirm.getText().equals("Request Delivery")) {
            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("Prompt");
            builder.setMessage("By clicking okay,you accept our T&c's");
            builder.setNegativeButton("Cancel", null);
            builder.setPositiveButton("Okay", null);
            builder.show();
                }
            });
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);




       mResultReceiver = new AddressResultReceiver(new Handler());


// If this check succeeds, proceed with normal processing.
        // Otherwise, prompt user to get valid Play Services APK.
        if (checkPlayServices()) {
            // notify user
            if (!AppUtils.isLocationEnabled(mContext)) {

                AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                dialog.setMessage("Location not enabled!");
                dialog.setPositiveButton("Open location settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                });
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        // TODO Auto-generated method stub

                    }
                });
                dialog.show();
            }

        } else {
            Toast.makeText(mContext, "Location not supported in this device", Toast.LENGTH_SHORT).show();
        }

    }

    private Dialog describe_load() {
        //TODO Add take picture action here
        final ArrayList load_char=new ArrayList();
        final String[] options={"Urgent","Fragile ","Perishable","In need of packing boxes","I need help loading"};
        String[] weight={"Load under 5 Kgs","Load between 5-30 Kgs","Load over 30Kgs"};
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Please describe your load").setSingleChoiceItems(weight,2,null)
            .setPositiveButton("Okay",new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {
                AlertDialog.Builder builder=new AlertDialog.Builder(mContext);
                builder.setTitle("Please describe your load");
                    builder.setMultiChoiceItems(options, null, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if(isChecked){
                    load_char.add(which);
                }else if(load_char.contains(which)){
                    load_char.remove(Integer.valueOf(which));
                }
            }
        });
                builder.setNegativeButton("Cancel",null);
                builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    View layoutInflater=View.inflate(mContext, R.layout.contact_details, null);
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AlertDialog.Builder builder=new AlertDialog.Builder(mContext);
                        builder.setTitle("Provide details of person to pick goods.");
                        builder.setView(layoutInflater);
                        final View nambayake=layoutInflater.findViewById(R.id.numbertoget);
                               layoutInflater.findViewById(R.id.bContact).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                EDIT_TEXT_TO_EDIT= (EditText) nambayake;
                                Intent pickContactIntent = new Intent( Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI );
                                pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                                startActivityForResult(pickContactIntent, CONTACT_PICKER_RESULT);
                            }
                        });
                        builder.setPositiveButton("Take picture of load", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //TODO CAPTURE PICTURE OF THE LOAD
                                Intent takePicture=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                if (takePicture.resolveActivity(getPackageManager())!=null){
                                    startActivityForResult(takePicture,REQUEST_IMAGE_CAPTURE);
                                }
                            }
                        });
                        builder.setNegativeButton("Cancel",null);
                        builder.show();
                    }
                });
                builder.setView(View.inflate(mContext,R.layout.hello,null));
                builder.show();

            }
        }).setNegativeButton("Cancel",new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        confirm.setText("Request Delivery");
        return builder.show();

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
@Override
public void onMapReady(GoogleMap googleMap) {
    //TODO: ON map load,show drivers in the area
    mMap = googleMap;
    mMap.setMyLocationEnabled(true);
  //  mMap.setMaxZoomPreference(16);
  //  mMap.setMinZoomPreference(8);
//    if(mLastLocation!=null) {
//        LatLng here = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
//        mMap.addMarker(new MarkerOptions()
//                .position(here)
//                .title("Here I am")
//                .snippet("Somewhere in Nairobi")
//                .icon(BitmapDescriptorFactory.fromResource(R.drawable.add_marker))
//                .anchor(0.0f, 1.0f));
//        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(here, 16);
//        mMap.animateCamera(cameraUpdate);
//    }else{
//        Toast.makeText(MapsActivity.this, "NULL LOCS", Toast.LENGTH_SHORT).show();
//    }

            mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
        @Override
        public void onCameraChange(CameraPosition cameraPosition) {
           //TODO SHOW REQUEST PICK UP HERE FROM CENTER MAP COORDS
            mCenterLatLong = cameraPosition.target;
            final Location mLocation = new Location("");
            mLocation.setLatitude(mCenterLatLong.latitude);
            mLocation.setLongitude(mCenterLatLong.longitude);

            //Check for drop of marker move
            if (pich_loc.getText().length()!=0&&(mLocationText.getText().equals("Goods pick up point")|mLocationText.getText().equals("Set drop point here"))) {
                mLocationText.setText("Set drop point here");
                mLocationText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        VIEW_TO_CHANGE = loc_rep;
                        MARKER_TYPE=DROP_FLAG;
                        startIntentService(mLocation);
                        go.setVisibility(View.GONE);
                        hide.setVisibility(View.VISIBLE);
                        request_pane.setVisibility(View.VISIBLE);
                        location_pick_graphic.setVisibility(View.INVISIBLE);
                        mMap.setMinZoomPreference(10);
                        LOCATION_TO=new LatLng(mLocation.getLatitude(),mLocation.getLongitude());
//                        mMap.addMarker(new MarkerOptions()
//                        .position(LOCATION_TO)
//                           .title("Drop point")
//                .icon(BitmapDescriptorFactory.fromResource(R.drawable.add_marker))
//                .anchor(0.0f, 1.0f));
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(LOCATION_TO, 16);
                        mMap.animateCamera(cameraUpdate);
                        if(!LOCATION_FROM.equals(null)&&!LOCATION_TO.equals(null)){
                            if(AppUtils.isDataEnabled(mContext)) {
                                showRoute();
                            }else{
                                Toast.makeText(getApplicationContext(),"Please check your internet connection",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                });

            } else {
                request_pane.setVisibility(View.GONE);
                try {
                    mLocationText.setText("Click to request pick up here");
                    mLocationText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //TODO Try find clicked location
                           MARKER_TYPE=PICK_FLAG;
                            VIEW_TO_CHANGE=pich_loc;
                            startIntentService(mLocation);
                            if(pich_loc.getText().equals("Select pick up location")) {
                                pich_loc.setTextColor(Color.DKGRAY);
                                pich_loc.setTextSize(16);
                            }
                           // pich_loc.setText(mAddressOutput);
                            mLocationText.setText("Set drop point here");
                            Toast.makeText(getApplicationContext(),"Goods pick up point set at pin",Toast.LENGTH_LONG).show();
                            LOCATION_FROM=new LatLng(mLocation.getLatitude(),mLocation.getLongitude());
                            mMap.setMinZoomPreference(10);
                        }

                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

//            if(!pich_loc.getText().equals("Select pick up location")&&!loc_rep.getText().equals("Drop Location")){
//                location_pick_graphic.setVisibility(View.INVISIBLE);
//            }
        }
    });
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
    //ADDS LOCATION Finder option on map
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
//or do this on myLocationButton
/*
            if(mMap.getMyLocation()!=null) {
                LatLng here = new LatLng(mMap.getMyLocation().getLatitude(), mMap.getMyLocation().getLongitude());
                mMap.clear();//Clear existing markers
                mMap.addMarker(new MarkerOptions()
                        .position(here)
                        .title("Here I am")
                        .snippet("Somewhere in Nairobi")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.add_marker))
                        .anchor(0.0f, 1.0f));
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(here, 16);
                mMap.animateCamera(cameraUpdate);
            }
            */
//SEE THE MOVEMENTS
   // flatMarker(mMap);
}

    private void showRoute() {
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(LOCATION_TO).title("Pick Up Location"));
        mMap.addMarker(new MarkerOptions().position(LOCATION_FROM).title("Drop Location"));
        String url = getMapsApiDirectionsUrl();
        ReadTask readTask = new ReadTask();
        readTask.execute(url);
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
            mMap.addPolyline(polyLineOptions);
        }


    }
    @Override
    public void onConnected(Bundle bundle) {

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
         mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            changeMap(mLastLocation);
            VIEW_TO_CHANGE= loc_rep;
            startIntentService(mLastLocation);
            Log.d(TAG, "ON connected");

        } else
            try {
                LocationServices.FusedLocationApi.removeLocationUpdates(
                        mGoogleApiClient, this);

            } catch (Exception e) {
                e.printStackTrace();
            }
        try {
            LocationRequest mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(10000);
            mLocationRequest.setFastestInterval(5000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            if (location != null)
                changeMap(location);

            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            mGoogleApiClient.connect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {

        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                //finish();
            }
            return false;
        }
        return true;
    }

    private void changeMap(Location location) {
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

        // check if map is created successfully or not
        if (mMap != null) {
            mMap.getUiSettings().setZoomControlsEnabled(false);
            LatLng latLong;


            latLong = new LatLng(location.getLatitude(), location.getLongitude());
            //TODO: CAMERA TILT TOPOGRAPHY
//            CameraPosition cameraPosition = new CameraPosition.Builder().target(latLong).zoom(19f).tilt(70).build();
            CameraPosition cameraPosition = new CameraPosition.Builder().target(latLong).zoom(19f).build();

            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            //TODO: CAMERA TILT TOPOGRAPHY
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            //mLocationMarkerText.setText("Lat : " + location.getLatitude() + "," + "Long : " + location.getLongitude());
//TODO Location getter
//            startIntentService(location);


        } else {
            Toast.makeText(getApplicationContext(),
                    "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public static void setPickDate(int year, int monthOfYear, int dayOfMonth) {
        Load.setDate(dayOfMonth,monthOfYear,year);
    }

    public static void setPickTime(int hourOfDay, int minute) {
       Load.setTime(hourOfDay,minute);
    }


    /**
 * Receiver for data sent from FetchAddressIntentService.
 */
class AddressResultReceiver extends ResultReceiver {

    public AddressResultReceiver(Handler handler) {

        super(handler);
    }

    /**
     * Receives data sent from FetchAddressIntentService and updates the UI in MainActivity.
     */
    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {

        // Display the address string or an error message sent from the intent service.
        mAddressOutput = resultData.getString(AppUtils.LocationConstants.RESULT_DATA_KEY);

        mAreaOutput = resultData.getString(AppUtils.LocationConstants.LOCATION_DATA_AREA);

        mCityOutput = resultData.getString(AppUtils.LocationConstants.LOCATION_DATA_CITY);
        mStateOutput = resultData.getString(AppUtils.LocationConstants.LOCATION_DATA_STREET);

        displayAddressOutput();

        // Show a toast message if an address was found.
        if (resultCode == AppUtils.LocationConstants.SUCCESS_RESULT) {
            //  showToast(getString(R.string.address_found));
        }
    }
}
    /**
     * Updates the address in the UI.
     */
    protected void displayAddressOutput() {
        //  mLocationAddressTextView.setText(mAddressOutput);
        try {
            if (mAreaOutput != null)
                // mLocationText.setText(mAreaOutput+ "");
                Log.d("MAP LOG",mAddressOutput);
//                mLocationAddress.setText(mAddressOutput);
            //mLocationText.setText(mAreaOutput);
            //SEND THIS LOCATION TO DRIVER REQUEST
           //Toast.makeText(getApplicationContext(),"This location is "+mAddressOutput,Toast.LENGTH_SHORT).show();
            VIEW_TO_CHANGE.setText(mAddressOutput);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates an intent, adds location data to it as an extra, and starts the intent service for
     * fetching an address.
     */
    protected void startIntentService(Location mLocation) {
        // Create an intent for passing to the intent service responsible for fetching the address.
        Intent intent = new Intent(this, FetchAddressIntentService.class);

        // Pass the result receiver as an extra to the service.
        intent.putExtra(AppUtils.LocationConstants.RECEIVER, mResultReceiver);

        // Pass the location data as an extra to the service.
        intent.putExtra(AppUtils.LocationConstants.LOCATION_DATA_EXTRA, mLocation);

        // Start the service. If the service isn't already running, it is instantiated and started
        // (creating a process for it if needed); if it is running then it remains running. The
        // service kills itself automatically once all intents are processed.
        startService(intent);
    }

    private void openAutocompleteActivity() {
        try {
            // The autocomplete activity requires Google Play Services to be available. The intent
            // builder checks this and throws an exception if it is not the case.
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                    .build(this);
            startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
        } catch (GooglePlayServicesRepairableException e) {
            // Indicates that Google Play Services is either not installed or not up to date. Prompt
            // the user to correct the issue.
            GoogleApiAvailability.getInstance().getErrorDialog(this, e.getConnectionStatusCode(),
                    0 /* requestCode */).show();
        } catch (GooglePlayServicesNotAvailableException e) {
            // Indicates that Google Play Services is not available and the problem is not easily
            // resolvable.
            String message = "Google Play Services is not available: " +
                    GoogleApiAvailability.getInstance().getErrorString(e.errorCode);

            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();

        }
    }

    /**
     * Called after the autocomplete activity has finished to return its result.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //IMAGE_CAPTURE
        if(requestCode==REQUEST_IMAGE_CAPTURE){
            if (resultCode==RESULT_OK){
                Bundle extras=data.getExtras();
                Bitmap image= (Bitmap) extras.get("data");
                Load.setImage(image);
                //LinearLayout linearLayout= (LinearLayout) findViewById(R.id.test);
                //linearLayout.setBackground((Drawable)new BitmapDrawable(image));
            }
        }
        //CONTACTPICKER
        if(requestCode==CONTACT_PICKER_RESULT){
        if (resultCode == RESULT_OK) {
            String phoneNo = null;
            Uri uri = data.getData();
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();

            int phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            phoneNo = cursor.getString(phoneIndex);

            phoneNo = phoneNo.replace("+254", "0");
            phoneNo = phoneNo.replace(" ", "");
            phoneNo = phoneNo.replace("-", "");
            //TODO GET CALLING NUMBER
           EDIT_TEXT_TO_EDIT.setText(phoneNo);
        }
    }
        if(requestCode==PLACE_PICKER_DEST_REQUEST){
            if(resultCode==RESULT_OK){
                String Latlong="";
                Place place=PlacePicker.getPlace(this,data);
                //TODO THISSSSSSSSSSSSSSSSSSSS
//
                Location x=new Location("");
                x.setLatitude(place.getLatLng().latitude);
                x.setLongitude(place.getLatLng().longitude);
                if(MARKER_TYPE==PICK_FLAG){
                    LOCATION_FROM=new LatLng(x.getLatitude(),x.getLongitude());
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(LOCATION_FROM, 16);
                    mMap.animateCamera(cameraUpdate);
//                    if(!LOCATION_FROM.equals(null)&&!LOCATION_TO.equals(null)){
//                        String url = getMapsApiDirectionsUrl();
//                        ReadTask readTask = new ReadTask();
//                        readTask.execute(url);
//                    }
                }else{
                    LOCATION_TO=new LatLng(x.getLatitude(),x.getLongitude());
                    if(!LOCATION_FROM.equals(null)&&!LOCATION_TO.equals(null)){
                        showRoute();
                    }
                }
                if(MARKER_TYPE==DROP_FLAG){
//                    mMap.addMarker(new MarkerOptions()
//                            .position(LOCATION_TO)
//                            .title("Drop point")
//                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.add_marker))
//                            .anchor(0.0f, 1.0f));
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(LOCATION_TO, 16);
                    mMap.animateCamera(cameraUpdate);
                }
                mAddressOutput=null;

                startIntentService(x);

                go.setVisibility(View.GONE);
                hide.setVisibility(View.VISIBLE);

//                drop_loc.setText(mAddressOutput);
                request_pane.setVisibility(View.VISIBLE);

//                Toast.makeText(this, Latlong, Toast.LENGTH_LONG).show();
            }
        }
        //Place Picker Request
        if(requestCode==PLACE_PICKER_REQUEST){
            if(resultCode==RESULT_OK){
                String Latlong="";
                Place place=PlacePicker.getPlace(this,data);
//                String toastMsg = String.format("Place: %s", place.getLatLng());
                if(place.getLatLng().toString().contains("lat/lng")){
                    Latlong = place.getLatLng().toString().replace("lat/lng:","").replace(" ","").replace("(","").replace(")","");
                }
                Toast.makeText(this, Latlong, Toast.LENGTH_LONG).show();
            }
        }

        //location autocomplete check
        // Check that the result was from the autocomplete widget.
        if (requestCode == REQUEST_CODE_AUTOCOMPLETE) {
            if (resultCode == RESULT_OK) {
                // Get the user's selected place from the Intent.
                Place place = PlaceAutocomplete.getPlace(mContext, data);

                // TODO call location based filter
                Toast.makeText(this,"Place: " + place.getName(),Toast.LENGTH_SHORT).show();

                LatLng latLong;


                latLong = place.getLatLng();

                //mLocationText.setText(place.getName() + "");

                //TODO: CAMERA TILT TOPOGRAPHY
               //CameraPosition cameraPosition = new CameraPosition.Builder().target(latLong).zoom(19f).tilt(70).build();

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
                //TODO: CAMERA TILT TOPOGRAPHY
               // mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


            }


        } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
            Status status = PlaceAutocomplete.getStatus(mContext, data);
        } else if (resultCode == RESULT_CANCELED) {
            // Indicates that the activity closed before a selection was made. For example if
            // the user pressed the back button.
        }
    }
    //stop tracking when app is in background

//    @Override
//    protected void onPause() {
//        super.onPause();
//        stopLocationUpdates();
//    }
//
//    private void stopLocationUpdates() {
//        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,this);
//    }

    //CUSTOM LOCATION METHODS
    public void flatMarker(GoogleMap mMap){
        LatLng mapCenter = new LatLng(41.889, -87.622);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mapCenter,13));
        mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.snail))
                .position(mapCenter)
                .flat(true)
                .rotation(245));

        CameraPosition cameraPosition=CameraPosition.builder()
                                                .target(mapCenter)
                                                .zoom(13)
                                                .bearing(90)
                                                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),2000,null);



    }
}
