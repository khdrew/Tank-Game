/* compsys302 2016 - group30
 * Wall
 * Contains the dimensions, position and image of the wall
 */
package gameObjects;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class Wall extends GameObject{
	
	private int tick; 
	
	public Wall(int x, int y, String id, BufferedImage sprite) {
		super(x, y, id, sprite);
		init();
	}
	
	public Wall(int x, int y, String id, BufferedImage sprite, String objID) {
		super(x, y, id, sprite, objID);
		init();
	}

	public void init() {
		tick = 0;
	}

	public void respawn(int x, int y, int angle) {}

	public void update() { // return to wall id if not wall id
		if (id != "Wall"){
			tick++;
			if (tick > 2){
				tick = 0;
				id = "Wall";
			}
		}
	}

	public void draw(Graphics2D g) {
		g.drawImage(image, x, y, null);
	}

	public void powered(String s) { // set new id
		id = s;
	}

	
}
