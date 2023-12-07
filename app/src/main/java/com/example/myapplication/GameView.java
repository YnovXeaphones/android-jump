package com.example.myapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
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
    private float screenYPosition = 0;
    private int platformCount = 10;

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
        character.update();
        //screenYPosition = screenY / 2 - character.y;
        //character.y += screenYPosition;

        //generatePlatforms();
        for (Platform platform : platforms) {
            if (character.getHitbox().intersect(platform.getHitbox())) {
                if (character.getHitbox().bottom >= platform.getHitbox().top && character.getVelocityY() > 0) {
                    character.jump();
                }
            }
        }
        adjustPlatforms();
        removeOffscreenPlatforms();
    }

    private void generatePlatforms() {
        // Ajoutez de nouvelles plateformes en fonction de la position verticale de l'écran
        if (character.y < platforms.get(platforms.size() - 1).y + 200) {
            float gap = screenY / platformCount;
            platforms.add(new Platform((float) Math.random() * screenX, platforms.get(platforms.size() - 1).y - gap));
            platformCount++;
        }
    }

    private void adjustPlatforms() {
        // Ajustement de la position y des plates-formes en fonction de la nouvelle position de l'écran
        for (Platform platform : platforms) {
            platform.y += screenYPosition;
        }
    }

    private void removeOffscreenPlatforms() {
        // Supprimez les plateformes qui ne sont plus sur l'écran
        for (int i = 0; i < platforms.size(); i++) {
            Platform platform = platforms.get(i);
            if (platform.y + platform.height < screenYPosition) {
                platforms.remove(i);
                i--; // Décrémentez i pour éviter de sauter la plateforme suivante après la suppression
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
