/* compsys302 2016 - group30
 * ScreenObject
 * Abstract class that all screens extend from.
 */

package screenState;

import java.awt.Graphics2D;

public abstract class ScreenObject {

	protected ScreenObjectHandler soh;
	public abstract void init();
	public abstract void update();
	public abstract void draw(Graphics2D g);
	public abstract void keyPressed(int k);
	public abstract void keyReleased(int k);
	
}
