package hu.pannon.workerdashboard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowMetrics;


import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private boolean isDark = false;
    private SensorManager sensorManager;
    private int shakeSensitivity = 32;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor sensorShake = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        SensorEventListener sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                if (sensorEvent!=null){
                    float x_accl = sensorEvent.values[0];
                    float y_accl = sensorEvent.values[1];
                    float z_accl = sensorEvent.values[2];

                    float floatSum = Math.abs(x_accl) + Math.abs(y_accl) + Math.abs(z_accl);

//                    if(x_accl > 2 ||
//                            x_accl < -2 ||
//                            y_accl > 12 ||
//                            y_accl < -12 ||
//                            z_accl > 2 ||
//                            z_accl < -2)

                    if (floatSum > shakeSensitivity) {
                        if (isDark == false) {
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                            Log.e("test","Rezgett");
                            isDark = true;
                        } else {
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                            Log.e("test"," vissza");
                            isDark = false;
                        }
                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
            }
        };
        sensorManager.registerListener(sensorEventListener, sensorShake, SensorManager.SENSOR_DELAY_NORMAL);


        FirebaseApp.initializeApp(this);
        DatabaseLoader dbLoader = DatabaseLoader.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        dbLoader.setDatabase(db);

        setContentView(R.layout.activity_main);

        Fragment mLoginFragment = new LoginFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentHolder, mLoginFragment).commit();



    }

    @Override
    protected void onPause() {
        super.onPause();
//        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


}