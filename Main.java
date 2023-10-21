/*
* NAME: Arihan Sharma & Krish Mendapara
* DATE: Sunday, 19th Jun, 2022
* COURSE CODE: ICS 3U1
* PROGRAM DESCRIPTION: This is a recreation of the multiple game, Rooftop Snipers. The game can be played by two players, one a single computer/screen We have mimicked the art style, game fundamentals, as well as the aesthetic details of the game

* The game begins with a title screen. The menu has several animations, such as the title falling down, and creating particles. After the animations have ended, 
* the play button appears, as well as another animation. From the title screen, the user can go to the settings tab. In the settings 
* tab, the user will be able to mute the music, as well as change the number of rounds they want to play the game up to (so if rounds is set to 7, it is first one to 7). 
* Moreover, we have listed our credits of the game in the settings tab.

* If the user presses the play button, a popup menu will appear. This menu too will have animations 
* (they will move from off-screen to on-screen). After the animation is complete, certain components 
* of this popup menu will continue moving (the animations are looped). These components are moving as 
* they require user interaction, and as such trying to get the user’s attention.  The components include:
 - The current score
 - How to play (jump to dodge, hold shoot to raise a weapon, let go of shoot to shoot)
 - The objective (to push the opponent off)
 - Controls for playing (Player 1 Jump: W, Player 1 Shoot: E, Player 2 Jump: I, Player 2 Shoot: O)
* How to ready up (the player first has to confirm that they understand the game. To do this, 
* the players simply press their respective jump buttons to “ready up”. When players 
* press their buttons to ready up, an icon is displayed showing that they are indeed ready

* After both players are ready, it will open up the game screen. The players will 
* spawn in the middle of a building. Their goal is to push the other of the building. 
* To do this, simply shoot the other player off. To do this, aim your gun by holding the shoot key 
* (which progressively rotates the arm in a 360 motion. When your gun is aimed at the other person, let go. 
* When a player gets shot by a bullet, a force vector is applied to them, making them move back at a certain velocity. 
* There is also friction, so if the player is on the ground when they are shot, they only move a slight amount, 
* versus when they are shot in the air where they will move a larger amount. It is important to note that the game 
* has gravity. Moreover, the player doesn’t stand still, instead they “wobble” (a side-to-side motion, where their 
* feet are stuck to the ground but their upper body is free to move). Players can move by pressing jump when the 
* player’s character is facing a certain direction. That way, they jump in their desired direction. This can be 
* used to readjust the position of the player, ensuring that they don’t fall off and prolonging the game. Moreover, the 
* characters of the two players cannot collide. If they try to, they will simply be pushed away from each other 
* (preventing one player from simply pushing another player off). Moreover, there is a camera, that zooms out/in & 
* moves left and right depending on how far the two characters are from each other. Furthermore, the background is 
* made so that it moves at a slower rate than the camera, creating the illusion of the background being the distance. 
* These two components are simply cosmetic details. 

* When a player falls off the edge, time slows down and they slowly fall to the ground. 
* After a certain amount of time, the two players are reset to their original positions and the 
* score of the winner of that round (the person who didn’t get knocked off) is incremented. The players 
* play for a variable amount of rounds, depending on how many they selected in the settings tab. The default is 
* 5 rounds. The winner is the person who reaches the rounds amount first. A JOptionPane appears to 
* congratulate the winner and prompts the players to play again. If the users press: “play again” the game restarts

* The game functions as a combination of a custom physics engine developed by me 
* (relying mostly upon Euler Integration and Angular Velocity in Pendulums) and simply gameplay loop of shooting. 
* The actual shooting itself is calculated through a variety of trigonometric functions, and this 
* data is then passed off to a more high level layer to calculate rounds and such.


*/


import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import javafx.scene.shape.DrawMode;

import java.util.Random;

public class Main extends JPanel implements ActionListener, KeyListener, MouseListener{
	
	//Declaring variables for Main:

	//Initializing the Jframe and Random Function
	private JFrame frame = new JFrame(); 
	private Random rand = new Random();
	
	/* Declaring the timers
	 * The menuTimer is the refresh rate of the menu
	 * The half_second time increments a value every 1/3 of a second. We use this to modify the clicking animations
	 * The particleTimer dictates how long the particlef (in the title animation) exist on screen for
	 * The gameTimer is the refresh rate of the actual game
	 */
	private Timer menuTimer, half_secondsTimer, particleTimer, gameTimer;
	
