/* compsys302 2016 - group30
 * Sprite Sheet
 * Loads in a image file that will be used to show animated frames.
 * Such as an explosions animation
 */

package imageObjects;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class SpriteSheet {
	
	private BufferedImage image;
	private ArrayList<BufferedImage> sprites;
	
	public SpriteSheet (BufferedImage image){ // load image
		this.image = image;
		init();
	}
	
	public BufferedImage grabSubImage(int col, int row, int width, int height){ // get sprite from sprite sheet
		BufferedImage temp = image.getSubimage((col * 48),(row * 48) , width, height);
		return temp;
	}
	
	public void init(){ // put all the sprite's into the array
		sprites = new ArrayList<BufferedImage>();
		for (int i = 0; i < 9; i++){
			for (int j = 0; j < 9; j++){
				sprites.add(grabSubImage(j,i,48,48));
			}
		}
	}
	
	public BufferedImage getImage(int i){ // return the sprite
		return sprites.get(i);
	}
	
}
