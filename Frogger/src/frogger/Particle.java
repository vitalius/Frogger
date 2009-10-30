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
 * Simple tiny particle
 * 
 * Used to show some weather effects
 * 
 * @author vitaliy
 *
 */
public class Particle extends MovingEntity {
	
	private int timeExpire = 1;
	private int timeAlive = 1;
	
	public Particle(String sprite, Vector2D pos, Vector2D v) {
		super(sprite);
		position = pos;
		velocity = v;
		setActivation(true);
		timeExpire = 0;
	}
	
	/**
	 * Build particle with expiration timer
	 * 
	 * @param pos - position
	 * @param v - velocity
	 * @param te - expiration timer in milliseconds
	 */
	public Particle(String sprite, Vector2D pos, Vector2D v, int te) {
		super(sprite);
		position = pos;
		velocity = v;
		setActivation(true);
		timeExpire = te;
	}
	
	public void update(final long deltaMs) {
		super.update(deltaMs);
		
		// Check the expiration
		if (timeExpire != 0) {
			timeAlive += deltaMs;
			if (timeAlive > timeExpire)
				setActivation(false);
		}
	}
}
