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

import jig.engine.physics.Body;
import jig.engine.util.Vector2D;

import java.util.LinkedList;
import java.util.List;

/**
 * Abstract class for moving entities in the game
 * 
 * They all have update, sync methods and underlining collision spheres
 * 
 * @author vitaliy
 *
 */
public abstract class MovingEntity extends Body {
	
	static final int STEP_SIZE = 32;
	
	// List that holds collision spheres
	protected List<CollisionObject> collisionObjects;
	
	
	public MovingEntity (String name) {
		super(name);
		collisionObjects = new LinkedList<CollisionObject>();
	}

	public List<CollisionObject> getCollisionObjects() {
		return collisionObjects;
	}
	
	/**
	 * Updates the collision spheres with new position
	 * 
	 * @param position
	 */
	public void sync(Vector2D position) {
		int i = 0;
		for (CollisionObject a : collisionObjects) {
			Vector2D deltaPos = new Vector2D(position.getX()+(STEP_SIZE*i), position.getY());
			a.setPosition(deltaPos);
			i++;
		}
		    
	}
	
	/**
	 * Check bounds in the game
	 * 
	 * The way this game works, we only worry about the x-axis
	 * 
	 * None of the objects (except the Frogger which has it's own collision detection) travel
	 * in y-axis
	 */
	public void update(final long deltaMs) {
		if (position.getX() > Main.WORLD_WIDTH+width || position.getX() < -(32*4))
			setActivation(false);
			
	    position = new Vector2D(
	    		position.getX()+velocity.getX()*deltaMs,
	    		position.getY()+velocity.getY()*deltaMs);
	    sync(position);
	}
}