/*
 * File: Breakout.java
 * -------------------
 * Name:
 * Section Leader:
 * 
 * This file will eventually implement the game of Breakout.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;
import sun.security.jca.GetInstance.Instance;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout extends GraphicsProgram {

/** Width and height of application window in pixels */
	public static final int APPLICATION_WIDTH = 400;
	public static final int APPLICATION_HEIGHT = 600;

/** Dimensions of game board (usually the same) */
	private static final int WIDTH = APPLICATION_WIDTH;
	private static final int HEIGHT = APPLICATION_HEIGHT;

/** Dimensions of the paddle */
	private static final int PADDLE_WIDTH = 60;
	private static final int PADDLE_HEIGHT = 10;

/** Offset of the paddle up from the bottom */
	private static final int PADDLE_Y_OFFSET = 30;

/** Number of bricks per row */
	private static final int NBRICKS_PER_ROW = 10;

/** Number of rows of bricks */
	private static final int NBRICK_ROWS = 10;

/** Separation between bricks */
	private static final int BRICK_SEP = 4;

/** Width of a brick */
	private static final int BRICK_WIDTH =
	  (WIDTH - (NBRICKS_PER_ROW - 1) * BRICK_SEP) / NBRICKS_PER_ROW;

/** Height of a brick */
	private static final int BRICK_HEIGHT = 8;

/** Radius of the ball in pixels */
	private static final int BALL_RADIUS = 10;

/** Offset of the top brick row from the top */
	private static final int BRICK_Y_OFFSET = 70;

/** Number of turns */
	private static final int NTURNS = 3;

