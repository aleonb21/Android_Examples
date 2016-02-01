package angleos.mylocation;

import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks
        , GoogleApiClient.OnConnectionFailedListener
        , com.google.android.gms.location.LocationListener{

    private TextView mLatitudeText;
    private TextView mLongitudeText;
    private static boolean flag;
    private GoogleMap mMap;
    private static int count = 0;
    protected GoogleApiClient mGoogleApiClient;
    protected Location mLastLocation;
    private LocationRequest mLocationRequest;
    public static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpMapIfNeeded();

        mLatitudeText = (TextView) findViewById(R.id.latitude);
        mLongitudeText = (TextView) findViewById(R.id.longitude);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)
                .setFastestInterval(1 * 1000);


    }

    private void setUpMapIfNeeded(){
        if (mMap == null){
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap(){
        //mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));

    }

    @Override
    public void onConnected(Bundle connectionHint){
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLastLocation != null){
            mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
            mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
            Toast.makeText(this, R.string.location_acquired, Toast.LENGTH_LONG).show();
        }
        else{
            handleNewLocation(mLastLocation);
            //Toast.makeText(this, R.string.no_location_detected, Toast.LENGTH_LONG).show();
        }
    }



    private void handleNewLocation(Location location){
        //Log.d(TAG, location.toString());
        if (flag)
            Toast.makeText(this, R.string.location_updated, Toast.LENGTH_SHORT).show();
        mLastLocation = location;
        mLatitudeText.setText(String.valueOf(location.getLatitude()));
        mLongitudeText.setText(String.valueOf(location.getLongitude()));

        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();
        LatLng latLng = new LatLng(currentLatitude, currentLongitude);

        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title("I am here!");
        mMap.clear();
        mMap.addMarker(options);
        if (!flag && count == 0) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            count = 1;
        }
    }

    private void manualRequest(){
        Toast.makeText(this, R.string.refreshing, Toast.LENGTH_SHORT).show();
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        Location tempLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        flag = true;
        handleNewLocation(tempLocation);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Suspended");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id){
            case R.id.action_refresh:
                manualRequest();
                return true;
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    protected void onResume(){
        super.onResume();
        setUpMapIfNeeded();
        Toast.makeText(this, R.string.acquiring_location, Toast.LENGTH_SHORT).show();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause(){
        super.onPause();
        count = 0;
        if (mGoogleApiClient.isConnected()){
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        flag = false;
        handleNewLocation(location);
    }

    //@Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    //@Override
    public void onProviderEnabled(String provider) {

    }

    //@Override
    public void onProviderDisabled(String provider) {

    }
}
