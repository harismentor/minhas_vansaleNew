package com.advanced.minhas.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.advanced.minhas.CoordinateManager;
import com.advanced.minhas.R;
import com.advanced.minhas.controller.ConnectivityReceiver;
import com.advanced.minhas.localdb.MyDatabase;
import com.advanced.minhas.model.Shop;
import com.advanced.minhas.session.SessionAuth;
import com.advanced.minhas.webservice.WebService;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

import static com.advanced.minhas.config.ConfigKey.CUSTOMER_KEY;
import static com.advanced.minhas.config.ConfigKey.DAY_REGISTER_KEY;
import static com.advanced.minhas.config.ConfigKey.SHOP_KEY;
import static com.advanced.minhas.webservice.WebService.webUpdateCustomer;

public class MapViewActivity extends FragmentActivity implements OnMapReadyCallback {

    private String TAG = "MapViewActivity";

    private MapView mapView;
    private GoogleMap gmap;

    private FloatingActionButton fabLocationUpdate;
    private TextView tvName, tvLocation;
    private Shop SELECTED_SHOP = null;

    private SessionAuth sessionAuth;
    private String dayRegId;

    private String EXECUTIVE_ID = "";

    ArrayList<LatLng> markerPoints;

    private static final int LOC_REQ_CODE = 1;
    private static final int PLACE_PICKER_REQ_CODE = 2;
    private ActivityManager manager;
    WindowManager mWindowManager;
    private View mBubble;
//private MyDatabase myDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);
        this.manager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        /*
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);




    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }





*/





    mapView = (MapView) findViewById(R.id.mapView);


    tvName = (TextView) findViewById(R.id.textView_map_name);
    tvLocation = (TextView) findViewById(R.id.textView_map_address);

    fabLocationUpdate=(FloatingActionButton)findViewById(R.id.fab_map_update);

        this.manager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        this.sessionAuth=new SessionAuth(MapViewActivity.this);
        this.EXECUTIVE_ID =sessionAuth.getExecutiveId();


        try {


        mapView.onCreate(savedInstanceState);
    }catch (RuntimeException e){
            Log.d(TAG, "Map view Exception  " + e.getMessage());
    }
        mapView.getMapAsync(this);


        try {
        SELECTED_SHOP = (Shop) getIntent().getSerializableExtra(SHOP_KEY);
    } catch (Exception e) {
        e.getStackTrace();
    }

        if (SELECTED_SHOP == null) {
        finish();
        return;
    }


        tvName.setText(SELECTED_SHOP.getShopName());
        tvLocation.setText(SELECTED_SHOP.getShopAddress());


    // Initializing
    markerPoints = new ArrayList<LatLng>();


        fabLocationUpdate.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            getCurrentPlaceItems();

        }
    });


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

        gmap = googleMap;


//        String _latitude=SELECTED_SHOP.getLatitude();
//        String _longitude=SELECTED_SHOP.getLongitude();

        String _latitude="11.059870";
        String _longitude="76.273499";


        if (_latitude!=null&&_longitude!=null&&!TextUtils.isEmpty(_latitude)&&!TextUtils.isEmpty(_longitude)&&CoordinateManager.isValidLatitude(Float.valueOf(_latitude))&&CoordinateManager.isValidLongitude(Float.valueOf(_longitude))) {
            final LatLng latLng = new LatLng(Double.valueOf(_latitude), Double.parseDouble(_longitude));
            setMarker(SELECTED_SHOP.getShopName(), latLng);
        }else {
            Toast.makeText(MapViewActivity.this,"Location Not Updated Please update new One",Toast.LENGTH_SHORT).show();
            getCurrentPlaceItems();
        }

    }



    public void setMarker(final String name, final LatLng latPoint) {


        try {


            if (gmap == null)
                return;


            gmap.clear();

//LatLngBound will cover all your marker on Google Maps
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latPoint, 18.0f);
            gmap.animateCamera(cameraUpdate);


            gmap.addMarker(new MarkerOptions().position(latPoint).
                    icon(BitmapDescriptorFactory.fromBitmap(
                            createCustomMarker(MapViewActivity.this,R.drawable.marker_image,name))));



        } catch (IllegalArgumentException | NullPointerException e) {
            e.getStackTrace();
        }




    }


    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();

    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }




    public static Bitmap createCustomMarker(Context context, @DrawableRes int resource, String _name) {

        View marker = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_layout, null);

