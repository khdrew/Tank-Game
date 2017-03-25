/* compsys302 2016 - group30
 * Menu Screen
 * First screen to navigate through the game's various game modes and other functions
 * These are such as game modes, maps and sound settings etc.
 * This extends ScreenObject, thus uses the abstract methods of the screen object class 
 */
package screenState;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.Random;
import javax.swing.JOptionPane;
import audio.AudioPlayer;
import gameObjects.Tank;
import imageObjects.Background;
import main.GameWindow;

public class MenuScreen extends ScreenObject {

	private Random rand = new Random();
	private Background bg, startUpImage;
	private int currentChoice = 0;
	private int currentOptions = 0;
	private String[] options = { "Local Play", "Online Play", "Help", "Settings", "Quit" };
	private String[] gameModes = { "Single Player", "Multi Player", "Practise", "Back" };
	private String[] settings;
	private String[] difficulty = { "Normal Mode", "Easy Mode", "Novice Mode", "Back" };
	private String[] maps = { "Tennis", "Burger", "Hi_Grounds", "Cross_Fire", "Bone_Field", "Random", "Back" };
	private AudioPlayer sfx;
	private Tank[] tanks;
	private BufferedImage tank1Image, tank2Image, helpImage;
	private boolean loading = true;
	private boolean helpScreen = false;;

	public MenuScreen(ScreenObjectHandler soh, AudioPlayer sfx) { // initialize start variables
		this.soh = soh;
		this.sfx = sfx;
		sfx.stop("bgm");
		settings = new String[] { sfx.isMusicOn()?"Music: On":"Music: Off", sfx.isSoundOn()?"Sound Effects: On":"Sound Effects: Off", "Back" };
		startUpImage = new Background("res/startUp.gif");
		
	}

	public void init() {}

	private int tick = 0;

	public void update() {
		if (loading) { // when game starts up, load in all the sound clips
			tick++;	// they take the longest to load over images
			if (tick == 1) {
				bg = new Background("res/menu_bg.gif");
				tanks = new Tank[2];
				tank1Image = GameWindow.LoadImage("res/tank1.png");
				tank2Image = GameWindow.LoadImage("res/tank2.png");
				helpImage = GameWindow.LoadImage("res/help_img.gif");
				tanks[0] = new Tank(600, 800, null, tank1Image, null, 2);
				tanks[0].setVelY(1);
				tanks[1] = new Tank(1200, 50, null, tank2Image, null, 10);
				tanks[1].setVelY(1);
				if (sfx.getClip("powerUp") == null){ // if they're already loaded, don't load
					sfx.addAudio("powerUp", "res/sfx/powerUp.wav");
					sfx.addAudio("gunShot", "res/sfx/gunShot.wav");
					sfx.addAudio("tankExplode", "res/sfx/tankExplode.wav");
					sfx.addAudio("bounce", "res/sfx/bounce.wav");
					sfx.addAudio("pop", "res/sfx/pop.wav");
					sfx.addAudio("select", "res/sfx/powerUp.wav");
					sfx.addAudio("navigate", "res/sfx/bounce.wav");
					sfx.addAudio("startUp", "res/sfx/startUp.wav");
				}

			}
			if (tick == GameWindow.secToTicks(1.0)) { // play start up chime
				sfx.play("startUp");
			}
			if (tick >= GameWindow.secToTicks(3.0)) {
				loading = false;
				if (sfx.getClip("bgm") == null){ // load back ground music
					sfx.addAudio("bgm", "res/gameBGM.wav");
				}
				// start back ground music
				sfx.loop("bgm");
			}
		} else if (!helpScreen) { // update logic for the show case tanks.
			tanks[0].update();
			tanks[1].update();
			for (int i = 0; i < 2; i++) { // if tanks go off screen, put them back into place.
				if (tanks[i].getX() > GameWindow.WIDTH && tanks[i].getAngle() == 2) {
					tanks[i].setX(1200);
					tanks[i].setY(50);
					tanks[i].setAngle(10);
				} else if (tanks[i].getY() > GameWindow.HEIGHT && tanks[i].getAngle() == 10) {
					tanks[i].setX(600);
					tanks[i].setY(800);
					tanks[i].setAngle(2);
				}
			}
		}
	}

