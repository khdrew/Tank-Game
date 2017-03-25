/* compsys302 2016 - group30
 * Map Loader
 * Used to load in predetermined maps using imported image files
 * The map loader displays the pixel coloring
 * Thus able to determine if the image is wanting a wall or tank in which positions
 */

package imageObjects;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;

import javax.imageio.ImageIO;

public class MapLoader {
	
	private BufferedImage image;
	private boolean[] wallArray;
	private final int NumOfWalls = 64 * 85;

	public MapLoader(String s) {
		try {
			image = ImageIO.read(new FileInputStream(s));
			wallArray = new boolean[NumOfWalls];
			int red, green, blue, pixel;
			int index = 0;
			for (int i = 0; i < 64; i++){
				for (int j = 0; j < 85; j++){
					pixel = image.getRGB(j,i);
					red = (pixel >> 16) & 0xFF;
					green = (pixel >> 8) & 0xFF;
					blue = (pixel) & 0xFF;
					if (red == 0 && green == 0 && blue == 0){
						wallArray[index] = true;
					}else{
						wallArray[index] = false;
					}
					index++;
				}
			}		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public int getR(int x, int y) {
		return (image.getRGB(x,y) >> 16) & 0xFF;
	}
	
	public int getG(int x, int y) {
		return (image.getRGB(x,y) >> 8) & 0xFF;
	}
	
	public int getB(int x, int y) {
		return (image.getRGB(x,y)) & 0xFF;
	}
	

}
