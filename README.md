# Snake Game Android Application

This repository contains an Android application implementing a simple Snake game using native C/C++ for controlling the hardware. The game features touch controls and updates an external display with the current score.

## Table of Contents

- [Installation](#installation)
- [Usage](#usage)
- [Features](#features)
- [Code Overview](#code-overview)
  - [MainActivity](#mainactivity)
  - [SnakeGame](#snakegame)
- [License](#license)

## Installation

1. **Clone the repository:**
   ```bash
   git clone https://github.com/yourusername/snakegame.git
   cd snakegame
   ```

2. **Open the project in Android Studio:**
   - Start Android Studio.
   - Select "Open an existing Android Studio project".
   - Navigate to the cloned repository folder and select it.

3. **Build the project:**
   - Connect your Android device or start an emulator.
   - Click on the "Run" button in Android Studio to build and deploy the app.

## Usage

Once the app is installed on your device:

1. **Start the game:**
   - Open the app.
   - Press the "Start" button in the menu to start the game.

2. **Control the snake:**
   - Swipe in any direction (up, down, left, right) to control the movement of the snake.

3. **Stop the game:**
   - Press the "Stop" button in the menu to pause the game.

## Features

- **Touch Controls:** Control the snake with swipe gestures.
- **Score Display:** The current score is displayed on an external LCD.
- **Sound Effects:** Piezo buzzer produces sounds based on game events.
- **Responsive UI:** The game adapts to different screen sizes and orientations.

## Code Overview

### MainActivity

The main activity of the application, responsible for initializing the game, handling menu actions, and touch events.

- **Native Methods:**
  - `TextLCDOut(String data0, String data1)`: Updates the external LCD display with the provided text.
  - `PiezoControl(int value)`: Controls the piezo buzzer with the provided value.

- **Lifecycle Methods:**
  - `onCreate(Bundle savedInstanceState)`: Initializes the game and sets up the score change listener.
  - `onCreateOptionsMenu(Menu menu)`: Inflates the menu options.
  - `onOptionsItemSelected(MenuItem item)`: Handles menu item selections to start or stop the game.

- **Touch Event Handling:**
  - `onTouchEvent(MotionEvent event)`: Detects swipe gestures to control the snake's direction.

- **Score Change Listener:**
  - `onScoreChanged(int newScore)`: Updates the LCD display with the new score and triggers the piezo buzzer based on the score change.

### SnakeGame

The custom `SurfaceView` implementation for the Snake game, responsible for rendering the game, updating the game state, and handling game logic.

- **Initialization:**
  - `init(Context context)`: Initializes the game surface, paint object, and sets up the initial snake and food positions.

- **Game Control:**
  - `startGame()`: Starts or resumes the game.
  - `stopGame()`: Pauses the game.

- **Game Loop:**
  - `run()`: The main game loop that updates and draws the game state.
  - `updateRequired()`: Checks if the game needs to update based on the frame rate.
  - `update()`: Updates the snake's position, handles food consumption, and checks for collisions.

- **Rendering:**
  - `draw()`: Draws the snake and food on the canvas.

- **Snake Control:**
  - `setDirection(int newDirection)`: Sets the snake's direction based on user input, preventing reversal.

- **Score Management:**
  - `initializeScore()`: Resets the score to zero.
  - `increaseScore()`: Increases the score by 10 points when the snake eats food and notifies the listener.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.
