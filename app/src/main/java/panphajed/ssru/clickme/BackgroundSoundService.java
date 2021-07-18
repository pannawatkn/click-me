package panphajed.ssru.clickme;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

public class BackgroundSoundService extends Service {
    MediaPlayer player;
    public IBinder onBind(Intent arg0) {

        return null;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        player = MediaPlayer.create(this, R.raw.gamemusic);
        player.setLooping(true); // Set looping
        player.setVolume(1,1);

    }
    @SuppressLint("WrongConstant")
    public int onStartCommand(Intent intent, int flags, int startId) {
        player.start();
        return 1;
    }

    public IBinder onUnBind(Intent arg0) {
        // TO DO Auto-generated method
        return null;
    }

    @Override
    public void onDestroy() {
        player.stop();
        player.release();
    }
}