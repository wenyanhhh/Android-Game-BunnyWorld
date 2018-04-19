package edu.stanford.cs108.finalproj;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;

/**
 * Created by wenshengli on 3/2/18.
 */

public class StartPopActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.popwindow_start); // make popwindow_start(layout) as my content window

        // get the size of window
        //DisplayMetrics screenDM = new DisplayMetrics();
        //getWindowManager().getDefaultDisplay().getMetrics(screenDM);
        //int scrnW = screenDM.widthPixels;
        //int scrnH = screenDM.heightPixels;

        // make the pop window as 1/4 of original window
        //getWindow().setLayout((int) (scrnW * 0.5), (int) (scrnH * 0.5));


        Button playBtn = (Button) findViewById(R.id.play_btn);

        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StartPopActivity.this, GameActivity.class));



            }
        });

        Button editBtn = (Button) findViewById(R.id.edit_btn);

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StartPopActivity.this, EditorActivity.class));
            }
        });



    }
}