	public void draw(Graphics2D g) {
		if (loading) {	// when game starts, draw the start image
			startUpImage.draw(g);
		} else if (helpScreen) {  // when in help state, draw over the menu with the help image
			g.drawImage(helpImage, 75, 120, null);
			g.setFont(new Font(GameWindow.FONTSTYLE, Font.BOLD, 34));
			g.setColor(Color.red);
			g.drawString("Back", 750, 200);
		} else {	// drawing the background and select-able options
			bg.draw(g);	//draw in show case tanks
			tanks[1].draw(g);
			tanks[0].draw(g);
			
			//figure out selection array lengths
			int tempI;
			if (currentOptions == 0) {
				tempI = options.length;
			} else if (currentOptions == 1) {
				tempI = gameModes.length;
			} else if (currentOptions == 2) {
				tempI = maps.length;
			} else if (currentOptions == 3) {
				tempI = settings.length;
			} else {
				tempI = difficulty.length;
			}
			for (int i = 0; i < tempI; i++) { // draw in selections
				if (i == currentChoice) {
					g.setFont(new Font(GameWindow.FONTSTYLE, Font.BOLD, 34));
					g.setColor(Color.red); // high light the selected choice as red

				} else {
					g.setFont(new Font(GameWindow.FONTSTYLE, Font.BOLD, 32));
					g.setColor(Color.white); // print non selected choices as white
				}
				if (currentOptions == 0) {  // draw in from the correct array
					g.drawString(options[i], 80, 170 + i * 40);
				} else if (currentOptions == 1) {
					g.drawString(gameModes[i], 80, 170 + i * 40);
				} else if (currentOptions == 2) {
					g.drawString(maps[i], 80, 180 + i * 40);
				} else if (currentOptions == 3) {
					g.drawString(settings[i], 80, 170 + i * 40);
				} else if (currentOptions == 4) {
					g.drawString(difficulty[i], 80, 170 + i * 40);
				}
			}
			g.setColor(Color.white);  // draw in a guide line for controls at the start
			g.setFont(new Font(GameWindow.FONTSTYLE, Font.BOLD, 20));
			g.drawString("Navigate using directional keys, select using Enter", 25, GameWindow.HEIGHT - 25);
		}
	}

