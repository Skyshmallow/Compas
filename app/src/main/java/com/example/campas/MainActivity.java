package com.example.campas;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private TextView textView;
    private ImageView imageView;
    private SensorManager sensorManager;
    private Sensor acclerometrSensor,magnetometrSensor;

    private float[] lastAccelerometr= new float[3];
    private float[] lastMagnetometr= new float[3];
    private float[] rotationMatrix= new float[9];
    private float[] orientation = new float[3];

    boolean isLastAccelerometrcopyed = false;
    boolean isLastMagnetometrcopyed = false;

    long lastUpdatedTime=0;
    float currentDegree=0f;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        textView=findViewById(R.id.tvDegree);
        imageView=findViewById(R.id.ivDinamic);

        sensorManager=(SensorManager)getSystemService(SENSOR_SERVICE);
        acclerometrSensor=sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometrSensor=sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor==acclerometrSensor) {
            System.arraycopy(event.values, 0, lastAccelerometr, 0, event.values.length);
            isLastAccelerometrcopyed=true;
        }else if (event.sensor==magnetometrSensor){
            System.arraycopy(event.values, 0, lastMagnetometr, 0, event.values.length);
            isLastMagnetometrcopyed=true;
        }

        if(isLastAccelerometrcopyed && isLastMagnetometrcopyed && System.currentTimeMillis()-lastUpdatedTime>250);{
            SensorManager.getRotationMatrix(rotationMatrix,null,lastAccelerometr,lastMagnetometr);
            SensorManager.getOrientation(rotationMatrix,orientation);

            float azimuthInRadian=orientation[0];
            float azimuthInDegree= (float) Math.toDegrees(azimuthInRadian);

            RotateAnimation rotateAnimation=new RotateAnimation(currentDegree,-azimuthInDegree,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
            rotateAnimation.setDuration(250);
            rotateAnimation.setFillAfter(true);
            imageView.startAnimation(rotateAnimation);

            currentDegree=-azimuthInDegree;
            lastUpdatedTime=System.currentTimeMillis();

            int x = (int) azimuthInDegree;
            textView.setText(x+"°");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this,acclerometrSensor,SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this,magnetometrSensor,SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this,acclerometrSensor);
        sensorManager.unregisterListener(this,magnetometrSensor);
    }
}