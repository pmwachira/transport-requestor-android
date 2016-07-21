package mushirih.pickup.mapping;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import mushirih.pickup.R;
import mushirih.pickup.pdf.PDF;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    PDF pdf;
    ToggleButton one, two, three;
    LinearLayout l1, l2, l3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context = this;

        //pdf=new PDF(context, mBitmap);


        setContentView(R.layout.activity_maps);

        one = (ToggleButton) findViewById(R.id.toggleButton1);
        two = (ToggleButton) findViewById(R.id.toggleButton2);
        three = (ToggleButton) findViewById(R.id.toggleButton3);

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

        // Toast.makeText(context,"Call PDF ? ",Toast.LENGTH_LONG).show();


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


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
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-1.22001084,36.89884089);
        CameraUpdate update= CameraUpdateFactory.newLatLngZoom(sydney, 16);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Somewhere in Nairobi"));
        mMap.moveCamera(update);


    }


}
