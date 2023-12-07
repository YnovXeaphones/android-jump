package com.example.myapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;

public class GameView extends SurfaceView implements Runnable {

    private static final String TAG = "GameView";
    private static final Boolean DEBUG = false;

    private Thread thread;
    private boolean isPlaying;
    public static int screenX, screenY;
    public static float screenRatioX, screenRatioY;
    private Paint paint;
    private Character character;
    private List<Platform> platforms;
    private float cameraY = 0;
    private int maxPlatformCount = 15;
    private int platformCount = 15;

    public GameView(Context context, int screenX, int screenY) {
        super(context);

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
        }
    }

    private void generatePlatforms() {
        if (platformCount < maxPlatformCount) {
            float gap = screenY / platformCount;
            platforms.add(new Platform((float) Math.random() * screenX, platforms.get(platforms.size() - 1).y - gap));
            platformCount++;
        }
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

            canvas.drawColor(Color.WHITE);

            for (Platform platform : platforms) {
                platform.draw(canvas, paint);
            }

            canvas.drawBitmap(character.getCharacterSprite(), character.x, character.y, paint);

            if (DEBUG) {
                paint.setColor(Color.rgb(255, 0, 0));
                canvas.drawRect(character.getHitbox(), paint);
            }

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
