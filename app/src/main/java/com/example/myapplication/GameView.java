package com.example.myapplication;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.room.Room;

import com.example.myapplication.Data.AppDatabase;
import com.example.myapplication.Data.Score;
import com.example.myapplication.Data.ScoreDao;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.example.myapplication.Data.ScoreDao;

import java.util.ArrayList;
import java.util.List;

public class GameView extends SurfaceView implements Runnable {

    private static final String TAG = "GameView";
    public static final Boolean DEBUG = false;
    private static final long TARGET_FRAME_TIME = 1000 / 120;

    private Context context;
    private Thread thread;
    private boolean isPlaying;
    private boolean isPaused;
    private boolean gameOver;
    public static int screenX, screenY;
    public static float screenRatioX, screenRatioY;
    private Paint paint;
    private Character character;
    private List<Platform> platforms;
    private float cameraY = 0;
    private int maxPlatformCount = 15;
    private int platformCount = 15;
    private int score = 0;
    private Background background;

    public GameView(Context context, int screenX, int screenY) {
        super(context);
        this.context = context;

        this.screenX = screenX;
        this.screenY = screenY;

        screenRatioX = 1080f / screenX;
        screenRatioY = 1920f / screenY;

        platforms = new ArrayList<>();

        float gap = screenY / platformCount;
        for (int i = 1; i < platformCount; i++) {
            platforms.add(new Platform((float) Math.random() * screenX, screenY - i * gap));
        }

        character = new Character(screenY, screenX, getResources());

        background = new Background(screenX, screenY, getResources());

        paint = new Paint();

        isPlaying = true;
    }

    @Override
    public void run() {
        while (isPlaying && !gameOver && !isPaused) {
            update();
            draw();
            sleep();
        }
        int tick = 0;
        while (isPlaying && gameOver && !isPaused) {
            gameOverUpdate(tick);
            draw();
            tick++;
            sleep();
        }
        if (!isPlaying && gameOver) {
            displayGameOverScreen();
        }
    }