	//Declares all the image Icons
	private ImageIcon background, settings, title, playButton, clickTemp, currentClick, holder, backButton, muteButton, tempMute, thanks, readyUp, objective, controls, playerButtons, pressButton, ready, to, red_score, blue_score;
	
	//Declares ImageIcon Array (for the button that dictates the number of rounds played)
	private ImageIcon[] roundsButtons = new ImageIcon[3];
	
	//used for resizing all the images
	private Image temp;
	
	//Declaring custom font information
	private Font f;
	private FontMetrics fm;
	
	//Declaring Objects
	private Clouds[] clouds;
	private Particles[] particles;
	private Triangle_Shape triangleShape;
	
	//Boolean values help determine what the paint() function should paint. it also determines whether the music is muted, whether the title animation is done, and whether the players have pressed the ready button
	private boolean menuslide = false, hasLanded = false, settingsslide = false, muted = false, menuInsideMenu = false, player1ready = false, player2ready = false, gameDraw = false, gameStarted = false;
	
	//integer values for the positions of various images (x and y coordinates), the number of rounds, and the scores
	private int title_yPos, half_seconds = 0, muteButton_xPos, muteButton_yPos, roundsButton_xPos, roundsButton_yPos, roundsArray = 0, rounds = 5, readyUp_yPos, readyUp_xPos, controls_xPos, controls_yPos, objective_yPos, objective_xPos, playerButtons_yPos, pressButtonY, blueScore = 0, redScore = 0, alpha = 127;
	
	//double values used for scaling (to ensure consistency & for time intervals0
	private double scaleAmount = 0.6, time = 0.1f;
	
	//custom colour for the cloud object
	private Color cloudBlue = new Color(230, 241, 255), grey, redish_Orange = new Color(247,77,61), transparentBlack = new Color(0, 0, 0, alpha);;

	 
	public Main() {	
		//Timers are set
		menuTimer = new Timer(10, this); 
		half_secondsTimer = new Timer(300, this); 
		gameTimer = new Timer (10, this);

		//Non-resizeable images are sets
		background = new ImageIcon("Images/background.png");
		settings = new ImageIcon("Images/settings_white.png");
		title = new ImageIcon("Images/rooftopSnipers.png");
		
		/*
		 * The follow chunk of code is responsible for setting various images to the desired size
		 */
		
		//Resizes the "Back" Button
		backButton = new ImageIcon("Images/backButton_white.png");
		temp = backButton.getImage().getScaledInstance((int)(135),(int)(55), Image.SCALE_SMOOTH);
		backButton = new ImageIcon(temp);
		
		//Resizes the "Click Animation"
		currentClick = new ImageIcon("Images/click.png");
		temp = currentClick.getImage().getScaledInstance((int)(currentClick.getIconWidth()*scaleAmount),(int)(currentClick.getIconHeight()*scaleAmount), Image.SCALE_SMOOTH);
		currentClick = new ImageIcon(temp);
		
		//This variables hold the other "click animation" frame. The actionperformed() function that alternates between these two frames
		clickTemp = new ImageIcon("Images/unclick.png");
		temp = clickTemp.getImage().getScaledInstance((int)(clickTemp.getIconWidth()*scaleAmount),(int)(clickTemp.getIconHeight()*scaleAmount), Image.SCALE_SMOOTH);
		clickTemp = new ImageIcon(temp);
		
		//Resizes the "Settings" Button
		settings = new ImageIcon("Images/settings_white.png");
		temp = settings.getImage().getScaledInstance(188, 45, Image.SCALE_SMOOTH); 
		settings = new ImageIcon(temp); 
		
		//Resizes the play button
		playButton = new ImageIcon("Images/playbutton.png");
		temp = playButton.getImage().getScaledInstance(160, 165, Image.SCALE_SMOOTH); 
		playButton = new ImageIcon(temp); 
		
		//Resizes the red mute button (indicates the game is not muted)
		muteButton = new ImageIcon("Images/muteButton_Red.png");
		temp = muteButton.getImage().getScaledInstance(800, 75, Image.SCALE_SMOOTH); 
		muteButton = new ImageIcon(temp); 
	
		//Resizes the green mute button (indicates that the game is indeed muted)
		tempMute = new ImageIcon("Images/muteButton_Green.png");
		temp = tempMute.getImage().getScaledInstance(800, 75, Image.SCALE_SMOOTH); 
		tempMute = new ImageIcon(temp); 
		
		//Resizes the "Thanks" text
		thanks = new ImageIcon("Images/Thanks.png");
		temp = thanks.getImage().getScaledInstance(250, 60, Image.SCALE_SMOOTH); 
		thanks = new ImageIcon(temp);  
		
		//Resizes the "Ready up" instructions (explains how to ready up)
		readyUp = new ImageIcon("Images/Ready Up.png");
		temp = readyUp.getImage().getScaledInstance(450, 103, Image.SCALE_SMOOTH); 
		readyUp = new ImageIcon(temp);  
		
		//Resizes the "objective" instructions (explains the objective of the game, i.e how to win)
		objective = new ImageIcon("Images/Objective.png");
		temp = objective.getImage().getScaledInstance(300, 80, Image.SCALE_SMOOTH); 
		objective = new ImageIcon(temp);  
		
		//Resizes the "Controls" instructions (explains the controls to the user)
		controls = new ImageIcon("Images/Controls.png");
		temp = controls.getImage().getScaledInstance(490, 150, Image.SCALE_SMOOTH); 
		controls = new ImageIcon(temp);  
		
		//Resizes the Buttons for the controls (shows the user which buttons to press)
		playerButtons = new ImageIcon("Images/buttonsControls.png");
		temp = playerButtons.getImage().getScaledInstance(1050, 150, Image.SCALE_SMOOTH); 
		playerButtons = new ImageIcon(temp);
		
		//Resizes up the "Press Button" instructions
		pressButton = new ImageIcon("Images/press.png");
		temp = pressButton.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH); 
		pressButton = new ImageIcon(temp);  
		
