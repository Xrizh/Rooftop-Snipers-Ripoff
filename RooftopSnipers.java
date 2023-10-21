

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.awt.Rectangle; 
import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Calendar;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*; 

public class RooftopSnipers extends JPanel implements ActionListener, KeyListener {
	//One timer to control everything
	Timer gameTick; 
	
	//Declare frame
	public JFrame frame; 
	
	//Intialize two player objects
	Player player1 = new Player(), player2 = new Player(); 
	
	//Declaring some images for the backdrop
	ImageIcon bgImg = new ImageIcon("sky_frame_3.png"); 
	ImageIcon buildingImg = new ImageIcon(new ImageIcon("building.png").getImage().getScaledInstance(4200/3, 4200/3, Image.SCALE_SMOOTH));
	
	//Rectangles to store colliders
	Rectangle2D groundCollision, playerCollision; 
	
	//Some integers and floats to store critical data regarding camera zoom/pan
	int xOffset = 0, yOffset = 0, groundYOffset, groundWidthMultiplier, startY = 175, startOffset; 
	float zoomLevel = 1f; 
	
	//A variable to calculate when each round should reset
	long respawnStartTime; 
	
	//Some variables to track player spawning, shooting, collision, etc. 
	boolean trackY = false, respawning = false, shooting = false, shooting2 = false, hit = false, hit2 = false, muted = false; 
	
	//Some variables to keep track of frames elapsed since a bullet was shot, player tracking, and score
	int framesShot = 0, framesShot2 = 0, trackPlayer = 0, p1Score = 0, p2Score = 0, rounds = 5; 
	
	//The lines made by the arc of the bullets
	Path2D line = new Path2D.Double(), line2 = new Path2D.Double(); 
	
	//Rectangle2D intersectionRect = new Rectangle2D.Double(); 
	
	//Some images to display the score
	ImageIcon p1ScoreImg = new ImageIcon("Score/0_blue.png"), p2ScoreImg = new ImageIcon("Score/0_red.png"), to = new ImageIcon(new ImageIcon("Images/to.png").getImage().getScaledInstance((int)(154/1.3), (int)(112/1.3), Image.SCALE_SMOOTH)); 
	
	//Method to start the game from an external class
	/*public void StartGame(int _rounds, boolean _muted) {
		//Start a new game and set settings
		
		new RooftopSnipers(_rounds, _muted); 
		//muted = _muted; 
		//rounds = _rounds; 
	}*/
	
	//Method to "respawn" each player at their respective positions
	void Spawn() {
		player1 = new Player(); 
		//player1.setImage(1);
		player1.chooseRandomCharacter(); 
		player1.setLocation(-50, startY); 
		
		player2 = new Player(); 
		//player2.setImage(2);
		player1.chooseRandomCharacter();
		player2.setLocation(750, startY);
		player2.changeDir(); 
	}
	
	//Default constructor
	public RooftopSnipers(int _rounds, boolean _muted) {
		//Assigning the frame
		frame = new JFrame(); 
		
		//Takes the values of muted (is the muted button on or off) and the number of rounds
		muted = _muted; 
		rounds = _rounds; 
		
		//Plays music, taking whether or not the game is muted or not
		playMusic(muted);
				
		//Settings for the first frame
		frame.setContentPane(this);
		frame.setTitle("Rooftop Snipers Ripoff");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setVisible(true);
		frame.setSize(1187, 678); 
		setBackground(Color.decode("#6dd9e8"));
		//setBackground(Color.RED); 
		
		//Once settings are set, spawn in the players
		Spawn(); 
		
		//Camera pan offset
		startOffset = (player1.getXWithoutOffset()+player2.getXWithoutOffset())/2; 
		
		//Start game timer
		gameTick = new Timer(1000/240, this); 
		gameTick.start(); 
		
		//Some method calls to make sure the window is in focus and the keylistener works
		addKeyListener(this);
		setFocusable(true);
		requestFocus(); 
	}
	
