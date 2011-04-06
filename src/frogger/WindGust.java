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

import java.util.Random;

import jig.engine.util.Vector2D;
/**
 * Generating the Wind effect in Frogger
 * 
 * @author vitaliy
 *
 */
public class WindGust {

	final static int PERIOD    = 5000; //milliseconds
	final static int DURATION  = 3000; //milliseconds
	
	Random r;
	
	private long timeMs;
	private long durationMs;
	
	private boolean isWindy;
	
	public WindGust() {
		timeMs = 0;
		isWindy = false;
		r = new Random(System.currentTimeMillis());
	}
	
	/**
	 * Apply wind force to the Frogger, higher levels have higher wind drag
	 * @param f - Frogger
	 * @param level 
	 */
	public void perform(Frogger f, int level, final long deltaMs) {
		if (!f.isAlive) {
			isWindy = false;
			return;
		}
		
		if (isWindy && durationMs < DURATION) {
			double vPos = deltaMs*r.nextDouble()*(0.01*level);
			f.windReposition(new Vector2D(vPos, 0));
		} else {
			isWindy = false;
		}
	}
	
	/**
	 * Initiate Wind effect
	 * 
	 * @param level - current game level
	 */
	public void start(final int level) {
		
		if (!isWindy && timeMs > PERIOD) {
			
			if (r.nextInt(100) < level*10) {
				durationMs = 1;
				isWindy = true;
				AudioEfx.wind.play(0.2);
			}
			
			timeMs = 0;	
		}
		
	}
	
	/**
	 * Wind particle generator
	 * 
	 * @param level
	 * @return - a wind particle object or null
	 */
	public MovingEntity genParticles(final int level) {

		if (!isWindy)
			return null;
		
		// Lower game level has less wind strength and should be less visible
		if (r.nextInt(100) > level*10)
			return null;
		
		int yPos = r.nextInt(13*32)+32;         // visible area in y-axis of the game
		Vector2D pos = new Vector2D(0, yPos);   // start behind left side
		
		// Build somewhat random velocity vector for each wind particle, looks cool
		Vector2D v = new Vector2D(0.2+r.nextDouble(),(r.nextDouble()-0.5)*0.1); 
		return new Particle(Main.SPRITE_SHEET + "#white_dot", pos,v);
	}
	
	public void update(final long deltaMs) {
		timeMs += deltaMs;
		durationMs += deltaMs;
	}
}
