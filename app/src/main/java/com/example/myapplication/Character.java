package com.example.myapplication;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.List;

public class Character {
    private float jumpForce = -40;
    private float gravity = 1.5f;
    private float movementSpeed = 20;
    private float movementSlowdown = 0.75f;
    private float currentYVelocity = 0;
    private float currentXVelocity = 0;
    private boolean isMoving = false;
    int x, y, width, height;
    private Bitmap characterSprite;
    private List<Platform> platforms;
    private boolean canJump = true;

    public Character(int screenY, int screenX, Resources res, List<Platform> platforms) {
        this.platforms = platforms;
        this.characterSprite = BitmapFactory.decodeResource(res, R.drawable.android_idle);

        width = characterSprite.getWidth();
        height = characterSprite.getHeight();

        width /= 3;
        height /= 3;

        width = (int) (width * GameView.screenRatioX);
        height = (int) (height * GameView.screenRatioY);

        characterSprite = Bitmap.createScaledBitmap(characterSprite, width, height, false);

        y = screenY / 2;
        x = (screenX / 2) - (width / 2);
    }

    public void jump() {
        if (isOnPlatform() && canJump) {
            currentYVelocity = jumpForce;
            canJump = false;
        }
    }

    private boolean isOnPlatform() {
        for (Platform platform : platforms) {
            if (collidesWith(platform)) {
                return true;
            }
        }
        return false;
    }

    public void setCanJump(boolean canJump) {
        this.canJump = canJump;
    }

    public void setPlatforms(List<Platform> platforms) {
        this.platforms = platforms;
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

    public boolean collidesWith(Platform platform) {
        float characterBottom = y + height;
        float characterRight = x + width;

        float platformTop = platform.y;
        float platformBottom = platform.y + platform.height;
        float platformLeft = platform.x;
        float platformRight = platform.x + platform.width;

        boolean topCollision = characterBottom >= platformTop && characterBottom <= platformTop + 5;
        boolean bottomCollision = characterBottom >= platformBottom && characterBottom <= platformBottom + 5;
        boolean sideCollision = characterRight >= platformLeft && x <= platformRight;

        return (topCollision || bottomCollision) && sideCollision;
    }

    public void setYVelocity(float yVelocity) {
        this.currentYVelocity = yVelocity;
    }
}
