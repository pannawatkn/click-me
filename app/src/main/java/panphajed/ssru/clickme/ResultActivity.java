package panphajed.ssru.clickme;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import panphajed.ssru.clickme.Class.UserModel;

import static panphajed.ssru.clickme.Class.Media.musicMute;
import static panphajed.ssru.clickme.Class.Media.soundMute;


public class ResultActivity extends AppCompatActivity {

    private DatabaseReference myRef, myRefHighScore;

    private TextView modeResultTextView,
                scoreResultTextView,
                textViewScore,
                percentResultTextView;
    private EditText inputNameResultEditText;
    private Button leaderboardButtonResult, playAgainButtonResult, backToHomeButtonResult;

    private String modeText;
    private int mode, score, maxCombo, scoreFull;

    private UserModel user;
    private boolean addNameSuccess = false;

    private MediaPlayer clickBtn;
    private boolean openActivity = false;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        modeResultTextView = findViewById(R.id.modeResultTextView);
        scoreResultTextView = findViewById(R.id.scoreResultTextView);
        textViewScore = findViewById(R.id.textViewScore);
        percentResultTextView = findViewById(R.id.percentResultTextView);

        leaderboardButtonResult = findViewById(R.id.leaderboardButtonResult);
        playAgainButtonResult = findViewById(R.id.playAgainButtonResult);
        backToHomeButtonResult = findViewById(R.id.backToHomeButtonResult);
        inputNameResultEditText = findViewById(R.id.inputNameResultEditText);

        // start background music when game is finished
        if(!musicMute){
            startService(new Intent(ResultActivity.this, BackgroundSoundService.class));
        }

        clickBtn = MediaPlayer.create(ResultActivity.this, R.raw.clickbutton1);
        if(soundMute){
            clickBtn.setVolume(0,0);
        }
        else{
            clickBtn.setVolume(1,1);
        }

        Intent intent = getIntent();
        mode = intent.getIntExtra("mode", 0);
        score = intent.getIntExtra("score", 0);
        maxCombo = intent.getIntExtra("maxCombo", 0);
        scoreFull = intent.getIntExtra("scoreFull", 0);

        if(mode == 1){
            modeText = "Easy";
        }
        else if(mode == 2){
            modeText = "Medium";
        }
        else if(mode == 3){
            modeText = "Hard";
        }

        myRef = FirebaseDatabase.getInstance().getReference("" + modeText.toLowerCase() + "/users");
        myRefHighScore = FirebaseDatabase.getInstance().getReference("" + modeText.toLowerCase());

        findHighScore();
        setOnclick();

    }

    // find high score
    private void findHighScore() {
        myRefHighScore.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int highScore = 0;
                for(DataSnapshot ds : dataSnapshot.child("/users").getChildren()){
                    if(ds.getValue(UserModel.class).getScore() > highScore){
                        highScore = ds.getValue(UserModel.class).getScore();
                    }
                }
                setText(highScore);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void setText(int highScore) {

        // set first text good or bad
        int percent1 = (int)(((float)score / (float)scoreFull) * 100);
        if(percent1 >= 0 && percent1 < 25){         // less than 25%
            textViewScore.setText("Bad!");
            textViewScore.setTextColor(getResources().getColor(R.color.bad));
        }
        else if(percent1 >= 25 && percent1 < 50){  // 25% - 49%
            textViewScore.setText("Not Bad");
            textViewScore.setTextColor(getResources().getColor(R.color.notBad));
        }
        else if(percent1 >= 50 && percent1 < 80){  // 50% - 79%
            textViewScore.setText("Good!");
            textViewScore.setTextColor(getResources().getColor(R.color.good));
        }
        else if(percent1 >= 80){                   // more or equal than 80%
            textViewScore.setText("Perfect!");
            textViewScore.setTextColor(getResources().getColor(R.color.perfect));
        }

        // set mode text
        modeResultTextView.setText(modeText + " (" + scoreFull + ")");
        if(modeText.equals("Easy")){ modeResultTextView.setTextColor(getResources().getColor(R.color.green)); }
        else if(modeText.equals("Medium")){ modeResultTextView.setTextColor(getResources().getColor(R.color.orange)); }
        else if(modeText.equals("Hard")){ modeResultTextView.setTextColor(getResources().getColor(R.color.red)); }

        // set score text
        scoreResultTextView.setText(score + " (x" + maxCombo + ")");
        scoreResultTextView.setTextColor(getResources().getColor(R.color.blue));

        // set score percent text
        if(score >= highScore){
            percentResultTextView.setText("Your score is high score!");
            percentResultTextView.setTextColor(getResources().getColor(R.color.red));
        }
        else{
            int percent2 = (int)(((float)score / (float)highScore) * 100);
            percentResultTextView.setText("( " + percent2 + "% of highest score )");
        }

        // leader button
        leaderboardButtonResult.setText("Add your score");
    }

    private void setOnclick() {

        leaderboardButtonResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                clickBtn.start();

                if(!inputNameResultEditText.getText().toString().isEmpty() && !addNameSuccess){
                    final String name = inputNameResultEditText.getText().toString();

                    myRef.orderByChild("/name").equalTo(name).addListenerForSingleValueEvent(new ValueEventListener() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            // if name exists in database
                            if(dataSnapshot.exists()){
                                AlertDialog.Builder builder = new AlertDialog.Builder(ResultActivity.this);
                                builder.setCancelable(false);
                                builder.setMessage("Name already exists!");
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                    }
                                });
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }
                            // if not do
                            else{
                                addNameSuccess = true;

                                leaderboardButtonResult.setText("Leaderboard");
                                user = new UserModel(inputNameResultEditText.getText().toString(), score, maxCombo);
                                myRef.push().setValue(user);

                                inputNameResultEditText.setText("");
                                inputNameResultEditText.setEnabled(false);


                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                else if(addNameSuccess){
                    openActivity = true;
                    Intent intent = new Intent(ResultActivity.this, LeaderBoardActivity.class);
                    startActivity(intent);
                }
            }
        });

        playAgainButtonResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickBtn.start();

                AlertDialog.Builder builder = new AlertDialog.Builder(ResultActivity.this);
                builder.setCancelable(false);
                builder.setMessage("Play again on the same difficulty ?");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        openActivity = true;

                        stopService(new Intent(ResultActivity.this, BackgroundSoundService.class));
                        Intent intent = new Intent(ResultActivity.this, CountDownActivity.class);
                        intent.putExtra("mode", mode);
                        startActivity(intent);
                        finish();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });

        backToHomeButtonResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickBtn.start();
                openActivity = true;
                backToHome();
            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        // if user click home
        if(!openActivity){
            stopService(new Intent(ResultActivity.this, BackgroundSoundService.class));
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        openActivity = false;

        if(!musicMute){
            startService(new Intent(ResultActivity.this, BackgroundSoundService.class));
        }
    }

    @Override
    public void onBackPressed() {
        backToHome();
    }

    private void backToHome() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage("Back to Home ?");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                openActivity = true;
                finish();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void goHelp(View view) {
        clickBtn.start();
        openActivity = true;
        Intent intent = new Intent(ResultActivity.this, HelpActivity.class);
        startActivity(intent);
    }
}
