package edu.stanford.cs108.finalproj;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.util.*;

public class MainActivity extends AppCompatActivity {
    private static final String TAG =  MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //**************** Only go to game activity for now *****************************
         setContentView(R.layout.activity_main);
        Button startBtn = (Button) findViewById(R.id.start_btn);

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, StartPopActivity.class)); // from mainactivity to startpop
            }
        });
        //*********************************************
        Log.d(TAG, "Main activity oncreate");

    }
}
