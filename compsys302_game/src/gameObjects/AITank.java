/* compsys302 2016 - group30
 * AI Tank
 * Exact copy of tank, however has AI control its forward/backwards and angle 
 * The control is competitive and able to seek and kill its target
 */
package gameObjects;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import audio.AudioPlayer;
import main.GameWindow;

public class AITank extends Tank {
	
	private static final double DSPEED = 3.0;
	private static final double DFIRECD = 1;
	private boolean rev, powerUped;
	private int life, powerTime, aiTick, axisTick;
	private BufferedImage shieldImage, flare0, flare1, bullet;
	private double fireCD, diffTime;
	private boolean ableFire;
	private int nextX, nextY, nextAngle;
	private int fireCount;
	private AudioPlayer sfx;
	private String powerString, outString, difficulty;
	private int dispTick, dispRange;
	private boolean justDown, dispEnd, activeAI, walled;
	private double futureTY = 0, futureTX = 0;
	private int futureX, futureY, walledTick;
	private GameObjectHandler goh;
	private Tank enemy;
	private boolean axis;
	
	public AITank(int x, int y, String id, BufferedImage tankSprite, BufferedImage shieldImage, BufferedImage bullet, BufferedImage flare0, BufferedImage flare1, GameObjectHandler goh, AudioPlayer sfx, Tank enemy, String difficulty) {
		super(x, y, id, tankSprite, shieldImage); //add all images into the game
		this.enemy = enemy;
		this.sfx = sfx;
		this.goh = goh;
		this.shieldImage = shieldImage;
		this.flare0 = flare0;
		this.flare1 = flare1;
		this.bullet = bullet;
		this.difficulty = difficulty;
		init();
	}

	public void init() { // initialize the variables
		activeAI = true;
		velY = 0;
		aiTick = 0;
		ableFire = true;
		axis = true;
		speed = DSPEED;
		fireCD = DFIRECD;
		fireCount = 0;
		rev = false;
		justDown = false;
		shield = false;
		life = 0;
		powerTime = 0;
		powerUped = false;
		if (difficulty == "Normal"){ // set the difficulty (ai response time)
			diffTime = 0.01;
		}else if (difficulty == "Easy"){
			diffTime = 0.05;
		}else{
			diffTime = 0.1;
		}
	}
	
	//gun fire cool down
	public boolean isAbleFire(){
		return ableFire;
	}
	
	public void setAbleFire(boolean a){
		ableFire = a;
	}
		
	public void respawn(int x, int y, int angle) { //respawn flags raised.
		life = 0;
		nextX = x;
		nextY = y;
		nextAngle = angle;
		rev = true;
		resetPower();
		powerUped = false; // reset power ups upon res
		justDown = false;
		dispEnd = false;
		activeAI = false;
	}
	
	public void aiReset(){
		velY = 0;
	}
	
