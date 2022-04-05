package com.example.numbergame;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        MaterialButton startPlay = findViewById(R.id.start_playing_btn);
        startPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StartActivity.this, PlayActivity.class);
                startActivity(intent);
                finish();
            }
        });
        Intent intent = getIntent();
        boolean played = intent.getBooleanExtra("played", false);
        if(played) {
            double score = intent.getDoubleExtra("score", 0);
            // show this score on the dialog box
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater layoutInflaterAndroid = LayoutInflater.from(this);
            View view = layoutInflaterAndroid.inflate(R.layout.score_dialog_layout, null);
            builder.setView(view);
//            builder.setCancelable(false);
            TextView scoreTV = view.findViewById(R.id.final_score_tv);
            scoreTV.setText("Final score: " + score);
            final AlertDialog alertDialog = builder.create();
            view.findViewById(R.id.dismiss).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                }
            });
            alertDialog.show();
        }

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                    View.SYSTEM_UI_FLAG_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            );
        }
    }
}