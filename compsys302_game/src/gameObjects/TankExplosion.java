/* compsys302 2016 - group30
 * TankExplosion
 * A object that is purely visual.
 * Displays each frame of the sprite sheet animation
 */
package gameObjects;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import imageObjects.SpriteSheet;
import main.GameWindow;

public class TankExplosion extends GameObject {
	private SpriteSheet ss;
	private int tick;
	private int currentImage;
	
	public TankExplosion(int x, int y, String id, SpriteSheet ss) {
		super(x, y, "Explosion", null); // set pos of the explosion
		this.ss = ss;
		init();
	}

	public void init() {
		currentImage = 0;
		image = ss.getImage(currentImage);
		tick = 0;
		
	}

	public void respawn(int x, int y, int angle) {}

	public void update() { // update each frame of the animation
		tick++;
		if (tick > GameWindow.secToTicks(0.01)){ // set next frame
			tick = 0;
			currentImage++;
			currentImage++;
			if (currentImage > 80){ // after animation is done, remove the object
				setAlive(false);				
			}else{
				image = ss.getImage(currentImage);
			}
		}
	}

	public void draw(Graphics2D g) { // draw to screen
		AffineTransform at = AffineTransform.getTranslateInstance(x, y);
		g.drawImage(image, at, null);
	}

	public void powered(String s) {}

}