	private void select() { // function called when enter is pressed.
		// currentOptions when 0 is options array (refer to string arrays at the top)
		// when 1 is the game mode (single, multi or practise modes)
		// when 2 is the map selection for the given game mode (except single);
		// when 3 is the settings (turn on/off music and sfx.
		// when 4 is difficulty of AI (only in single mode)
		if (helpScreen) { // when the help is active, remove it.
			helpScreen = false;
		} else {
			if (currentOptions == 0) {// game choice
				if (currentChoice == 0) {// local play
					currentChoice = 0;
					currentOptions = 1;
				}
				if (currentChoice == 1) { // future implementation of online mode.
					soh.setScreen(ScreenObjectHandler.ONLINEGAMESCREEN);
				}
				if (currentChoice == 2) { // turn on help screen
					helpScreen = true;
				}
				if (currentChoice == 3) { // settings screen
					currentOptions = 3;
					currentChoice = 0;
				}
				if (currentChoice == 4) { // quit game
					if (JOptionPane.showConfirmDialog(null, "Are you sure?", "Quitting?",
							JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
						System.exit(0);
					}
				}
			} else if (currentOptions == 1) {// mode selection
				currentOptions = 2;
				if (currentChoice == 0) {  // single player mode
					soh.setMode("Single Player");
					soh.setMap("Tennis"); // default map and select AI difficulty
					currentOptions = 4;
				}
				if (currentChoice == 1) { // multi-player
					soh.setMode("Multi Player");
				}
				if (currentChoice == 2) { // practise mode
					soh.setMode("Practise");
				}
				if (currentChoice == 3) { // back start screen
					currentOptions = 0;
				}
				currentChoice = 0;
			} else if (currentOptions == 2) {// map selection, game should start after this select
				if (currentChoice >= 0 && currentChoice <= maps.length - 3) { // set chosen map
					soh.setMap(maps[currentChoice]);
					soh.setScreen(ScreenObjectHandler.GAMESCREEN);
				}
				if (currentChoice == maps.length - 2) { // set randomized map
					soh.setMap(maps[rand.nextInt(3)]);
					soh.setScreen(ScreenObjectHandler.GAMESCREEN);
				}
				if (currentChoice == maps.length - 1) { // back to mode selection
					currentOptions = 1;
					currentChoice = 0;
				}
			} else if (currentOptions == 3) {// settings
				if (currentChoice == 0) {
					if (settings[0] == "Music: On") {  // set music on/off
						settings[0] = "Music: Off";
						sfx.setMusic(false);
						sfx.stop("bgm");
					} else {
						settings[0] = "Music: On";
						sfx.setMusic(true);
						sfx.loop("bgm");
					}
				}
				if (currentChoice == 1) { // set sound effects on/off
					if (settings[1] == "Sound Effects: On") {
						settings[1] = "Sound Effects: Off";
						sfx.setSound(false);
					} else {
						sfx.setSound(true);
						sfx.play("select");
						settings[1] = "Sound Effects: On";
					}
				}
				if (currentChoice == 2) { // back to main screen
					currentChoice = 3;
					currentOptions = 0;
				}

			} else if (currentOptions == 4) {// difficulty, game should start after this select
				if (currentChoice == 0) {	// normal
					soh.setDifficulty("Normal");
					soh.setScreen(ScreenObjectHandler.GAMESCREEN);
				}
				if (currentChoice == 1) { // easy
					soh.setDifficulty("Easy");
					soh.setScreen(ScreenObjectHandler.GAMESCREEN);
				}
				if (currentChoice == 2) { // novice
					soh.setDifficulty("Novice");
					soh.setScreen(ScreenObjectHandler.GAMESCREEN);
				}
				if (currentChoice == 3) { // back to mode select
					currentOptions = 1;
					currentChoice = 0;
				}

			}
		}
	}

	public void keyPressed(int k) {
		if (!loading) { // if game is loading, dont register any commands
			if (k == KeyEvent.VK_ENTER) {
				sfx.play("select"); // select using enter key
				select();
			}
			if (k == KeyEvent.VK_UP && !helpScreen) { // scroll through the array of choices
				sfx.play("navigate");
				currentChoice--;
				if (currentChoice == -1) { // out of selection bounds logic
					if (currentOptions == 0) {
						currentChoice = options.length - 1;
					} else if (currentOptions == 1) {
						currentChoice = gameModes.length - 1;
					} else if (currentOptions == 2){
						currentChoice = maps.length - 1;
					} else if (currentOptions == 3){
						currentChoice = settings.length - 1;
					} else if (currentOptions == 4){
						currentChoice = difficulty.length - 1;
					}
				}
			}
			if (k == KeyEvent.VK_DOWN && !helpScreen) {
				sfx.play("navigate");
				currentChoice++;
				if (currentOptions == 0) { // out of selection bounds logic
					if (currentChoice == options.length) {
						currentChoice = 0;
					}
				} else if (currentOptions == 1) {
					if (currentChoice == gameModes.length) {
						currentChoice = 0;
					}
				} else if (currentOptions == 2){
					if (currentChoice == maps.length) {
						currentChoice = 0;
					}
				} else if (currentOptions == 3){
					if (currentChoice == settings.length){
						currentChoice = 0;
					}
				} else if (currentOptions == 4){
					if (currentChoice == difficulty.length){
						currentChoice = 0;
					}
				}
			}
			if (k == KeyEvent.VK_ESCAPE) { // exit game, asks with a yes no question box
				if (JOptionPane.showConfirmDialog(null, "Are you sure?", "Quitting?",
						JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					System.exit(0);
				}
			}
		}
	}

	public void keyReleased(int k) {}
}
