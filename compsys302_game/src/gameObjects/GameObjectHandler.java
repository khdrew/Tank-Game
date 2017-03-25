/* compsys302 2016 - group30
 * GameObjectHandler
 * The core of all game objects, all object updates and draw all cycles through this.
 * Contains the core ArrayList of all the objects within the game.
 * Also computes every single collision of the game.
 */

package gameObjects;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.Random;

import audio.AudioPlayer;
import imageObjects.HUD;
import imageObjects.SpriteSheet;
import main.GameWindow;

public class GameObjectHandler {
	private ArrayList<GameObject> objectList;
	private boolean running;
	private HUD hud;
	private Random rand;
	private SpriteSheet ss;
	private AudioPlayer sfx;
	
	public ArrayList<GameObject> getObjectList(){
		return objectList;
	}
	
	public GameObjectHandler(HUD hud) { // load in the sprite for the tank explosion and initialize variables
		objectList = new ArrayList<GameObject>();
		running = false;
		rand = new Random();
		this.hud = hud;
		ss = new SpriteSheet(GameWindow.LoadImage("res/explosion_spritesheet.png"));

	}

	
	public void update() {
		hud.update();
		if (running) {  // check collisions
			for (int i = 0; i < objectList.size(); i++) {
				GameObject tempObject = objectList.get(i);
				if (tempObject.getId() != "Wall") {
					checkCollision(tempObject);
				}
				tempObject.update(); // check if object should die
				if (!tempObject.isAlive()) {
					sfx.play("pop");
					removeObject(tempObject);
				}
			}
		}
	}

