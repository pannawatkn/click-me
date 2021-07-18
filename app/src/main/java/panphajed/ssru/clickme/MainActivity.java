package panphajed.ssru.clickme;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import static panphajed.ssru.clickme.Class.Media.musicMute;
import static panphajed.ssru.clickme.Class.Media.soundMute;


public class MainActivity extends AppCompatActivity {

    private Button startButtonHome, leaderBoardButtonHome, exitButtonHome, helpButtonMain, settingButtonMain;
    private MediaPlayer clickBtnMainSound;
    private boolean openActivity = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startButtonHome = findViewById(R.id.startButtonHome);
        leaderBoardButtonHome = findViewById(R.id.leaderBoardButtonHome);
        exitButtonHome = findViewById(R.id.exitButtonHome);
        helpButtonMain = findViewById(R.id.helpButtonMain);
        settingButtonMain = findViewById(R.id.settingButtonMain);

        if(!musicMute) {
            startService(new Intent(MainActivity.this, BackgroundSoundService.class));
        }

        clickBtnMainSound = MediaPlayer.create(MainActivity.this, R.raw.clickbutton1);
        setOnClick();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // if user click home
        if(!openActivity){
            stopService(new Intent(MainActivity.this, BackgroundSoundService.class));
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        openActivity = false;

        if(!musicMute){
            startService(new Intent(MainActivity.this, BackgroundSoundService.class));
        }

        if(soundMute){
            clickBtnMainSound.setVolume(0,0);
        }
        else{
            clickBtnMainSound.setVolume(1,1);
        }
    }

    private void setOnClick() {

        startButtonHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickBtnMainSound.start();
                Intent intent = new Intent(MainActivity.this, SelectDifficultyActivity.class);
                startActivity(intent);
                openActivity = true;
            }
        });

        leaderBoardButtonHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickBtnMainSound.start();
                Intent intent = new Intent(MainActivity.this, LeaderBoardActivity.class);
                startActivity(intent);
                openActivity = true;
            }
        });

        exitButtonHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickBtnMainSound.start();

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setCancelable(false);
                builder.setMessage("Exit The Game?");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
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

        helpButtonMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickBtnMainSound.start();
                Intent intent = new Intent(MainActivity.this, HelpActivity.class);
                startActivity(intent);
                openActivity = true;
            }
        });

        settingButtonMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickBtnMainSound.start();
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
                openActivity = true;
            }
        });
    }
}
