package com.jokker.fingerspeedgame;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private TextView timerTextView;
    private TextView remainingTapsTextView;
    private Button tapButton;

    private CountDownTimer countDownTimer;
    private long initialCountDownInMillis = 15000;
    private long timeIntervalInMillis = 1000;
    private int remainingTime = 60;

    private int targetScore = 20;

    private final String REMAINING_TIME = "REMAINING_TIME";
    private final String REMAINING_SCORE = "REMAINING_SCORE";

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        makeToast("OnSaved() InstanceState Called...");

        outState.putInt( REMAINING_TIME, remainingTime );
        outState.putInt( REMAINING_SCORE, targetScore );
        countDownTimer.cancel();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        makeToast("onDestroy() Called...");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timerTextView = findViewById(R.id.timerTextViewId);
        remainingTapsTextView = findViewById(R.id.remainingTapsTextViewId);
        tapButton = findViewById(R.id.tapButtonId);

        remainingTapsTextView.setText(targetScore + "");

        if( savedInstanceState != null ) {

            remainingTime = savedInstanceState.getInt( REMAINING_TIME );
            targetScore = savedInstanceState.getInt( REMAINING_SCORE );



            restoreGame();
        }

        tapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                targetScore--;

                remainingTapsTextView.setText(targetScore + "");

                if( remainingTime > 0  &&  targetScore <= 0 ) {
                    makeToast("Congrats! You Won !!!");

                    countDownTimer.cancel();
                    showGameEndAlert( "Congratulations, you Won", "Reset the Game ?" );
                }

            }
        });

        if( savedInstanceState == null ) {

            countDownTimer = new CountDownTimer(initialCountDownInMillis, timeIntervalInMillis) {
                @Override
                public void onTick(long millisUntilFinished) {
                    remainingTime = (int) millisUntilFinished / 1000;
                    timerTextView.setText(remainingTime + "");
                    Log.i(TAG, "searchInLogs : onTick: ------ " + remainingTime + " -------");
                }

                @Override
                public void onFinish() {
                    makeToast("Time Up!");
                    showGameEndAlert("You Lost !", "Wanna try again ?");
                }
            };

            countDownTimer.start();
        }

    }

    private void restoreGame() {

        int restoredRemainingTime = remainingTime ;
        int restoredRemainingScore = targetScore ;

        timerTextView.setText( restoredRemainingTime + "" );
        remainingTapsTextView.setText( restoredRemainingScore + "" );

        countDownTimer = new CountDownTimer( (long) restoredRemainingTime * 1000, timeIntervalInMillis ) {
            @Override
            public void onTick(long millisUntilFinished) {
                remainingTime = (int) millisUntilFinished / 1000 ;
                timerTextView.setText( remainingTime + "");
            }

            @Override
            public void onFinish() {
                showGameEndAlert( "You Lost !", "Wanna try again ?" );
            }
        };

        countDownTimer.start();

    }

    private void resetGame() {

        if( countDownTimer != null ) {
            countDownTimer.cancel();
            countDownTimer = null;
        }

        targetScore = 20;
        remainingTapsTextView.setText( targetScore + "" );

        timerTextView.setText( remainingTime + "" );

        countDownTimer = new CountDownTimer(initialCountDownInMillis, timeIntervalInMillis) {
            @Override
            public void onTick(long millisUntilFinished) {
                remainingTime = (int) millisUntilFinished / 1000 ;
                timerTextView.setText(remainingTime + "");
            }

            @Override
            public void onFinish() {
                countDownTimer.cancel();
                showGameEndAlert( "Game Finished", "Want to reset the game ?" );
            }
        };

        countDownTimer.start();
    }

    private void showGameEndAlert(String title, String message) {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle(title)
                .setMessage(message)

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        resetGame();
                    }
                })
                .setCancelable(false)
//                // A null listener allows the button to dismiss the dialog and take no further action.
//                .setNegativeButton(android.R.string.no, null)
//                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

    }

    private void makeToast(String message) {
        Toast.makeText( MainActivity.this, message + "", Toast.LENGTH_SHORT ).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate( R.menu.info_menu, menu );
        return true ;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if( item.getItemId() == R.id.infoMenuId ) {
            makeToast( BuildConfig.VERSION_NAME );
        }
        return true;
    }
}