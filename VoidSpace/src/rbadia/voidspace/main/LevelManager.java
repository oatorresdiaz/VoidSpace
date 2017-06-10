package rbadia.voidspace.main;

/**
 * Handles the levels: what happens during the game 
 */
public class LevelManager {
	
	private int asteroidsDestroyed = 0; //Set initial value to 0 to avoid errors
	private int asteroidsOnScreen = 0;
	private int originalAsteroidsDestroyedValue = 0;
	private int enemyShipsDestroyed = 0;
	private int originalEnemyShipsDestroyed = 0;
	private int level = 1;
	
	/**
	 * This method returns the current level in the game depending on the amount of asteroids and enemy ships destroyed	
	 * @return level
	 */
	public int level(){
		if(this.asteroidsDestroyed - this.originalAsteroidsDestroyedValue == 5){
			this.originalAsteroidsDestroyedValue = this.asteroidsDestroyed;
			level = level + 1;
		}
		else if(this.enemyShipsDestroyed - this.originalEnemyShipsDestroyed == 1){
			this.originalEnemyShipsDestroyed = this.enemyShipsDestroyed;
			level = level + 1;
		}
		return level;
	}
	/**
	 * This method resets all the variables to their default value 
	 */
	public void reset(){
		this.asteroidsOnScreen = 1;
		this.asteroidsDestroyed = this.originalAsteroidsDestroyedValue;
		this.enemyShipsDestroyed = this.originalEnemyShipsDestroyed;
		this.level = 1;
	}
	
	/**
	 * This method returns an int what determines how 'diagonally' the asteroids will travel depending on the current level
	 * @return int value
	 */
	public int getDiagonalMotion(){
		if(this.level() >= 2){
			if(this.level()%2 == 0){
				return 2;
			}
			else{
				return -2;
			}
		}
		else{
			return 0;
		}
	}
	
	/**
	 * This method returns the speed of the enemy ship depending on the current level
	 * @return int value
	 */
	public int enemyShipSpeed(){
		if(this.level() == 2){
			return 3;
		}
		else if(this.level() == 3){
			return 4;
		}
		else if(this.level() >= 4){
			return 5;
		}
		else{
			return 2;
		}
	}
	
	/**
	 * This method returns the amount of asteroids that will appear on screen
	 * @return int value
	 */
	public int getAsteroidsOnScreen(){
		this.asteroidsOnScreen = this.asteroidsDestroyed + 1; //For each asteroid destroyed, add one more
		if(this.asteroidsOnScreen >= 10){ //The maximum asteroids on screen 
			this.asteroidsOnScreen = 10;
		}
		return this.asteroidsOnScreen;
	}
	
	/**
	 * Increases the amount of asteroids destroyed
	 */
	public void asteroidDestroyed(){
		this.asteroidsDestroyed++;
	}
	
	/**
	 * Increases the amount of enemy ships destroyed
	 */
	public void enemyShipDestroyed(){
		this.enemyShipsDestroyed++;
	}
}
