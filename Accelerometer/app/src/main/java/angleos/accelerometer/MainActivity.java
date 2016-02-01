package angleos.accelerometer;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    private static boolean start = false;
    private static boolean saved = false;
    private static boolean energySaved = false;
    private static double x;
    private static double y;
    private static double z;
    private static double energ;
    private static double energyData;

    private Button startButton;
    private Button stopButton;
    private Button energyButton;
    private Button chartButton;

    private TextView xaxis;
    private TextView yaxis;
    private TextView zaxis;
    private TextView energy;

    private SensorManager sensorManager;

    private FileOutputStream writer;
    private FileOutputStream writer2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Intent plotEnergy = new Intent(this, EnergyDataChart.class);

        startButton = (Button) findViewById(R.id.button_start);
        stopButton = (Button) findViewById(R.id.button_stop);
        energyButton = (Button) findViewById(R.id.energyButton);
        chartButton = (Button) findViewById(R.id.chartButton);

        energyButton.setEnabled(false);
        chartButton.setEnabled(false);

        xaxis = (TextView) findViewById(R.id.xaxis);
        yaxis = (TextView) findViewById(R.id.yaxis);
        zaxis = (TextView) findViewById(R.id.zaxis);
        energy = (TextView) findViewById(R.id.energy);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        final String filePath = /*Environment.getExternalStorageDirectory().toString()+*/"RawData.txt";
        final String filePath2 = "Energy.txt";

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    writer = openFileOutput(filePath, Context.MODE_PRIVATE);
                    writer2 = openFileOutput(filePath2, Context.MODE_PRIVATE);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                start = true;
                Toast.makeText(getApplicationContext(), "Saving Raw Data to File", Toast.LENGTH_SHORT).show();
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start = false;
                if (saved) {
                    Toast.makeText(getApplicationContext(), "Raw Data Recorded", Toast.LENGTH_SHORT).show();
                    energyButton.setEnabled(true);
                }
            }
        });

        energyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    computeEnergyFromFile();
                    Toast.makeText(getApplicationContext(), "Energy Data Recorded", Toast.LENGTH_SHORT).show();
                    chartButton.setEnabled(true);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        chartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (energySaved){
                    startActivity(plotEnergy);
                }
            }
        });
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        synchronized (this) {
            if (start) {
                if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                    x = event.values[0];
                    y = event.values[1];
                    z = event.values[2];

                    energ = computeEnergy(x, y, z);

                    String xOut = String.format("%.2f", x);
                    String yOut = String.format("%.2f", y);
                    String zOut = String.format("%.2f", z);
                    String energyOut = String.format("%.2f", energ);

                    xaxis.setText(xOut);
                    yaxis.setText(yOut);
                    zaxis.setText(zOut);
                    energy.setText(energyOut);

                    try {
                        savetoFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onPause (){
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onStop(){
        super.onStop();
        saved = false;
        energySaved = false;

        if (writer != null)
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        if (writer2 != null){
            try {
                writer2.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
    }

    public void savetoFile() throws IOException {
        String rawData = String.format("%.3f %.3f %.3f \n", x, y, z);
        writer.write(rawData.getBytes());
        saved = true;
    }

    public void saveToEnergyFile() throws IOException {
        String energyDataOut = String.format("%.3f \n", energyData);
        writer2.write(energyDataOut.getBytes());
    }

    public void computeEnergyFromFile() throws FileNotFoundException {
        Scanner scan;
        double xData, yData, zData;
        File file = new File("/data/data/angleos.accelerometer/files/RawData.txt");
        try {
            scan = new Scanner(file);
            while (scan.hasNextDouble()) {
                xData = scan.nextDouble();
                yData = scan.nextDouble();
                zData = scan.nextDouble();
                energyData = computeEnergy(xData, yData, zData);
                saveToEnergyFile();
            }
        } catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        energySaved = true;
    }

    public double computeEnergy(double xD, double yD, double zD){
        return Math.sqrt(((Math.pow(xD, 2)) + (Math.pow(yD, 2)) + (Math.pow(zD, 2))));
    }
}
