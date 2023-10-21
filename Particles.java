import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.*;

public class Particles implements ActionListener{
	
	//Declaring variables for Particles Object
	
	//Intializing integers values such as position (coordinates), speed (horizontal and veritical), the maximun size of the maximum size of the particle, the current size, the growrate and the how fast the colour changes
	private int xPos, yPos, sizeCurrent, sizeMax, Xspeed, Yspeed, ticks, dir, growrate, colourshiftRate;
	
	//CONSTANTS FOR DIRECTION: Determines the direction the particle is traveling in
	private final int LEFT_UPWARDS = 1, LEFT_DOWNWARDS = 2, RIGHT_UPWARDS = 3, RIGHT_DOWNWARDS = 4;
	
	//Declaring custom color
	private int colour;
	
	//Declaring timer for particles. This is used to make the particles grow, shrink, & change colour 
	private Timer Timer;
	
	//Declaring random function (for direction)
	private Random rand = new Random();
	
	Particles () {
		
		//Setting the default values
		
		//Default position of the particles
		xPos = 0;
		yPos = 0;
		
		//Default speed of the particles
		Yspeed = 0;
		Xspeed = 0;
		
		//Grow rate of the particles
		growrate = 4;
		
		//How fast the particles change colour
		colourshiftRate = 4;
		
		//The direction of the particle
		dir = rand.nextInt(4)+1;
		
		//Ticks (used to smooth the particle animation and reduce any delay)
		ticks = 0;
		
		//Current size of the particles
		sizeCurrent = 0;
		
		//Maximum size of the particles
		sizeMax = rand.nextInt(10) + 20;
		
		//Default colour of particle
		colour = 200;
		
		//Timer --> since the main function is painting the particles move, there is a delay between the actual movement of the particles
		// and the new position that appear on the screen. The timer is used to reduce this delay, as well as add grow/shrink rate & colour change
		Timer = new Timer(10, this); 
		
		//Start the timer
		Timer.start();

	}

	//Set the x of the particle
	public void setX(int x) {
		 xPos = x;
	 }
	
	//Set the y of the particles
	 public void setY(int y) {
		 yPos = y;
	 }
	
	//Set the speed of the particles
	 public void setSpeed(int sx, int sy) {
		 Yspeed = sy;
		 Xspeed = sx;
	 }
	 //Get the current size of the particles
	 public int getSize() {
		 return sizeCurrent;
	 }
	 //Move the particles (in one of 4 directions, left upwards, left downwards, right upwards, right downwards)
	 public void move() {
		 if (dir == LEFT_UPWARDS) {
			 xPos-=Xspeed;
			 yPos-=Yspeed;
		 }
		 else if (dir == LEFT_DOWNWARDS) {
			 xPos-=Xspeed;
			 yPos+=Yspeed;
		 }
		 else if (dir == RIGHT_UPWARDS) {
			 xPos+=Xspeed;
			 yPos-=Yspeed;
		 }
		 else {
			 xPos+=Xspeed;
			 yPos+=Yspeed;
		 }
	 }
	 
	 //Get the colour of the particle (so the paint function can fill the particles to the corret colour
	 public int getColour() {
		 return colour;
	 }
	 
	 //Get the x-pos of the particle
	 public int getX() {
		 return xPos;
	 }
	 
	 //Get the y-pos of the particle
	 public int getY() {
		 return yPos;
	 }
	
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == Timer) {
			
			//Increments the number of ticks (to reduce delay)
			ticks++;
			
			//During 50 - 100 ticks, increase the size of the particles until it reachs the maximun value. Then, stop growing the particle
			if (ticks >= 50 && ticks <= 110) {
				sizeCurrent+=growrate;
				if (sizeCurrent >= sizeMax) {
					growrate = 0;
				}
			}
			
			//After a certain duration (150 ticks), start shrinking the particle & changing the colour (darkening it, until it is black)
			if (ticks >=150) {
				sizeCurrent -= 1;
				colour -= colourshiftRate;
				
				if (colour <= 0) {
					colourshiftRate = 0;
				}
			}
		}
		
	}
	 
	//Reset timer count (used for debugging)
	public void stopTimer() {
		ticks = 0;
	}
	
}
