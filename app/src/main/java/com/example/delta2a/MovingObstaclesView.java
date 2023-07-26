package com.example.delta2a;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MovingObstaclesView extends View {
    private static final int CIRCLE_RADIUS = 50;
    private static final int OBSTACLE_WIDTH = 100;
    private static final int OBSTACLE_HEIGHT = 70, OBSTACLE_HEIGHT1 = 140;
    private static final int OBSTACLE_SPEED = 5;
    private static final int MAX_OBSTACLES = 1;
    private static final int JUMP_VELOCITY = -50; // Adjust the value for the desired jump height
    private static final int MAX_SPEED_INCREASE = 10;
    private Paint circlePaint;
    private Paint obstaclePaint;
    private int circleX,circleX1;
    private int circleY,circleY1;
    private List<RectF> obstacles;
    private int velocityY,velocityY1;
    private boolean isJumping, isSmallObstacle = true, hasCollision = false, isJumping1,cannotCollide = false;
    private boolean isGameOver;
    private long startTime;
    private int collisionCount = 0;
    private boolean isChaserJumping = false;
    private int score = 0;
    private float prevCircleX1 = 0;
    private boolean isDoubleTap = false;
    private int jumpCount = 0;
    private boolean canJump = true;
    private MediaPlayer jumpSound;
    private Drawable mario;
    private static final int AUTO_JUMP_DISTANCE = 600;
    private static final int OBSTACLE_VERTICAL_SPEED = 60;
    private boolean isObstacleMovingUp = true;
    private static final long SPLASH_DELAY = 200;
    private AlertDialog gameOverDialog;

    public interface GameOverListener {
        void onGameOver(int score);
    }

//    private GameOverListener gameOverListener; // Callback listener
//
//    // Add a setter method for the callback listener
//    public void setGameOverListener(GameOverListener listener) {
//        this.gameOverListener = listener;
//    }
//
//    private void gameOver(int score) {
//        if (gameOverListener != null) {
//            gameOverListener.onGameOver(score);
//        }
//    }

    public MovingObstaclesView(Context context, AttributeSet attrs) {
        super(context, attrs);

        circlePaint = new Paint();
        circlePaint.setColor(Color.RED);
        obstaclePaint = new Paint();
        obstaclePaint.setColor(Color.BLUE);
        obstacles = new ArrayList<>();
        startTime = System.currentTimeMillis();
        jumpSound = MediaPlayer.create(context, R.raw.jump_sound);

    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);

        // Calculate the bottom-left corner position with spacing
        int spacing = 100; // Adjust this value for the desired spacing
        circleX = (width - CIRCLE_RADIUS - 400)/2;
        circleY = height - spacing - CIRCLE_RADIUS +30;


        circleX1 = (width - CIRCLE_RADIUS - 1200)/2;
        circleY1 = height - spacing - CIRCLE_RADIUS + 2;

        // Generate initial obstacles
        generateObstacles(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Calculate the baseline position
        int baselineY = getHeight() - OBSTACLE_HEIGHT;

        // Calculate the distance between the yellow circle and the obstacles
        float distanceToObstacles = calculateDistanceToObstacles();

        Drawable backgroundDrawable = getResources().getDrawable(R.drawable.bluesky);
        backgroundDrawable.setBounds(0, 0, getWidth(), getHeight());
        backgroundDrawable.draw(canvas);

        // Draw the darkish grey color below the baseline
        Paint fillPaint = new Paint();
        fillPaint.setColor(Color.GREEN);
        canvas.drawRect(0, baselineY, getWidth(), getHeight(), fillPaint);

        // Draw the baseline
        Paint baselinePaint = new Paint();
        baselinePaint.setColor(Color.BLACK);
        canvas.drawLine(0, baselineY, getWidth(), baselineY, baselinePaint);

        Drawable chaserDrawable = getResources().getDrawable(R.drawable.bowser);
        chaserDrawable.setBounds(circleX1 - 100, circleY1 - 100, circleX1 + 100, circleY1 + 100);
        chaserDrawable.draw(canvas);

        // Set the color for the circle
        mario = getResources().getDrawable(R.drawable.mario); // Replace with your Mario character drawable
        mario.setBounds(circleX - CIRCLE_RADIUS, circleY - CIRCLE_RADIUS, circleX + CIRCLE_RADIUS, circleY + CIRCLE_RADIUS);
        mario.draw(canvas);

        long elapsedTime = System.currentTimeMillis() - startTime;
        int obstacleSpeed = OBSTACLE_SPEED + (int) (elapsedTime / 2000);
        obstacleSpeed = Math.min(obstacleSpeed, OBSTACLE_SPEED + MAX_SPEED_INCREASE);

        hasCollision = false;
        int intersectCount = 0;

        for (RectF obstacleRect : obstacles) {

            obstacleRect.offset(-obstacleSpeed, 0);

            obstacleRect.offset(-obstacleSpeed, 0);

            // Move the obstacle vertically
//            if (isObstacleMovingUp) {
//                if (obstacleRect.top > getHeight() - OBSTACLE_HEIGHT - 70) {
//                    obstacleRect.offset(0, -OBSTACLE_VERTICAL_SPEED);
//                } else {
//                    isObstacleMovingUp = false;
//                }
//            }else {
//                if (obstacleRect.bottom < getHeight()) {
//                    obstacleRect.offset(0, OBSTACLE_VERTICAL_SPEED);
//                } else {
//                    isObstacleMovingUp = true;
//                }
//            }
            // Reverse the vertical movement if the obstacle reaches the top or bottom
//            if (obstacleRect.top <= 0 || obstacleRect.bottom >= getHeight()) {
//                isObstacleMovingUp = !isObstacleMovingUp;
//            }

            // Draw the obstacle
            if (obstacles.indexOf(obstacleRect) % 2 == 0) {
                // Draw a small obstacle
                Drawable smallObstacleDrawable = getResources().getDrawable(R.drawable.brickwalls);
                smallObstacleDrawable.setBounds((int) obstacleRect.left, (int) obstacleRect.top, (int) obstacleRect.right, (int) obstacleRect.bottom);
                smallObstacleDrawable.draw(canvas);
            } else {
                // Draw a tall obstacle
                float top = obstacleRect.top - 50;
                RectF tallObstacleRect = new RectF(obstacleRect.left, top, obstacleRect.right, obstacleRect.bottom);
                Drawable tallObstacleDrawable = getResources().getDrawable(R.drawable.brickwalls);
                tallObstacleDrawable.setBounds((int) tallObstacleRect.left, (int) tallObstacleRect.top, (int) tallObstacleRect.right, (int) tallObstacleRect.bottom);
                tallObstacleDrawable.draw(canvas);
            }

            // Check for collision with the obstacle
            if (RectF.intersects(obstacleRect, getCircleRect())) {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        hasCollision = true;
                        cannotCollide = true;
                        circleX1 += 20;
                    }
                }, SPLASH_DELAY);
                intersectCount++;
            }

            if (obstacleRect.right < circleX + CIRCLE_RADIUS && hasCollision == false) {
                score += 50;
            }

        }

        if (circleX1 >= circleX - CIRCLE_RADIUS) {
            isGameOver = true;
        }

        if(isGameOver){
            showGameOverDialog();
            return;
        }
        // Draw the MARIO =)
        mario.draw(canvas);

        // Remove obstacles that have moved off the screen
        for (int i = obstacles.size() - 1; i >= 0; i--) {
            RectF obstacleRect = obstacles.get(i);
            if (obstacleRect.right <= 0) {
                obstacles.remove(i);
            }
        }

        // Generate new obstacles if needed
        if (obstacles.size() < MAX_OBSTACLES) {
            generateObstacles(getWidth(), getHeight());
        }

        // Apply gravity and update the circle's position
        if (isJumping) {
            velocityY += 2.5; // Gravity
            circleY += velocityY;
            int baselineBottom = getHeight() - OBSTACLE_HEIGHT;
            if (circleY >= baselineBottom - CIRCLE_RADIUS) {
                // The circle has landed on the baseline
                circleY = baselineBottom - CIRCLE_RADIUS;
                isJumping = false;
                canJump = true;
            }
        }

        // Check if the yellow circle should jump automatically
        if (!isJumping1 && !isChaserJumping && canJump && distanceToObstacles <= AUTO_JUMP_DISTANCE) {
            isChaserJumping = true;
            jump1();

        }

        if(isJumping1){
            circleY1 += velocityY1;
            velocityY1 += 3;

            int baselineBottom = getHeight() - OBSTACLE_HEIGHT;
            if (circleY1 >= baselineBottom - CIRCLE_RADIUS-20) {
                // The circle has landed on the baseline
                circleY1 = baselineBottom - CIRCLE_RADIUS ;
                isJumping1 = false;
                velocityY1 = 0;
            }else {
                isJumping1 = false;
            }
        }

        // Draw the score on the top right corner
        Paint scorePaint = new Paint();
        scorePaint.setColor(Color.BLACK);
        scorePaint.setTextSize(50);
        String scoreText = "Score: " + score;
        float textWidth = scorePaint.measureText(scoreText);
        float x = getWidth() - textWidth - 20;
        float y = 50 + Math.abs(scorePaint.ascent());
        canvas.drawText(scoreText, x, y, scorePaint);

        // Update the previous circle position for calculating distance traveled
        prevCircleX1 = circleX1;

        // Invalidate the view to trigger a redraw
        invalidate();
    }

    private RectF getCircleRect() {
        return new RectF(circleX - CIRCLE_RADIUS, circleY - CIRCLE_RADIUS, circleX + CIRCLE_RADIUS, circleY + CIRCLE_RADIUS);
    }

    private void generateObstacles(int width, int height) {
        int obstacleY = height - OBSTACLE_HEIGHT - 70;

        Random random = new Random();
        boolean isSmallObstacle = random.nextBoolean();

        if (isSmallObstacle) {
            // Generate a small obstacle
            RectF smallObstacleRect = new RectF(width, obstacleY, width + OBSTACLE_WIDTH, obstacleY + OBSTACLE_HEIGHT);
            obstacles.add(smallObstacleRect);
        } else {
            // Generate a tall obstacle
            obstacleY = height - OBSTACLE_HEIGHT1 - 70;
            RectF tallObstacleRect = new RectF(width, obstacleY, width + OBSTACLE_WIDTH, obstacleY + OBSTACLE_HEIGHT1);
            obstacles.add(tallObstacleRect);
        }

        // Toggle the flag for the next obstacle
        isSmallObstacle = !isSmallObstacle;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN && !isJumping && !isGameOver) {
            jump();
            playJumpSound();
        }
        return super.onTouchEvent(event);
    }

    private void jump() {
        velocityY = JUMP_VELOCITY;
        isJumping = true;
    }

    private void jump1(){

        if (!isJumping1){
            velocityY1 = JUMP_VELOCITY;
            isJumping1 = true;
            canJump = false;
            jumpCount++;
            if (jumpCount > 1) {
                jumpCount = 0;
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isJumping1 = false;
                    }
                }, 0);
            }
        }

    }
    public void startMoving() {
        // Start the animation by repeatedly redrawing the view
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isGameOver) {
                    invalidate();
                }
            }
        }, 0);
    }

    private void playJumpSound() {
        if (jumpSound != null) {
            jumpSound.seekTo(0); // Reset sound to the beginning
            jumpSound.start(); // Play the jump sound effect
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        releaseResources();
    }

    private void releaseResources() {
        if (jumpSound != null) {
            jumpSound.release(); // Release the MediaPlayer resources
            jumpSound = null;
        }
    }

    private float calculateDistanceToObstacles() {
        float closestObstacleX = Float.MAX_VALUE;
        for (RectF obstacleRect : obstacles) {
            float obstacleX = obstacleRect.left;
            closestObstacleX = Math.min(closestObstacleX - 50, obstacleX - 50);
        }
        return closestObstacleX - (circleX1 - CIRCLE_RADIUS);
    }

    private void showGameOverDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Game Over")
                .setMessage("Your score: " + score)
                .setPositiveButton("Restart", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Restart the game
                        restartGame();
                    }
                })
                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Exit the game
                        exitGame();
                    }
                });
            builder.setCancelable(false);
            gameOverDialog = builder.create();
            gameOverDialog.show();
    }


    private void restartGame() {
        if (gameOverDialog != null && gameOverDialog.isShowing()) {
            gameOverDialog.dismiss();
        }
        // Reset game state
        isGameOver = false;
        circleX = (getWidth() - CIRCLE_RADIUS - 400) / 2;
        circleY = getHeight() - 100 - CIRCLE_RADIUS;
        circleX1 = (getWidth() - CIRCLE_RADIUS - 1200) / 2;
        circleY1 = getHeight() - 100 - CIRCLE_RADIUS - 2;
        obstacles.clear();
        score = 0;
        startTime = System.currentTimeMillis();
        invalidate();
    }

    private void exitGame() {
        // Exit the game
        // ...
    }
}