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

public class LongLog extends MovingEntity {
	
	public static int LENGTH = 32*4;
	
	public LongLog (Vector2D pos, Vector2D v) {
		super(Main.SPRITE_SHEET + "#longlog");
		position = pos;
		Vector2D posSphere1 = position;
		Vector2D posSphere2 = new Vector2D(position.getX()+32*1, position.getY());
		Vector2D posSphere3 = new Vector2D(position.getX()+32*2, position.getY());
		Vector2D posSphere4 = new Vector2D(position.getX()+32*3, position.getY());
		collisionObjects.add(new CollisionObject("colSmall",posSphere1));
		collisionObjects.add(new CollisionObject("colSmall",posSphere2));
		collisionObjects.add(new CollisionObject("colSmall",posSphere3));
		collisionObjects.add(new CollisionObject("colSmall",posSphere4));
		velocity = v;
		
		if (v.getX() < 0)
			setFrame(1);
		else
			setFrame(0);
	}
}