	public void checkCollision(GameObject object) {
		// get info that will be used to find collision of objects
		int angle = ((object.getId() == "Player1") || (object.getId() == "Player2"))?object.getAngle():0;
		AffineTransform at = AffineTransform.getTranslateInstance(object.getX(), object.getY());
		at.rotate(Math.toRadians(angle * 22.5), object.image.getWidth() / 2, object.image.getHeight() / 2);
		GeneralPath path = new GeneralPath();
		path.append(object.getBounds().getPathIterator(at), true);
		Area a1 = new Area(path);
        
		for (int i = 0; i < objectList.size(); i++) { // cycle thru all objects to check collide
			GameObject tempObject = objectList.get(i);
			if (object != tempObject) {
				if (checkCollisionOne(a1,tempObject)){
					// bullet collision
					if (object.getId() == "Bullet" && object.getOwner() != tempObject.getId()) {
						// bullet to bullet
						if (tempObject.getId() == "Bullet"){
							tempObject.powered("");
							object.powered("");
						}
						//bullet to powerUp
						if (tempObject.getId().charAt(0) == 'p'){
							object.setAlive(false);
							tempObject.setX(2500);
							tempObject.setY(2500);
							object.powered("");
						}
						// bullet to tank
						if (tempObject.getId() == "Player1" || tempObject.getId() == "Player2"){
							if (tempObject.hasShield()){
								tempObject.setShield(false);
								object.setAlive(false);
								sfx.play("bounce");
							}else if (!tempObject.hasShield()){
								if (tempObject.getId() == "Player1"){
									hud.add2();
								}else{
									hud.add1();
								}
								object.setAlive(false);
								addObject(new TankExplosion(tempObject.getX(), tempObject.getY(), "Explosion", ss));
								respawnObject(tempObject);
								sfx.play("tankExplode");
							}
							object.powered("");
						}
						// bullet to wall
						if (tempObject.getId() == "Wall"){
							int changeX = 1, changeY = 1;
							tempObject.powered("ss");
							ArrayList<GameObject> array = new ArrayList<GameObject>();
							array.add(objectList.get(i));
							// reset array
							for (int j = i + 1; j < objectList.size(); j++) {
								GameObject otherObject = objectList.get(j);
								if (otherObject.getId() == "Wall" && checkCollisionOne(a1,otherObject)){
									otherObject.powered("s");
									array.add(objectList.get(j));
									if (array.size() >= 3){
										j = objectList.size();
									}
								}
							}
							// check how many walls touched
							if (array.size() == 3){
								// 3 walls
								changeX = -1;
								changeY = -1;
							}else if (array.size() == 2){
								// 2 walls
								if (array.get(0).getX() == array.get(1).getX()){
									// 2 vertical walls
									changeX = -1;
								}else if (array.get(0).getY() == array.get(1).getY()){
									// 2 horizontal walls
									changeY = -1;
								}else{
									//special case where 2 walls are not aligned/ are staggered
									changeY = -1;
									changeX = -1;
								}
							}else{
								// 1 wall collide
								if ((object.getX() >= tempObject.getX()) && ((object.getX() + object.image.getWidth() - 1) <= (tempObject.getX() + tempObject.image.getWidth() - 1))){
									// top or bottom collisions
									changeY = -1;
								} else if ((object.getY() >= tempObject.getY()) && ((object.getY() + object.image.getHeight() - 1) <= (tempObject.getY() + tempObject.image.getHeight() - 1))){
									// left or right collisions
									changeX = -1;
								} else {
									//intersect corner
									System.out.println("Corner Wall Collisions");
									Rectangle temp = getIntersect(object, tempObject);
									//intersect left side
									if (temp.getX() == tempObject.getX()){
										//intersect top left side;
										if (temp.getY() == tempObject.getY()){
											// coming from left side
											if (object.getPrevX() + 4 < tempObject.getX()) {
												//coming from top left side
												if (object.getPrevY() +4 < tempObject.getY()){
													 changeY = -1;
													 changeX = -1;
												}else {//coming from bottom left side
													changeX = -1;
												}
											}else { // coming from right side
												changeY = -1;
											}
										}else { // intersect button left side;
											// coming from left side
											if (object.getPrevX() +4< tempObject.getX()) {
												// coming from bottom left side
												if (object.getPrevY() +4> tempObject.getY() + 12){
													changeY = -1;
													changeX = -1;
												}else { // coming from top left side
													changeX = -1;
												}
											}else{ // coming from right side
												changeY = -1;
											}
										}
									}else{ // intersect right side
										//intersect top right side;
										if (temp.getY() == tempObject.getY()){
											// coming from right side
											if (object.getPrevX() +4> tempObject.getX() + 12) {
												//coming from top right side
												if (object.getPrevY() +4< tempObject.getY()){
													 changeY = -1;
													 changeX = -1;
												}else {//coming from bottom right side
													changeX = -1;
												}
											}else { // coming from left side
												changeY = -1;
											}
										}else { // intersect button right side;
											// coming from right side
											if (object.getPrevX() +4> tempObject.getX() + 12) {
												// coming from bottom right side
												if (object.getPrevY()+4 > tempObject.getY() + 12){
													changeY = -1;
													changeX = -1;
												}else {
													// coming from top right side
													changeX = -1;
												}
											}else{ // coming from left side
												changeY = -1;
											}
										}
									}
								}
							}
							sfx.play("bounce");
							object.setVelX(object.getVelX() * changeX);
							object.setVelY(object.getVelY() * changeY);
						}
						
					}
					// tank collision;
					if ((object.getId() == "Player1") ||(object.getId() == "Player2")){
						// tank to tank
						if ((tempObject.getId() == "Player2") || (tempObject.getId() == "Player1")){
							addObject(new TankExplosion(tempObject.getX(), tempObject.getY(), "Explosion", ss));
							addObject(new TankExplosion(object.getX(), object.getY(), "Explosion", ss));
							sfx.play("tankExplode");
							sfx.play("tankExplode");
							respawnObject(tempObject);
							respawnObject(object);
						}
						// tank to power up
						if (tempObject.getId().charAt(0) == 'p'){
							object.powered(tempObject.getId());
							sfx.play("powerUp");
							tempObject.setX(2500);
							tempObject.setY(2500);
						}
						// tank to wall collisions
						if (tempObject.getId() == "Wall"){
							object.setPrevPov();
						}
					}
				}
			}
		}
	}

