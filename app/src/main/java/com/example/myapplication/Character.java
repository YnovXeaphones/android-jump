package com.example.myapplication;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Character {
    private float jumpForce = -30;
    private float gravity = 1.5f;
    private float movementSpeed = 20;
    private float movementSlowdown = 0.75f;
    private float currentYVelocity = 0;
    private float currentXVelocity = 0;
    private boolean isMoving = false;
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

        if (!isMoving) {
            if (currentXVelocity > 0) {
                currentXVelocity -= movementSlowdown;
            } else if (currentXVelocity < 0) {
                currentXVelocity += movementSlowdown;
            }
        }

        y += currentYVelocity;
        x += currentXVelocity;

        // if the character is at the left edge of the screen, move it back to the right edge
        if (x + width < 0) {
            x = GameView.screenX;
        }
        if (x > GameView.screenX) {
            x = -width;
        }
    }

    public Bitmap getCharacterSprite() {
        return characterSprite;
    }

    public void move(float touchX, float screenX) {
        float middleOfScreen = screenX / 2;
        float offset = 100;

        this.isMoving = true;

        if (touchX > middleOfScreen + offset) {
            this.moveRight();
        } else if (touchX < middleOfScreen - offset) {
            this.moveLeft();
        }
    }

    private void moveLeft() {
        this.currentXVelocity = -movementSpeed;
    }

    private void moveRight() {
        this.currentXVelocity = movementSpeed;
    }

    public void stopMoving() {
        this.isMoving = false;
        // this.currentXVelocity = 0;
    }
}
