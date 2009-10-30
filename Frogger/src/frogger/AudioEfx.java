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

import java.util.List;
import java.util.Random;
import java.util.LinkedList;

import jig.engine.ResourceFactory;
import jig.engine.audio.AudioState;
import jig.engine.audio.jsound.AudioClip;
import jig.engine.audio.jsound.AudioStream;

/**
 * Controls the audio effects
 * 
 * @author vitaliy
 *
 */
public class AudioEfx {

	// These are referenced as to when to play the sound effects
	FroggerCollisionDetection fc;
	Frogger frog;
	
	public Random rand = new Random(System.currentTimeMillis());
	
	// Background music
	private AudioStream gameMusic;
	
	public static final String A_FX_PATH = Main.RSC_PATH + "ambient_fx/";
	
	public static AudioClip frogJump = ResourceFactory.getFactory().getAudioClip(
			Main.RSC_PATH + "jump.wav");
	
	public static AudioClip frogDie = ResourceFactory.getFactory().getAudioClip(
			Main.RSC_PATH + "frog_die.ogg");	

	public static AudioClip frogGoal = ResourceFactory.getFactory().getAudioClip(
			Main.RSC_PATH + "goal.ogg");
	
	public static AudioClip levelGoal = ResourceFactory.getFactory().getAudioClip(
			Main.RSC_PATH + "level_goal.ogg");

	public static AudioClip wind = ResourceFactory.getFactory().getAudioClip(
			Main.RSC_PATH + "wind.ogg");
	
	public static AudioClip heat = ResourceFactory.getFactory().getAudioClip(
			Main.RSC_PATH + "match.ogg");
	
	public static AudioClip bonus = ResourceFactory.getFactory().getAudioClip(
			Main.RSC_PATH + "bonus.ogg");	
	
	public static AudioClip siren = ResourceFactory.getFactory().getAudioClip(
			A_FX_PATH + "siren.ogg");
	
	// one effect is randomly picked from road_effects or water_effects every couple of seconds
	private List<AudioClip> road_effects = new LinkedList<AudioClip>();
	private List<AudioClip> water_effects = new LinkedList<AudioClip>();
	
	private int effectsDelay = 3000;
	private int deltaT = 0;
	
	/**
	 * In order to know when to play-back certain effects, we track the state of 
	 * collision detector and Frogger
	 * @param f
	 * @param frg
	 */
	public AudioEfx(FroggerCollisionDetection f, Frogger frg) {
		fc = f;
		frog = frg;
		
		road_effects.add(ResourceFactory.getFactory().getAudioClip(A_FX_PATH + "long-horn.ogg"));
	    road_effects.add(ResourceFactory.getFactory().getAudioClip(A_FX_PATH + "car-pass.ogg"));
		road_effects.add(ResourceFactory.getFactory().getAudioClip(A_FX_PATH + "siren.ogg"));

		water_effects.add(ResourceFactory.getFactory().getAudioClip(A_FX_PATH + "water-splash.ogg"));
	    water_effects.add(ResourceFactory.getFactory().getAudioClip(A_FX_PATH + "splash.ogg"));
		water_effects.add(ResourceFactory.getFactory().getAudioClip(A_FX_PATH + "frog.ogg"));

		gameMusic = new AudioStream(Main.RSC_PATH + "bg_music.ogg");
	}
	
	public void playGameMusic() {
	    gameMusic.loop(0.2, 0);
	}
	
	public void playCompleteLevel() {
		gameMusic.pause();
		levelGoal.play(2.0);
	}
	
	public void playRandomAmbientSound(final long deltaMs) {
		deltaT += deltaMs;
		
		if (deltaT > effectsDelay && fc.isOnRoad()) {
			deltaT = 0;
			road_effects.get(rand.nextInt(road_effects.size())).play(0.2);
		}
		
		if (deltaT > effectsDelay && fc.isInRiver()) {
			deltaT = 0;
			water_effects.get(rand.nextInt(road_effects.size())).play(0.2);
		}
	}
	
	public void update(final long deltaMs) {
		playRandomAmbientSound(deltaMs);
		
		if (frog.isAlive && (gameMusic.getState() == AudioState.PAUSED))
			gameMusic.resume();
		
		if (!frog.isAlive && (gameMusic.getState() == AudioState.PLAYING))
			gameMusic.pause();	

	}

}
