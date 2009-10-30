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
import jig.engine.physics.vpe.VanillaSphere;
import jig.engine.util.Vector2D;


public class CollisionObject extends VanillaSphere {

	public CollisionObject(Vector2D pos) {
		super("col");
		setPosition(pos);
	}
	
	public CollisionObject(String name, Vector2D pos) {
		super(name);
		setPosition(pos);
	}
	
	/**
	 * Depending on the collision sphere, we offset it's position so
	 * that it appears in the middle of the object
	 */
	public void setPosition(Vector2D pos) {
		double dX = 16 - getRadius();
		double dY = -getRadius() +16;
		position = new Vector2D(pos.getX()+dX, pos.getY()+dY);
	}
	
	public void update(long deltaMs) {
		;
	}
}