/** Offset of the brick row from the left side of console */
	private static final int BRICK_X_OFFSET = (WIDTH - ( NBRICKS_PER_ROW * BRICK_WIDTH ) - ( (NBRICKS_PER_ROW - 1) * BRICK_SEP )) / 2;
	
	private RandomGenerator rgen = RandomGenerator.getInstance();
	
	private GRect paddle;
	
	private GRect brick;
	
	private GOval ball;
	
	private GRect rect;
	
	private GLabel text;
	
	private double vx, vy;
	
	private int amountOfBricksLefted;

	public void init() {
		createBricks();
		addMouseListeners();
	}

	/* Method: run() */
	/** Runs the Breakout program. */
	public void run() {
		createPaddle();
		startGame();
	}

	// This creates bricks 
	private void createBricks() {
		for ( int i = NBRICK_ROWS; i > 0; i-- ) {
			for ( int j = 0; j < NBRICKS_PER_ROW; j++ ){
				buildBricks(i , j);
			}
		}
	}
	
	// This makes each brick
	private void buildBricks(int i, int j) {
		int x =  BRICK_X_OFFSET + (BRICK_SEP + BRICK_WIDTH) * j;
		int y = BRICK_Y_OFFSET + ( BRICK_HEIGHT + BRICK_SEP )* i;
		
		brick = new GRect(x, y, BRICK_WIDTH, BRICK_HEIGHT);
		brick.setFilled(true);
		brick.setColor(chooseColor(i));
		add(brick);
	}

	// This sets color to each brick
	private Color chooseColor(int i) {
		if ( i <= 2 ) {
			return Color.RED;
		} else if ( i >= 2 && i <= 4 ) {
			return Color.ORANGE;
		} else if ( i >= 4 && i <= 6 ) {
			return Color.YELLOW;
		} else if ( i >= 6 && i <= 8 ) {
			return Color.GREEN;
		} else {
			return Color.CYAN;
		}
	}
	
	// This creates a new ball
	private void createBall() {
		double x = WIDTH / 2 - BALL_RADIUS;
		double y = HEIGHT / 2 - BALL_RADIUS;
		
		ball = new GOval(x, y, BALL_RADIUS * 2, BALL_RADIUS * 2);
		ball.setFilled(true);
		add(ball);
	}

	// This method after click will start the game process
	private void startGame() {
		createBall();
		waitForClick();
		amountOfBricksLefted = NBRICKS_PER_ROW * NBRICK_ROWS;
		makeBallMove();
		remove(ball);
		printYourResult();
	}
	
	
	// This method gives ball speed, its 3 health and decides if you win or loose
	private void makeBallMove() {
		for (int i = NTURNS; i > 0; i--) {
			vx = rgen.nextDouble(1.0, 3.0);
			if(rgen.nextBoolean(0.5)) vx = -vx;
			vy = 3.0;
			ballMoveBeforeSomethingGetInTheWay();
			remove(ball);
			if ( amountOfBricksLefted != 0) {
				createBall();
			} else {
				break;
			}
		}
	}
	
	// This runs before we lost our one heart or win the game and it moves the ball infinitely
	private void ballMoveBeforeSomethingGetInTheWay() {
		while(true) {
			checkForTouches();
			if (facingLowestWall()) {
				break;
			}
			ball.move(vx, vy);
			pause(10);
			if (amountOfBricksLefted == 0) {
				break;
			}
		}
	}
	
	// This prints if you win or lost
	private void printYourResult() {
		if ( amountOfBricksLefted != 0 ) {
			drawLabel("YOU LOST");
		} else {
			drawLabel("YOU WIN");
		}
	}
	
	// This checks if ball touches anything
	private void checkForTouches() {
		checkForPaddle();
		checkForBrick();
		checkForWalls();
	}

	// This checks if ball touches brick or not, if yes removes it
	private void checkForBrick() {
		checkForTouch();
		if (rect != null && rect != paddle) {
			// This boolean checks if we hit it from side
			boolean checkIfTouchSideOfBrick = 
					ball.getY() + 2 * BALL_RADIUS < rect.getY() + BRICK_HEIGHT / 2 
					&& 
					ball.getY() > rect.getY() + BRICK_HEIGHT / 2;
			remove(rect);
			if ( checkIfTouchSideOfBrick ) {
				vx = -vx;
				ball.move(vx, 0);
			} else {
				vy = -vy;
				ball.move(0, vy);
			}
			amountOfBricksLefted--;
		}
	}
	
	// This check if ball touch paddle or not and changes direction if yes
	private void checkForPaddle() {
		checkForTouch();
		if ( rect == paddle) {
			vy = -(Math.abs(vy));
		}
	}

	// This method checks every angle of ball's square if it is  touched
	private void checkForTouch() {
		rect = (GRect) getElementAt(ball.getX(), ball.getY());
		if ( rect == null ) {
			rect = (GRect) getElementAt(ball.getX(), ball.getY() + BALL_RADIUS * 2);
		}
		if (rect == null ) {
			rect = (GRect) getElementAt(ball.getX() + BALL_RADIUS * 2, ball.getY() + BALL_RADIUS * 2);
		}
		if ( rect == null ) {
			rect = (GRect) getElementAt(ball.getX() + BALL_RADIUS * 2, ball.getY());
		}
	}
	
	// This method makes what do to in order to change direction when face wall
	private void checkForWalls() {
		facingRightWall();
		facingUpperWall();
		facingLeftWall();
	}
	
	// This method check if we touch right wall and if yes changes direction
	private void facingRightWall() {
		double x = ball.getX() + BALL_RADIUS * 2;
		
		if ( x > WIDTH) {
			vx = -vx;
		}
	}
	
	// This method check if we touch left wall and if yes changes direction
	private void facingLeftWall() {
		double x = ball.getX();
		
		if ( x < 0) {
			vx = -vx;
		}
	}
	
	// This methods return true if we touch lowest wall and that kills our ball
	private boolean facingLowestWall() {
		double y = ball.getY() + BALL_RADIUS * 3;
		boolean bl = y > getHeight();
		return bl;
	}
	
	// This method check if we touch up wall and if yes changes direction
	private void facingUpperWall() {
		double y = ball.getY();
		
		if ( y < 0) {
			vy = -vy;
		}
	}
	
	// This creates a paddle
	private void createPaddle() {
		double x = WIDTH / 2 - PADDLE_WIDTH / 2;
		double y = getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT; 
		
		paddle = new GRect(x, y, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);
	}
	
	// This moves paddle while mouse is moving
	public void mouseDragged (MouseEvent e) {
		double x = e.getX();
		double y = getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT;
		
		x = checkToNotCrossBorders(x);
				
		paddle.setLocation(x, y);
		pause(10);
	}
	
	// This prevents paddle to not cross borders
	public double checkToNotCrossBorders(double x) {
		if ( x + PADDLE_WIDTH > WIDTH ) {
			x = WIDTH - PADDLE_WIDTH;
		} else if ( x < 0 ) {
			x = 0;
		}
		return x;
	}
	
	// This draws labels to say if you win or lost
	private void drawLabel(String label) {
		text = new GLabel(label);
		
		double x = (getWidth() - text.getWidth()) / 4; 
		double y = (getHeight() - text.getAscent()) / 2;
		
		text.setFont("Ariel-50");
		text.setLocation(x, y);
		text.setColor(Color.RED);
		
		add(text);
	}
}
