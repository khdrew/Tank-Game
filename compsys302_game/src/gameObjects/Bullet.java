/* compsys302 2016 - group30
 * Bullet
 * Contains the dimensions, movement and images of the bullet
 * Calculates its movement of its angle
 */

package gameObjects;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import main.GameWindow;

public class Bullet extends GameObject {
	private int lifeTick;
	private boolean explode;
	private int ownTick;
	private BufferedImage flare0, flare1;
	private BufferedImage renderImage;
	
	public Bullet(Tank tank, String id, int speed, String owner, BufferedImage sprite, BufferedImage flare0, BufferedImage flare1) {
		super(tank.getX() + 20, tank.getY() + 20, "Bullet", sprite);
		this.speed = speed;
		angle = tank.getAngle(); // obtain info for the shooter
		this.flare0 = flare0;
		this.flare1 = flare1;
		setOwner(tank.getId());
		if (tank.getPowerString() == "Speed Up..."){ // upon spawn, do no collide with shooter for first few seconds to avoid shooting self
			ownTick = 9; // if speed of tank is high, this needs to be higher
		}else{
			ownTick = 6;
		}
		init();
	}
	
	public Bullet(Tank tank, String id, int speed, String owner, BufferedImage sprite, BufferedImage flare0, BufferedImage flare1, String objID) {
		super(tank.getX() + 20, tank.getY() + 20, "Bullet", sprite, objID);
		this.speed = speed;
		angle = tank.getAngle(); // obtain info for the shooter
		this.flare0 = flare0;
		this.flare1 = flare1;
		setOwner(tank.getId());
		if (tank.getPowerString() == "Speed Up..."){ // upon spawn, do no collide with shooter for first few seconds to avoid shooting self
			ownTick = 9; // if speed of tank is high, this needs to be higher
		}else{
			ownTick = 6;
		}
		init();
	}

	public void init() {
		angleCheck();
		renderImage = image; // Initialize and set image
		explode = false;
		lifeTick = 0;
		velX = 1;
		velY = 1;
		
	}

	public void update() {
		checkLife();
		if (!explode) { // update movement
			setTempXY();
			prevX = x;
			prevY = y;
			x += (int) ((tempX * speed) * velX);
			y += (int) ((tempY * speed) * velY);

			if (x < 0 || x > GameWindow.WIDTH - 8) {
				velX *= -1;
			}
			if (y < 0 || y > GameWindow.HEIGHT - 8) {
				velY *= -1;
			}
		}
	}

	public void respawn(int x, int y, int nextAngle) {

	}

	public void checkLife() {
		lifeTick++;
		if (lifeTick > ownTick) { // upon spawn, do no collide with shooter for first few seconds
			setOwner("");
		}
		if (lifeTick >= GameWindow.WIDTH / speed) { // set bullet life time
			if (lifeTick >= ((GameWindow.WIDTH / speed) + GameWindow.secToTicks(0.125))){
				if (lifeTick >= ((GameWindow.WIDTH / speed) + GameWindow.secToTicks(0.25))){
					setAlive(false);
				}else{
					renderImage = flare1;
				}
			}else{
				explode = true; // render bullet death
				id = "_";
				renderImage = flare0;
			}
		}
		
		
	}
	public void draw(Graphics2D g) { // draw image
		g.drawImage(renderImage, x, y, null);

	}

	public void powered(String s) { // set life time of bullet
		lifeTick = (int) (GameWindow.WIDTH / speed);
	}

}
