package rbadia.voidspace.model;

import java.awt.Rectangle;

import rbadia.voidspace.main.GameScreen;

/**
 * Represents a enemy ship/space craft.
 *
 */
public class EnemyShip extends Rectangle {
	private static final long serialVersionUID = 1L;
	
	public static final int DEFAULT_SPEED = 5;
	
	private int shipWidth = 25;
	private int shipHeight = 25;
	private int speed = DEFAULT_SPEED;
	
	/**
	 * Creates a new ship at the default initial location. 
	 * @param screen the game screen
	 */
	public EnemyShip(GameScreen screen){
		this.setLocation((screen.getWidth() - shipWidth)/2, 50);
		this.setSize(shipWidth, shipHeight);
	}
	
	/**
	 * Get the default ship width
	 * @return the default ship width
	 */
	public int getShipWidth() {
		return shipWidth;
	}
	
	/**
	 * Get the default ship height
	 * @return the default ship height
	 */
	public int getShipHeight() {
		return shipHeight;
	}
	
	/**
	 * Returns the current ship speed
	 * @return the current ship speed
	 */
	public int getSpeed() {
		return speed;
	}
	
	/**
	 * Set the current ship speed
	 * @param speed the speed to set
	 */
	public void setSpeed(int speed) {
		this.speed = speed;
	}
	
	/**
	 * Returns the default ship speed.
	 * @return the default ship speed
	 */
	public int getDefaultSpeed(){
		return DEFAULT_SPEED;
	}
	
}
