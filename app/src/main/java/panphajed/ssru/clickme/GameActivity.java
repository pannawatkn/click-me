package panphajed.ssru.clickme;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

import static panphajed.ssru.clickme.Class.Media.musicMute;
import static panphajed.ssru.clickme.Class.Media.soundMute;

public class GameActivity extends AppCompatActivity {

    // ตัวแปรเก็บปุ่ม และเก็บเวลาของปุ่ม
    private ImageView[] circleArray;
    private long[] circleTimeArray;

    // สำหรับสุ่มแรนดอม
    private Random random = new Random();

    // ตัวนับเวลาถอยหลังของเวลาหลัก (30 วิ)
    private MainTimer mainTimer;
    private long mainTimeLeft = 30000;  // 30 second

    // ตัวนับเวลาถอยหลังของปุ่ม
    private CircleTimer circleTimer;

    // เช็คว่าเกมหยุดหรือเกมกำลังดำเนินอยู่
    private boolean gamePause = false;
    private boolean gameStart = false;

    // combo เก็บคอมโบ score เก็บคะแนน scoreFull เก็บคะแนนสูงสุดของโหมดนั้นๆ mode เก็บโหมด
    private int combo = 0;
    private int score = 0;
    private int scoreFull = 0;
    private int mode = 0;

    // circleCount เก็บจำนวนปุ่มตามโหมดที่เลือก delayMills คือเวลาการเกิดของปุ่ม
    private int circleCount = 0; // for recursive method
    private int delayMills = 0; // for recursive method

    // totalCircle เก็บจำนวนปุ่มที่เกิด maxCombo เก็บคอมโบสูงสุด
    private int totalCircle = 0;
    private int maxCombo = 0;

    // ตัวแปรสำหรับ layout
    private RelativeLayout relativelayout, relativeTimeColorInGame, relativeLayoutBackgroudInGame;
    private TextView timeTv, scoreTv, comboTv, circleCountTV;

    // เสียงคลิก เสียงกดพลาด เสียงกดคลิกย้อนกลับ
    private MediaPlayer clickBtnGameSound, clickCircleSound, clickMissSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        relativelayout = findViewById(R.id.relativeLayout);
        relativeTimeColorInGame = findViewById(R.id.relativeTimeColorInGame);
        relativeLayoutBackgroudInGame = findViewById(R.id.relativeLayoutBackgroudInGame);

        timeTv = findViewById(R.id.timeRemainingInGameTextView);
        scoreTv = findViewById(R.id.scoreInGameTextView);
        comboTv = findViewById(R.id.comboInGameTextView);
        circleCountTV = findViewById(R.id.circleCountTextView);

        // รับ mode มาจาก countdown activity
        Intent intent = getIntent();
        mode = intent.getIntExtra("mode", 0);

        // เชื่อมตัวแปรเพลงเข้ากับเพลงจริง
        clickBtnGameSound = MediaPlayer.create(GameActivity.this, R.raw.clickbutton1);
        clickCircleSound = MediaPlayer.create(GameActivity.this, R.raw.click2);
        clickMissSound = MediaPlayer.create(GameActivity.this, R.raw.miss2);

        // ถ้าตั้งค่าในเกมปิดเสียงไว้อยู่ ให้ปิดเสียงคลิกทุกเสียง ถ้าไม่ให้เปิดเสียงเหมือนเดิม
        if(soundMute){
            clickBtnGameSound.setVolume(0,0);
            clickCircleSound.setVolume(0,0);
            clickMissSound.setVolume(0,0);
        }
        else{
            clickCircleSound.setVolume(1,1);
            clickCircleSound.setVolume(1,1);
            clickMissSound.setVolume(1,1);
        }


