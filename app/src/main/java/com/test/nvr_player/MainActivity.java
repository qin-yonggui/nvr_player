package com.test.nvr_player;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.test.nvrsearch.NvrManager;

public class MainActivity extends AppCompatActivity {

    boolean hasStart = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView textView = findViewById(R.id.testSearch);
        NvrManager nvrManager = new NvrManager();
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("qyg", "onClick: start detect");
                if (hasStart) {
                    nvrManager.stop();
                    textView.setText(" start search");
                    hasStart = false;
                } else {
                    nvrManager.startDetect();
                    textView.setText(" searching...");
                    hasStart = true;
                }

            }
        });
    }
}