package rbadia.voidspace.main;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JLabel;
import javax.swing.JPanel;

import rbadia.voidspace.graphics.GraphicsManager;
import rbadia.voidspace.model.Asteroid;
import rbadia.voidspace.model.Bullet;
import rbadia.voidspace.model.EnemyShip;
import rbadia.voidspace.model.Ship;
import rbadia.voidspace.sounds.SoundManager;

/**
 * Main game screen. Handles all game graphics updates and some of the game logic.
 */
public class GameScreen extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private BufferedImage backBuffer;
	private Graphics2D g2d;
	
	private static final int NEW_SHIP_DELAY = 500;
	private static final int NEW_ENEMYSHIP_DELAY = 500;
	private static final int NEW_ASTEROID_DELAY = 500;
	
	private long lastShipTime;
	private long lastEnemyShipTime;
	private long lastAsteroidTime;
	
	private Rectangle asteroidExplosion;
	private Rectangle shipExplosion;
	
	private JLabel shipsValueLabel;
	private JLabel destroyedValueLabel;
	private JLabel levelValueLabel;

	private Random rand;
	
	private Font originalFont;
	private Font bigFont;
	private Font biggestFont;
	
	private GameStatus status;
	private SoundManager soundMan;
	private GraphicsManager graphicsMan;
	private GameLogic gameLogic;
	
	public LevelManager level = new LevelManager();

	/**
	 * This method initializes 
	 * 
	 */
	public GameScreen() {
		super();
		// initialize random number generator
		rand = new Random();
		
		initialize();
		
		// init graphics manager
		graphicsMan = new GraphicsManager();
		
		// init back buffer image
		backBuffer = new BufferedImage(500, 400, BufferedImage.TYPE_INT_RGB);
		g2d = backBuffer.createGraphics();

	}

	/**
	 * Initialization method (for VE compatibility).
	 */
	private void initialize() {
		// set panel properties
        this.setSize(new Dimension(500, 400));
        this.setPreferredSize(new Dimension(500, 400));
        this.setBackground(Color.BLACK);
	}

	/**
	 * Update the game screen.
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		// draw current backbuffer to the actual game screen
		g.drawImage(backBuffer, 0, 0, this);
	}
	
	int translation = 2;
	int astIndex;
	
	/**
	 * Update the game screen's backbuffer image.
	 */
	public void updateScreen(){
		Ship ship = gameLogic.getShip();
		EnemyShip enemyShip = gameLogic.getEnemyShip();
		Asteroid asteroid = gameLogic.getAsteroid();
		List<Asteroid> asteroids = gameLogic.getAsteroids();
		List<Bullet> bullets = gameLogic.getBullets();
		List<Bullet> enemyBullets = gameLogic.getEnemyBullets();
		
		// set orignal font - for later use
		if(this.originalFont == null){
			this.originalFont = g2d.getFont();
			this.bigFont = originalFont;
		}
		
		// erase screen
		g2d.setPaint(Color.BLACK);
		g2d.fillRect(0, 0, getSize().width, getSize().height);

		// draw 50 random stars
		drawStars(50);
		
		// if the game is starting, draw "Get Ready" message
		if(status.isGameStarting()){
			soundMan.playStarWarsScore2(); //Play background music
			level.reset();
			for(int i = 0; i<level.getAsteroidsOnScreen(); i++){
				asteroids.get(i).setLocation(this.getWidth() + 10000, this.getHeight() + 10000);
			}
			status.setNewAsteroid(true);
			drawGetReady();
			return;
		}
		
		// if the game is over, draw the "Game Over" message
		if(status.isGameOver()){
			// draw the message
			drawGameOver();
			long currentTime = System.currentTimeMillis();
			// draw the explosions until their time passes
			if((currentTime - lastAsteroidTime) < NEW_ASTEROID_DELAY){
				graphicsMan.drawAsteroidExplosion(asteroidExplosion, g2d, this);
			}
			if((currentTime - lastShipTime) < NEW_SHIP_DELAY){
				graphicsMan.drawShipExplosion(shipExplosion, g2d, this);
			}
			return;
		}
		
		// the game has not started yet
		if(!status.isGameStarted()){
			// draw game title screen
			initialMessage();
			return;
		}

		// draw asteroid
		if(!status.isNewAsteroid()){
			// draw the asteroid until it reaches the bottom of the screen
			for(int i = 0; i<level.getAsteroidsOnScreen(); i++){
				this.translateAsteroid(asteroids, i);
			}
		}
		else{
			long currentTime = System.currentTimeMillis();
			for(int i = 0; i<level.getAsteroidsOnScreen(); i++){
				if(!asteroids.get(astIndex).equals(asteroids.get(i))){
					this.translateAsteroid(asteroids, i);
				}
			}
			if((currentTime - lastAsteroidTime) > NEW_ASTEROID_DELAY){
				// draw a new asteroid
				lastAsteroidTime = currentTime;
				status.setNewAsteroid(false);
				asteroids.get(astIndex).setLocation(rand.nextInt(getWidth() - asteroids.get(astIndex).width), 0);
			}
			else{
				// draw explosion
				graphicsMan.drawAsteroidExplosion(asteroidExplosion, g2d, this);
			}
		}
		
		// draw bullets
		for(int i=0; i<bullets.size(); i++){
			Bullet bullet = bullets.get(i);
			graphicsMan.drawBullet(bullet, g2d, this);
			boolean remove = gameLogic.moveBullet(bullet);
			if(remove){
				bullets.remove(i);
				i--;
			}
		}
		
		// draw enemy bullets
		for(int i=0; i<enemyBullets.size(); i++){
			Bullet enemyBullet = enemyBullets.get(i);
			graphicsMan.drawEnemyBullet(enemyBullet, g2d, this);
			boolean remove = gameLogic.moveEnemyBullet(enemyBullet, level, ship);
			if(remove){
				bullets.remove(i);
				i--;
			}
		}
		
		//Check bullet-asteroid collision
		for(int i=0; i<bullets.size(); i++){
			Bullet bullet = bullets.get(i);
			for(int j = 0; j<level.getAsteroidsOnScreen(); j++){
				if(asteroids.get(j).intersects(bullet)){
					this.destroyAsteroid(asteroids, j);
					// remove bullet
					bullets.remove(i);
					break;
				}
			}
		}
		
		//Check bullet-enemyShip collisions
		for(int i=0; i<bullets.size(); i++){
			Bullet bullet = bullets.get(i);
				if(bullet.intersects(enemyShip)){
				// "remove" enemy ship
		        shipExplosion = new Rectangle(
		        		enemyShip.x,
		        		enemyShip.y,
		        		enemyShip.width,
		        		enemyShip.height);
				enemyShip.setLocation(this.getWidth() + enemyShip.width, -enemyShip.height);
				status.setNewEnemyShip(true);
				lastEnemyShipTime = System.currentTimeMillis();
				
				// play asteroid explosion sound
				soundMan.playShipExplosionSound();
				
				// remove bullet
				bullets.remove(i);
				
				level.enemyShipDestroyed();
				
				break;
			}
		}
		
		//ship-enemy bullet collisions
		for(int i=0; i<enemyBullets.size(); i++){
			Bullet enemyBullet = enemyBullets.get(i);
			if(ship.intersects(enemyBullet)){
				// decrease number of ships left
				status.setShipsLeft(status.getShipsLeft() - 1);
				
				// "remove" ship
				shipExplosion = new Rectangle(
		        		ship.x,
		        		ship.y,
		        		ship.width,
		        		ship.height);
				ship.setLocation(this.getWidth() + ship.width, -ship.height - 100);
				status.setNewShip(true);
				lastShipTime = System.currentTimeMillis();
				
				// play asteroid explosion sound
				soundMan.playShipExplosionSound();
				
				// remove bullet
				enemyBullets.remove(i);
				
				break;
			}
		}
		
		// draw ship
		if(!status.isNewShip()){
			// draw it in its current location
			graphicsMan.drawShip(ship, g2d, this);
		}
		else{
			// draw a new one
			long currentTime = System.currentTimeMillis();
			if((currentTime - lastShipTime) > NEW_SHIP_DELAY){
				lastShipTime = currentTime;
				status.setNewShip(false);
				ship = gameLogic.newShip(this);
			}
			else{
				// draw explosion
				graphicsMan.drawShipExplosion(shipExplosion, g2d, this);
			}
		}
		
		// check ship-asteroid collisions 
		for(int i = 0; i<level.getAsteroidsOnScreen(); i++){
			if(asteroids.get(i).intersects(ship)){
				this.shipCrashAsteroid(asteroids, i, ship);
			}
		}
		
		//check ship-enemy ship collisions
		if (ship.intersects(enemyShip)){
			// decrease number of ships left
			status.setShipsLeft(status.getShipsLeft() - 1);
			
			// "remove" ship
	        shipExplosion = new Rectangle(
	        		ship.x,
	        		ship.y,
	        		ship.width,
	        		ship.height);
			ship.setLocation(this.getWidth() + ship.width, -ship.height);
			status.setNewShip(true);
			lastShipTime = System.currentTimeMillis();
			
			// "remove" enemy ship
	        shipExplosion = new Rectangle(
	        		enemyShip.x,
	        		enemyShip.y,
	        		enemyShip.width,
	        		enemyShip.height);
			enemyShip.setLocation(this.getWidth() + enemyShip.width, -enemyShip.height - ship.height);
			status.setNewEnemyShip(true);
			lastEnemyShipTime = System.currentTimeMillis();
			
			// play ship explosion sound
			soundMan.playShipExplosionSound();
		}
		
		//Draw enemy-ship
		if(level.level() >= 3){
			if(!status.isNewEnemyShip()){
				Random ran = new Random();
				// draw it in its current location
				if(enemyShip.x >= this.getWidth() - enemyShip.getWidth()){
					translation = -level.enemyShipSpeed();
					enemyShip.y = enemyShip.y + 1;
				}
				else if(enemyShip.x <= 0){
					translation = level.enemyShipSpeed();
					enemyShip.y = enemyShip.y + 1;
				}
				
				if(ran.nextInt(50) > 48){
					gameLogic.fireEnemyBullet();	
				}
				
				enemyShip.x = enemyShip.x + translation;
				graphicsMan.drawEnemyShip(enemyShip, g2d, this);
			}
			else{
				// draw a new one
				long currentTime = System.currentTimeMillis();
				if((currentTime - lastEnemyShipTime) > NEW_ENEMYSHIP_DELAY){
					lastEnemyShipTime = currentTime;
					status.setNewEnemyShip(false);
					enemyShip = gameLogic.newEnemyShip(this);
					enemyShip.setLocation(rand.nextInt(this.getWidth() - enemyShip.width), rand.nextInt(this.getHeight()/2) + enemyShip.height);
				}
				else{
					// draw explosion
					graphicsMan.drawShipExplosion(shipExplosion, g2d, this);
				}
			}
		}
		
		// update asteroids destroyed label
		destroyedValueLabel.setText(Long.toString(status.getAsteroidsDestroyed()*100 + status.getEnemyShipsDestroyed()*500));
		
		// update ships left label
		shipsValueLabel.setText(Integer.toString(status.getShipsLeft()));
		
		//update level label
		levelValueLabel.setText(Integer.toString(level.level()));
	}

	/**
	 * Draws the "Game Over" message.
	 */
	private void drawGameOver() {
		String gameOverStr = "GAME OVER";
		Font currentFont = biggestFont == null? bigFont : biggestFont;
		float fontSize = currentFont.getSize2D();
		bigFont = currentFont.deriveFont(fontSize + 1).deriveFont(Font.BOLD);
		FontMetrics fm = g2d.getFontMetrics(bigFont);
		int strWidth = fm.stringWidth(gameOverStr);
		if(strWidth > this.getWidth() - 10){
			biggestFont = currentFont;
			bigFont = biggestFont;
			fm = g2d.getFontMetrics(bigFont);
			strWidth = fm.stringWidth(gameOverStr);
		}
		int ascent = fm.getAscent();
		int strX = (this.getWidth() - strWidth)/2;
		int strY = (this.getHeight() + ascent)/2;
		g2d.setFont(bigFont);
		g2d.setPaint(Color.WHITE);
		g2d.drawString(gameOverStr, strX, strY);
	}
	
	/**
	 * Draws the initial "Get Ready!" message.
	 */
	private void drawGetReady() {
		String readyStr = "Get Ready!";
		g2d.setFont(originalFont.deriveFont(originalFont.getSize2D() + 1));
		FontMetrics fm = g2d.getFontMetrics();
		int ascent = fm.getAscent();
		int strWidth = fm.stringWidth(readyStr);
		int strX = (this.getWidth() - strWidth)/2;
		int strY = (this.getHeight() + ascent)/2;
		g2d.setPaint(Color.WHITE);
		g2d.drawString(readyStr, strX, strY);
	}

	/**
	 * Draws the specified number of stars randomly on the game screen.
	 * @param numberOfStars the number of stars to draw
	 */
	private void drawStars(int numberOfStars) {
		g2d.setColor(Color.WHITE);
		for(int i=0; i<numberOfStars; i++){
			int x = (int)(Math.random() * this.getWidth());
			int y = (int)(Math.random() * this.getHeight());
			g2d.drawLine(x, y, x, y);
		}
	}

	/**
	 * Display initial game title screen.
	 */
	private void initialMessage() {
		String gameTitleStr = "Void Space";
		
		Font currentFont = biggestFont == null? bigFont : biggestFont;
		float fontSize = currentFont.getSize2D();
		bigFont = currentFont.deriveFont(fontSize + 1).deriveFont(Font.BOLD).deriveFont(Font.ITALIC);
		FontMetrics fm = g2d.getFontMetrics(bigFont);
		int strWidth = fm.stringWidth(gameTitleStr);
		if(strWidth > this.getWidth() - 10){
			bigFont = currentFont;
			biggestFont = currentFont;
			fm = g2d.getFontMetrics(currentFont);
			strWidth = fm.stringWidth(gameTitleStr);
		}
		g2d.setFont(bigFont);
		int ascent = fm.getAscent();
		int strX = (this.getWidth() - strWidth)/2;
		int strY = (this.getHeight() + ascent)/2 - ascent;
		g2d.setPaint(Color.YELLOW);
		g2d.drawString(gameTitleStr, strX, strY);
		
		g2d.setFont(originalFont);
		fm = g2d.getFontMetrics();
		String newGameStr = "Press <Space> to Start a New Game.";
		strWidth = fm.stringWidth(newGameStr);
		strX = (this.getWidth() - strWidth)/2;
		strY = (this.getHeight() + fm.getAscent())/2 + ascent + 16;
		g2d.setPaint(Color.WHITE);
		g2d.drawString(newGameStr, strX, strY);
		
		fm = g2d.getFontMetrics();
		String exitGameStr = "Press <Esc> to Exit the Game.";
		strWidth = fm.stringWidth(exitGameStr);
		strX = (this.getWidth() - strWidth)/2;
		strY = strY + 16;
		g2d.drawString(exitGameStr, strX, strY);
	}
	
	/**
	 * Prepare screen for game over.
	 */
	public void doGameOver(){
		shipsValueLabel.setForeground(new Color(128, 0, 0));
	}
	
	/**
	 * Prepare screen for a new game.
	 */
	public void doNewGame(){		
		lastAsteroidTime = -NEW_ASTEROID_DELAY;
		lastShipTime = -NEW_SHIP_DELAY;
				
		bigFont = originalFont;
		biggestFont = null;
				
        // set labels' text
		shipsValueLabel.setForeground(Color.BLACK);
		shipsValueLabel.setText(Integer.toString(status.getShipsLeft()));
		destroyedValueLabel.setText(Long.toString(status.getAsteroidsDestroyed()));
		levelValueLabel.setText(Integer.toString(level.level()));
	}

	/**
	 * Sets the game graphics manager.
	 * @param graphicsMan the graphics manager
	 */
	public void setGraphicsMan(GraphicsManager graphicsMan) {
		this.graphicsMan = graphicsMan;
	}

	/**
	 * Sets the game logic handler
	 * @param gameLogic the game logic handler
	 */
	public void setGameLogic(GameLogic gameLogic) {
		this.gameLogic = gameLogic;
		this.status = gameLogic.getStatus();
		this.soundMan = gameLogic.getSoundMan();
	}

	/**
	 * Sets the label that displays the value for asteroids destroyed.
	 * @param destroyedValueLabel the label to set
	 */
	public void setDestroyedValueLabel(JLabel destroyedValueLabel) {
		this.destroyedValueLabel = destroyedValueLabel;
	}
	
	/**
	 * Sets the label that displays the value for ship (lives) left
	 * @param shipsValueLabel the label to set
	 */
	public void setShipsValueLabel(JLabel shipsValueLabel) {
		this.shipsValueLabel = shipsValueLabel;
	}
	
	/**
	 * Sets the label that displays the current level
	 * @param shipsValueLabel the label to set
	 */
	public void setLevelValueLabel(JLabel levelValueLabel) {
		this.levelValueLabel = levelValueLabel;
	}
	
	/**
	 * Destroys the asteroid count and increases the asteroid destroyed count by 1
	 * @param asteroids list
	 *  @param element of the asteroid destroyed
	 */
	public void destroyAsteroid(List<Asteroid> asteroids, int element){
		// increase asteroids destroyed count
		status.setAsteroidsDestroyed(status.getAsteroidsDestroyed() + 1);

		// "remove" asteroid
        asteroidExplosion = new Rectangle(
        		asteroids.get(element).x,
        		asteroids.get(element).y,
        		asteroids.get(element).width,
        		asteroids.get(element).height);
		asteroids.get(element).setLocation(-asteroids.get(element).width, -asteroids.get(element).height);
		status.setNewAsteroid(true);
		
		lastAsteroidTime = System.currentTimeMillis();
		
		// play asteroid explosion sound
		soundMan.playAsteroidExplosionSound();
		
		level.asteroidDestroyed();
		
		astIndex = element;
	}
	
	/**
	 * Destroys the asteroid count and increases the asteroid destroyed count by 1
	 * @param asteroids list
	 * @param asteroidElement from the list asteroids
	 * @param ship that crashes with the asteroid
	 */
	public void shipCrashAsteroid(List<Asteroid> asteroids, int asteroidElement, Ship ship){
		// decrease number of ships left
		status.setShipsLeft(status.getShipsLeft() - 1);
		
		status.setAsteroidsDestroyed(status.getAsteroidsDestroyed() + 1);
		
		this.destroyAsteroid(asteroids, asteroidElement);
		
		// "remove" ship
        shipExplosion = new Rectangle(
        		ship.x,
        		ship.y,
        		ship.width,
        		ship.height);
		ship.setLocation(this.getWidth() + ship.width, -ship.height);
		status.setNewShip(true);
		lastShipTime = System.currentTimeMillis();
		
		// play ship explosion sound
		soundMan.playShipExplosionSound();
		// play asteroid explosion sound
		soundMan.playAsteroidExplosionSound();
	}
	
	/**
	 * Moves the asteroid
	 * @param asteroids list
	 * @param element of the asteroids list to move
	 */
	public void translateAsteroid(List<Asteroid> asteroids, int element){
		if(asteroids.get(element).getY() + asteroids.get(element).getSpeed() < this.getHeight()){
				asteroids.get(element).translate(level.getDiagonalMotion(), asteroids.get(element).getSpeed()); //CHANGE X COMPONENT VALUE TO ADD DIAGONAL MOTION
				graphicsMan.drawAsteroid(asteroids.get(element), g2d, this);
		}
		else{
				asteroids.get(element).setLocation(rand.nextInt(this.getWidth() - asteroids.get(element).width), 0);
		}
	}
}
