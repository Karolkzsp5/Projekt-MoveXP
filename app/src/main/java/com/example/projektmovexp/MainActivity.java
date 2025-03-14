package com.example.projektmovexp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.projektmovexp.databinding.ActivityMainBinding;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private TextView stepCountTextView;
    private TextView distanceTextView;
    private TextView timeTextView;
    private Button pauseButton;
    private SensorManager sensorManager;
    private Sensor stepCounterSensor;
    private int initialStepCount = -1;
    private int stepCount = 0;
    private long timePaused = 0;
    private long startTime;
    private Handler timerHandler = new Handler();
    private  Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            long milliseconds = System.currentTimeMillis() - startTime;
            long seconds = milliseconds / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;
            //timeTextView.setText(String.format(Locale.getDefault(), "Time: %02d:%02d:%02d", hours % 60, minutes % 60, seconds % 60));
            //skomentowane bo wywala aplikację na tą chwilę
            timerHandler.postDelayed(this, 1000);

        }
    };

    private boolean isPaused = false;
    private float stepLength = 0.7f;
    private int stepCountGoal = 5000;
    private TextView stepCountGoalTextView;
    private ProgressBar progressBar;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // to jest do podłączenie do ui z activity_main.xml
        setContentView(R.layout.fragment_home);

        historyActivity = new HistoryActivity(this);

        // to zakomentowałem bo nie wiem jak dodac do poszczególnych zakładek z szablonu


//        binding = ActivityMainBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());

//        BottomNavigationView navView = findViewById(R.id.nav_view);
//        // Passing each menu ID as a set of Ids because each
//        // menu should be considered as top level destinations.
//        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
//                .build();
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
//        NavigationUI.setupWithNavController(binding.navView, navController);

        // to są pola tekstowe, przycisk, progress bar który powinien byc w ui, nie dziala bo ui nie miałem zeby polaczyc!!!!!!!!!!
        // reszta powinna dzialac na oko, nie wiem nie testowalem bo ui nie chce mi sie robic

        stepCountTextView = findViewById(R.id.stepCountTextView);
        /*distanceTextView = findViewById(R.id.distanceTextView);
        timeTextView = findViewById(R.id.timeTextView);
        pauseButton = findViewById(R.id.pauseButton);
        stepCountGoalTextView = findViewById(R.id.stepCountGoalTextView);
        stepCountGoalTextView.setText("Goal: " + stepCountGoal + " steps");
        progressBar = findViewById(R.id.progressBar);*/

        startTime = System.currentTimeMillis();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        /*progressBar.setMax(stepCountGoal);
        stepCountGoalTextView.setText("Goal: " + stepCountGoal + " steps");*/

        if(stepCounterSensor == null) {
            stepCountTextView.setText("Step Counter Sensor not available");
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if(stepCounterSensor!=null){
            sensorManager.unregisterListener(this);
            timerHandler.removeCallbacks(timerRunnable);
        }
        saveHistoryActivity(); // Zapisanie spaceru przed zamknięciem aplikacji
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (stepCounterSensor != null) {
            sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_NORMAL);
            timerHandler.postDelayed(timerRunnable, 0);
        }
    }

    // poprawiona wersja tej metody (z zapamiętaniem wartości początkowej)
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            if (initialStepCount == -1) {
                initialStepCount = (int) sensorEvent.values[0];
            }
            stepCount = (int) sensorEvent.values[0] - initialStepCount;

            stepCountTextView.setText("Kroki: " + stepCount);
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // nie wiem do czego to służy ale chyba działa gdy jest puste
    }

    public void onPausedButtonClicked(View view){
        if (isPaused){
            isPaused = false;
            pauseButton.setText("Pause");
            startTime = System.currentTimeMillis() - timePaused;
            timerHandler.postDelayed(timerRunnable,0);
        }else{
            isPaused=true;
            pauseButton.setText("Resume");

            timerHandler.removeCallbacks(timerRunnable);
            timePaused = System.currentTimeMillis() - startTime;
        }
    }

    // tworzenie obiektu HistoryActivity
    private HistoryActivity historyActivity;

    // metoda do zapisywania spaceru
    private void saveHistoryActivity() {
        long duration = System.currentTimeMillis() - startTime;
        float distance = stepCount * stepLength / 1000;
        historyActivity.saveWalk(stepCount, distance, duration);
    }

}