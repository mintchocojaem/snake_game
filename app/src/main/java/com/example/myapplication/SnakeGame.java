package com.example.myapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SnakeGame extends SurfaceView implements Runnable {

    private Thread thread;
    private boolean isPlaying = false;

    private SurfaceHolder surfaceHolder;
    private Paint paint;

    private List<Point> snake;
    private Point food;

    private int blockSize = 20; // blockSize 값을 작게 조정하여 네모의 크기를 줄입니다.
    private int screenWidth, screenHeight;
    private int direction = 1; // 0: up, 1: right, 2: down, 3: left

    private long nextFrameTime;
    private final long FPS = 10;
    private final long MILLIS_PER_FRAME = 1000 / FPS;

    private int score = 0; // 점수 변수 추가

    private OnScoreChangeListener scoreChangeListener;

    // 생성자 수정
    public SnakeGame(Context context) {
        super(context);
        init(context);
    }

    public SnakeGame(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SnakeGame(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public interface OnScoreChangeListener {
        void onScoreChanged(int newScore);
    }

    public void setOnScoreChangeListener(OnScoreChangeListener listener) {
        this.scoreChangeListener = listener;
    }

    private  void initializeScore(){
        score = 0;
        if (scoreChangeListener != null) {
            scoreChangeListener.onScoreChanged(score);
        }
    }

    private void increaseScore() {
        score += 10; // 10점씩 증가
        if (scoreChangeListener != null) {
            scoreChangeListener.onScoreChanged(score);
        }
    }

    // 게임을 시작하는 메서드 추가
    public void startGame() {
        if (!isPlaying) {
            nextFrameTime = System.currentTimeMillis();
            resume();
        }
    }

    // 게임을 중지하는 메서드 추가
    public void stopGame() {
        pause();
    }

    private void init(Context context) {
        surfaceHolder = getHolder();
        paint = new Paint();

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;

        // snake 리스트 초기화
        snake = new ArrayList<>();
        spawnSnake();
        spawnFood();
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        screenWidth = w;
        screenHeight = h;

        Log.d("SnakeGame", "Screen size changed: " + screenWidth + " x " + screenHeight);
    }

    private void spawnSnake() {
        snake.clear();
        snake.add(new Point(5, 5)); // Initial position of the snake
        initializeScore();
    }

    private void spawnFood() {
        Random random = new Random();
        boolean foodOnSnake;
        int x, y;
        int maxX = (screenWidth / blockSize) -3;
        int maxY = (screenHeight / blockSize) -7;

        do {
            foodOnSnake = false;
            x = random.nextInt(maxX);
            y = random.nextInt(maxY);

            // Debugging log
            Log.d("SnakeGame","maxX :"+maxX +" "+ "maxY :"+maxY);
            Log.d("SnakeGame", "Generated food coordinates: (" + x + ", " + y + ")");

            for (Point point : snake) {
                if (point.x == x && point.y == y) {
                    foodOnSnake = true;
                    break;
                }
            }
        } while (foodOnSnake);

        food = new Point(x, y);
    }

    @Override
    public void run() {
        while (isPlaying) {
            if (updateRequired()) {
                update();
                draw();
            }
        }
    }

    private boolean updateRequired() {
        if (nextFrameTime <= System.currentTimeMillis()) {
            nextFrameTime = System.currentTimeMillis() + MILLIS_PER_FRAME;
            return true;
        }
        return false;
    }

    private void update() {

        // snake 리스트가 비어있는지 확인
        if (snake.isEmpty()) {
            return;
        }

        Point head = snake.get(0);
        Point newHead = new Point(head.x, head.y);
        switch (direction) {
            case 0: // Up
                newHead.y -= 1;
                break;
            case 1: // Right
                newHead.x += 1;
                break;
            case 2: // Down
                newHead.y += 1;
                break;
            case 3: // Left
                newHead.x -= 1;
                break;
        }

        // Check if the snake eats the food
        if (newHead.equals(food)) {
            snake.add(0, newHead); // Add new head at the position of the food
            spawnFood(); // Spawn new food
            increaseScore(); // 점수 증가
        } else {
            snake.add(0, newHead); // Add new head
            snake.remove(snake.size() - 1); // Remove the tail

            // Check for wall collision
            if (newHead.x < 0 || newHead.x >= screenWidth / blockSize ||
                    newHead.y < 0 || newHead.y >= screenHeight / blockSize) {
                spawnSnake();
            }

            // Check for self collision
            for (int i = 1; i < snake.size(); i++) {
                if (newHead.equals(snake.get(i))) {
                    spawnSnake();
                    break;
                }
            }
        }
    }
    private void draw() {
        if (surfaceHolder != null && surfaceHolder.getSurface().isValid()) {
            Canvas canvas = surfaceHolder.lockCanvas();
            if (canvas != null) {
                canvas.drawColor(Color.BLACK);

                // 그리기 순서 변경: 음식 먼저, 뱀 나중에 그리기
                paint.setColor(Color.RED);
                canvas.drawRect(food.x * blockSize, food.y * blockSize,
                        (food.x + 1) * blockSize, (food.y + 1) * blockSize, paint);

                paint.setColor(Color.GREEN);
                for (Point point : snake) {
                    canvas.drawRect(point.x * blockSize, point.y * blockSize,
                            (point.x + 1) * blockSize, (point.y + 1) * blockSize, paint);
                }

                surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
    }


    public void pause() {
        isPlaying = false;
        try {
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

    public void setDirection(int newDirection) {
        // Prevent the snake from reversing
        if ((direction == 0 && newDirection == 2) ||
                (direction == 2 && newDirection == 0) ||
                (direction == 1 && newDirection == 3) ||
                (direction == 3 && newDirection == 1)) {
            return;
        }
        this.direction = newDirection;
    }
}

class Point {
    int x, y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Point)) return false;
        Point other = (Point) obj;
        return this.x == other.x && this.y == other.y;
    }
}
