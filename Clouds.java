import java.awt.*;
import java.util.Random;

import javax.swing.*;

public class Clouds {
	
	//Declaring variables for Cloud Object
	
	//Intializing the x pos, y pos, height and speed of the clouds
	private int xPos, yPos, width, height, speed;
	
	//Declaring the random function
	private Random rand = new Random();
		
	Clouds () {
		//Setting the x-pos, y-pos, speed, width, and heigt of the cloud to its default values
		xPos = 0;
		yPos = 0;
		speed = 0;
		width = 0;
		height = 0;
	}
	
	 //Sets the x position of the cloud
	 public void setX(int x) {
		 xPos = x;
	 }
	 
	 //Sets the y position of the cloud
	 public void setY(int y) {
		 yPos = y;
	 }
	 
	 //Sets the size of the cloud
	 public void setSize(int w, int h) {
		 width = w;
		 height = h;
	 }
	 
	 //Sets the speed of the cloud
	 public void setSpeed(int s) {
		 speed = s;
	 }
	 
	 //Gets the width of the cloud (randomized)
	 public int getWidth() {
		 return width;
	 }
	 
	 //Gets the height of the cloud (randomized)
	 public int getHeight() {
		 return height;
	 }
	 
	 //Moves the cloud (to the left)
	 public void move() {
		 xPos -= speed;
	 }
	 
	 //Returns the current x-pos of the cloud 
	 public int getX() {
		 return xPos;
	 }
	 
	 //Returns the current y-pos of the cloud 
	 public int getY() {
		 return yPos;
	 }
}
