package mushirih.pickup.mapping;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
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
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.badoualy.stepperindicator.StepperIndicator;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
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
import mushirih.pickup.http.Load;
import mushirih.pickup.internal.MyApplication;
import mushirih.pickup.internal.MyPreferenceManager;
import mushirih.pickup.ui.DatePickerFragment;
import mushirih.pickup.ui.MainActivity;
import mushirih.pickup.ui.PrefManager;
import mushirih.pickup.ui.TimePickerFragment;

public class MapsActivity extends ActionBarActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener  {
   static Context mContext;
    LinearLayout searchloc;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    TextView mLocationText,pich_loc,drop_loc;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    ToggleButton zero,one, two, three;
    LinearLayout l0,l1, l2, l3,request_pane,request_time,describe_load,destination_pane,location_pick_graphic,top_dest;
    Button confirm;
    String TAG="MAPSACTIVITY LOG";
    private LatLng mCenterLatLong;
    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;
    int PLACE_PICKER_DEST_REQUEST=02022;
    int REQUEST_CHECK_SETTINGS = 20;
    private AddressResultReceiver mResultReceiver;
    protected String mAddressOutput;
    protected String mAreaOutput;
    protected String mCityOutput;
    protected String mStateOutput;
    static Activity activity;
   Location mLastLocation;
    CardView hide;
  TextView VIEW_TO_CHANGE;
    EditText EDIT_TEXT_TO_EDIT;
    int CONTACT_PICKER_RESULT=999;
    int PICK_FLAG=44;
    int DROP_FLAG=55;
    int MARKER_TYPE;
    static final int REQUEST_IMAGE_CAPTURE=802;
    static final int REQUEST_CONTACTS_PERMISSION=12;
    PrefManager prefManager;
    LatLng LOCATION_TO,LOCATION_FROM;
    String weight;
    ArrayList load_char;
    Bitmap image;
    ProgressDialog progressDialog;
    int showProgress=0;
    int DISTANCE_BETWEEN=0;
    String namee,idd,numm;
    boolean pick_point_set=false;
    boolean load_desc_set=false;
    boolean load_weight_set=false;
    boolean am_done=false;
    TextView progressTitle,next_action;
    StepperIndicator progressStepper;
    String[] options;
    int CAMERA_ZOOM=16;
    ProgressDialog loading,loader;
    String desc="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext=this;
        activity=this;
        prefManager=new PrefManager(mContext);
        if(!prefManager.isFirstLaunch()){
            //TODO SHOW APP INTRO
        }

        //pdf=new PDF(context, mBitmap);

        buildGoogleApiClient();
        setContentView(R.layout.activity_maps);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        progressTitle= (TextView) findViewById(R.id.progressTitle);
        next_action= (TextView) findViewById(R.id.next_action);
        progressStepper= (StepperIndicator) findViewById(R.id.progressStepper);

        updateProgress(true,0,"Set Collection Point",false);
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


