package com.example.myapplication;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;

public class Character {
    private float jumpForce = -40;
    static final float gravity = 1.5f;
    private float movementSpeed = 20;
    private float movementSlowdown = 0.75f;
    private float currentYVelocity = 0;
    private float currentXVelocity = 0;
    private boolean isMoving = false;
    int x, y, width, height;
    RectF hitbox;
    private Bitmap characterSprite;
    private boolean canJump = true;

    public Character(int screenY, int screenX, Resources res) {

        y = screenY / 2;
        x = (screenX / 2) - (width / 2);

        this.characterSprite = BitmapFactory.decodeResource(res, R.drawable.android_idle);

        width = characterSprite.getWidth();
        height = characterSprite.getHeight();

        width /= 3;
        height /= 3;

        width = (int) (width * GameView.screenRatioX);
        height = (int) (height * GameView.screenRatioY);

        characterSprite = Bitmap.createScaledBitmap(characterSprite, width, height, false);

        hitbox = new RectF(x, y, x + width, y + height);
    }

    public void jump() {
        if (canJump) {
            currentYVelocity = jumpForce;
            canJump = false;
        }
    }

    public void setCanJump(boolean canJump) {
        this.canJump = canJump;
    }

    public void update(float cameraY) {
        currentYVelocity += gravity;

        if (currentYVelocity > gravity * 15) {
            setCanJump(true);
        }

        if (!isMoving) {
            if (currentXVelocity > 0) {
                currentXVelocity -= movementSlowdown;
            } else if (currentXVelocity < 0) {
                currentXVelocity += movementSlowdown;
            }
        }

        y += currentYVelocity + cameraY;
        x += currentXVelocity;

        // if the character is at the left edge of the screen, move it back to the right edge
        if (x + width < 0) {
            x = GameView.screenX;
        }
        if (x > GameView.screenX) {
            x = -width;
        }

        this.hitbox.set(x, y, x + width, y + height);
    }

    public Bitmap getCharacterSprite() {
        return characterSprite;
    }

    public void move(float touchX, float screenX) {
        float middleOfScreen = screenX / 2;
        float offset = 0;

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
    }

    public void setYVelocity(float yVelocity) {
        this.currentYVelocity = yVelocity;
    }

    public RectF getHitbox() {
        return hitbox;
    }

    public int getVelocityY() {
        return (int) currentYVelocity;
    }

    public boolean checkGameOver() {
        if (y > GameView.screenY) {
            return true;
        }
        return false;
    }
}