//        ImageView markerImage = (ImageView) marker.findViewById(R.id.user_dp);
//        markerImage.setImageResource(resource);
        TextView txt_name = (TextView)marker.findViewById(R.id.textView_marker_name);
        txt_name.setText(_name);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        marker.setLayoutParams(new ViewGroup.LayoutParams(52, ViewGroup.LayoutParams.WRAP_CONTENT));
        marker.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(marker.getMeasuredWidth(), marker.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        marker.draw(canvas);

        return bitmap;
    }

    public double CalculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.d(TAG, "Radius Value" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);

        return Radius * c;
    }



    private void getCurrentPlaceItems() {
        if (isLocationAccessPermitted()) {
            Log.e("if","if");
            showPlacePicker();
        } else {
            Log.e("else","else");
            requestLocationAccessPermission();
        }
    }

    @SuppressLint("MissingPermission")
    private void showPlacePicker() {
        Log.e("if reached","2");

        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        Log.e("if reached","3");
        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQ_CODE);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage().toString());
            Log.e(TAG, e.getStackTrace().toString());
        }
    }

    private boolean isLocationAccessPermitted() {
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationAccessPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOC_REQ_CODE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == LOC_REQ_CODE) {
            if (resultCode == RESULT_OK) {
                showPlacePicker();
            }
        }else if(requestCode == PLACE_PICKER_REQ_CODE){
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);

                String toastMsg = String.format("Place: %s", place.getName());


                String _latitude=String.valueOf(place.getLatLng().latitude);
                String _longitude=String.valueOf(place.getLatLng().longitude);

                updateCustomerRequest( place );
               /* if (new MyDatabase(MapViewActivity.this).updateLocation(SELECTED_SHOP.getShopId(),_latitude,_longitude)) {
                    Toast.makeText(MapViewActivity.this, "Location Updated", Toast.LENGTH_SHORT).show();
                    setMarker(SELECTED_SHOP.getShopName(), place.getLatLng());

                }else
                    Toast.makeText(MapViewActivity.this, "Updated Failed", Toast.LENGTH_SHORT).show();
*/

            }
        }
    }



    //    update customer request
    private void updateCustomerRequest(final Place place) {

        if (!checkConnection()) {
            Toast.makeText(MapViewActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();



            return;
        }
       final String _latitude=String.valueOf(place.getLatLng().latitude);
       final String _longitude=String.valueOf(place.getLatLng().longitude);

        JSONObject object = new JSONObject();
        try {
            object.put(CUSTOMER_KEY, SELECTED_SHOP.getShopId());
            object.put(DAY_REGISTER_KEY, dayRegId);
            object.put("latitude", _latitude);
            object.put("longitude", _longitude);
        } catch (JSONException e) {
            e.printStackTrace();
        }


//        Log.v(TAG, "updateCustomerRequest  object   " + object);

        final ProgressDialog pd = ProgressDialog.show(MapViewActivity.this, null, "Please wait...", false, false);

        webUpdateCustomer(new WebService.webObjectCallback() {
            @Override
            public void onResponse(JSONObject response) {

//                Log.v(TAG, "updateCustomerRequest  response   " + response);
                try {
                    if (response.getString("status").equals("success")) {
                        pd.dismiss();
                        if (new MyDatabase(MapViewActivity.this).updateLocation(SELECTED_SHOP.getShopId(), _latitude, _longitude)){

                            Toast.makeText(MapViewActivity.this, "Location Updated", Toast.LENGTH_SHORT).show();
                            setMarker(SELECTED_SHOP.getShopName(), place.getLatLng());
                        }else {


                            Toast.makeText(MapViewActivity.this, "Updated Failed", Toast.LENGTH_SHORT).show();

                            onBackPressed();
                        }
                    } else {

                            pd.dismiss();
                        Toast.makeText(MapViewActivity.this, response.getString("status"), Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (pd.isShowing())
                    pd.dismiss();
                }


                /*

                SELECTED_SHOP=new MyDatabase(MapViewActivity.this).getIdWiseCustomer(SELECTED_SHOP.getShopId())

                String _latitude=SELECTED_SHOP.getLatitude();
                String _longitude=SELECTED_SHOP.getLongitude();


                if (_latitude!=null&&_longitude!=null&&!TextUtils.isEmpty(_latitude)&&!TextUtils.isEmpty(_longitude)&&CoordinateManager.isValidLatitude(Float.valueOf(_latitude))&&CoordinateManager.isValidLongitude(Float.valueOf(_longitude))) {
                    final LatLng latLng = new LatLng(Double.valueOf(_latitude), Double.parseDouble(_longitude));
                    setMarker(SELECTED_SHOP.getShopName(), latLng);
                }else {

                    getCurrentPlaceItems();
                }
*/

                if (pd.isShowing())
                    pd.dismiss();



            }

            @Override
            public void onErrorResponse(String error) {

                pd.dismiss();
                Toast.makeText(MapViewActivity.this, error, Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
        }, object);



    }

        /**
         * Check InterNet
         */
        private boolean checkConnection() {
            return ConnectivityReceiver.isConnected();
        }

}