       drop_loc= (TextView) findViewById(R.id.top_bar_location_rep);
        searchloc= (LinearLayout) findViewById(R.id.search_loc);
        searchloc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder()/*.setLatLngBounds(LatLngBounds.builder().include(mCenterLatLong).build()*/;
                try {
                    VIEW_TO_CHANGE=pich_loc;
                    MARKER_TYPE=PICK_FLAG;
                    startActivityForResult(builder.build(MapsActivity.this), PLACE_PICKER_DEST_REQUEST);
                    hide.setVisibility(View.VISIBLE);
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
        //TODO TODAYS EDIT
        hide= (CardView) findViewById(R.id.second_hide);
        hide.setVisibility(View.GONE);
        hide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pich_loc.getText().equals("Select pick up location")) {
                    pich_loc.setTextColor(Color.RED);
                    pich_loc.setTextSize(20);
                }else{
                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder()/*.setLatLngBounds(LatLngBounds.builder().include(mCenterLatLong).build()*/;
                    try {
                        VIEW_TO_CHANGE=drop_loc;
                        MARKER_TYPE=DROP_FLAG;
                        startActivityForResult(builder.build(MapsActivity.this), PLACE_PICKER_DEST_REQUEST);

                    } catch (GooglePlayServicesRepairableException e) {
                        e.printStackTrace();
                    } catch (GooglePlayServicesNotAvailableException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        location_pick_graphic= (LinearLayout) findViewById(R.id.locationMarker);
        //TODO PUT THIS IN CONTEXT
        confirm= (Button) findViewById(R.id.confirm);
        confirm.setText("Describe load");
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCenterLatLong.latitude>4.9||mCenterLatLong.latitude<-4.8||mCenterLatLong.longitude<34||mCenterLatLong.longitude>41){
                    Toast.makeText(mContext, "Only PickUp requests from Kenya Allowed", Toast.LENGTH_LONG).show();
                }else {
//                    //SET DATES FIRST
//                    //time pick
//                    DialogFragment newFragment = new TimePickerFragment();
//                    newFragment.show(getSupportFragmentManager(), "timePicker");
//                    //datePicker
//                    DialogFragment newfragment = new DatePickerFragment();
//                    newfragment.show(getSupportFragmentManager(), "datePicker");
                    //then proceed with load
                      //  describe_load();
                }
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
            builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Load.send();
                }
            });
            builder.show();
                }
            });
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        showProgress = 1;
        loader = new ProgressDialog(mContext);
        loader.setIndeterminate(true);
        loader.setMessage("Finding your location");
        loader.setCancelable(false);
        loader.show();

        mResultReceiver = new AddressResultReceiver(new Handler());


// If this check succeeds, proceed with normal processing.
        // Otherwise, prompt user to get valid Play Services APK.
        if (checkPlayServices()) {
            // notify user
            if (!AppUtils.isLocationEnabled(mContext)) {
                showLocationPermissionDialog();
//                AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
//                dialog.setMessage("Location not enabled!");
//                dialog.setPositiveButton("Open location settings", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
//                        //Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                        //startActivity(myIntent);
//                        test();
//                    }
//                });
//                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//
//                    @Override
//                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
//                        //  TODO Auto-generated method stub
//
//                    }
//                });
//                dialog.show();
            }

        } else {
            Toast.makeText(mContext, "Location not supported in this device", Toast.LENGTH_SHORT).show();
        }

        Explode explode = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            explode = new Explode();
            explode.setDuration(500);
            getWindow().setExitTransition(explode);
            getWindow().setEnterTransition(explode);
        }
    }
    private void updateProgress(boolean initial,int where, String nextActivity, boolean end){
        final int max=progressStepper.getStepCount();
        if(where<=max) {
            next_action.setText(nextActivity);
            progressStepper.setCurrentStep(where);
        }
    }
    private void showLocationPermissionDialog() {
            GoogleApiClient googleApiClient = new GoogleApiClient.Builder(mContext)
                    .addApi(LocationServices.API).build();
            googleApiClient.connect();

            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(10000);
            locationRequest.setFastestInterval(10000 / 2);

            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
            builder.setAlwaysShow(true);

            PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            Log.i(TAG, "All location settings are satisfied.");
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                            try {
                                // Show the dialog by calling startResolutionForResult(), and check the result
                                // in onActivityResult().
                                status.startResolutionForResult(MapsActivity.this, REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException e) {
                                Log.i(TAG, "PendingIntent unable to execute request.");
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                            break;
                    }
                }
            });
    }
    private void setDateTime() {
        //time pick
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker2");

        //datePicker
        DialogFragment newfragment = new DatePickerFragment();
       newfragment.setCancelable(false);
      newfragment.show(getSupportFragmentManager(), "datePicker2");

            //SET DATES FIRST
        updateProgress(false, 3, "Indicate weight of load", false);

            describe_load();


    }
    private  void describe_load() {
        //TODO test to see if button dissapears
        confirm.setVisibility(View.GONE);
        load_char=new ArrayList();
         options= new String[]{"Urgent", "Fragile ", "Perishable", "In need of packing boxes", "I need help loading"};
        final String[] weight_options={"Load under 5 Kgs","Load between 5-30 Kgs","Load over 30Kgs"};
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Please describe your load")
                .setCancelable(false)
                .setSingleChoiceItems(weight_options,-1 , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        weight=weight_options[which].toString();
                        if(load_weight_set==false) {
                            updateProgress(false,4, "Provide a description of load", false);
                            load_weight_set=true;
                        }

                    }

                })
            .setPositiveButton("Okay",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder
                            .setCancelable(false)
                            .setTitle("Please describe your load");
                    builder.setMultiChoiceItems(options, null, new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                            if(load_desc_set==false) {
                                updateProgress(false,5, "Provide details of collector", false);
                            }
                            load_desc_set=true;
                            if (isChecked) {
                                load_char.add(which);
                            } else if (load_char.contains(which)) {
                                load_char.remove(Integer.valueOf(which));
                            }
                        }
                    });
                    builder.setNegativeButton("Cancel", null);
                    //TODO: Contact permission
                    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED){
                      ActivityCompat.requestPermissions(MapsActivity.this, new String[] {
                                        Manifest.permission.READ_CONTACTS},
                                REQUEST_CONTACTS_PERMISSION);
                    }
                    if (ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MapsActivity.this, new String[]{
                                       Manifest.permission.CALL_PHONE},
                                8709);
                    }
                    builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        View layoutInflater = View.inflate(mContext, R.layout.contact_details, null);

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                            builder.setCancelable(false).setTitle("Provide details of person to pick goods.");
                            builder.setView(layoutInflater);
                            final View nambayake = layoutInflater.findViewById(R.id.numbertoget);
                            layoutInflater.findViewById(R.id.bContact).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    EDIT_TEXT_TO_EDIT = (EditText) nambayake;
                                    Intent pickContactIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                                    pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                                    startActivityForResult(pickContactIntent, CONTACT_PICKER_RESULT);
                                }
                            });
                            builder.setPositiveButton("Take picture of load", null);
