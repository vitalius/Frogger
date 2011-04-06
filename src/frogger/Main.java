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

import java.awt.event.KeyEvent;
import jig.engine.ImageResource;
import jig.engine.PaintableCanvas;
import jig.engine.RenderingContext;
import jig.engine.ResourceFactory;
import jig.engine.PaintableCanvas.JIGSHAPE;
import jig.engine.hli.ImageBackgroundLayer;
import jig.engine.hli.StaticScreenGame;
import jig.engine.physics.AbstractBodyLayer;
import jig.engine.util.Vector2D;

public class Main extends StaticScreenGame {
	static final int WORLD_WIDTH = (13*32);
	static final int WORLD_HEIGHT = (14*32);
	static final Vector2D FROGGER_START = new Vector2D(6*32,WORLD_HEIGHT-32);
	
	static final String RSC_PATH = "resources/";
	static final String SPRITE_SHEET = RSC_PATH + "frogger_sprites.png";
	
    static final int FROGGER_LIVES      = 5;
    static final int STARTING_LEVEL     = 1;
	static final int DEFAULT_LEVEL_TIME = 60;
	
	private FroggerCollisionDetection frogCol;
	private Frogger frog;
	private AudioEfx audiofx;
	private FroggerUI ui;
	private WindGust wind;
	private HeatWave hwave;
	private GoalManager goalmanager;
	
	private AbstractBodyLayer<MovingEntity> movingObjectsLayer;
	private AbstractBodyLayer<MovingEntity> particleLayer;
	
	private MovingEntityFactory roadLine1;
	private MovingEntityFactory roadLine2;
	private MovingEntityFactory roadLine3;
	private MovingEntityFactory roadLine4;
	private MovingEntityFactory roadLine5;
	
	private MovingEntityFactory riverLine1;
	private MovingEntityFactory riverLine2;
	private MovingEntityFactory riverLine3;
	private MovingEntityFactory riverLine4;
	private MovingEntityFactory riverLine5;
	
	private ImageBackgroundLayer backgroundLayer;
	
    static final int GAME_INTRO        = 0;
    static final int GAME_PLAY         = 1;
    static final int GAME_FINISH_LEVEL = 2;
    static final int GAME_INSTRUCTIONS = 3;
    static final int GAME_OVER         = 4;
    
	protected int GameState = GAME_INTRO;
	protected int GameLevel = STARTING_LEVEL;
	
    public int GameLives    = FROGGER_LIVES;
    public int GameScore    = 0;
    
    public int levelTimer = DEFAULT_LEVEL_TIME;
    
    private boolean space_has_been_released = false;
	private boolean keyPressed = false;
	private boolean listenInput = true;
	