	public void aiControl(){ // set new AI control logic each tick
		if (activeAI){
			// determine future angle movement
			if (angle >= 0 && angle <= 3) {
				futureTY = -Math.cos(angle * Math.PI / 8);
				futureTX = Math.sin(angle * Math.PI / 8);
			} else if (angle >= 4 && angle <= 7) {
				futureTX = Math.cos((angle - 4) * Math.PI / 8);
				futureTY = Math.sin((angle - 4) * Math.PI / 8);
			} else if (angle >= 8 && angle <= 11) {
				futureTY = Math.cos((angle - 8) * Math.PI / 8);
				futureTX = -Math.sin((angle - 8) * Math.PI / 8);
			} else if (angle >= 12 && angle <= 15) {
				futureTX = -Math.cos((angle - 12) * Math.PI / 8);
				futureTY = -Math.sin((angle - 12) * Math.PI / 8);
			} 
			// determine where it lands within the next movement
			futureX = x + (int)( (futureTX * speed) * 4);
			futureY = y + (int)( (futureTY * speed) * 4);
			Tank tempTank = new Tank(futureX, futureY, "Player2", image, shieldImage, angle);
			
			AffineTransform at = AffineTransform.getTranslateInstance(tempTank.getX(), tempTank.getY());
			at.rotate(Math.toRadians(angle * 22.5), tempTank.image.getWidth() / 2, tempTank.image.getHeight() / 2);
			GeneralPath path = new GeneralPath();
			path.append(tempTank.getBounds().getPathIterator(at), true);
			Area a1 = new Area(path);
			
			boolean collided = false; // check if tank will collide into a wall with the foreseeable movement
			for (int i = 0; i < goh.getObjectList().size() ; i++){
				GameObject tempObject = goh.getObjectList().get(i);
				if (tempObject != this && (tempObject.getId() == "Wall" || tempObject.getId() == "")){
					if (GameObjectHandler.checkCollisionOne(a1,tempObject)){
						collided = true;
					}
				}
			}
			if (collided){ // if collided, swerve away from the wall
				velY = -1;
				walled = true;
				walledTick = 0;
				if (axis){
					turnLeft();
				}else{
					turnRight();
				}
			}else{ // else try seek out the enemy, and fire at enemy
				velY = 1;
				walledTick++;
				if (walledTick > GameWindow.secToTicks(1)){
					walled = false;
				}
				if (!walled){
					if (scanEnemy() == 1){ // turn to enemy target
						turnRight();
						velY = 0;
					}else if(scanEnemy() == -1){
						turnLeft();
						velY = 0;
					}else{
						if (isAbleFire()){ // shoot when is in its sights
							setAbleFire(false);
							sfx.play("gunShot");
							Tank tempTank1 = new Tank(x,y,id,angle);
							goh.addObject(new Bullet(tempTank1, "Bullet", 9, getId(), bullet, flare0, flare1));
						}
					}
				}
			}
			
			
		}
	}
	
	public int scanEnemy(){ // seek enemy logic
		boolean left = false;
		boolean above = false;
		boolean sameX = false;
		boolean sameY = false;
		int aimAngle = 0;
		// look if the target is to the left or right of current position.
		if (getX() + 24 > enemy.getX() && getX() + 24 < enemy.getX() + 48){
			sameX = true;
		}else if (getX() + 24 > enemy.getX() + 24) {
			left = true;
		}
		
		//look if the target is above or under of current position.
		if (getY() + 24 > enemy.getY() && getY() + 24 < enemy.getY() + 48){
			sameY = true;
		}else if (getY() + 24 > enemy.getY() + 24 ){
			above = true;
		}
		
		
		// set the angle to face target
		if (!sameX && !sameY){
			if (left && !above){
				aimAngle = 10;
			}
			if (left && above){
				aimAngle = 14;
			}
			if (!left && !above){
				aimAngle = 6;
			}
			if (!left && above){
				aimAngle = 2;
			}
		}else{
			if (sameX){
				if (above){
					aimAngle = 0;
				}else{
					aimAngle = 8;
				}
			}else {
				if (left){
					aimAngle = 12;
				}else{
					aimAngle = 4;
				}
			}
		}
		// calculate if angle is the right angle that seeks the target
		if (angle == aimAngle){	// if already on target, don't turn
			return 0;
		}
		
		// calculate smallest turn (left or right turning)
		int tempAngle = angle;
		int leftTurns = 0, rightTurns = 0;
		while (tempAngle != aimAngle){
			leftTurns++;
			tempAngle--;
			if (tempAngle <= -1){
				tempAngle = 15;
			}
		}
		tempAngle = angle;
		while (tempAngle != aimAngle){
			rightTurns++;
			tempAngle++;
			if (tempAngle >= 16){
				tempAngle = 0;
			}
		}
		if (rightTurns < leftTurns){
			return 1;
		}else{
			return -1;
		}
		
	}
	
