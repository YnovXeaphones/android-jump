package com.example.myapplication;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class Platform {
    float x, y;
    float height = 30;
    float width = 120;
    Rect itbox;

    public Platform(float x, float y) {
        this.x = x;
        this.y = y;
        this.itbox = new Rect((int) x, (int) y, (int) (x + width), (int) (y + height));
    }

    public void draw(Canvas canvas, Paint paint) {
        paint.setColor(Color.rgb(100, 255, 100));
        canvas.drawRect(x, y, x + width, y + height, paint);
    }

    public Rect getHitbox() {
        return itbox;
    }
}