    private void displayGameOverScreen() {
        isPlaying = false;
        gameOver = true;
        ((Activity) context).runOnUiThread(() -> {
            ((Activity) context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            ((Activity) context).setContentView(R.layout.activity_game_over_screen);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
            int fadeDuration = 1000;

            TextView[] textViews = new TextView[4];
            textViews[0] = ((Activity) context).findViewById(R.id.titleText);
            textViews[1] = ((Activity) context).findViewById(R.id.scoreText);
            textViews[2] = ((Activity) context).findViewById(R.id.highScoreText);
            textViews[3] = ((Activity) context).findViewById(R.id.usernameText);

            Button[] buttons = new Button[2];
            buttons[0] = ((Activity) context).findViewById(R.id.restartButton);
            buttons[1] = ((Activity) context).findViewById(R.id.menuButton);

            EditText usernameEditText = ((Activity) context).findViewById(R.id.usernameInput);

            textViews[1].setText("Your score is: " + score);
            textViews[2].setText("Your best score is: " + score);
            usernameEditText.setText(prefs.getString("username", ""));

            buttons[0].setOnClickListener((view) -> {
                saveScore(prefs, usernameEditText, score);
                ((Activity) context).startActivity(GameActivity.getIntent((this.context)));
                ((Activity) context).finish();
            });

            buttons[1].setOnClickListener((view) -> {
                saveScore(prefs, usernameEditText, score);
                ((Activity) context).startActivity(MainActivity.getIntent(context));
                ((Activity) context).finish();
            });

            for (TextView textView : textViews) {
                ObjectAnimator.ofFloat(textView, "alpha", 0f, 1f)
                        .setDuration(fadeDuration).start();
            }
            for (Button button : buttons) {
                ObjectAnimator.ofFloat(button, "alpha", 0f, 1f)
                        .setDuration(fadeDuration).start();
            }
            ObjectAnimator.ofFloat(usernameEditText, "alpha", 0f, 1f)
                    .setDuration(fadeDuration).start();
        });
    }

    private void saveScore(SharedPreferences prefs, EditText usernameEditText, int score){
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("username", usernameEditText.getText().toString());
        editor.apply();

        AppDatabase db = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "android-jump").build();
        ScoreDao scoreDao = db.scoreDao();

        new Thread(new Runnable() {
            @Override
            public void run() {
                scoreDao.insertScore(new Score(
                        usernameEditText.getText().toString(),
                        score,
                        System.currentTimeMillis()));
            }
        }).start();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference scores = database.getReference("scores");

        scores.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Assuming it's a list of strings
                List<Score> currentList = dataSnapshot.getValue(new GenericTypeIndicator<List<Score>>() {});
                if (currentList == null) {
                    currentList = new ArrayList<>();
                }

                // Add new item to the list
                currentList.add(new Score(
                        usernameEditText.getText().toString(),
                        score,
                        System.currentTimeMillis()));

                // Update the list in Firebase
                scores.setValue(currentList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        scores.getKey();
        ScoreDao scoreDao = db.scoreDao();

        new Thread(new Runnable() {
            @Override
            public void run() {
                scoreDao.insertScore(new Score(
                        usernameEditText.getText().toString(),
                        score,
                        System.currentTimeMillis()));
            }
        }).start();
    }

    private void gameOverUpdate(int tick) {
        if (character.y - 100 > screenY && tick > 20) {
            isPlaying = false;
        }

        if (tick < 18) {
            cameraY = character.gravity * 200;
        } else {
            cameraY = 0;
        }
        character.update(-cameraY);
        for (Platform platform : platforms) {
            platform.update(-cameraY);
        }
    }

    private void update() {
        if (isPlaying && checkGameOver()) {
            gameOver = true;
        }

        if (character.y < screenY / 2 && character.getVelocityY() < 0) {
            cameraY += -character.getVelocityY();
        } else if (cameraY > 0) {
            cameraY -= character.gravity * 2;
            if (cameraY < 0) {
                cameraY = 0;
            }
        }

        removeOffscreenPlatforms();
        generatePlatforms();

        character.update(cameraY);

        for (Platform platform : platforms) {
            platform.update(cameraY);
            if (character.getHitbox().intersect(platform.getHitbox())) {
                if (character.getHitbox().bottom >= platform.getHitbox().top && character.getVelocityY() > 0) {
                    character.jump();
                }
            }

            if (platform.scoreHitbox.intersect(character.getHitbox()) && !platform.isScored && character.getVelocityY() < 0) {
                score++;
                platform.isScored = true;
            }
        }
    }

    private void generatePlatforms() {
        if (platformCount < maxPlatformCount) {
            float gap = screenY / platformCount;
            platforms.add(new Platform((float) Math.random() * screenX, platforms.get(platforms.size() - 1).y - gap));
            platformCount++;
        }
    }

    private boolean checkGameOver() {
        if (character.checkGameOver()) {
            return true;
        }
        return false;
    }

    private void removeOffscreenPlatforms() {
        for (Platform platform : platforms) {
            if (platform.y > screenY) {
                platforms.remove(platform);
                platformCount--;
                break;
            }
        }
    }
    private void draw() {
        if (getHolder().getSurface().isValid()) {
            Canvas canvas = getHolder().lockCanvas();

            canvas.drawBitmap(background.background, background.x, background.y, paint);

            for (Platform platform : platforms) {
                platform.draw(canvas, paint);
            }

            canvas.drawBitmap(character.getCharacterSprite(), character.x, character.y, paint);

            if (DEBUG) {
                paint.setColor(Color.rgb(255, 0, 0));
                canvas.drawRect(character.getHitbox(), paint);
            }

            if (!gameOver) {
                drawScore(canvas, score);
            }

            getHolder().unlockCanvasAndPost(canvas);
        }
    }

    private void drawScore(Canvas canvas, int score) {
        paint.setColor(Color.BLACK);
        paint.setTextSize(200);

        String scoreString = score + "";

        // Draw text at the top-center of the canvas
        canvas.drawText(scoreString, canvas.getWidth() / 2f - scoreString.length() * 20, 200, paint);
    }

    private void sleep() {
        try {
            Thread.sleep(17);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void pause() {
        try {
            isPaused = true;
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void resume() {
        isPaused = false;
        thread = new Thread(this);
        thread.start();
    }

    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                character.move(event.getX(), screenX);
                break;
            case MotionEvent.ACTION_UP:
                character.stopMoving();
                break;
        }
        return true;
    }

    public Bundle saveState() {
        Bundle bundle = new Bundle();
        bundle.putBoolean("isPlaying", isPlaying);
        bundle.putBoolean("gameOver", gameOver);
        bundle.putInt("score", score);

        return bundle;
    }

    public void restoreState(Bundle savedInstanceState) {
        score = savedInstanceState.getInt("score");
        isPlaying = savedInstanceState.getBoolean("isPlaying");
        gameOver = savedInstanceState.getBoolean("gameOver");
    }
}
