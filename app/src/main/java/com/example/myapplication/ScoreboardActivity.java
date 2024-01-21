package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.Data.AppDatabase;
import com.example.myapplication.Data.Score;
import com.example.myapplication.Data.ScoreDao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ScoreboardActivity extends AppCompatActivity implements ScoreAdapter.OnItemClickListener {

    private RecyclerView scoresRecyclerView;
    private ScoreAdapter scoreAdapter;

    private TextView bestScoreTextView;
    private List<Score> scoresList;
    private AppDatabase db;
    private ScoreDao scoreDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoreboard);

        bestScoreTextView = findViewById(R.id.bestScoreTextView);
        scoresRecyclerView = findViewById(R.id.scoresRecyclerView);
        scoresRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "android-jump").build();
        scoreDao = db.scoreDao();

        scoresList = fetchScoresFromDatabase();
        scoreAdapter = new ScoreAdapter(scoresList, this);
        scoresRecyclerView.setAdapter(scoreAdapter);

        ImageView imageReturn = findViewById(R.id.imageReturn);
        imageReturn.setVisibility(View.GONE);
        imageReturn.setOnClickListener(v -> returnToGeneralScores());

        fetchAndDisplayBestScore();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
        }
    }

    private List<Score> fetchScoresFromDatabase() {
        List<Score> scores = new ArrayList<>();
        new Thread(() -> {
            scores.addAll(scoreDao.getAllButHighestScore());
            runOnUiThread(() -> scoreAdapter.notifyDataSetChanged());
        }).start();
        return scores;
    }

    private void fetchAndDisplayBestScore() {
        new Thread(() -> {
            Score highestScore = scoreDao.getHighestScore();
            if (highestScore != null) {
                String bestScoreText =
                        highestScore.getUsername() + "\n" +
                                "High Score: " + highestScore.getScore();

                runOnUiThread(() -> bestScoreTextView.setText(bestScoreText));
            }
        }).start();
    }

    private void fetchAndDisplayBestScoreForUser(String username) {
        new Thread(() -> {
            List<Score> userScores = scoreDao.getScoresByUsername(username);
            if (!userScores.isEmpty()) {
                Score highestUserScore = userScores.get(0);
                String bestScoreText =
                        username + "\n" +
                                "High Score: " + highestUserScore.getScore();

                runOnUiThread(() -> bestScoreTextView.setText(bestScoreText));
            }
        }).start();
    }

    @Override
    public void onUsernameClicked(String username) {
        new Thread(() -> {
            List<Score> userScores = scoreDao.getScoresByUsername(username);
            runOnUiThread(() -> {
                scoresList.clear();
                scoresList.addAll(userScores.subList(1, userScores.size()));
                scoreAdapter.notifyDataSetChanged();
                if (!userScores.isEmpty()) {
                    fetchAndDisplayBestScoreForUser(username);
                }
                ImageView imageReturn = findViewById(R.id.imageReturn);
                imageReturn.setVisibility(View.VISIBLE);
            });
        }).start();
    }

    private String convertLongToDate(Long time) {
        if (time == null) return "Inconnu";
        Date date = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return format.format(date);
    }

    private void returnToGeneralScores() {
        new Thread(() -> {
            List<Score> allScores = scoreDao.getScores();
            runOnUiThread(() -> {
                scoresList.clear();
                scoresList.addAll(allScores.subList(1, allScores.size()));
                scoreAdapter.notifyDataSetChanged();
                fetchAndDisplayBestScore();

                ImageView imageReturn = findViewById(R.id.imageReturn);
                imageReturn.setVisibility(View.GONE);
            });
        }).start();
    }
}