		//Resizes up the ready icon (indicating the player is ready)
		ready = new ImageIcon("Images/ready.png");
		temp = ready.getImage().getScaledInstance(100, 40, Image.SCALE_SMOOTH); 
		ready = new ImageIcon(temp);  
		
		//Resizes the word "TO" (used for score keeping)
		to = new ImageIcon("Images/to.png");
		temp = to.getImage().getScaledInstance(90, 70, Image.SCALE_SMOOTH); 
		to = new ImageIcon(temp);
		
		//Resizes the score of the red player
		red_score = new ImageIcon("Images/Score/0_red.png");
		temp = red_score.getImage().getScaledInstance(50, 70, Image.SCALE_SMOOTH); 
		red_score = new ImageIcon(temp);
		
		//Resizes the score of the blue player
		blue_score = new ImageIcon("Images/Score/0_blue.png");
		temp = blue_score.getImage().getScaledInstance(50, 70, Image.SCALE_SMOOTH); 
		blue_score = new ImageIcon(temp);
		
		//Declares & resizes the various round buttons (and adds them to the Rounds Buttond Array)
		for (int i = 0; i < roundsButtons.length; i++) {
			roundsButtons[i] = new ImageIcon("Images/roundButton"+i+".png");
			temp = roundsButtons[i].getImage().getScaledInstance(800, 75, Image.SCALE_SMOOTH); 
			roundsButtons[i] = new ImageIcon(temp); 
		}
		
