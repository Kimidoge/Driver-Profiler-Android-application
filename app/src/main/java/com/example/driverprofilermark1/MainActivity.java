package com.example.driverprofilermark1;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private static final String TAG = "MainActivity";

    // LABELS = ['Accelerate', 'Aggressive Accelerate', 'Aggressive Brake', 'Aggressive Left', 'Aggressive Right'
    //        , 'Brake', 'Idling', 'Left', 'Right']
    private String[] labels = {"Accelerate", "Aggressive Accelerate", "Aggressive Brake", "Aggressive Left", "Aggressive Right","Brake", "Idling", "Left", "Right"};

    private TextView accText, agroAccText, brakeText, agroBrakeText, rightText;
    private TextView agroRightText, leftText, agroLeftText, idlingText;

    private TextView firstTextView, secondTextView, thirdTextView, fourthTextView;
    private TextView fifthTextView, sixthTextView, sevenTextView, eightTextView, nineTextView;




    private static final int TIME_STAMP = 300;
    private static List<Float> ax, ay, az;
    private static List<Float> gx, gy, gz;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer, mGyroscope;
    private ActivityClassifier classifier;
    private float[] result;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prepareTextViews();
        setTextViews();


        ax = new ArrayList<>();
        ay = new ArrayList<>();
        az = new ArrayList<>();

        gx = new ArrayList<>();
        gy = new ArrayList<>();
        gz = new ArrayList<>();

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

       classifier = new ActivityClassifier(getApplicationContext());

       mSensorManager.registerListener(this, mAccelerometer, 10000);
       mSensorManager.registerListener(this, mGyroscope, 10000);



    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        predictActivity();
        Sensor sensor = event.sensor;
        if(sensor.getType()==Sensor.TYPE_ACCELEROMETER){
            ax.add(event.values[0]);
            ay.add(event.values[1]);
            az.add(event.values[2]);
        }

        else if(sensor.getType()==Sensor.TYPE_GYROSCOPE){
            gx.add(event.values[0]);
            gy.add(event.values[1]);
            gz.add(event.values[2]);
        }

        Log.d(TAG, "onSensorChanged: ax"+ ax);
        Log.d(TAG, "onSensorChanged: ay"+ ay);
        Log.d(TAG, "onSensorChanged: az"+ az);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private void predictActivity() {
        List<Float> data = new ArrayList<>();
        if(ax.size() >= TIME_STAMP && ay.size() >= TIME_STAMP && az.size() >= TIME_STAMP
        && gx.size() >= TIME_STAMP && gy.size() >= TIME_STAMP && gz.size() >= TIME_STAMP ){

            data.addAll(ax.subList(0,TIME_STAMP));
            data.addAll(ay.subList(0,TIME_STAMP));
            data.addAll(az.subList(0,TIME_STAMP));

            data.addAll(gx.subList(0,TIME_STAMP));
            data.addAll(gy.subList(0,TIME_STAMP));
            data.addAll(gz.subList(0,TIME_STAMP));

            //Log.d(TAG, "predictActivity: data" + data);



            result = classifier.predictProbabilities(toFloatArray(data));
            Log.i("Predictions", "predictActivity: "+ Arrays.toString(result) );

            // LABELS = ['Accelerate', 'Aggressive Accelerate', 'Aggressive Brake', 'Aggressive Left', 'Aggressive Right'
            //        , 'Brake', 'Idling', 'Left', 'Right']

            accText.setText("Accelerate" + Float.toString(result[0]));
            agroAccText.setText("Aggressive Accelerate" + Float.toString(result[1]));
            agroBrakeText.setText("Aggressive Brake" + Float.toString(result[2]));
            agroLeftText.setText("Aggressive Left" + Float.toString(result[3]));
            agroRightText.setText("Aggressive Right" + Float.toString(result[4]));
            brakeText.setText("Brake" + Float.toString(result[5]));
            idlingText.setText("Idling" + Float.toString(result[6]));
            leftText.setText("Left" + Float.toString(result[7]));
            rightText.setText("Right" + Float.toString(result[8]));


            data.clear();
            ax.clear();
            ay.clear();
            az.clear();
            gx.clear();
            gy.clear();
            gz.clear();

        }
    }

    private float[] toFloatArray(List<Float> data){
        int i = 0;
        float[] array = new float[data.size()];
        for (Float f:data){
            array[i++] = (f !=null ? f: Float.NaN);
        }
        return array;
    }

    private void prepareTextViews(){

        accText = (TextView)findViewById(R.id.accelerate_prob);
        agroAccText = (TextView)findViewById(R.id.agroAccelerate_prob);
        brakeText = (TextView) findViewById(R.id.brake_prob);
        agroBrakeText =(TextView) findViewById(R.id.agroBrake_prob);
        leftText = (TextView) findViewById(R.id.left_prob);
        agroLeftText = (TextView) findViewById(R.id.agroLeft_prob);
        rightText = (TextView) findViewById(R.id.right_prob);
        agroRightText = (TextView)findViewById(R.id.agroRight_prob);
        idlingText = (TextView) findViewById(R.id.idling_prob);

        firstTextView = (TextView) findViewById(R.id.accelerate_title);
        secondTextView = (TextView) findViewById(R.id.agroAccelerate_title);
        thirdTextView = (TextView) findViewById(R.id.agroBrake_prob);
        fourthTextView = (TextView) findViewById(R.id.agroLeft_title);
        fifthTextView = (TextView) findViewById(R.id.agroRight_title);
        sixthTextView = (TextView) findViewById(R.id.brake_title);
        sevenTextView = (TextView) findViewById(R.id.idling_title);
        eightTextView = (TextView) findViewById(R.id.left_title);
        nineTextView = (TextView) findViewById(R.id.right_title);

    }

    private void setTextViews(){

        firstTextView.setText(labels[0]);
        secondTextView.setText(labels[1]);
        thirdTextView.setText(labels[2]);
        fourthTextView.setText(labels[3]);
        fifthTextView.setText(labels[4]);
        sixthTextView.setText(labels[5]);
        sevenTextView.setText(labels[6]);
        eightTextView.setText(labels[7]);
        nineTextView.setText(labels[8]);

       // LABELS = ['Accelerate', 'Aggressive Accelerate', 'Aggressive Brake', 'Aggressive Left', 'Aggressive Right'
       //        , 'Brake', 'Idling', 'Left', 'Right']

    }





    @Override
    protected void onResume() {
        super.onResume();

        mSensorManager.registerListener(this, mAccelerometer, 10000);
        mSensorManager.registerListener(this, mGyroscope, 10000);
        Toast.makeText(this, "onResume started", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "onDestroy started", Toast.LENGTH_SHORT).show();
        mSensorManager.unregisterListener(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        Toast.makeText(this, "onPause started", Toast.LENGTH_SHORT).show();
        mSensorManager.unregisterListener(this);
    }
}