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

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.AffineTransform;
import java.util.List;

import jig.engine.FontResource;
import jig.engine.ImageResource;
import jig.engine.RenderingContext;
import jig.engine.ResourceFactory;
import jig.engine.ViewableLayer;

public class FroggerUI implements ViewableLayer {
	List<ImageResource> heart = ResourceFactory.getFactory().getFrames(
			Main.SPRITE_SHEET + "#heart");
	List<ImageResource> gameOver = ResourceFactory.getFactory().getFrames(
			Main.SPRITE_SHEET + "#gameover");
	List<ImageResource> levelFinish = ResourceFactory.getFactory().getFrames(
			Main.SPRITE_SHEET + "#level_finish");
	List<ImageResource> introTitle = ResourceFactory.getFactory().getFrames(
			Main.SPRITE_SHEET + "#splash");
	List<ImageResource> instructions = ResourceFactory.getFactory().getFrames(
			Main.SPRITE_SHEET + "#help");
	
	FontResource font = ResourceFactory.getFactory().getFontResource(
			new Font("Sans Serif", Font.BOLD, 14), Color.white, null );

	FontResource fontBlack = ResourceFactory.getFactory().getFontResource(
			new Font("Sans Serif", Font.BOLD, 14), Color.black, null );
	
	Main game;
	
	public FroggerUI(final Main g) {
		game = g;
	}
	
	
	public void render(RenderingContext rc) {
		
		font.render("Time: " + game.levelTimer, rc, 
				AffineTransform.getTranslateInstance(180, 7));
		
		font.render("Score: " + game.GameScore, rc, 
				AffineTransform.getTranslateInstance(310, 7));
		
		if (game.GameLives > 0) {
			int dx = 0;
			
			// if player has more than 10 lives, draw only 10 hearts
			int maxHearts = game.GameLives;
			if (maxHearts > 10)
				maxHearts = 10;
			else 
				maxHearts = game.GameLives;
			
			for (int i = 0; i < maxHearts; i++ ) {
				heart.get(0).render(rc, 
						AffineTransform.getTranslateInstance(dx+8, 8));
				dx = 16 * (i + 1);
			}
		}

		font.render("L" + game.GameLevel, rc, 
				AffineTransform.getTranslateInstance(270, 7));
		
		if (game.GameState == Main.GAME_INTRO) {
			   introTitle.get(0).render(rc, 
						AffineTransform.getTranslateInstance(
								(Main.WORLD_WIDTH - introTitle.get(0).getWidth())/2, 150));
			   return;
		}
		
		if (game.GameState == Main.GAME_INSTRUCTIONS) {
			   instructions.get(0).render(rc, 
						AffineTransform.getTranslateInstance(
								(Main.WORLD_WIDTH - instructions.get(0).getWidth())/2, 100));
			   return;			
		}
		
		if (game.GameState == Main.GAME_OVER) {
		   gameOver.get(0).render(rc, 
					AffineTransform.getTranslateInstance(
							(Main.WORLD_WIDTH - gameOver.get(0).getWidth())/2, 150));
		   return;
		}
		
		if (game.GameState == Main.GAME_FINISH_LEVEL) {
			 levelFinish.get(0).render(rc, 
						AffineTransform.getTranslateInstance(
								(Main.WORLD_WIDTH - levelFinish.get(0).getWidth())/2, 150));		 
		}
	}

	public void update(long deltaMs) {
	}

	public boolean isActive() {
		return true;
	}

	public void setActivation(boolean a) {
		// can't turn this layer off!
	}
	
}