    /**
	 * Initialize game objects
	 */
	public Main () {
		
		super(WORLD_WIDTH, WORLD_HEIGHT, false);
		
		gameframe.setTitle("Frogger");
		
		ResourceFactory.getFactory().loadResources(RSC_PATH, "resources.xml");

		ImageResource bkg = ResourceFactory.getFactory().getFrames(
				SPRITE_SHEET + "#background").get(0);
		backgroundLayer = new ImageBackgroundLayer(bkg, WORLD_WIDTH,
				WORLD_HEIGHT, ImageBackgroundLayer.TILE_IMAGE);
		
		// Used in CollisionObject, basically 2 different collision spheres
		// 30x30 is a large sphere (sphere that fits inside a 30x30 pixel rectangle)
		//  4x4 is a tiny sphere
		PaintableCanvas.loadDefaultFrames("col", 30, 30, 2, JIGSHAPE.RECTANGLE, null);
		PaintableCanvas.loadDefaultFrames("colSmall", 4, 4, 2, JIGSHAPE.RECTANGLE, null);
			
		frog = new Frogger(this);
		frogCol = new FroggerCollisionDetection(frog);
		audiofx = new AudioEfx(frogCol,frog);
		ui = new FroggerUI(this);
		wind = new WindGust();
		hwave = new HeatWave();
		goalmanager = new GoalManager();
		
		movingObjectsLayer = new AbstractBodyLayer.IterativeUpdate<MovingEntity>();
		particleLayer = new AbstractBodyLayer.IterativeUpdate<MovingEntity>();
		
		initializeLevel(1);
	}
	
	
	public void initializeLevel(int level) {

		/* dV is the velocity multiplier for all moving objects at the current game level */
		double dV = level*0.05 + 1;
		
		movingObjectsLayer.clear();
		
		/* River Traffic */
		riverLine1 = new MovingEntityFactory(new Vector2D(-(32*3),2*32), 
				new Vector2D(0.06*dV,0)); 
		
		riverLine2 = new MovingEntityFactory(new Vector2D(Main.WORLD_WIDTH,3*32),  
				new Vector2D(-0.04*dV,0)); 
		
		riverLine3 = new MovingEntityFactory(new Vector2D(-(32*3),4*32), 
				new Vector2D(0.09*dV,0)); 
		
		riverLine4 = new MovingEntityFactory(new Vector2D(-(32*4),5*32),  
				new Vector2D(0.045*dV,0));
		
		riverLine5 = new MovingEntityFactory(new Vector2D(Main.WORLD_WIDTH,6*32), 
				new Vector2D(-0.045*dV,0));
		
		/* Road Traffic */
		roadLine1 = new MovingEntityFactory(new Vector2D(Main.WORLD_WIDTH, 8*32), 
				new Vector2D(-0.1*dV, 0)); 
		
		roadLine2 = new MovingEntityFactory(new Vector2D(-(32*4), 9*32), 
				new Vector2D(0.08*dV, 0)); 
		
		roadLine3 = new MovingEntityFactory(new Vector2D(Main.WORLD_WIDTH, 10*32),
			    new Vector2D(-0.12*dV, 0)); 
		
		roadLine4 = new MovingEntityFactory(new Vector2D(-(32*4), 11*32),
				new Vector2D(0.075*dV, 0));
		
		roadLine5 = new MovingEntityFactory(new Vector2D(Main.WORLD_WIDTH, 12*32),
				new Vector2D(-0.05*dV, 0)); 
		
		goalmanager.init(level);
		for (Goal g : goalmanager.get()) {
			movingObjectsLayer.add(g);
		}
			
		/* Build some traffic before game starts buy running MovingEntityFactories for fews cycles */
		for (int i=0; i<500; i++)
			cycleTraffic(10);
	}
	
	
	/**
	 * Populate movingObjectLayer with a cycle of cars/trucks, moving tree logs, etc
	 * 
	 * @param deltaMs
	 */
	public void cycleTraffic(long deltaMs) {
		MovingEntity m;
		/* Road traffic updates */
		roadLine1.update(deltaMs);
	    if ((m = roadLine1.buildVehicle()) != null) movingObjectsLayer.add(m);
		
		roadLine2.update(deltaMs);
	    if ((m = roadLine2.buildVehicle()) != null) movingObjectsLayer.add(m);
	    
		roadLine3.update(deltaMs);
	    if ((m = roadLine3.buildVehicle()) != null) movingObjectsLayer.add(m);
	    
		roadLine4.update(deltaMs);
	    if ((m = roadLine4.buildVehicle()) != null) movingObjectsLayer.add(m);

		roadLine5.update(deltaMs);
	    if ((m = roadLine5.buildVehicle()) != null) movingObjectsLayer.add(m);
	    
		
		/* River traffic updates */
		riverLine1.update(deltaMs);
	    if ((m = riverLine1.buildShortLogWithTurtles(40)) != null) movingObjectsLayer.add(m);
		
		riverLine2.update(deltaMs);
	    if ((m = riverLine2.buildLongLogWithCrocodile(30)) != null) movingObjectsLayer.add(m);
		
		riverLine3.update(deltaMs);
	    if ((m = riverLine3.buildShortLogWithTurtles(50)) != null) movingObjectsLayer.add(m);
		
		riverLine4.update(deltaMs);
	    if ((m = riverLine4.buildLongLogWithCrocodile(20)) != null) movingObjectsLayer.add(m);

		riverLine5.update(deltaMs);
	    if ((m = riverLine5.buildShortLogWithTurtles(10)) != null) movingObjectsLayer.add(m);
	    
	    // Do Wind
	    if ((m = wind.genParticles(GameLevel)) != null) particleLayer.add(m);
	    
	    // HeatWave
	    if ((m = hwave.genParticles(frog.getCenterPosition())) != null) particleLayer.add(m);
	        
	    movingObjectsLayer.update(deltaMs);
	    particleLayer.update(deltaMs);
	}
	
	/**
	 * Handling Frogger movement from keyboard input
	 */
	public void froggerKeyboardHandler() {
 		keyboard.poll();
		
 		boolean keyReleased = false;
        boolean downPressed = keyboard.isPressed(KeyEvent.VK_DOWN);
        boolean upPressed = keyboard.isPressed(KeyEvent.VK_UP);
		boolean leftPressed = keyboard.isPressed(KeyEvent.VK_LEFT);
		boolean rightPressed = keyboard.isPressed(KeyEvent.VK_RIGHT);
		
		// Enable/Disable cheating
		if (keyboard.isPressed(KeyEvent.VK_C))
			frog.cheating = true;
		if (keyboard.isPressed(KeyEvent.VK_V))
			frog.cheating = false;
		if (keyboard.isPressed(KeyEvent.VK_0)) {
			GameLevel = 10;
			initializeLevel(GameLevel);
		}
		
		
		/*
		 * This logic checks for key strokes.
		 * It registers a key press, and ignores all other key strokes
		 * until the first key has been released
		 */
		if (downPressed || upPressed || leftPressed || rightPressed)
			keyPressed = true;
		else if (keyPressed)
			keyReleased = true;
		
		if (listenInput) {
		    if (downPressed) frog.moveDown();
		    if (upPressed) frog.moveUp();
		    if (leftPressed) frog.moveLeft();
	 	    if (rightPressed) frog.moveRight();
	 	    
	 	    if (keyPressed)
	            listenInput = false;
		}
		
		if (keyReleased) {
			listenInput = true;
			keyPressed = false;
		}
		
		if (keyboard.isPressed(KeyEvent.VK_ESCAPE))
			GameState = GAME_INTRO;
	}
	
