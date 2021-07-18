package panphajed.ssru.clickme;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import panphajed.ssru.clickme.Adapter.ScoreAdapter;
import panphajed.ssru.clickme.Class.Score;
import panphajed.ssru.clickme.Class.UserModel;

import static panphajed.ssru.clickme.Class.Media.musicMute;
import static panphajed.ssru.clickme.Class.Media.soundMute;


public class LeaderBoardActivity extends AppCompatActivity {

    private DatabaseReference myRef;

    private List<Score> scoreList = new ArrayList<>();
    private ScoreAdapter adapter;
    private List<UserModel> users = new ArrayList<>();

    private ListView scoreBoardList;
    private Button easyBtnScore, mediumBtnScore, hardBtnScore, backButtonScore;

    private String mode;

    private MediaPlayer clickBtn;
    private boolean openActivity = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_board);

        scoreBoardList = findViewById(R.id.scoreBoardList);
        easyBtnScore =  findViewById(R.id.easyBtnScore);
        mediumBtnScore = findViewById(R.id.mediumBtnScore);
        hardBtnScore = findViewById(R.id.hardBtnScore);
        backButtonScore = findViewById(R.id.backButtonScore);

        clickBtn = MediaPlayer.create(LeaderBoardActivity.this, R.raw.clickbutton1);
        if(soundMute){
            clickBtn.setVolume(0,0);
        }
        else{
            clickBtn.setVolume(1,1);
        }

        adapter = new ScoreAdapter(this, scoreList);
        scoreBoardList.setAdapter(adapter);

        easyBtnScore.setEnabled(false);
        mode = "easy";

        setOnClick();
    }

    @Override
    protected void onStart() {
        super.onStart();

        myRef = FirebaseDatabase.getInstance().getReference("" + mode);

        // get data and show
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.child("/users").getChildren()){
                    UserModel user = ds.getValue(UserModel.class);
                    users.add(user);
                }

                // reset list view first (restart view when data is incoming)
                scoreList.removeAll(scoreList);

                // if users is get all of data from firebase then sort arraylist of users
                sortTheScore();

                // show in score board
                setInScoreBoard(users.size());

                // when show on the score board then remove the list of users for the next incoming data
                users.removeAll(users);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        // if user click home
        if(!openActivity){
            stopService(new Intent(LeaderBoardActivity.this, BackgroundSoundService.class));
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        openActivity = false;

        if(!musicMute){
            startService(new Intent(LeaderBoardActivity.this, BackgroundSoundService.class));
        }
    }

    // set in adapter
    private void setInScoreBoard(int indexRank) {
        for (int i = 0; i < indexRank; i++) {
            Score scoreboard = new Score();
            scoreboard.setRank("" + (i + 1));
            scoreboard.setName(users.get(i).getName());
            scoreboard.setScore("" + users.get(i).getScore() + "(x" + users.get(i).getMax_combo() + ")");

            scoreList.add(scoreboard);
        }

        adapter.notifyDataSetChanged();
    }

    private void sortTheScore() {
        // sort if score2-score1 value equal | "-" mean lower | "+" mean higher | "0" mean equals
        Collections.sort(users, new Comparator<UserModel>(){
            public int compare(UserModel s1, UserModel s2) {
                return s2.getScore() - s1.getScore();
            }
        });
    }

    private void setOnClick() {
        easyBtnScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickBtn.start();

                mode = "easy";
                onStart();

                // reset list view
                scoreList.removeAll(scoreList);

                easyBtnScore.setEnabled(false);
                mediumBtnScore.setEnabled(true);
                hardBtnScore.setEnabled(true);
            }
        });

        mediumBtnScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickBtn.start();

                mode = "medium";
                onStart();

                // reset list view
                scoreList.removeAll(scoreList);

                easyBtnScore.setEnabled(true);
                mediumBtnScore.setEnabled(false);
                hardBtnScore.setEnabled(true);
            }
        });

        hardBtnScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickBtn.start();

                mode = "hard";
                onStart();

                // reset list view
                scoreList.removeAll(scoreList);

                easyBtnScore.setEnabled(true);
                mediumBtnScore.setEnabled(true);
                hardBtnScore.setEnabled(false);
            }
        });

        backButtonScore.setOnClickListener(new View.OnClickListener() {
            @Override
                public void onClick(View v) {
                clickBtn.start();
                openActivity = true;
                finish();
            }
        });
    }
}
