package rbadia.voidspace.main;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Timer;

import rbadia.voidspace.model.Asteroid;
import rbadia.voidspace.model.Bullet;
import rbadia.voidspace.model.EnemyShip;
import rbadia.voidspace.model.Ship;
import rbadia.voidspace.sounds.SoundManager;


/**
 * Handles general game logic and status.
 */
public class GameLogic {
	private GameScreen gameScreen;
	private GameStatus status;
	private SoundManager soundMan;
	
	private Ship ship;
	private EnemyShip enemyShip;
	private Asteroid asteroid;
	private List<Asteroid> asteroids;
	private List<Bullet> bullets;
	private List<Bullet> enemyBullets;
	
	private int MAX_ASTEROIDS_ON_SCREEN = 10;
	
	/**
	 * Craete a new game logic handler
	 * @param gameScreen the game screen
	 */
	public GameLogic(GameScreen gameScreen){
		this.gameScreen = gameScreen;
		
		// initialize game status information
		status = new GameStatus();
		// initialize the sound manager
		soundMan = new SoundManager();
		// init some variables
		bullets = new ArrayList<Bullet>();
		enemyBullets = new ArrayList<Bullet>();
		asteroids = new ArrayList<Asteroid>();
		
	}

	/**
	 * Returns the game status
	 * @return the game status 
	 */
	public GameStatus getStatus() {
		return status;
	}

	public SoundManager getSoundMan() {
		return soundMan;
	}

	public GameScreen getGameScreen() {
		return gameScreen;
	}

	/**
	 * Prepare for a new game.
	 */
	public void newGame(){
		status.setGameStarting(true);
		
		// init game variables
		bullets = new ArrayList<Bullet>();

		status.setShipsLeft(3);
		status.setGameOver(false);
		status.setAsteroidsDestroyed(0);
		status.setNewAsteroid(false);
				
		// init the ship and the asteroid
        newShip(gameScreen);
        newAsteroid(gameScreen);
        newEnemyShip(gameScreen);
        
        
        // prepare game screen
        gameScreen.doNewGame();
        
        // delay to display "Get Ready" message for 1.5 seconds
		Timer timer = new Timer(1500, new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				status.setGameStarting(false);
				status.setGameStarted(true);
			}
		});
		timer.setRepeats(false);
		timer.start();
	}
	
	/**
	 * Check game or level ending conditions.
	 */
	public void checkConditions(){
		// check game over conditions
		if(!status.isGameOver() && status.isGameStarted()){
			if(status.getShipsLeft() == 0){
				gameOver();
			}
		}
	}
	
	/**
	 * Actions to take when the game is over.
	 */
	public void gameOver(){
		status.setGameStarted(false);
		status.setGameOver(true);
		gameScreen.doGameOver();
		
        // delay to display "Game Over" message for 3 seconds
		Timer timer = new Timer(3000, new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				status.setGameOver(false);
			}
		});
		timer.setRepeats(false);
		timer.start();
	}
	
	/**
	 * Fire a bullet from ship.
	 */
	public void fireBullet(){
		Bullet bullet = new Bullet(ship);
		bullets.add(bullet);
		soundMan.playBulletSound();
	}
	
	/**
	 * Fire a bullet from enemy ship.
	 */
	public void fireEnemyBullet(){
		Bullet enemyBullet = new Bullet(enemyShip);
		enemyBullets.add(enemyBullet);
		soundMan.playTieFighterBlast();;
	}
	
	/**
	 * Move a bullet once fired.
	 * @param bullet the bullet to move
	 * @return if the bullet should be removed from screen
	 */
	public boolean moveBullet(Bullet bullet){
		if(bullet.getY() - bullet.getSpeed() >= 0){
			bullet.translate(0, -bullet.getSpeed());
			return false;
		}
		else{
			return true;
		}
	}
	
	/**
	 * Move a bullet once fired.
	 * @param enemyBullet the enemy bullet to move
	 * @return if the enemyBullet should be removed from screen
	 */
	public boolean moveEnemyBullet(Bullet enemyBullet, LevelManager level, Ship ship){
		if(level.level()%5 == 0){
			if(enemyBullet.getY() - enemyBullet.getSpeed() >= 0){
				int xDir;
				int yDir;
				if(ship.x > enemyBullet.x){
					xDir = 1;
				}
				else{
					xDir = -1;
				}
				if(ship.y > enemyBullet.y){
					yDir = 1;
				}
				else{
					yDir = -1;
				}
				enemyBullet.translate(xDir, yDir);
				return false;
			}
			else{
				return true;
			}
		}
		else{
			if(enemyBullet.getY() - enemyBullet.getSpeed() >= 0){
				enemyBullet.translate(0, -enemyBullet.getSpeed());
				return false;
			}
			else{
				return true;
			}
		}
	}
	
	/**
	 * Create a new ship (and replace current one).
	 */
	public Ship newShip(GameScreen screen){
		this.ship = new Ship(screen);
		return ship;
	}
	
	/**
	 * Create a new enemy ship (and replace current one).
	 */
	public EnemyShip newEnemyShip(GameScreen screen){
		this.enemyShip = new EnemyShip(screen);
		return enemyShip;
	}
	
	/**
	 * Create a new asteroid.
	 */
	public Asteroid newAsteroid(GameScreen screen){ 
		this.asteroid = new Asteroid(screen);
		return asteroid;
	}
	
	/**
	 * Returns the ship.
	 * @return the ship
	 */
	public Ship getShip() {
		return ship;
	}
	
	/**
	 * Returns the enemy ship.
	 * @return the enemyShip
	 */
	public EnemyShip getEnemyShip(){
		return enemyShip;
	}

	/**
	 * Returns the asteroid.
	 * @return the asteroid
	 */
	public Asteroid getAsteroid() {
		return asteroid;
	}
	
	/**
	 * Returns the asteroid list.
	 * @return the asteroids
	 */
	public List<Asteroid> getAsteroids(){
		for(int i = 0; i < MAX_ASTEROIDS_ON_SCREEN; i++){
			asteroids.add(new Asteroid(gameScreen));
		}
		return asteroids;
	}
	
	/**
	 * Returns the list of bullets.
	 * @return the list of bullets
	 */
	public List<Bullet> getBullets() {
		return bullets;
	}
	
	/**
	 * Returns the list of enemy bullets.
	 * @return the list of enemyBullets
	 */
	public List<Bullet> getEnemyBullets() {
		return enemyBullets;
	}
}
