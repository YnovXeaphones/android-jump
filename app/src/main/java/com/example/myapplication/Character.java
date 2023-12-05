package com.example.myapplication;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Character {
    private float jumpForce = -30;
    private float gravity = 1.5f;
    private float currentYVelocity = 0;
    int x, y, width, height;
    private Bitmap characterSprite;

    public Character(int screenY, int screenX, Resources res) {
        this.characterSprite = BitmapFactory.decodeResource(res, R.drawable.android_idle);

        width = characterSprite.getWidth();
        height = characterSprite.getHeight();

        width /= 2;
        height /= 2;

        width = (int) (width * GameView.screenRatioX);
        height = (int) (height * GameView.screenRatioY);

        characterSprite = Bitmap.createScaledBitmap(characterSprite, width, height, false);

        y = screenY / 2;
        x = (screenX / 2) - (width / 2);
    }

    public void jump() {
        currentYVelocity = jumpForce;
    }

    public void update() {
        currentYVelocity += gravity;
        y += currentYVelocity;
    }

    public Bitmap getCharacterSprite() {
        return characterSprite;
    }
}