	public void update() {
		if (rev) { // revive logic
			life++;
			if (life >= GameWindow.secToTicks(1)) { // revive 1 second after death
				x = nextX;
				y = nextY;
				angle = nextAngle;
				rev = false;
				ableFire = true;
				activeAI = true;
			}
		} else { // set AI control, and AI response time
			aiTick++;
			if (aiTick >= GameWindow.secToTicks(diffTime)){
				aiTick = 0;
				aiControl();
			}
			axisTick++;
			if (axisTick >= GameWindow.secToTicks(10)){
				axisTick = 0;
				axis = !axis;
			}
			setTempXY();
			prevX = x;
			prevY = y;
			x += (int) (tempX * speed) * velY; // set pos
			y += (int) (tempY * speed) * velY;

			 // out of bounds logic
			if (x < 0 || x > GameWindow.WIDTH - 46) {
				x -= (int) (tempX * speed) * velY;
			}
			if (y < 0 || y > GameWindow.HEIGHT - 48) {
				y -= (int) (tempY * speed) * velY;
			}
		}
		
		if (powerUped){ // calculate powerUp up time
			powerTime++;
			if (powerTime >= (GameWindow.secToTicks(15.0))){
				resetPower();
				powerUped = false;
				justDown = true;
				dispTick = 0;
				dispRange = 1;
				dispEnd = false;
				powerString = "Effect End...";
			}
		}
		if (!ableFire){ // set gun fire cool down time
			fireCount++;
			if (fireCount >= GameWindow.secToTicks(fireCD)){
				fireCount = 0;
				ableFire = true;
			}
		}
	}

	public void turnLeft() { // turn angle left
		setAngle(getAngle() - 1);
		angleCheck();
	}

	public void turnRight() { // turn angle right
		setAngle(getAngle() + 1);
		angleCheck();
	}

	public void draw(Graphics2D g) { 
		// draw to screen the tank
		AffineTransform at = AffineTransform.getTranslateInstance(x, y);
		at.rotate(Math.toRadians(angle * 22.5), image.getWidth() / 2, image.getHeight() / 2);
		g.drawImage(image, at, null); // rotate image
		
		AffineTransform at1 = AffineTransform.getTranslateInstance(x - 9, y - 9);
		if (shield){ // draw shield if shield is active
			g.drawImage(shieldImage, at1, null);
		}
		if (powerUped || justDown){ // display the power up text that is obtained
			dispTick++;	// as well as display when power up is lost
			if (!dispEnd){
				if (dispTick >= GameWindow.secToTicks(0.01)){
					if (dispRange <= powerString.length()){
						dispTick = 0;
						outString = powerString.substring(0,dispRange);
						dispRange++;
						dispTick = 2000;
					}
				}
				g.setColor(Color.WHITE);
				g.setFont(new Font(GameWindow.FONTSTYLE, Font.BOLD, 10));
				g.drawString(outString, x + 24, y + 65);
			}
			if (dispTick >= GameWindow.secToTicks(3) + 2000){
				dispEnd = true;
				justDown = false;
			}
		}
		

	}

	public void powered(String s) { // power up logic, determines the effect gained.
		powerUped = true;
		resetPower();
		if (s == "pshield"){
			powerString = "Shield Up...";
			setShield(true);
		}else if (s == "pspeedUp"){
			powerString = "Speed Up...";
			setSpeed(DSPEED * 1.5);
		}else if (s == "pspeedDown"){
			powerString = "Speed Down...";
			setSpeed(DSPEED * 0.5);
		}else if (s == "pfireUp"){
			powerString = "Fire Rate Up...";
			fireCD = DFIRECD * 0.5;
		}else if (s == "pfireDown"){
			powerString = "Fire Rate Down...";
			fireCD = fireCD * 1.5;
		}
		dispTick = 0;
		dispRange = 1; // start the power up text display
		dispEnd = false;
	}
	
	public String getPowerString(){ 
		return powerString;
	}
	
	public void resetPower(){ // reset power up flags
		powerTime = 0;
		shield = false;
		speed = DSPEED;
		fireCD = DFIRECD;
		dispTick = 0;
	}

}
