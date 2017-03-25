/* compsys302 2016 - group30
 * Game
 * Where the main is called and the first JFrame is created
 * The base game window is placed into this frame.
 */

package main;


import javax.swing.JFrame;

public class Game{

	public static void main (String[] args){
		JFrame window = new JFrame("Tank Combat");
		window.setContentPane(new GameWindow());
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setResizable(false);
		window.pack();
		window.setVisible(true);
	}
	
}
