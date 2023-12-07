package com.example.myapplication;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

public class Platform {
    float x, y;
    float height = 50;
    float width = 180;
    RectF hitbox;
    RectF scoreHitbox;
    boolean isScored = false;

    public Platform(float x, float y) {
        this.x = x;
        this.y = y;
        this.hitbox = new RectF(x, y, (x + width), (y + height));
        this.scoreHitbox = new RectF(0, y, GameView.screenX, (y + height));
    }

    public void draw(Canvas canvas, Paint paint) {
        if (GameView.DEBUG) {
            paint.setColor(Color.rgb(255, 0, 0));
            canvas.drawRect(scoreHitbox, paint);
        }

        paint.setColor(Color.rgb(100, 255, 100));
        canvas.drawRect(x, y, x + width, y + height, paint);
    }

    public RectF getHitbox() {
        return hitbox;
    }

    public void update(float screenYPosition) {
        y += screenYPosition;

        hitbox.set(x, y, x + width, y + height);
        scoreHitbox.set(0, y, GameView.screenX, y + height);
    }
}

