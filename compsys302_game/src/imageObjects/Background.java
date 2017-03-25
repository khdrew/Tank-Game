/* compsys302 2016 - group30
 * Background
 * Image object to load in a image to be displayed as a background for each screen
 */

package imageObjects;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import javax.imageio.ImageIO;

public class Background {
	private BufferedImage image;

	public Background(String s) { // load in background
		try {
			image = ImageIO.read(new FileInputStream(s));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void draw(Graphics2D g) { // draw background
		g.drawImage(image, 0, 0, null);
	}

}
