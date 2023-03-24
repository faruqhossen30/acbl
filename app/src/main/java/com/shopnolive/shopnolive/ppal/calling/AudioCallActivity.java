package com.shopnolive.shopnolive.ppal.calling;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Chronometer;

import com.shopnolive.shopnolive.R;
import com.shopnolive.shopnolive.ppal.util.Common;

import java.util.Timer;
import java.util.TimerTask;

public class AudioCallActivity extends AppCompatActivity {

    private Chronometer chronometer;
    private boolean isRunning;
    private TimerTask timerTask;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Common.changeStatusBarColor(this);
        setContentView(R.layout.activity_audio_call2);
        //find xml
        chronometer=findViewById(R.id.chronometer);



        //initChronometer
        initChronometer();
    }

    private void initChronometer() {
        if (!isRunning){
            chronometer.start();
            isRunning = true;
        }
    }

    @Override
    protected void onDestroy() {
        if (isRunning){
            chronometer.stop();
            isRunning = false;
        }
        super.onDestroy();
    }
}