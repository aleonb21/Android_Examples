package angleos.sensors;

import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

import static java.lang.StrictMath.pow;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    /** Called when the activity is first created. */
// Buttons
    private Button startButton;
    private Button stopButton;
    // Views
    private TextView accelerationView;
    private TextView orientationView;
    private TextView gyroscopeView;
    private TextView magneticfieldView;
    private TextView proximityView;
    private TextView luminosityView;
    private TextView barometerView;
    private TextView altitudeView;
   // private ListView wifiListView;
    private static Boolean started = false;
    private static double altitude;
    // Signal Carrier
    private String carrierName;
    // Event Managers and Listeners:
// Sensors
    private SensorManager sensorManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
// Find Buttons
        startButton = (Button) findViewById(R.id.StartButton);
        stopButton = (Button) findViewById(R.id.StopButton);
// Find Views
        accelerationView = (TextView) findViewById(R.id.acelerationView);
        orientationView = (TextView) findViewById(R.id.orientationView);
        gyroscopeView = (TextView) findViewById(R.id.gyroscopeView);
        magneticfieldView = (TextView) findViewById(R.id.magneticfieldView);
        proximityView = (TextView) findViewById(R.id.proximityView);
        luminosityView = (TextView) findViewById(R.id.luminosityView);
        barometerView = (TextView) findViewById(R.id.barometerView);
        altitudeView = (TextView) findViewById(R.id.altitudeView);

// Real sensor Manager
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                started = true;
            }
        });
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                started = false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register sensors listeners
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY),
                SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT),
                SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE),
                SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    public void onSensorChanged(SensorEvent event) {
        synchronized (this) {
            if (started) {
                switch (event.sensor.getType()){
                    case Sensor.TYPE_ACCELEROMETER:
/* Get the Measures the acceleration force in m/s2 that is applied to a device
on all three physical axes (x, y, and z), including the force of gravity.*/
                        float xA = event.values[0];
                        float yA = event.values[1];
                        float zA = event.values[2];
                        String accelerationOut = String.format("X: %.2f Y: %.2f Z: %.2f", xA, yA, zA);
                        accelerationView.setText(accelerationOut);
                        break;
                    case Sensor.TYPE_ORIENTATION:
// Measures degrees of rotation that a device makes around all three physical axes (x, y, z)
                        float xO = event.values[0];
                        float yO = event.values[1];
                        float zO = event.values[2];
                        String orientationOut = String.format("X: %.2f Y: %.2f Z: %.2f", xO, yO, zO);
                        orientationView.setText(orientationOut);
                        break;
                    case Sensor.TYPE_GYROSCOPE:
// Measures a device's rate of rotation in rad/s around each of the three physical axes (x, y, and z)
                        float xG = event.values[0];
                        float yG = event.values[1];
                        float zG = event.values[2];
                        String gyroscopeOut = String.format("X: %.2f Y: %.2f Z: %.2f", xG, yG, zG);
                        gyroscopeView.setText(gyroscopeOut);
                        break;
                    case Sensor.TYPE_PROXIMITY:
// Measures the proximity of an object in cm relative to the view screen of a device.
                        float distance = event.values[0];
                        String proximityOut = String.format("%.2f cm", distance);
                        proximityView.setText(proximityOut);
                        break;
                    case Sensor.TYPE_PRESSURE:
                        float pressure = event.values[0];
                        String pressureOut = String.format("%.2f hPa", pressure);
                        barometerView.setText(pressureOut);
                        altitude = getAltitude(pressure);
                        String altitudeOut = String.format("%.2f m", altitude);
                        altitudeView.setText(altitudeOut);
                        break;
                    case Sensor.TYPE_LIGHT:
// Measures the ambient light level (illumination) in lx.
                        float ligth = event.values[0];
                        String lumninoxityOut = String.format("%.2f lx", ligth);
                        luminosityView.setText(lumninoxityOut);
                        break;
                    case Sensor.TYPE_MAGNETIC_FIELD:
//Measures the ambient geomagnetic field for all three physical axes (x, y, z) in mircoT.
                        float xM = event.values[0];
                        float yM = event.values[1];
                        float zM = event.values[2];
                        String magneticfiedlOut = String.format("X: %.2f Y: %.2f Z: %.2f", xM, yM, zM);
                        magneticfieldView.setText(magneticfiedlOut);
                        break;
                }
            }
        }
    }

    protected double getAltitude (float pressure){
        double altitude;
        altitude = (pow(((pressure * 100) / 101325), (1/5.25588)) - 1)/((-1)*0.0000225577);
        return altitude;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

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
        //if (id == R.id.action_settings) {
        //    return true;
        //}

        return super.onOptionsItemSelected(item);
    }
}
