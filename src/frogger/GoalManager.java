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

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import jig.engine.util.Vector2D;


public class GoalManager {
	
	final static int MAX_NUM_OF_GOALS = 6;
	
	private List<Goal> goals;
	private Random r;

	protected boolean showingBonus = false;
	
	private int bonusRateMs = 5000;
	private int bonusShowMs = 5000;
	private int dRMs = 0;
	private int dSMs = 0;
	
	public GoalManager() {
		goals = new LinkedList<Goal>();
		r = new Random(System.currentTimeMillis());
		init(1);
	}
	
	
	/**
	 * First level only has 2 goals
	 * Second - 4 goals
	 * All others have standard 6 goals
	 * @param level
	 */
	public void init(final int level) {
		
		goals.clear();
		
		//if (level < 3) {
			switch(level){
				case 1:
					goals.add(new Goal(new Vector2D(5*32,32)));
					goals.add(new Goal(new Vector2D(7*32,32)));
					break;
				case 2:
				default:
					goals.add(new Goal(new Vector2D(5*32,32)));
					goals.add(new Goal(new Vector2D(7*32,32)));
					goals.add(new Goal(new Vector2D(3*32,32)));
					goals.add(new Goal(new Vector2D(9*32,32)));
					break;
			}
			return;
		//}
		
		//for (int i=0; i<MAX_NUM_OF_GOALS; i++)
		//	goals.add(new Goal(i));		
	}
	
	/**
	 * 'goals' is a protected
	 * @return
	 */
	public List<Goal> get() {
		return goals;
	}
	
	/**
	 * getUnreached
	 * @return - list of goals currently haven't been reached
	 */
	public List<Goal> getUnreached() {
		List<Goal> l = new LinkedList<Goal>();
		for (Goal g : goals)
			if (!g.isReached)
				l.add(g);
		
		return l;		
	}
	
	/**
	 * Based on internal timer, display bonus at a goal
	 * that hasn't been reached for a duration specified by bonusShowMs
	 */
	public void doBonusCheck() {
		if (!showingBonus && dRMs > bonusRateMs) {
			dSMs = 0;
			showingBonus = true;
			List<Goal> l = getUnreached();
			l.get(r.nextInt(l.size())).setBonus(true);
		}
		
		if (showingBonus && dSMs > bonusShowMs) {
			dRMs = 0;
			showingBonus = false;
			for (Goal g : goals)
				if (!g.isReached)
					g.setBonus(false);		
		}		
	}
	
	public void update(long deltaMs) {
		dRMs += deltaMs;
		dSMs += deltaMs;
		doBonusCheck();
	}
}
