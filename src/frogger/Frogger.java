/**
 * Copyright (c) 2009 Vitaliy Pavlenko
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

package frogger;

import jig.engine.util.Vector2D;

/**
 * Main sprite in the game that a player can control
 * 
 * @author vitaliy
 *
 */
public class Frogger extends MovingEntity {
	
	final static int MOVE_STEP = 32;
	
	// Animation related variables 
	final static private int ANIMATION_STEP = 4; // 32/4 = 8, 8 animation frames, 10 ms each
	
	private int curAnimationFrame = 0;
	private int finalAnimationFrame = 0;
	private long animationDelay = 10; // milliseconds
	private long animationBeginTime = 0;
	private boolean isAnimating = false;
	private Vector2D dirAnimation = new Vector2D(0,0);
	
	// Object to follow, such as Tree Log in the river
	private MovingEntity followObject = null;
	
	
	public boolean isAlive = false;
    private long timeOfDeath = 0;
    
    // Current sprite frame displayed
    private int currentFrame = 0;
    private int tmpFrame = 0;
    
    public int deltaTime = 0;
    
    public boolean cheating = false;
    
    public boolean hw_hasMoved = false;
    
    private Main game;
    
    /**
     * Build frogger!
     */
	public Frogger (Main g) {
		super(Main.SPRITE_SHEET + "#frog");
		game = g;
		resetFrog();
		collisionObjects.add(new CollisionObject(position));
	}
	
	/**
	 * Reset the Frogger to default state and position
	 */
	public void resetFrog() {
		isAlive = true;
		isAnimating = false;
		currentFrame = 0;
		followObject = null;
		position = Main.FROGGER_START;
		game.levelTimer = Main.DEFAULT_LEVEL_TIME;
	}
	
	/**
	 * Moving methods, called from Main upon key strokes
	 */
	public void moveLeft() {
		if (getCenterPosition().getX()-16 > 0 && isAlive && !isAnimating) {
			currentFrame = 3;
		    move(new Vector2D(-1,0));
		    AudioEfx.frogJump.play(0.2);
		}
	}
	
	public void moveRight() {
		
		if (getCenterPosition().getX()+32 < Main.WORLD_WIDTH && isAlive && !isAnimating) {
			currentFrame = 2;
		    move(new Vector2D(1,0));
		    AudioEfx.frogJump.play(0.2);
		}
	}
	
	public void moveUp() {
		if (position.getY() > 32  && isAlive && !isAnimating) {
			currentFrame = 0;
		    move(new Vector2D(0,-1));
		    AudioEfx.frogJump.play(0.2);
		}
	}
	
	public void moveDown() {
		if (position.getY() < Main.WORLD_HEIGHT - MOVE_STEP && isAlive && !isAnimating) {
			currentFrame = 1;
		    move(new Vector2D(0,1));
		    AudioEfx.frogJump.play(0.2);
		}
	}
	
	/**
	 * Short-cut for systems current time
	 * @return
	 */
	public long getTime() {
		return System.currentTimeMillis();
	}
	
	/**
	 * Initiate animation sequence into specified direction, given by
	 * 
	 * @param dir - specifies direction to move
	 * 
	 * The collision sphere of Frogger is automatically moved to the final
	 * position. The animation then lags behind by a few seconds(or frames). 
	 * This resolves the positioning bugs when objects collide during the animation.
	 */
	public void move(Vector2D dir) {
		followObject = null;
		curAnimationFrame = 0;
		finalAnimationFrame = MOVE_STEP/ANIMATION_STEP;
		isAnimating = true;
		hw_hasMoved = true;
		animationBeginTime = getTime();
		dirAnimation = dir;

		tmpFrame = currentFrame;
		currentFrame += 5;
		
		// Move CollisionSphere to an already animated location
		sync(new Vector2D(
				position.getX()+dirAnimation.getX()*MOVE_STEP, 
				position.getY()+dirAnimation.getY()*MOVE_STEP)
		);
	}
	
	/**
	 * Cycle through the animation frames
	 */
	public void updateAnimation() {
		// If not animating, sync position of the sprite with its collision sphere
		if (!isAnimating || !isAlive) {
			sync(position);
			return;
		}
		
		// Finish animating
		if (curAnimationFrame >= finalAnimationFrame) {
			isAnimating = false;
			currentFrame = tmpFrame;
			return;
		}
		
		// Cycle animation
		if (animationBeginTime + animationDelay < getTime()) {
			animationBeginTime = getTime();
			position = new Vector2D(
					position.getX() + dirAnimation.getX()*ANIMATION_STEP,
					position.getY() + dirAnimation.getY()*ANIMATION_STEP
					);
			curAnimationFrame++;
			return;
		}
	}
	
	/**
	 * Re-align frog to a grid
	 */
	public void allignXPositionToGrid() {
		if (isAnimating || followObject != null) 
			return;
		double x = position.getX();
		x = Math.round(x/32)*32;
		position = new Vector2D(x, position.getY());
		
	}
	
	/**
	 * Following a Tree Log on a river by getting it's velocity vector
	 * @param deltaMs
	 */
	public void updateFollow(long deltaMs) {
		if (followObject == null || !isAlive) 
			return;
		Vector2D dS = followObject.getVelocity().scale(deltaMs);
		position = new Vector2D(position.getX()+dS.getX(), position.getY()+dS.getY());
	}
	
	/**
	 * Setting a moving entity to follow
	 * @param log
	 */
	public void follow(MovingEntity log) {
		followObject = log;
	}
	
	
	/**
	 * Effect of a wind gust on Frogger
	 * @param d
	 */
	public void windReposition(Vector2D d) {
		if (isAlive) {
			hw_hasMoved = true;
			setPosition(new Vector2D(getPosition().getX()+d.getX(), getPosition().getY()));
			sync(position);
		}
	}
	
	/**
	 * Effect of Heat Wave on Frogger
	 * @param randDuration
	 */
	public void randomJump(final int rDir) {
		switch(rDir) {
		case 0:
			moveLeft();
			break;
		case 1:
			moveRight();
			break;
		case 2:
			moveUp();
			break;
		default:
			moveDown();
		}
	}
	
    /**
     * Frogger dies
     */
	public void die() {
		if (isAnimating)
			return;
		
		if (!cheating) {
		    AudioEfx.frogDie.play(0.2);
		    followObject = null;
		    isAlive = false;
		    currentFrame = 4;	// dead sprite   
		    game.GameLives--;
		    hw_hasMoved = true;
		}
		
		timeOfDeath = getTime();
		game.levelTimer = Main.DEFAULT_LEVEL_TIME;
	}
	
	/**
	 * Frogger reaches a goal
	 */
	public void reach(final Goal g) {
		if (g.isReached == false) {
			AudioEfx.frogGoal.play(0.4);
			game.GameScore += 100;
			game.GameScore += game.levelTimer;
			if (g.isBonus) {
				AudioEfx.bonus.play(0.2);
				game.GameLives++;
			}
			g.reached();
			resetFrog();
		}
		else {
			setPosition(g.getPosition());
		}
	}
	
	public void update(final long deltaMs) {
		if (game.GameLives <= 0)
			return;
		
		// if dead, stay dead for 2 seconds.
		if (!isAlive && timeOfDeath + 2000 < System.currentTimeMillis())
				resetFrog();
		
		updateAnimation();	
		updateFollow(deltaMs);
		setFrame(currentFrame);
		
		// Level timer stuff
		deltaTime += deltaMs;
		if (deltaTime > 1000) {
			deltaTime = 0;
			game.levelTimer--;
		}
		
		if (game.levelTimer <= 0)
			die();
	}
}