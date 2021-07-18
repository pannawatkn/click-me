package panphajed.ssru.clickme;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import static panphajed.ssru.clickme.Class.Media.musicMute;
import static panphajed.ssru.clickme.Class.Media.soundMute;

public class SelectDifficultyActivity extends AppCompatActivity {

    private Button easyButton, mediumButton, hardButton;

    private int mode = 0;

    private MediaPlayer clickBtnSelectDiffSound;
    private boolean openActivity = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_difficulty);

        easyButton = findViewById(R.id.easyButton);
        mediumButton = findViewById(R.id.mediumButton);
        hardButton = findViewById(R.id.hardButton);

        clickBtnSelectDiffSound = MediaPlayer.create(SelectDifficultyActivity.this, R.raw.clickbutton1);
        if(soundMute){
            clickBtnSelectDiffSound.setVolume(0,0);
        }
        else{
            clickBtnSelectDiffSound.setVolume(1,1);
        }

        easyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickBtnSelectDiffSound.start();

                mediumButton.setEnabled(false);
                hardButton.setEnabled(false);

                mode = 1;
                startLoadActivity(mode);
            }
        });

        mediumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickBtnSelectDiffSound.start();

                easyButton.setEnabled(false);
                hardButton.setEnabled(false);

                mode = 2;
                startLoadActivity(mode);
            }
        });

        hardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickBtnSelectDiffSound.start();

                easyButton.setEnabled(false);
                mediumButton.setEnabled(false);

                mode = 3;
                startLoadActivity(mode);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        // if user click home
        if(!openActivity){
            stopService(new Intent(SelectDifficultyActivity.this, BackgroundSoundService.class));
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        openActivity = false;

        if(!musicMute){
            startService(new Intent(SelectDifficultyActivity.this, BackgroundSoundService.class));
        }
    }

    private void startLoadActivity(int mode) {
        // stop sound before game start
        stopService(new Intent(SelectDifficultyActivity.this, BackgroundSoundService.class));

        openActivity = true;

        Intent intent = new Intent(SelectDifficultyActivity.this, CountDownActivity.class);
        intent.putExtra("mode", mode);
        startActivity(intent);
        finish();

        easyButton.setEnabled(true);
        mediumButton.setEnabled(true);
        hardButton.setEnabled(true);
    }

    public void goBack(View view) {
        clickBtnSelectDiffSound.start();
        openActivity = true;
        finish();
    }
}