        // use handler because we need to finish create relative layout first so we can getWidth() and getHeight()
        // ใช้ handler postDelayed เพื่อหน่วงเวลาก่อนที่จะเริ่มเกม เพื่อให้ relative layout สร้างหน้าเสร็จก่อน
        // จะได้ใช้ getWidth() และ getHeight() ของ relative layout แล้วนำใช้แรนดอมได้
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // เริ่มเกม
                startGame();
            }
        }, 100);    // หน่วงเวลา 0.1 วิ

    }

    // เมื่อคลิกที่หน้าจอ
    @SuppressLint("SetTextI18n")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // เมื่อคลิกหน้าจอระหว่างเกมเริ่ม
        if(gameStart){

            // เมื่อกดลง (ACTION_DOWN)
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                // เปลี่ยนสีหน้าจอเป็นสีแเดง
                relativeLayoutBackgroudInGame.setBackgroundResource(R.color.red);

                // เล่นเสียงคลิกพลาด
                clickMissSound.start();

                // เริ่มนับคอมโบใหม่ และแสดง FAIL -3 คะแนน
                combo = 0;
                comboTv.setText("FAIL -3");

                // เคะแนน -3 คะแนน
                score -= 3;
                // ถ้าคะแนนลบแล้วน้อยกว่า 0 ให้คะแนนเท่ากับ 0
                if(score < 0){ score = 0; }
                // โชว์คะแนนปัจจุบัน
                scoreTv.setText("Score: " + score);
            }

            // เมื่อยกนิ้ว (ACTION_UP)
            if(event.getAction() == MotionEvent.ACTION_UP){
                // เปลี่ยนสีหน้าจอเป็นแบบเดิม
                relativeLayoutBackgroudInGame.setBackgroundResource(R.drawable.bg4);
            }
        }

        return super.onTouchEvent(event);
    }

    // เริ่มเกม
    private void startGame() {
        // เซ็ทตัวแปรให้เป็นค่า default ใหม่ทั้งหมด
        gameStart = true;
        gamePause = false;
        score = 0;
        combo = 0;

        // ตัวแปรของเวลาหลัก เซ็ทให้นับเวลาต่อจากเวลาเดิม แล้วเริ่มนับถอยหลังใหม่
        mainTimer = new MainTimer(mainTimeLeft, 1000);
        mainTimer.start();

        // เซ็ท mode
        // easy
        if(mode == 1){
            circleCount = 60; // ปุ่มมี 60 ปุ่ม
            delayMills = 460; // ระยะเวลาการเกิด 0.46 วิ (fix ไว้)
        }
        // medium
        if(mode == 2){
            circleCount = 80; // ปุ่มมี 80 ปุ่ม
            delayMills = 330; // ระยะเวลาการเกิด 0.33 วิ (fix ไว้)
        }
        // hard
        if(mode == 3){
            circleCount = 100; // ปุ่มมี 100 ปุ่ม
            delayMills = 250;  // ระยะเวลาการเกิด 0.25 วิ (fix ไว้)
        }

        // high score when click all the green circle and combo 40+
        // หา score ที่สูงที่สุดของโหมดนั้นๆ เพื่อนำมาควณในอนาคต
        scoreFull = findMaxScoreInThisMode(circleCount);

        // bind circle and time circle in array list
        // สุ่มปุ่มขึ้นมาแล้วใส่ใน array พร้อมกับใส่เวลาตัวมันเองให้แต่ละปุ่ม
        bindCircle();

        // do recursive method random position
        // เมื่อ array ปุ่ม กับ array เวลาของปุ่ม เต็มให้เริ่มสุ่มขึ้นมา
        doRandom(0, circleCount, delayMills);

    }

    // หา score สูงสุด
    private int findMaxScoreInThisMode(int circleCount) {

        // ทดลองหาคะแนนสูงสุด ถ้ากดปุ่มสีเขียว (3 คะแนน) ครบทุกปุ่ม
        int count = circleCount;
        int maxScore = 0;
        int tempCombo = 0;

        for(int i=1; i<=count; i++){
            maxScore += 3;

            if(tempCombo > 3 && tempCombo <= 6){
                maxScore += Math.round(maxScore * 0.03);       // round 3%
            }
            else if(tempCombo > 6){
                maxScore += Math.round(maxScore * 0.04);        // round 4%
            }

            tempCombo++;
        }

        return maxScore;
    }

    // ใส่ปุ่มลง array และใส่เวลาของปุ่มลง array
    private void bindCircle() {
        // สร้าง array ขนาดเท่ากับจำนวนปุ่มที่เลือกโหมดมา
        circleArray = new ImageView[circleCount];
        circleTimeArray = new long[circleCount];

        for(int i=0; i<circleCount; i++){
            // สร้าง image view
            ImageView circle = new ImageView(GameActivity.this);
            // ใส่รูปเป็นวงกลมสีเขียว
            circle.setImageResource(R.drawable.green);

            // แรนดอมขนาดความกว้างของ relative layout (-300 เพื่อจะได้ไม่เกินขนาดหน้าจอ)
            int w = random.nextInt(relativelayout.getWidth() - 300);
            // เซ็ทตำแหน่ง X ของวงกลม
            circle.setX(w);

            // แรนดอมขนาดความสูงของ relative layout (-400 เพื่อจะได้ไม่เกินขนาดหน้าจอ)
            int h = random.nextInt(relativelayout.getHeight() - 400);
            // เซ็ทตำแหน่ง Y ของวงกลม
            circle.setY(h);

            // ใส่ image view ลงใน array ตำแหน่งที่ i ไปเรื่อยๆจนครบจำนวนปุ่ม
            circleArray[i] = circle;
            // ใส่เวลาของปุ่ม 3 วิ
            circleTimeArray[i] = 3000;
        }
    }

    // แรนดอม position
    private void doRandom(final int i, final int n, final int delayMills) {

        // เซ็ทเวลาการเกิด ให้เกิดตาม delay mills
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // วนลูปไปเรื่อยๆ ถ้าเกมไม่ได้หยุด
                if (i < n && !gamePause) {
                    // เซ็ทเวลาของปุ่ม แล้วเริ่มนับเวลา
                    circleTimer = new CircleTimer(circleTimeArray[i], 1000, circleArray[i], i);
                    circleTimer.start();


                    // ถ้าจำนวนปุ่มเกิดไม่เกินจำนวนทั้งหมด ให้ทำต่อจนกว่าจะเกิดครบ
                    if(totalCircle < circleCount){      // fix when circle appear more than 1 when restart activity
                        doRandom(i + 1, n, delayMills);
                    }
                    // โชว์จำนวนปุ่ม
                    circleCountTV.setText("Circle: " + totalCircle);
                }
            }
        }, delayMills);
    }


    // ========================= onRestart onStop onBackPressed =========================

    @Override
    protected void onRestart() {    // open app again
        gamePause = false;

        mainTimer = new MainTimer(mainTimeLeft, 1000);
        mainTimer.start();

        // find circle which is still have time left
        int indexNotFinish = 0;
        while(circleTimeArray[indexNotFinish] == 0){
            indexNotFinish++;
        }

        doRandom(indexNotFinish, circleCount, delayMills);

        super.onRestart();
    }

    @Override
    protected void onStop() {   // click home
        gamePause = true;
        mainTimer.cancel();
        circleTimer.cancel();

        super.onStop();
    }

    @Override
    public void onBackPressed() {
        onStop();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage("Back to select difficulty ?");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if(!musicMute){
                    startService(new Intent(GameActivity.this, BackgroundSoundService.class));
                }
                Intent intent = new Intent(GameActivity.this, SelectDifficultyActivity.class);
                startActivity(intent);
                finish();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                onRestart();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void goBack(View view) {
        clickBtnGameSound.start();
        onBackPressed();
    }

    // ========================= Class =========================

    private class MainTimer extends CountDownTimer {

        private MainTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onTick(long millisUntilFinished) {
            mainTimeLeft = millisUntilFinished;

            timeTv.setText("" + ((millisUntilFinished/1000)));

            if(mainTimeLeft/1000 < 20){
                relativeTimeColorInGame.setBackgroundResource(R.drawable.time_remain_background_yellow);
            }

            if(mainTimeLeft/1000 < 10){
                relativeTimeColorInGame.setBackgroundResource(R.drawable.time_remain_background_red);
            }

            // find max combo
            if(maxCombo <= combo){
                maxCombo = combo;
            }
        }

        @Override
        public void onFinish() {
            gameStart = false;
            gamePause = true;

            final Intent intent = new Intent(GameActivity.this, ResultActivity.class);
            intent.putExtra("mode", mode);
            intent.putExtra("score", score);
            intent.putExtra("maxCombo", maxCombo);
            intent.putExtra("scoreFull", scoreFull);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(intent);
                    finish();
                }
            }, 1500);
        }
    }

    private class CircleTimer extends CountDownTimer {

        ImageView circle;
        int index;

        private CircleTimer(long millisInFuture, long countDownInterval, ImageView circle, int i) {
            super(millisInFuture, countDownInterval);
            this.circle = circle;
            this.index = i;

            // ลบ view อันล่าสุดออกก่อน
            relativelayout.removeView(circleArray[index]);

            // เพิ่ม view อันปัจจุบันเข้าไป
            relativelayout.addView(circle);
            totalCircle++;
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onTick(long millisUntilFinished) {

            if(gamePause){
                cancel();
            }

            circleTimeArray[index] = millisUntilFinished;

            // change color ***********************************************************************
            if (millisUntilFinished / 1000 == 0 && !gamePause) {
                circle.setImageResource(R.drawable.red);
            }
            if (millisUntilFinished / 1000 == 1 && !gamePause) {
                circle.setImageResource(R.drawable.yellow);
            }
            if (millisUntilFinished / 1000 == 2 && !gamePause) {
                circle.setImageResource(R.drawable.green);
            }

            // set on click listener
            circle.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onClick(View view) {

                    clickCircleSound.start();

                    // score
                    if (circle.getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.green).getConstantState())) {
                        score += 3;
                    }
                    else if (circle.getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.yellow).getConstantState())) {
                        score += 2;
                    }
                    else if (circle.getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.red).getConstantState())) {
                        score += 1;
                    }

                    if(combo > 3 && combo <= 6){
                        score += Math.round(score * 0.03);       // round 3%
                    }
                    else if(combo > 6){
                        score += Math.round(score * 0.04);      // round 4%
                    }

                    combo++;

                    scoreTv.setText("Score: " + score);
                    comboTv.setText("Combo: " + combo);

                    // after click on circle, circle will be disappear and plus combo
                    onFinish();
                }
            });
        }

        @Override
        public void onFinish() {
            if(!gamePause) {
                // remove circle
                relativelayout.removeView(circle);
                // set time of circle = 0;
                circleTimeArray[index] = 0;
            }
        }
    }
}
