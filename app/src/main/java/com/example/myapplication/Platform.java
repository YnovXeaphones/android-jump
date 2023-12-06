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

    public Platform(float x, float y) {
        this.x = x;
        this.y = y;
        this.hitbox = new RectF(x, y, (x + width), (y + height));
    }

    public void draw(Canvas canvas, Paint paint) {
        paint.setColor(Color.rgb(100, 255, 100));
        canvas.drawRect(x, y, x + width, y + height, paint);
    }

    public RectF getHitbox() {
        return hitbox;
    }
}

