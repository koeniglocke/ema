package de.beuth.ema.ema_lightsensortest;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {


    private SensorManager sensorManager;
    private Sensor lightSensor;
    private SensorEventListener lightEventListener;
    private View root;
    private float maxValue;
    private float currentValue = 0;
    private TextView updown;

    private boolean isCalibrated = false;
    private int countCalibrator = 5;
    private int[] valuesForCalibration = new int[countCalibrator];
    private int maxCalValue = 11;
    private int currentMax;
    private int currentMin;
    private int currentAverage;
    private double interimCalculation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        root = findViewById(R.id.root);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        updown = (TextView) findViewById(R.id.updown);


        if (lightSensor == null) {
            Toast.makeText(this, "The device has no light sensor !", Toast.LENGTH_SHORT).show();
            finish();
        }

        // max value for light sensor
        maxValue = lightSensor.getMaximumRange();

        lightEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                float value = sensorEvent.values[0];
                getSupportActionBar().setTitle("Luminosity : " + value + " lx");

                if(!isCalibrated) {
                    if(countCalibrator > 0){
                        Log.i("Calibrator", "onSensorChanged: not calibrated ("+countCalibrator+" left)");

                        valuesForCalibration[countCalibrator-1] = (int) value;
                        countCalibrator--;
                    } else if(countCalibrator <= 0) {
                        Log.i("Calibrator", "onSensorChanged: YEAH");
                        getMinMaxAverage();
                        isCalibrated = true;
                    }
                }

                //

                if(isCalibrated){
                    updown.setVisibility(View.VISIBLE);


                    if (currentValue < value) {
                        updown.setText("UP");
                    } else if (currentValue > value) {
                        updown.setText("Down");
                    }
                    currentValue = value;

                }



                /* Change Display brightness -- is optional*/
                // between 0 and 255
                //int newValue = (int) (255f * value / maxValue);
                //root.setBackgroundColor(Color.rgb(newValue, newValue, newValue));


            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(lightEventListener, lightSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(lightEventListener);
    }

    private void getMinMaxAverage() {

        currentMax = 0;
        currentMin = 0;
        currentAverage = 0;
        boolean firstTurn = true;

        for (int v:valuesForCalibration) {
            Log.i("Calibrator", "valueOfCalibration: " + v);

            if(firstTurn){
                currentMin = v;
                currentMax = v;
                firstTurn = false;
            } else if(v > currentMax) {
                currentMax = v;
            } else if(v < currentMin) {
                currentMin = v;
            }
            currentAverage = currentAverage + currentAverage;
        }

        currentAverage = currentAverage / valuesForCalibration.length;

        Log.d("MinMAXAverage", "getMinMaxAverage: min - " + currentMin);
        Log.d("MinMAXAverage", "getMinMaxAverage: max - " + currentMax);
        Log.d("MinMAXAverage", "getMinMaxAverage: average - " + currentAverage);

    }

}