	//Paint function; renders graphics to the screen
	public void paint(Graphics g) {
		//Make sure the frame actually paints to the graphics object
		super.paint(g);
		Graphics2D g2 = (Graphics2D) g; 
		
		//This portion of the code acts as a very simple camera controller
		if (trackPlayer == 1) { //If the first player is falling
			//Calculate the translation for the camera to focus on them using offsets
			yOffset = (int)lerp(yOffset, 100-player1.getY(), 0.1f); 
			xOffset = (int)lerp(xOffset, 1500-player1.getX(), 0.05f); 
			
			//Keep zooming in before reaching a stopping threshold
			zoomLevel += 0.002f; 
			zoomLevel = clamp(zoomLevel, 0.5f, 1.2f); 
			
			//Calculate ground collider
			groundYOffset = buildingImg.getIconHeight(); 
			groundWidthMultiplier = 2; 
			setBackground(Color.GRAY);
		} else if (trackPlayer == 2) { //If the second player is falling
			//Do the EXACT same thing as before but for player 2 instead this time
			
			yOffset = (int)lerp(yOffset, 100-player2.getY(), 0.1f); 
			xOffset = (int)lerp(xOffset, -450-player2.getX(), 0.05f); 
			zoomLevel += 0.002f; 
			zoomLevel = clamp(zoomLevel, 0.5f, 1.2f); 
			groundYOffset = buildingImg.getIconHeight(); 
			groundWidthMultiplier = 2; 
			setBackground(Color.GRAY);
		} else { //If the game is being played as normal
			//Keep the y offset as zero
			yOffset = 0; 
			//Horizontally, the camera should always be centered between the two players
			xOffset = (int)lerp(xOffset, startOffset - (player1.getXWithoutOffset()+player2.getXWithoutOffset())/2, 0.05f);
			
			//Zoom out a little bit if the players exit the camera's frame of vision using a custom lerp function (interpolation)
			zoomLevel = lerp(zoomLevel, 1 - (player2.getX() - player1.getX() - 600) / 5000f, 0.05f); 
			zoomLevel = clamp(zoomLevel, 0.2f, 1f); 
			
			groundYOffset = 0; 
			groundWidthMultiplier = 1; 
			setBackground(Color.decode("#6dd9e8"));
		}
		
		//To zoom out the camera, first move it to it's "origin" and then scale, since by default the top-left is assumed as the anchor point
		g2.translate(getWidth() * (1f-zoomLevel)/2f, getHeight() * (1f-zoomLevel)/2f);
		g2.scale(zoomLevel, zoomLevel); 
		
		//Draw the background offsetted by the camera's offset (pan)
		g2.drawImage(bgImg.getImage(), -1500 + (int)(xOffset / 2f), 80 + yOffset, this);
		
		//Since the camera is moving according to the player, we don't want to offset them according to the camera, since that would end in a recursive feebdack loop, so we draw them normally but offset their backend position with the camera offset
		player1.setOffset(xOffset); 
		g2.drawImage(player1.getImage().getImage(), player1.getX(), player1.getY() + yOffset, this); 
		
		//Same for player 2
		player2.setOffset(xOffset);
		g2.drawImage(player2.getImage().getImage(), player2.getX()+480, player2.getY() + yOffset, -player2.getImage().getIconWidth(), player2.getImage().getIconHeight(), this); 
		
		//Draw the building
		g2.drawImage(buildingImg.getImage(), -100 + xOffset, 200 + yOffset, this); 
		
		//Code for debugging (needed because it's so frequent)
		//g2.draw(player1.getCollisionBounds()); 
		//g2.draw(player2.getCollisionBounds());
		
		//Check if the player is falling. If so, make the ground at the bottom of the building. Otherwise, make it at the top. 
		if (trackPlayer != 0) groundCollision = new Rectangle2D.Double(-1000, buildingImg.getIconHeight() - 116, 5000, 15);
		else groundCollision = new Rectangle2D.Double((getWidth() - buildingImg.getIconWidth())/2f + xOffset + 12, 510, buildingImg.getIconWidth(), 15); 
		//g2.draw(groundCollision); 
		
		if (framesShot < 24) { //I want the bullet's trail to disappear after 24 frames, so I simply keep track of them manually like this
			g2.setStroke(new BasicStroke(5f)); 
			g2.setColor(new Color(255, 255, 255, (int)((24-framesShot)*7))); //Fade the trail out over the course of the 24 frames
			
			//Draw the trail
			g2.draw(line); 
			
			//Set the state of the player as shooting to false and increase the amount of "frames shot"
			shooting = false; 
			++framesShot; 
		} else {
			//If this is not the case, make sure the player knows they're not hit, and reset the bullet's arc to be uninitialized
			hit = false; 
			line = new Path2D.Double(); 
		}
		
		//Do the EXACT same thing as before but this time for player 2
		if (framesShot2 < 24) {
			g2.setStroke(new BasicStroke(5f)); 
			g2.setColor(new Color(255, 255, 255, (int)((24-framesShot2)*7))); 
			
			g2.draw(line2); 
			shooting2 = false; 
			++framesShot2; 
		} else {
			hit2 = false; 
			line2 = new Path2D.Double(); 
		}
		
		//Draw the arms as simple peach blocks
		g2.setColor(new Color(221, 172, 127)); 
		g2.fill(player1.getArm()); 
		g2.fill(player2.getArm()); 
		
		//Scale down the images used to display the score
		p1ScoreImg = new ImageIcon(new ImageIcon("Score/"+p1Score+"_blue.png").getImage().getScaledInstance((int)(70/1.2), (int)(100/1.2), Image.SCALE_SMOOTH)); 
		p2ScoreImg = new ImageIcon(new ImageIcon("Score/"+p2Score+"_red.png").getImage().getScaledInstance((int)(70/1.2), (int)(100/1.2), Image.SCALE_SMOOTH));
		
		if (trackPlayer == 0) { //If the player is not falling
			//Display the score
			g2.drawImage(p1ScoreImg.getImage(), 445, 20, this); 
			g2.drawImage(to.getImage(), getWidth()/2-70, 20, this); 
			g2.drawImage(p2ScoreImg.getImage(), getWidth()-530, 20, this);
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (trackPlayer != 0) return; //If the player is falling, don't take any input
		
		if (e.getKeyCode() == KeyEvent.VK_W && player1.grounded()) { //If player 1 is on the ground and presses W, jump
			player1.jump(8f); 
		}
		
		if (e.getKeyCode() == KeyEvent.VK_I && player2.grounded()) { //Same for player 2
			player2.jump(8f);
		}
		
		if (e.getKeyCode() == KeyEvent.VK_E) { //If the E key is pressed, make player 1 start rotating his arm
			player1.startArmRotation(); 
		}
		
		if (e.getKeyCode() == KeyEvent.VK_O) { //Same for player 2
			player2.startArmRotation(); 
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (trackPlayer != 0) return; //No input if playing is falling
		
		if (e.getKeyCode() == KeyEvent.VK_E) { //If the e key is released
			//Set the "framesshot" to zero (refer above)
			framesShot = 0; 
			
			//Set the line to the player's shooting vector
			line = player1.shoot(); 
		}
		
		if (e.getKeyCode() == KeyEvent.VK_O) { //Same for the second player
			framesShot2 = 0; 
			
			line2 = player2.shoot(); 
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (p1Score == rounds) { //If the player 1's score is equal to the game win condition
			//Ask the player if they want to play again
			String[] buttons = {"Yes", "No"}; 
			int playAgain = JOptionPane.showOptionDialog(null, "Blue wins!\nPlay again?", "GAME OVER", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, buttons, null);
			
			if (playAgain == 0) { //If so
				//Reset the game
				p1Score = 0; 
				p2Score = 0; 
				Spawn(); 
			} else System.exit(0); //Otherwise just exit
		} else if (p2Score == rounds) { //Same for player 2's score
			String[] buttons = {"Yes", "No"}; 
			int playAgain = JOptionPane.showOptionDialog(null, "Red wins!\nPlay again?", "GAME OVER", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, buttons, null);
			
			if (playAgain == 0) {
				p1Score = 0; 
				p2Score = 0; 
				Spawn(); 
			} else System.exit(0);
		}
		
		if (respawning && ZonedDateTime.now().toInstant().toEpochMilli() - respawnStartTime >= 3000) { //If any player was set to respawn, and 3 minutes have passed since that was originally set
			//Move on to a new round by respawning the players
			Spawn(); 
			trackPlayer = 0; 
			respawning = false; 
		}
		
		if (player1.getCollisionBounds().intersects(player2.getCollisionBounds().getBounds2D())) { //If the players collide with each other
			//Call their respective functions for resolving collisions
			player1.collision(); 
			player2.collision(); 
		}
		
		//PLAYER 1
		if (player1.getY() >= 520f && trackPlayer != 1) { //If player 1 falls off the edge and the other one is not currently being tracked
			//Track player 1 with the camera
			trackY = true; 
			trackPlayer = 1; 
			
			//Set his velocity to an amount set to simulate time slowing down
			player1.setVelocity(0f, -1f);
			player1.setGravity(-1f);
		} 
		
		if (line2.intersects(player1.getCollisionBounds().getBounds2D()) && !hit2) { //If player 1 is hit by the bullet arc from player 2
			//Call his methods for resolving collisions
			//player1.setVelocity(-5f, 2f);
			player1.shot(); 
			player1.addAngular(2f);
			
			//Set him to having been hit, so he cant be hit again with the same bullet arc
			hit2 = true; 
		}
		
		player1.bob(); //Rotate the player based on his angular velocity
		player1.move(); //Move the player based on his linear velocity components
		
		if (!player1.getCollisionBounds().intersects(groundCollision)) { //If player 1 is touching the ground
			player1.isGrounded = false; //Set his grounded state to false? This works because we're going to reset it later based on some parameters
		}
		
		if ((player1.getCollisionBounds().intersects(groundCollision) && !player1.grounded()) | trackPlayer == 2) { //If player 1 is touching the ground or player 2 is being tracked
			player1.ground(); //Ground him
			
			if (trackPlayer == 1) { //If HE is the one being tracked, this means he must have hit the road
				//Set him to respawn (refer above)
				respawning = true; 
				respawnStartTime = ZonedDateTime.now().toInstant().toEpochMilli();
				
				//Increase the other player's score
				++p2Score; 
			}
		}
		
		if (player1.armRotating) { //If his arm is supposed to be rotating
			player1.rotateArm(-5f); //Rotate it! 
		} else if (player1.getArmRot() < 90f) { //Otherwise
			player1.rotateArm(4f); //Move it down to a resting position
		}
		
		//PLAYER 2 - EXACT SAME AS PLAYER 1 BUT USING PLAYER2'S VARIABLES
		if (player2.getY() >= 520f && trackPlayer != 2) {
			trackY = true; 
			trackPlayer = 2; 
			player2.setVelocity(0f, -1f);
			player2.setGravity(-1f); 
		}
		
		if (line.intersects(player2.getCollisionBounds().getBounds2D()) && !hit) {
			//player2.setVelocity(-10f, 2f);
			player2.shot(); 
			player2.addAngular(2f);
			hit = true; 
		}
		
		player2.bob(); 
		player2.move();
		
		if (!player2.getCollisionBounds().intersects(groundCollision)) {
			player2.isGrounded = false; 
		}
		
		if ((player2.getCollisionBounds().intersects(groundCollision) && !player2.grounded()) | trackPlayer == 1) {
			player2.ground(); 
			
			if (trackPlayer == 2) {
				respawning = true; 
				respawnStartTime = ZonedDateTime.now().toInstant().toEpochMilli(); 
				++p1Score; 
			}
		}
		
		if (player2.armRotating) {
			player2.rotateArm(5f);
		} else if (player2.getArmRot() > 90f){
			player2.rotateArm(-4f);
		}
		
		repaint(); 
	}
	
	public static void main(String[] args) throws LineUnavailableException, UnsupportedAudioFileException, IOException { //When the program starts
		//new RooftopSnipers(muted,rounds); //Call the constructor
		
		//Play the audio
		//playMusic(muted);
	}
	
	//A function for interpolating between two points based on some given speed
	float lerp(float a, float b, float f)
	{
	    return a + f * (b - a);
	}
	
	public void playMusic(boolean muted) {
		File file = new File("BGMusic.wav"); 
		AudioInputStream audioStream;
		try {
			audioStream = AudioSystem.getAudioInputStream(file);
			Clip clip = AudioSystem.getClip(); 
			clip.open(audioStream); 
			
			if (muted != true) {
				clip.start();
			}
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}
	
	//Basic clamp function
	public static float clamp(float val, float min, float max) {
	    return Math.max(min, Math.min(max, val));
	}
}