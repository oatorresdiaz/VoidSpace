package rbadia.voidspace.sounds;

import java.applet.Applet;
import java.applet.AudioClip;

import rbadia.voidspace.main.GameScreen;

/**
 * Manages and plays the game's sounds.
 */
public class SoundManager {
	private static final boolean SOUND_ON = true;

    private AudioClip shipExplosionSound = Applet.newAudioClip(GameScreen.class.getResource(
    "/rbadia/voidspace/sounds/shipExplosion.wav"));
    private AudioClip bulletSound = Applet.newAudioClip(GameScreen.class.getResource(
    "/rbadia/voidspace/sounds/laser.wav"));
    private AudioClip tieFighterBlast = Applet.newAudioClip(GameScreen.class.getResource(
    	    "/rbadia/voidspace/sounds/tieFighterBlast.wav"));
    private AudioClip xWingBlast = Applet.newAudioClip(GameScreen.class.getResource(
    	    "/rbadia/voidspace/sounds/xWingBlast.wav"));
    private AudioClip starWarsScore2 = Applet.newAudioClip(GameScreen.class.getResource(
    	    "/rbadia/voidspace/sounds/starWarsScore2.wav"));
    
    /**
     * Plays sound for bullets fired by the ship.
     */
    public void playBulletSound(){
    	if(SOUND_ON){
    		new Thread(new Runnable(){
    			public void run() {
    				xWingBlast.play();
    			}
    		}).start();
    	}
    }
    
    public void playTieFighterBlast(){
    	if(SOUND_ON){
    		new Thread(new Runnable(){
    			public void run() {
    				tieFighterBlast.play();
    			}
    		}).start();
    	}
    }
    
    public void playStarWarsScore2(){
    	if(SOUND_ON){
    		new Thread(new Runnable(){
    			public void run() {
    				starWarsScore2.play();
    			}
    		}).start();
    	}
    }
    
    /**
     * Plays sound for ship explosions.
     */
    public void playShipExplosionSound(){
    	if(SOUND_ON){
    		new Thread(new Runnable(){
    			public void run() {
    				shipExplosionSound.play();
    			}
    		}).start();
    	}
    }
    
    /**
     * Plays sound for asteroid explosions.
     */
    public void playAsteroidExplosionSound(){
		// play sound for asteroid explosions
    	if(SOUND_ON){
    		
    	}
    }
}
