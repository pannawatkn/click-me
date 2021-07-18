package panphajed.ssru.clickme;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import static panphajed.ssru.clickme.Class.Media.musicMute;
import static panphajed.ssru.clickme.Class.Media.soundMute;


public class CountDownActivity extends AppCompatActivity {

    // ตัวเลขแสดงเวลาถอยหลัง
    private TextView loadingTextView;

    // ตัวนับเวลาถอยหลัง และ intent
    private Intent intent;
    private MyCountDownTimer newCountDown;

    // เวลาถอยหลังที่กำหนดไว้ (3 วิ)
    private long timeLeft = 3000;

    // เสียงนับถอยหลัง และเสียงตอนนับเสร็จ
    private MediaPlayer countDownSound, countDownFinishSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_count_down);

        loadingTextView = findViewById(R.id.loadingTextView);

        // รับค่า mode มาจากหน้าเลือกโหมด (SelectDifficultyActivity)
        intent = getIntent();
        int mode = intent.getIntExtra("mode", 0);
        intent = new Intent(CountDownActivity.this, GameActivity.class);
        intent.putExtra("mode", mode);

        // เชื่อมตัวแปรเสียงเข้ากับไฟล์เสียง
        countDownSound = MediaPlayer.create(CountDownActivity.this, R.raw.countdown_sound);
        countDownFinishSound = MediaPlayer.create(CountDownActivity.this, R.raw.countdown_finish_sound);

        // ถ้าตั้งค่าในเกมปิดเสียงไว้อยู่ ให้ลดเสียงเหลือ 0 แต่ถ้าเปิดเสียงอยู่ให้เพิ่มเสียงเท่าเดิม
        if(soundMute){
            countDownSound.setVolume(0,0);
            countDownFinishSound.setVolume(0,0);
        }
        else{
            countDownSound.setVolume(1,1);
            countDownFinishSound.setVolume(1,1);
        }

        // สร้างตัวนับเวลาถอยหลัง
        newCountDown = new MyCountDownTimer(timeLeft, 1000);
        newCountDown.start();

    }

    // activity โหลดใหม่อีกครั้ง
    @Override
    protected void onRestart() {
        // ให้นับเวลาต่อจากเวลาเดิมที่เหลือ
        newCountDown = new MyCountDownTimer(timeLeft, 1000);
        newCountDown.start();
        super.onRestart();
    }

    // activity หยุดทำงาน
    @Override
    protected void onStop() {
        // หยุดการนับเวลา
        newCountDown.cancel();
        super.onStop();
    }

    // กดปุ่ม back
    @Override
    public void onBackPressed() {
        // หยุดการนับเวลา
        newCountDown.cancel();

        // โชว์ Dialog แจ้งเตือนย้อนกลับไปหน้าเลือกระดับ
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage("Back to select a difficulty?");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                // ถ้าย้อนกลับให้หยุดการนับเวลา
                newCountDown.cancel();

                // ถ้าตั้งค่าในเกมไม่ได้ปิดเพลงไว้อยู่ ให้เล่นเพลงต่อ
                if(!musicMute) {
                    startService(new Intent(CountDownActivity.this, BackgroundSoundService.class));
                }

                // เปลี่ยนหน้าไปหน้าเลือกระดับความยาก
                Intent intentSelect = new Intent(getApplicationContext(), SelectDifficultyActivity.class);
                startActivity(intentSelect);
                finish();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                // ถ้าไม่ย้อนกลับ ให้นับเวลาต่อจากเวลาเดิมที่เหลือ
                newCountDown = new MyCountDownTimer(timeLeft, 1000);
                newCountDown.start();
            }
        });

        // แสดง Dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // ====================== Class ===========================
    // สร้างคลาสที่สืบทอดมาจาก countdowntimer เพื่อเอาไว้นับเวลาถอยหลัง
    private class MyCountDownTimer extends CountDownTimer {

        private MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onTick(long millisUntilFinished) {
            // ตัวแปรเวลาจะเก็บเวลาปัจจุบันไว้
            timeLeft = millisUntilFinished;

            // แสดงเวลานับถอยหลัง
            loadingTextView.setText("" + ((timeLeft / 1000) + 1));

            // เล่นเสียงนับถอยหลัง
            if (countDownSound.isPlaying()) {
                countDownSound.stop();
                countDownSound.release();
                countDownSound = MediaPlayer.create(CountDownActivity.this, R.raw.countdown_sound);
            }countDownSound.start();
        }

        @Override
        public void onFinish() {
            // ปิดเสียงนับถอยหลัง
            countDownSound.stop();
            countDownSound.release();

            // เล่นเสียงเสร็จสิ้น
            countDownFinishSound.start();

            // เริ่ม activity game
            loadingTextView.setText("");
            startActivity(intent);
            finish();
        }
    }
}


