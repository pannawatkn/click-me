package panphajed.ssru.clickme;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import androidx.appcompat.app.AppCompatActivity;

import static panphajed.ssru.clickme.Class.Media.musicMute;
import static panphajed.ssru.clickme.Class.Media.soundMute;

public class SettingActivity extends AppCompatActivity {

    private CheckBox checkBox1, checkBox2;
    private MediaPlayer clickBtn;
    private boolean openActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        checkBox1 = findViewById(R.id.checkBox1);
        checkBox2 = findViewById(R.id.checkBox2);

        clickBtn = MediaPlayer.create(SettingActivity.this, R.raw.clickbutton1);

        if(musicMute){
            checkBox1.setChecked(true);
        }

        if(soundMute){
            checkBox2.setChecked(true);
        }

        setOnClick();
    }

    private void setOnClick() {
        checkBox1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicMute = checkBox1.isChecked();

                if(musicMute){
                    stopService(new Intent(SettingActivity.this, BackgroundSoundService.class));
                }
                else{
                    startService(new Intent(SettingActivity.this, BackgroundSoundService.class));
                }

            }
        });

        checkBox2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundMute = checkBox2.isChecked();
            }
        });
    }

    public void goBack(View view) {
        openActivity = true;

        if(soundMute){
            clickBtn.setVolume(0,0);
        }
        else{
            clickBtn.setVolume(1,1);
        }

        clickBtn.start();
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // if user click home
        if(!openActivity){
            stopService(new Intent(SettingActivity.this, BackgroundSoundService.class));
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        openActivity = false;

        if(!musicMute){
            startService(new Intent(SettingActivity.this, BackgroundSoundService.class));
        }
    }

}