	public Rectangle getIntersect(GameObject object, GameObject tempObject) {
		Rectangle temp;
		// produced a rectangle of the intersect that between two objects
		int tempX, tempY, tempW, tempH;
		tempX = (tempObject.getX() > object.getX()) ? tempObject.getX() : object.getX();
		tempY = (tempObject.getY() > object.getY()) ? tempObject.getY() : object.getY();
		if (tempX == tempObject.getX()) {
			tempW = object.getX() + object.image.getWidth() - tempX;
		} else {
			tempW = tempObject.getX() + tempObject.image.getWidth() - tempX;
		}
		if (tempY == tempObject.getY()) {
			tempH = object.getY() + object.image.getHeight() - tempY;
		} else {
			tempH = tempObject.getY() + tempObject.image.getHeight() - tempY;
		}
		temp = new Rectangle(tempX, tempY, tempW, tempH);
		return temp;
	}

	// respawn object that will not overlap
	public void respawnObject(GameObject object) {
		if (object.getId() == "Player1") {
			object.setX(2000);
			object.setY(2000);
		} else if (object.getId() == "Player2") {
			object.setX(3000);
			object.setY(3000);
		} else if (object.getId().charAt(0) == 'p') {
			object.setX(2500);
			object.setY(2500);
		}
		int nextX, nextY, nextAngle;

		nextX = rand.nextInt(GameWindow.WIDTH - 45);
		nextY = rand.nextInt(GameWindow.HEIGHT - 45);
		nextAngle = rand.nextInt(15);
		while (checkSpawn(nextX, nextY, nextAngle, object)) { // check if object overlaps
			nextX = rand.nextInt(GameWindow.WIDTH - 45); // keeps respawning until it doesn't have an overlap
			nextY = rand.nextInt(GameWindow.HEIGHT - 45);
			nextAngle = rand.nextInt(15);
		}
		object.respawn(nextX, nextY, nextAngle);

	}

	// check if new object will overlap with current objects;
	public boolean checkSpawn(int nextX, int nextY, int nextAngle, GameObject object) {
		AffineTransform at = AffineTransform.getTranslateInstance(nextX, nextY);
		at.rotate(Math.toRadians(nextAngle * 22.5), object.image.getWidth() / 2, object.image.getHeight() / 2);
		GeneralPath path = new GeneralPath();
		path.append(object.getBounds().getPathIterator(at), true);
		Area a1 = new Area(path);
		for (int i = 0; i < objectList.size(); i++) {
			GameObject tempObject = objectList.get(i);
			if (checkCollisionOne(a1, tempObject)) {
				return true;
			}
		}
		return false;
	}

	// check collision with object to another object;
	public static boolean checkCollisionOne(Area a1, GameObject tempObject) {
		int angle = ((tempObject.getId() == "Player1") || (tempObject.getId() == "Player2")) ? tempObject.getAngle()
				: 0;
		AffineTransform at = new AffineTransform();
		at = AffineTransform.getTranslateInstance(tempObject.getX(), tempObject.getY());
		at.rotate(Math.toRadians(angle * 22.5), tempObject.image.getWidth() / 2, tempObject.image.getHeight() / 2);
		GeneralPath path = new GeneralPath();
		path.append(tempObject.getBounds().getPathIterator(at), true);
		Area a2 = new Area(path);
		a2.intersect(a1);
		if (!a2.isEmpty()) {
			return true;
		} else {
			return false;
		}
	}

	public void draw(Graphics2D g) {
		// draw all objects
		for (int i = objectList.size() - 1; i >= 0; i--) {
			GameObject tempObject = objectList.get(i);
			tempObject.draw(g);
		}

		// draw HUD
		hud.draw(g);

	}
	
	
	//pausing flag
	public void pause() {
		running = !running;
		hud.pause();
	}
	public void setRunning(boolean running){
		this.running = running;
	}

	public boolean isRunning() {
		return running;
	}

	public boolean isPaused() {
		return !running;
	}
	
	// add objects to list
	public void addObject(GameObject object) {
		this.objectList.add(object);
	}

	// remove objects from list
	public void removeObject(GameObject object) {
		if (running) {
			this.objectList.remove(object);
		}
	}

	public void addSfx(AudioPlayer sfx) {
		this.sfx = sfx;
	}

}