	/**
	 * Handle keyboard events while at the game intro menu
	 */
	public void menuKeyboardHandler() {
		keyboard.poll();
		
		// Following 2 if statements allow capture space bar key strokes
		if (!keyboard.isPressed(KeyEvent.VK_SPACE)) {
			space_has_been_released = true;
		}
		
		if (!space_has_been_released)
			return;
		
		if (keyboard.isPressed(KeyEvent.VK_SPACE)) {
			switch (GameState) {
			case GAME_INSTRUCTIONS:
			case GAME_OVER:
				GameState = GAME_INTRO;
				space_has_been_released = false;
				break;
			default:
				GameLives = FROGGER_LIVES;
				GameScore = 0;
				GameLevel = STARTING_LEVEL;
				levelTimer = DEFAULT_LEVEL_TIME;
				frog.setPosition(FROGGER_START);
				GameState = GAME_PLAY;
				audiofx.playGameMusic();
				initializeLevel(GameLevel);			
			}
		}
		if (keyboard.isPressed(KeyEvent.VK_H))
			GameState = GAME_INSTRUCTIONS;
	}
	
	/**
	 * Handle keyboard when finished a level
	 */
	public void finishLevelKeyboardHandler() {
		keyboard.poll();
		if (keyboard.isPressed(KeyEvent.VK_SPACE)) {
			GameState = GAME_PLAY;
			audiofx.playGameMusic();
			initializeLevel(++GameLevel);
		}
	}
	
	
	/**
	 * w00t
	 */
	public void update(long deltaMs) {
		switch(GameState) {
		case GAME_PLAY:
			froggerKeyboardHandler();
			wind.update(deltaMs);
			hwave.update(deltaMs);
			frog.update(deltaMs);
			audiofx.update(deltaMs);
			ui.update(deltaMs);

			cycleTraffic(deltaMs);
			frogCol.testCollision(movingObjectsLayer);
			
			// Wind gusts work only when Frogger is on the river
			if (frogCol.isInRiver())
				wind.start(GameLevel);		
			wind.perform(frog, GameLevel, deltaMs);
			
			// Do the heat wave only when Frogger is on hot pavement
			if (frogCol.isOnRoad())
				hwave.start(frog, GameLevel);
			hwave.perform(frog, deltaMs, GameLevel);
			
	
			if (!frog.isAlive)
				particleLayer.clear();
			
			goalmanager.update(deltaMs);
			
			if (goalmanager.getUnreached().size() == 0) {
				GameState = GAME_FINISH_LEVEL;
				audiofx.playCompleteLevel();
				particleLayer.clear();
			}
			
			if (GameLives < 1) {
				GameState = GAME_OVER;
			}
			
			break;
		
		case GAME_OVER:		
		case GAME_INSTRUCTIONS:
		case GAME_INTRO:
			goalmanager.update(deltaMs);
			menuKeyboardHandler();
			cycleTraffic(deltaMs);
			break;
			
		case GAME_FINISH_LEVEL:
			finishLevelKeyboardHandler();
			break;		
		}
	}
	
	
	/**
	 * Rendering game objects
	 */
	public void render(RenderingContext rc) {
		switch(GameState) {
		case GAME_FINISH_LEVEL:
		case GAME_PLAY:
			backgroundLayer.render(rc);
			
			if (frog.isAlive) {
				movingObjectsLayer.render(rc);
				//frog.collisionObjects.get(0).render(rc);
				frog.render(rc);		
			} else {
				frog.render(rc);
				movingObjectsLayer.render(rc);				
			}
			
			particleLayer.render(rc);
			ui.render(rc);
			break;
			
		case GAME_OVER:
		case GAME_INSTRUCTIONS:
		case GAME_INTRO:
			backgroundLayer.render(rc);
			movingObjectsLayer.render(rc);
			ui.render(rc);
			break;		
		}
	}
	
	public static void main (String[] args) {
		Main f = new Main();
		f.run();
	}
}