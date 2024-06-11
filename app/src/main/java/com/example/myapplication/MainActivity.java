package com.example.myapplication;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements SnakeGame.OnScoreChangeListener {

    static {
        System.loadLibrary("myapplication");
    }

    public native int TextLCDOut(String data0, String data1);
    public native int PiezoControl(int value);

    private float startX, startY;
    private SnakeGame snakeGame;

    private MenuItem startMenuItem;
    private MenuItem stopMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        snakeGame = findViewById(R.id.snake_game);
        snakeGame.setOnScoreChangeListener(this); // 점수 변경 리스너 설정

        TextLCDOut("Snake Game", "Score : 0");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        startMenuItem = menu.findItem(R.id.action_start);
        stopMenuItem = menu.findItem(R.id.action_stop);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_start) {
            startGame();
            startMenuItem.setVisible(false);
            stopMenuItem.setVisible(true);
            return true;
        } else if (id == R.id.action_stop) {
            stopGame();
            startMenuItem.setVisible(true);
            stopMenuItem.setVisible(false);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void startGame() {
        snakeGame.startGame();
    }

    private void stopGame() {
        snakeGame.stopGame();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                startY = event.getY();
                return true;
            case MotionEvent.ACTION_UP:
                float endX = event.getX();
                float endY = event.getY();
                float deltaX = endX - startX;
                float deltaY = endY - startY;

                if (Math.abs(deltaX) > Math.abs(deltaY)) {
                    if (deltaX > 0) {
                        snakeGame.setDirection(1); // Right
                    } else {
                        snakeGame.setDirection(3); // Left
                    }
                } else {
                    if (deltaY > 0) {
                        snakeGame.setDirection(2); // Down
                    } else {
                        snakeGame.setDirection(0); // Up
                    }
                }
                return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void onScoreChanged(int newScore) {
        TextLCDOut("Snake Game", "Score : " + newScore);
        if(newScore != 0){
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    //TODO your background code
                    PiezoControl(0x15);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    PiezoControl(0x21);
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    PiezoControl(0);
                }
            });
        }else{
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    //TODO your background code
                    PiezoControl(0x3);
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    PiezoControl(0);
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    PiezoControl(0x3);
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    PiezoControl(0x32);
                    try {
                        Thread.sleep(400);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    PiezoControl(0);
                }
            });
        }
    }
}