		//Declares custom font
		try {
			f = Font.createFont(Font.TRUETYPE_FONT, new File("Images/Black Block.otf")).deriveFont(40f); 
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment(); 
			ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("Images/Black Block.otf")));
			fm = getFontMetrics(f);
			} catch (Exception e) {}
		
		//Set properties of the frame
		addKeyListener(this);
		addMouseListener(this);
		setFocusable(true);
		requestFocus();
	
		frame.setSize(background.getIconWidth(),background.getIconHeight()); 
		frame.setLocationRelativeTo(null);
		frame.setContentPane(this);
		frame.setTitle("ROOFTOP SNIPERS");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setVisible(true);
		
		//Declares the Clouds Object. Randomly determines the number of clouds in the sky
		clouds = new Clouds[rand.nextInt(6) + 3]; 
		
		//Declares the Particles Object (see method) --> reaosn it is used in method is because the particles are called multiple times
		setParticles();
		
		//Sets the x & y position of various buttons
		muteButton_xPos = 195;
		muteButton_yPos = 235;
		roundsButton_xPos = 195;
		roundsButton_yPos = 235 + muteButton.getIconHeight() + 15;
	
		//Sets the triangle hitbox of play button (using points, then filling in the triangle made by the 3 points)
		triangleShape = new Triangle_Shape(new Point2D.Double(531, 474),  new Point2D.Double(659, 542), new Point2D.Double(529, 610));

		//Declares the Clouds Object. Then, sets the clouds to be a random size, and sets them at a random x and y position (within a certain range, ensuring the clouds are in the "sky" of the background)
		for (int i = 0; i < clouds.length; i++)
		{
			clouds[i] = new Clouds();
			clouds[i].setSize(rand.nextInt(50) + 40, rand.nextInt(30) + 20);
			clouds[i].setX(rand.nextInt(getWidth() - clouds[i].getWidth()));
			clouds[i].setY(rand.nextInt(150) - clouds[i].getHeight());
			
			//Each cloud moves at a random rate
			clouds[i].setSpeed(rand.nextInt(1) + 1);
		}
		
		//Sets the positions of the control images --> method is used as the controls have a changing x and y position and we utilise them multiple things
		setButtonsMenuInsideMenu();
				
		//Starts the menu/title timer (the first "slide" of the game)
		menuTimer.start(); 
		
		//sets the menu/title boolean to true so the paint method actually displays the title slide 
		menuslide = true;

	}
	
	public static void main(String[] args) {
		//New Game
		new Main(); 
	}
	
	public void paint(Graphics g) {
		// Repaints the frame and its components
		super.paint(g);
		
		// Declare and initialize a Graphics2D object
		Graphics2D g2 = (Graphics2D) g;
		
		//If the user is only the menuslide/title slide
		if (menuslide == true) {
			
			//display the background
			g2.drawImage(background.getImage(), 0, 0, this); 
			
			//draw each of the clouds using the custom colour, at their unqiue locations
			for (int i = 0; i < clouds.length; i++)
			{			
				g2.setColor(cloudBlue);
				g2.fill(new Rectangle2D.Double(clouds[i].getX(), clouds[i].getY(), clouds[i].getWidth(), clouds[i].getHeight()));
			}
			
			//draw the settings button and title 
			g2.drawImage(settings.getImage(), getWidth()/2 - settings.getIconWidth()/2, 30, this); 
			g2.drawImage(title.getImage(), getWidth()/2 - title.getIconWidth()/2, title_yPos, this); 
			
			//if the title animation is complete
			if (hasLanded == true) {
				//draw the play button and the click animations
				g2.drawImage(playButton.getImage(), getWidth()/2 - playButton.getIconWidth()/2, 455, this); 
				g2.drawImage(currentClick.getImage(), getWidth()/2 + 80, 495, this); 
				
				//Draw the particles
				for (int i = 0; i < particles.length; i++)
				{		
					//Get the colour of the current particle from the object array. The set the rectangle function to draw the particle (using its custom size, and colouer)
					grey = new Color(particles[i].getColour(), particles[i].getColour(), particles[i].getColour());
					g2.setColor(grey);
					g2.fill(new Rectangle2D.Double(particles[i].getX(), particles[i].getY(), particles[i].getSize(), particles[i].getSize()));
				}				
			}
		}
		
		//If the user is on the settings menu
		if (settingsslide == true) {
			
			//fill the background in white
			g2.setColor(Color.WHITE);
			g2.fill(new Rectangle2D.Double(0, 0, background.getIconWidth(), background.getIconHeight()));
			
			//draw the Back Button
			g2.drawImage(backButton.getImage(), 40, 20, this); 
			
			//draw the shadow
			g2.setColor(Color.GRAY);
			g2.fill(new Rectangle2D.Double(200, 240, muteButton.getIconWidth(), muteButton.getIconHeight()));
			g2.drawImage(muteButton.getImage(), muteButton_xPos, muteButton_yPos, this); 
			
			g2.fill(new Rectangle2D.Double(200, 240 + roundsButtons[roundsArray].getIconHeight() + 15, roundsButtons[roundsArray].getIconWidth(), roundsButtons[roundsArray].getIconHeight()));
			g2.drawImage(roundsButtons[roundsArray].getImage(), roundsButton_xPos, roundsButton_yPos, this); 
			
			//Draw the thanks text
			g2.drawImage(thanks.getImage(), getWidth()/2 - thanks.getIconWidth()/2, 180 + (roundsButtons[roundsArray].getIconHeight() + 20)*2 + 100, this); 
			
			//Use custom font
			g2.setFont(f); 
			
			//Set colour of font to custom colour. Then write the appreciation text
			g2.setColor(redish_Orange);
			
			//String manipulation --> Easter egg for Mr. Conway to see
			String text = (("mr. conway, our amazing teacher, without whom this would not be possible. "
					+ "thank you for understanding and allowing us to submit our game a "
					+ "little after the deadline. This is an easter egg. Hope you like it :) --> From your two favourite students (we hope lol)").toUpperCase()).substring(0, 33);
			
			g2.drawString(text, (getWidth()/2 - fm.stringWidth(text) / 2), 555);
			
			//Change font of the text
			Font newFont = f.deriveFont(f.getSize() / 1.33F);
			g2.setFont(newFont);
			
			//change colour of the font
			g2.setColor(Color.BLUE);
			
			//More String manipulation because I was too lazy to retype "Created By" in caps so I used the function
			String more_text = ("Created By").toUpperCase();
			g2.drawString(more_text, 800, 100);
			
			//change size of the font
			newFont = f.deriveFont(f.getSize() / 0.8F);
			g2.setFont(newFont);
			g2.drawString("AK-47 Games", 800, 140);
		}	
		
		//If the user is on the game
		if (gameDraw == true) {
			
			//display the background
			g2.drawImage(background.getImage(), 0, 0, this); 
			
			//if the user has not yet "readied up"
			if (menuInsideMenu == true) {
				//alpha = 127; --> 50% transparent
				
				//Dim the background of the game by drawing a transparent rectangle over the
				g2.setColor(transparentBlack);
				g2.fill(new Rectangle2D.Double(0, 0, getWidth(), getHeight()));
				
				//Draws the back buttons
				g2.drawImage(backButton.getImage(), 40, 20, this); 
				
				//Draws the Controls, The objective and how to ready up
				
				//The sine function is used to create the ready up animation (to make it "jiggle" from side to side, hence why it is in the x-value) 
				g2.drawImage(readyUp.getImage(), readyUp_xPos + (int)(Math.sin(time)*30), readyUp_yPos, this); 
				g2.drawImage(controls.getImage(), controls_xPos, controls_yPos, this); 
				g2.drawImage(objective.getImage(), objective_xPos, objective_yPos, this); 
				g2.drawImage(playerButtons.getImage(), getWidth()/2 - playerButtons.getIconWidth()/2, playerButtons_yPos, this); 
				
				//If the first player is ready, draw that they are ready. Otherwise, display an arrow indicating the user to press the ready button
				if (player1ready == true) {
					g2.drawImage(ready.getImage(), 220, pressButtonY + 20, this); 
				} else {
					//The sin function is used to create the button animation (to make it "jiggle" up to down, hence why it is in the y-value)
					g2.drawImage(pressButton.getImage(), 135, pressButtonY + (int)(Math.sin(time)*15), this); 
				}
				//If the first player is ready, draw that they are ready. Otherwise, display an arrow indicating the user to press the ready button
				if (player2ready == true) {
					g2.drawImage(ready.getImage(), getWidth()/2 + 187 + 85, pressButtonY + 20, this); 
				} else {
					//The sin function is used to create the button animation (to make it "jiggle" up to down, hence why it is in the y-value)
					g2.drawImage(pressButton.getImage(), getWidth()/2 + 187, pressButtonY + (int)(Math.sin(time)*15), this); 
				}
				
			}
			
			//Draw the Score, and the "TO" text regardless if the menu is displaying or not
			g2.drawImage(to.getImage(), getWidth()/2 - to.getIconWidth()/2, 20, this); 
			g2.drawImage(red_score.getImage(), getWidth()/2 - to.getIconWidth()/2 - red_score.getIconWidth(), 20, this); 
			g2.drawImage(blue_score.getImage(), getWidth()/2 - to.getIconWidth()/2 + blue_score.getIconWidth() + 40, 20, this);
		}
	}
	
	public void actionPerformed(ActionEvent e) {
		// if the user is on the menu
		if (e.getSource() == menuTimer) {
			
			//If the title has reached the y-position of 232, stop title animation, start the clicking animation & particl movement (by turning on the respetive timers)
			if (title_yPos >= 232) {
				hasLanded = true;
				half_secondsTimer.start();
				particleTimer.start(); 
			} 
			//If not, then use a lerp/logarithmic function to move the title (to create animation)
			else {
				title_yPos = (int)lerp((float)title_yPos, 250f, 0.055f); 
			}
			
			//For each cloud:
			for (int i = 0; i < clouds.length; i++)
			{
				//Move the clouds
				clouds[i].move();
				
				//if the cloud's x-position is past the left of the screen, reset the x to be past the right of the screen & the y to a random position (so we can have clouds infinitely moving across the screen)
				if (clouds[i].getX() + clouds[i].getWidth() <= 0) {
					clouds[i].setX(rand.nextInt(getWidth()) + getWidth());
					clouds[i].setY(rand.nextInt(150) - clouds[i].getHeight());
					clouds[i].setSize(rand.nextInt(50) + 40, rand.nextInt(30) + 20);
				}
			}
			
		}
		//If the timer for the particles is running
		if (e.getSource() == particleTimer) {
			//Moves the particles
			particles();
		}
		//If the halfstimer has started and the menu slide is on
		if (e.getSource() == half_secondsTimer && menuslide == true) {
			//Increment the number of halfseconds
			half_seconds++;
			
			//Each other half second, change the frame of the click animation to the next other, and infinitely cycle this pattern
			if (half_seconds%2 == 0) {
				//used to swap the clicking images, with holder being used as a temp holder to avoid the images overwriting each other
				holder = currentClick;
				currentClick = clickTemp;
				clickTemp = holder;
			}
			
		}
		//if the game has started
		if (e.getSource() == gameTimer) {
			//if the game is displaying the controls/menu
			if (menuInsideMenu == true) {
				
				//Linearly moves the ready up text, the controls text and the objective text to their respective positions
				//Creates animation for text
				if (readyUp_yPos >= 100) {
					readyUp_xPos += 0;
				} else {
					readyUp_yPos += 30;
				}
				if (controls_xPos >= getWidth()/2 - controls.getIconWidth()/2) {
					controls_xPos += 0;
				} else {
					controls_xPos += 30;
				}
				if (objective_xPos <= getWidth()/2 - objective.getIconWidth()/2) {
					objective_xPos += 0;
				} else {
					objective_xPos -=30;
				}
				//increases the time, so that the sine functions change as time increases (mimics the sinx function)
				time += 0.07f;
				
				//If both players are ready
				if (player1ready == true && player2ready == true) {
					//end the menu
					menuInsideMenu = false;
				}
			}
			else {
				//Game
			}
			
			//Sets and draws the score for the players
			setScore(redScore, blueScore);
		}
		
		repaint(); 
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mousePressed(MouseEvent e) {
		
		//Gets the current x-position and y-position of the mouse
		int x = e.getX();
		int y = e.getY();

		//If the mouse clicked on the settings buttons
		if (x >= getWidth()/2 - settings.getIconWidth()/2 && x <= getWidth()/2 + settings.getIconWidth()/2 && y >= 30 && y <= 30 + settings.getIconHeight() && menuslide == true) {
			//Pressing animation: dim the settings button so that the text becomes grey
			settings = new ImageIcon("Images/settings_grey.png");
			
			//Scaling the "Settings" to the adequate amount
			Image temp = settings.getImage().getScaledInstance(188, 45, Image.SCALE_SMOOTH); 
			settings = new ImageIcon(temp); 
		}
		
		//If the mouse clicked on the back buttons
		if (x >= 40 && x <= 40 + backButton.getIconWidth() && y >= 20 && y <= 20 + backButton.getIconHeight() && (settingsslide == true || menuInsideMenu == true)) {
			//Pressing animation: dim the bacj button so that the text becomes grey
			backButton = new ImageIcon("Images/backButton_grey.png");
		
			//Scaling the "Back" to the adequate amount
			Image temp = backButton.getImage().getScaledInstance(135, 55, Image.SCALE_SMOOTH); 
			backButton = new ImageIcon(temp); 
		}
		
		//If the mouse clicks the mute button
		if (x > muteButton_xPos && x <= muteButton_xPos + muteButton.getIconWidth() && y >= muteButton_yPos && y <= muteButton_yPos + muteButton.getIconHeight() && settingsslide == true) {
			//Shift the xPos & yPos of the mute button to create a pressing animation
			muteButton_xPos+=5;
			muteButton_yPos+=5;
		}
		//If the mouse clicks the rounds button
		if (x > roundsButton_xPos && x <= roundsButton_xPos + roundsButtons[roundsArray].getIconWidth() && y >= roundsButton_yPos && y <= roundsButton_yPos + roundsButtons[roundsArray].getIconHeight() && settingsslide == true) {
			//Shift the xPos & yPos of the rounds button to create a pressing animation
			roundsButton_xPos+=5;
			roundsButton_yPos+=5;
		}
		
		//Creates rectangle hitbox of the mouse that 1x1 pixel (so we can use intersect method which only takes rectangle2D as input)
		Rectangle2D mouse = new Rectangle2D.Double(x, y, 1, 1); 
		
		
		//If the mouse clicks the play button
		if (triangleShape.intersects(mouse)) {
			
			//Turn off the menu slide, turn on the objective/controls slide
			menuslide = false;
			menuInsideMenu = true;
			
			//Stop menu & particle timers
			menuTimer.stop();
			particleTimer.stop();
			
			//Turn on the game slide & timer
			gameDraw = true;
			gameTimer.start();
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		//Gets the current x-position and y-position of the mouse
		int x = e.getX();
		int y = e.getY();

		//If the mouse releases the settings buttons
		if (x >= getWidth()/2 - settings.getIconWidth()/2 && x <= getWidth()/2 + settings.getIconWidth()/2 && y >= 30 && y <= 30 + settings.getIconHeight() && menuslide == true) {
			//Completes Pressing animation: brightens the settings button so that the text becomes white
			settings = new ImageIcon("Images/settings_white.png");
		
			//Scaling the "Settings" to the adequate amount
			Image temp = settings.getImage().getScaledInstance(188, 45, Image.SCALE_SMOOTH); 
			settings = new ImageIcon(temp); 
			
			//Stops particle timer & opens settings
			particleTimer.stop();
			settings();
		}
		
		//If the mouse releases the back buttons
		if (x >= 40 && x <= 40 + backButton.getIconWidth() && y >= 20 && y <= 20 + backButton.getIconHeight() && (settingsslide == true || menuInsideMenu == true)) {
			//Completes Pressing animation: brightens the back button so that the text becomes white
			backButton = new ImageIcon("Images/backButton_white.png");
		
			//Scaling the "Back" to the adequate amount
			Image temp = backButton.getImage().getScaledInstance(135, 55, Image.SCALE_SMOOTH); 
			backButton = new ImageIcon(temp); 
			
			//Since the back button is used twice (with its purpose being the same, to go to the menu/title slide), we can make its function constants both times
			
			//Turn off the popup menu (objectives/controls, etc) & settings slides
			menuInsideMenu = false;
			settingsslide = false;
			
			//Stop drawing the game
			gameDraw = false;
			
			//stops the game & menu timer
			gameTimer.stop();
			menuTimer.start();
			
			//Resets the original positions of the buttons (pre-animation)
			setButtonsMenuInsideMenu();
			
			//Resets the position of the title before the falling animation. Also sets the boolean hasLanded to false it doesn't display the play button prematurely
			title_yPos = -(title.getIconHeight() + 20);
			hasLanded = false;
			
			//Resets particles & title
			setParticles();
			particles();
			title();
			
			
		}
		
		//If the user player presses the mute button, shift the xPos and yPost to create pressing animation
		if (x > muteButton_xPos && x <= muteButton_xPos + muteButton.getIconWidth() && y >= muteButton_yPos && y <= muteButton_yPos + muteButton.getIconHeight() && settingsslide == true) {
			muteButton_xPos-=5;
			muteButton_yPos-=5;
			
			//redraw the slide to make animation smooth
			repaint();
			
			//To switch between red and green muted buttons, we use a temp "Holder" that allows us to continuously swap the two variables without overwriting the other
			holder = muteButton;
			muteButton = tempMute;
			tempMute = holder;
			
			//Determiens if music is muted or not
			muted = !(muted);
			
		}
		
		//If the user player presses the rounds button, shift the xPos and yPost to create pressing animation
		if (x > roundsButton_xPos && x <= roundsButton_xPos + roundsButtons[roundsArray].getIconWidth() && y >= roundsButton_yPos && y <= roundsButton_yPos + roundsButtons[roundsArray].getIconHeight() && settingsslide == true) {

			roundsButton_xPos-=5;
			roundsButton_yPos-=5;
			
			
			//if the user presses the buttons again and it is currently on 11 rounds, it will loop back to the first button (5 rounds) 
			if (roundsArray >= 2)  {
				roundsArray = -1;
			}
			//changes the numberr of the rounds
			roundsArray++;
			
			//Correlates the rounds array to the actual number of roudns each button represent (i.e the 0th button represents 5 rounds0 
			if (roundsArray == 0) {
				rounds = 5;
			} else if (roundsArray == 1) {
				rounds = 7;
			} else {
				rounds = 11;
			}
			
			//redraw the slide to make animation smooth
			repaint();
		}
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (gameStarted) return; 
		
		//If player 1 presses W and the popup menu is open, set player 1 to ready
		if (e.getKeyCode() == KeyEvent.VK_W && menuInsideMenu == true) {
			player1ready = true;	
			
		}
		//If player 2 presses I and the popup menu is open, set player 1 to ready
		if (e.getKeyCode() == KeyEvent.VK_I && menuInsideMenu == true) {
			player2ready = true;
		}
		
		//If both players r ready
		if (player1ready && player2ready) {
			//RooftopSnipers.StartGame(); 
			
			//makes frame invisible
			frame.setVisible(false);
			
			//Starts the game
			gameStarted = true; 
			
			//Ends all timers inside Main 
			gameTimer.stop(); 
			half_secondsTimer.stop(); 
			menuTimer.stop(); 
			particleTimer.stop(); 

			//Start game
			RooftopSnipers rooftopSnipers = new RooftopSnipers(rounds, muted); 
			//rooftopSnipers.StartGame(rounds, muted); 
			
			
		}
	}
	
	public float lerp(float a, float b, float f) {
		//a is the current position
		//b is the ending position
		//f is the speed (changes over time --> log function)
		return a + f * (b + a); 
	}
	
	public void settings() {
		//If the user presses settings, stop displaying the menu slide & display settings slide
		menuslide = false;
		settingsslide = true;
	}
	
	public void title() {
		//If the user presses back (to go to title), stop displaying the settings slide & display title slide
		settingsslide = false;
		menuslide = true;
	}
	
	public void particles() {
		//Move every particle in the particle array
		for (int i = 0; i < particles.length; i++)
		{
			particles[i].move();	
		}
	}
	
	public void setParticles() {
		//Declares particle timer (determines how fast the particles update)
		particleTimer = new Timer(30, this); 
		
		//Spawns 70 particles in
		particles = new Particles[70];
		
		//For every particle in the particle array, randomly set them at a random x and y position
		for (int i = 0; i < particles.length; i++)
		{
			particles[i] = new Particles();
			particles[i].setX(rand.nextInt(title.getIconWidth()) + (getWidth()/2 - title.getIconWidth()/2 - 30));
			particles[i].setY(rand.nextInt(60) + 190 + title.getIconHeight());
			
			//The horizontal & vertical speed is between 2-13, 
			particles[i].setSpeed(rand.nextInt(13) + 2, rand.nextInt(13) + 2);
		}
	}
	
	public void setButtonsMenuInsideMenu() {
		//Default values of all the image icons in the objective/control slides (pre-animation, off screen)
		title_yPos = -(title.getIconHeight() + 20);
		readyUp_yPos = -(readyUp.getIconHeight());
		readyUp_xPos = getWidth()/2 - readyUp.getIconWidth()/2;
		controls_yPos = 100 + readyUp.getIconHeight();
		controls_xPos = (-controls.getIconWidth());
		objective_yPos = controls_yPos + controls.getIconHeight();
		objective_xPos = (getWidth() + objective.getIconWidth());
		playerButtons_yPos = objective_yPos + 70 + objective.getIconHeight();
		pressButtonY = playerButtons_yPos - pressButton.getIconHeight() - 10;
	}
	
	public void setScore(int redScore, int blueScore) {
		//Sets the score of the user according to their current score, then resizes the image
		//Changes the red player/player 1's score
		red_score = new ImageIcon("Images/Score/" + redScore +"_red.png");
		temp = red_score.getImage().getScaledInstance(50, 70, Image.SCALE_SMOOTH); 
		red_score = new ImageIcon(temp);
		
		//Changes the blue player/player 2's score
		blue_score = new ImageIcon("Images/Score/" + blueScore +"_blue.png");
		temp = blue_score.getImage().getScaledInstance(50, 70, Image.SCALE_SMOOTH); 
		blue_score = new ImageIcon(temp);

	}
}

//Class that creates triangle hitboxe
class Triangle_Shape extends Path2D.Double {
	
	//Takes as input 3 points (hence the [...])
    public Triangle_Shape(Point2D... points) {
    	//Start at point 0
        moveTo(points[0].getX(), points[0].getY());
        
        //draw a line from point 0 to point 1
        lineTo(points[1].getX(), points[1].getY());
        
        //draw a line from point 1 to point 2
        lineTo(points[2].getX(), points[2].getY());
        
        //Fill triangle made using 3 points
        closePath();
    }
}
