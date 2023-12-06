package com.example.myapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;

public class GameView extends SurfaceView implements Runnable {

    private Thread thread;
    private boolean isPlaying;
    public static int screenX, screenY;
    public static float screenRatioX, screenRatioY;
    private Paint paint;
    private Character character;
    private List<Platform> platforms;
    private static final float GRAVITY = 1.5f;

    public GameView(Context context, int screenX, int screenY) {
        super(context);

        this.screenX = screenX;
        this.screenY = screenY;

        screenRatioX = 1080f / screenX;
        screenRatioY = 1920f / screenY;

        platforms = new ArrayList<>();

        int platformCount = 6;
        float gap = screenY / platformCount;
        for (int i = 1; i < 6; i++) {
            platforms.add(new Platform((float) Math.random() * screenX, screenY - i * gap));
        }

        character = new Character(screenY, screenX, getResources(), platforms);

        paint = new Paint();
    }

    @Override
    public void run() {
        while (isPlaying) {
            update();
            draw();
            sleep();
        }
    }

    private void update() {
        character.setPlatforms(platforms);
        character.update();

        for (Platform platform : platforms) {
            if (character.collidesWith(platform)) {
                if (character.y + character.height <= platform.y + 5) {
                    character.jump();
                } else {
                    if (character.y >= platform.y + platform.height) {
                        character.setYVelocity(GRAVITY);
                    } else {
                        character.jump();
                    }
                }
                character.setCanJump(true);
            }
        }

    }

    private void draw() {
        if (getHolder().getSurface().isValid()) {
            Canvas canvas = getHolder().lockCanvas();

            canvas.drawColor(Color.WHITE);

            for (Platform platform : platforms) {
                platform.draw(canvas, paint);
            }

            canvas.drawBitmap(character.getCharacterSprite(), character.x, character.y, paint);

            getHolder().unlockCanvasAndPost(canvas);
        }
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
            isPlaying = false;
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void resume() {
        isPlaying = true;
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
}
