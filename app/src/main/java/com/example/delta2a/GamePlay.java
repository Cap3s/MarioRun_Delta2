package com.example.delta2a;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.LinearLayout;

public class GamePlay extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_play);

        LinearLayout container = findViewById(R.id.contain);

        MovingObstaclesView movingObstaclesView = new MovingObstaclesView(this, null);
        container.addView(movingObstaclesView);

        // Set the orientation to landscape
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

//        Intent intent = getIntent();
//        Integer Score = intent.getIntExtra("score");
//        onGameOver(Score);
//        // Set the game over listener
//        movingObstaclesView.setGameOverListener(GamePlay.this);

        movingObstaclesView.startMoving();
    }

//    private void showScoreDialog(int score) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(GamePlay.this);
//        builder.setTitle("Game Over");
//        builder.setMessage("Score: " + score);
//        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
//        AlertDialog dialog = builder.create();
//        dialog.show();
//    }
//
//    public void onGameOver(int score) {
//        showScoreDialog(score);
//    }
}