package panphajed.ssru.clickme;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import static panphajed.ssru.clickme.Class.Media.musicMute;
import static panphajed.ssru.clickme.Class.Media.soundMute;

public class HelpActivity extends AppCompatActivity {

    private MediaPlayer clickBtnHelpSound;
    private boolean openActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        clickBtnHelpSound = MediaPlayer.create(HelpActivity.this, R.raw.clickbutton1);
        if(soundMute){
            clickBtnHelpSound.setVolume(0,0);
        }
        else{
            clickBtnHelpSound.setVolume(1,1);
        }
    }

    public void goBack(View view) {
        clickBtnHelpSound.start();
        openActivity = true;
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // if user click home
        if(!openActivity){
            stopService(new Intent(HelpActivity.this, BackgroundSoundService.class));
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        openActivity = false;

        if(!musicMute){
            startService(new Intent(HelpActivity.this, BackgroundSoundService.class));
        }
    }
}