//                            builder.setNegativeButton("Cancel", null);
                            final AlertDialog alertDialog = builder.create();
                            alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                                @Override
                                public void onShow(DialogInterface dialog) {
                                    Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                                    final EditText name, id, num;
                                    name = (EditText) layoutInflater.findViewById(R.id.name);
                                   id = (EditText) layoutInflater.findViewById(R.id.id);
                                    num = (EditText) layoutInflater.findViewById(R.id.numbertoget);
                                    b.setOnClickListener(new View.OnClickListener() {

                                        @Override
                                        public void onClick(View view) {
                                            namee=name.getText().toString();
                                            numm=num.getText().toString();
                                            idd=id.getText().toString();
                                            if (name.getText().length() == 0) {
                                                name.setError("Please fill in all details");
                                            }else  if (id.getText().length() == 0) {
                                                id.setError("Please fill in all details");
                                            }else  if (num.getText().length() == 0) {
                                                num.setError("Please fill in all details");
                                            }
                                            else {

                                                for(int i=0;i<load_char.size();i++){
                                                    desc+=options[(int)load_char.get(i)]+" ";
                                                }
                                                Load.bulkSet(mContext,LOCATION_FROM,LOCATION_TO,weight,desc,namee,idd,numm,DISTANCE_BETWEEN);
                                                //TODO CAPTURE PICTURE OF THE LOAD
                                                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                                    ActivityCompat.requestPermissions(MapsActivity.this, new String[]{
                                                                    Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                            879);
                                                }
                                                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                                if (takePicture.resolveActivity(getPackageManager()) != null) {
                                                    startActivityForResult(takePicture, REQUEST_IMAGE_CAPTURE);
                                                    alertDialog.cancel();


                                                }
                                            }
                                        }
                                    });

                                 //  builder.show();
                                }
                            });
                            alertDialog.show();
                            //builder.setView(View.inflate(mContext, R.layout.hello, null));
                            //builder.show();

                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    //TODO SECOND BUILDER
                     builder.show();


                }
            });
        builder.setNegativeButton("Cancel",null);
         builder.show();
    }
    private void showRoute() {
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(LOCATION_FROM).title("Pick Up Location"));
        MarkerOptions dest=new MarkerOptions().position(LOCATION_TO).title("Drop Location");
        Marker DEST= mMap.addMarker(dest);

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

    public static void requestSuccessful() {
         activity.finish();
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
                DISTANCE_BETWEEN= (int) (totalDistance/1000);

            }
        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera. In this case,
         * we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to install
         * it inside the SupportMapFragment. This method will only be triggered once the user has
         * installed Google Play services and returned to the app.
         */
        //TODO: ON map load,show drivers in the area
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION },
                    123);
        }else {

            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);

            mMap.setMyLocationEnabled(true);

            mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                @Override
                public void onCameraChange(CameraPosition cameraPosition) {
                    //TODO SHOW REQUEST PICK UP HERE FROM CENTER MAP COORDS
                    mCenterLatLong = cameraPosition.target;
                    final Location mLocation = new Location("");
                    mLocation.setLatitude(mCenterLatLong.latitude);
                    mLocation.setLongitude(mCenterLatLong.longitude);
                    centerMarkerClick(mLocation);
                }
            });
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        }
            //ADDS LOCATION Finder option on map
            // flatMarker(mMap);
    }
    private void centerMarkerClick(final Location mLocation) {
        //Check for drop of marker move
        if (mLocationText.getText().equals("Set drop point here") || pick_point_set) {
            mLocationText.setText("Set drop point here");
            //SET DESTINATION
            mLocationText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    VIEW_TO_CHANGE = drop_loc;
                    MARKER_TYPE = DROP_FLAG;
                    mAddressOutput = "";
                    startIntentService(mLocation);
                    hide.setVisibility(View.VISIBLE);
                    //TODO change this to pop
                    request_pane.setVisibility(View.VISIBLE);
                    location_pick_graphic.setVisibility(View.INVISIBLE);
                    mMap.setMinZoomPreference(10);
                    LOCATION_TO = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
                    if (!LOCATION_FROM.equals(null) && !LOCATION_TO.equals(null)) {
                        if (AppUtils.isDataEnabled(mContext)) {
                            showRoute();
                        } else {
                            Toast.makeText(getApplicationContext(), "Please check your internet connection", Toast.LENGTH_SHORT).show();
                        }
                    }
                    LatLng x= new LatLng((LOCATION_FROM.latitude+LOCATION_TO.latitude)/2,(LOCATION_FROM.longitude+LOCATION_TO.longitude)/2);
                    mMap.resetMinMaxZoomPreference();
                    if(DISTANCE_BETWEEN<30){
                        CAMERA_ZOOM=8;
                    }
                    else if(DISTANCE_BETWEEN>30&&DISTANCE_BETWEEN<60){
                        CAMERA_ZOOM=6;
                    }else{
                        CAMERA_ZOOM=4;
                    }
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(x, CAMERA_ZOOM);
                    mMap.animateCamera(cameraUpdate);
                    updateProgress(false,2, "Provide load weight", false);
                    setDateTime();
                }

            });

        } else {
            //TODO test to see if button dissapears
            request_pane.setVisibility(View.GONE);
            try {
                mLocationText.setText("Click to request pick up here");
                mLocationText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hide.setVisibility(View.VISIBLE);
                        hide.setVisibility(View.GONE);
                        //TODO Try find clicked location
                        MARKER_TYPE = PICK_FLAG;
                        VIEW_TO_CHANGE = pich_loc;
                        mAddressOutput = "";
                        startIntentService(mLocation);
                        if (pich_loc.getText().equals("Select pick up location")) {
                            pich_loc.setTextColor(Color.DKGRAY);
                            pich_loc.setTextSize(16);
                        }
                        // pich_loc.setText(mAddressOutput);
                        mLocationText.setText("Set drop point here");
                        Toast.makeText(getApplicationContext(), "Goods pick up point set at pin", Toast.LENGTH_LONG).show();
                        updateProgress(false,1, "Set destination point", false);
                        LOCATION_FROM = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
                        mMap.setMinZoomPreference(10);
                        hide.setVisibility(View.VISIBLE);
                    }

                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void onConnected(Bundle bundle) {
        //TODO TEST IF MAPS RELOADS
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
        if (showProgress == 1&&mLastLocation!=null) {
            loader.dismiss();
        }
        if (mLastLocation != null) {
          changeMap(mLastLocation);
            centerMarkerClick(mLastLocation);
           // VIEW_TO_CHANGE = drop_loc;
            //startIntentService(mLastLocation);
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
//            mLocationRequest.setInterval(10000);
//            mLocationRequest.setFastestInterval(5000);
            mLocationRequest.setInterval(20000);
            mLocationRequest.setFastestInterval(10000);
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
            if (showProgress == 1) {
               //progressDialog.dismiss();
                loader.dismiss();
//                centerMarkerClick(location);
            }
            try {
                if (location != null)
                    changeMap(location);
                centerMarkerClick(location);

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
if(!am_done) {
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
                "Sorry! unable to create maps"+am_done, Toast.LENGTH_LONG)
                .show();
    }
}

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

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
            mAddressOutput="";
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate your Menu
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id==R.id.action_logOut){
            MyPreferenceManager myPreferenceManager=new MyPreferenceManager(this);
            myPreferenceManager.logOutUser();
            startActivity(new Intent(getBaseContext(),MainActivity.class));
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Called after the autocomplete activity has finished to return its result.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //LOCATIONS SETTINGS REQUEST
        if(requestCode==REQUEST_CHECK_SETTINGS){
            switch (resultCode) {
                case Activity.RESULT_OK:
                    showProgress=1;
//                    progressDialog=new ProgressDialog(mContext);
//                    progressDialog.setIndeterminate(true);
//                    progressDialog.setMessage("Finding your position");
//                    progressDialog.setCancelable(false);
//                    progressDialog.show();
                    break;
                case Activity.RESULT_CANCELED:
                 //TODO HANDLE USER NOT TURNED LOCATION
                    break;
            }

        }
        //IMAGE_CAPTURE
        if(requestCode==REQUEST_IMAGE_CAPTURE){
            if (resultCode==RESULT_OK){
                Bundle extras=data.getExtras();
                image= (Bitmap) extras.get("data");
                Load.setImage(image);
                am_done=true;
                updateProgress(false,6, "Details Completed",true);

  //TODO change requestor button
                confirm.setVisibility(View.VISIBLE);
                confirm.setText("Request Delivery");
                confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //TODO ESTIMATE PRICE FIRST
                        getCostEstimate();
                        //Load.send();
                    }
                });
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
            cursor.close();
        }
    }
        if(requestCode==PLACE_PICKER_DEST_REQUEST){
            if(resultCode==RESULT_OK){
                Place place=PlacePicker.getPlace(this,data);
                Location x=new Location("");
                x.setLatitude(place.getLatLng().latitude);
                x.setLongitude(place.getLatLng().longitude);
                if(MARKER_TYPE==PICK_FLAG){
                    LOCATION_FROM=new LatLng(x.getLatitude(),x.getLongitude());
                    pick_point_set=true;
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(LOCATION_FROM, 16);
                    mMap.animateCamera(cameraUpdate);
                    startIntentService(x);
                    drop_loc.setText(null);
                    hide.setVisibility(View.VISIBLE);
                    updateProgress(false,1,"Set Destination Point",false);
                }else if(MARKER_TYPE==DROP_FLAG){
                    LOCATION_TO=new LatLng(x.getLatitude(),x.getLongitude());
                    if(!LOCATION_FROM.equals(null)&&!LOCATION_TO.equals(null)){
                        if(AppUtils.isDataEnabled(mContext)) {
                        showRoute();
                    }
                    }else{
                        Toast.makeText(getApplicationContext(),"Please check your internet connection",Toast.LENGTH_SHORT).show();
                    }

                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(LOCATION_TO, CAMERA_ZOOM);
                    mMap.animateCamera(cameraUpdate);
                    startIntentService(x);
                    //TODO WATCHING
                    centerMarkerClick(x);
                  //  setDateTime();
                    //updateProgress(false,2,"Choose Date and time of transport",false);


                }
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

    private void getCostEstimate() {
        //On acceping price
        //Load.send();
            loading = ProgressDialog.show(mContext, null, "Calculating price...",true,false);
            StringRequest strCost = new StringRequest(Request.Method.POST,
                    MyApplication.ONLINE_CALCULATE_COST, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    Log.e(TAG, "response: " + response);
                    try {
                        JSONObject obj = new JSONObject(response);
                        // check for error flag
                        if (obj.getString("error").equals("false")) {
                            // user successfully logged in
                            loading.dismiss();
                            View layoutInflater = View.inflate(mContext, R.layout.cost_est, null);
                            TextView est,tme;
                            Button accept,decline;
                                final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                builder.setCancelable(false);
                                //.setTitle("Request Details.");
                                builder.setView(layoutInflater);
                           est= (TextView) layoutInflater.findViewById(R.id.tvestimatedcost);
                            est.setText(obj.getString("cost"));
                           tme= (TextView) layoutInflater.findViewById(R.id.tvtimeanddate);
                            tme.setText(Load.DAY+"/"+Load.MONTH+"/"+Load.YEAR);
                             accept= (Button) layoutInflater.findViewById(R.id.accept);
                            decline= (Button) layoutInflater.findViewById(R.id.decline);
                            final AlertDialog alertDialog = builder.create();
                            accept.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    alertDialog.cancel();
                                    Load.send();
                                }
                            });
                             decline.setOnClickListener(new View.OnClickListener() {
                                 @Override
                                 public void onClick(View v) {
                                     //TODO ASK COMMENT,ASK PRICE EXPECTED
                                     finish();
                                 }
                             });
                            //builder.setPositiveButton("OKKKK", null);
//                            builder.setNegativeButton("Cancel", null);

                            alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                                    @Override
                                    public void onShow(DialogInterface dialog) {
                                        Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                                    }
                                    });
                            alertDialog.show();


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
                    Toast.makeText(getApplicationContext(), "Volley error: " + error.getMessage(), Toast.LENGTH_SHORT).show();

                }
            }) {

                @Override

                public Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("eng_id", String.valueOf(000));
                    params.put("distance",String.valueOf(DISTANCE_BETWEEN));
                    params.put("weight",weight);
                    params.put("nature",desc);
                    params.put("value", String.valueOf(000));

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
            MyApplication.getInstance().addToRequestQueue(strCost
            );

    }
    //stop tracking when app is in background
    @Override
    protected void onPause() {
        super.onPause();
        if(null!=mGoogleApiClient&&mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }
    }

    private void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,this);
    }
//
//    //CUSTOM LOCATION METHODS
//    public void flatMarker(GoogleMap mMap){
//        LatLng mapCenter = new LatLng(41.889, -87.622);
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mapCenter,13));
//        mMap.addMarker(new MarkerOptions()
//                .icon(BitmapDescriptorFactory.fromResource(R.drawable.snail))
//                .position(mapCenter)
//                .flat(true)
//                .rotation(245));
//
//        CameraPosition cameraPosition=CameraPosition.builder()
//                                                .target(mapCenter)
//                                                .zoom(13)
//                                                .bearing(90)
//                                                .build();
//        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),2000,null);
//
//
//
//    }
}
