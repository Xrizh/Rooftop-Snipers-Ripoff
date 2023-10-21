import java.awt.Graphics;
import java.awt.Image;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.Console;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class Player {
	//Some variables to hold the x position, y position, direction, etc. 
	private int xPos, yPos, choice, width = 0, height = 0; 
	
	//Some floats to store the angle the player forms with the ground, gravity, veloctity components, etc. 
	private float degs, gravity = -9.8f, yVelocity, xVelocity, angularVelocity = 0f, angularDamping = 0.995f, angularLandingForceMultiplier = 4f, offset = 0, armDegs = 90f; 
	
	//Some booleans to store whether the player is grounded, inverted, etc. 
	public boolean isGrounded = false, inverted = false, armRotating = false; 
	
	//An Image representing the player
	private ImageIcon imgPlayer; 
	
	//Pool (array) of possible player images
	private ImageIcon[] images = {new ImageIcon("johnnydepp1.png"), new ImageIcon("cool_dude.png"), new ImageIcon("jacked_dude.png"), new ImageIcon("joker.png"), new ImageIcon("voldemort.png"), new ImageIcon("batman.png")}; 
	
	//Some affine tranforms to rotate and translate the player and it's parts
	AffineTransform transform = new AffineTransform(), colTransform = new AffineTransform(), armTransform = new AffineTransform(); 
	
	//Constructor - called when the object is instantiated with the "new" tag, sets default values
	public Player() {
		//Default direction = up, x, y position = 0, etc. 
		xPos = 0; 
		yPos = 0; 
		xVelocity = 0; 
		yVelocity = 0; 
		imgPlayer = new ImageIcon(); 
		
		//Go through each image and resize them to the appropriate size
		for (int i = 0; i < images.length; ++i) {
			//images[i] = new ImageIcon(images[i].getImage().getScaledInstance(175, 165, Image.SCALE_SMOOTH));
			images[i] = new ImageIcon(images[i].getImage().getScaledInstance(500, 500, Image.SCALE_SMOOTH));
		}
		
		chooseRandomCharacter(); //Choose a random character
	}
	
	//Method to set the player's velocity components
	public void setVelocity(Float x, Float y) {
		xVelocity = x; 
		yVelocity = y; 
	}
	
	//Method to add some velocity to the player's angular velocity
	public void addAngular(float _value) {
		angularVelocity += _value; 
	}
	
	//Set the gravity
	public void setGravity(float _grav) {
		gravity = _grav; 
	}
	
	//Method to move the player based on it's velocity vector
	public void move() {
		if (yVelocity < 0 && isGrounded) return; //If the player is going downwards and touching the ground, don't move him 
		isGrounded = false; //If we get past there, they should not be grounded (takes care of corner-case logic for the next frame) 
		
		//Algorithm based on "Euler Integration" method of calculating velocity
		//yVelocity += gravity/240f * -gravity/1.64f; //Latter can be changed to "6" with default gravity
		yVelocity += gravity/240f * 6f; 
		
		//Subtract the velocity at the current frame from the player's y position
		yPos -= yVelocity; 
		
		if (inverted) xPos -= xVelocity; //If the player is inverted, move him in the opposite direction of his true x velocity
		else xPos += xVelocity; //Otherwise, just move him horizontally based on his (constant) x velocity
	}
	
	//Method to to calculate and apply player's angular velocity to their rotation
	public void bob() {
		float mass = 1f; //By default, mass should be set to 1 kg. 
		if (!isGrounded) { //If the player is in the air
			//Do the bottom calculations, but with different values (to adjust for angular drag)
			mass = 0.5f; 
			float force = 0.175f * (float)Math.sin(Math.toRadians(degs));
			float angularAcceleration = force * -1f; 
			angularVelocity += angularAcceleration; 
			rotate(angularVelocity * mass); 
			return; 
		}
		
		//Calculate angular velocity by using equations meant for pendulums while applying some drag
		//float force = 0.05f * (float)Math.sin(Math.toRadians(degs));
		float force = 0.175f * (float)Math.sin(Math.toRadians(degs));
		float angularAcceleration = force * -1f; 
		angularVelocity += angularAcceleration; 
		
		//Finally, rotate the player based on this calculated angular velocity
		rotate(angularVelocity * mass); 
		
		//Apply damping to get the player to stabilize in the middle (evenually)
		angularVelocity *= angularDamping; 
		
		//System.out.println(angularVelocity);
	}
	
	//Method to make the player jump
	public void jump(float _vel) {
		//Reset x and y velocities
		yVelocity = 0f; 
		xVelocity = 0f; 
		
		//Mark the player as no longer grounded
		isGrounded = false; 
		
		//Calculate the degree of theta
		float _degs = 90f - degs; 
		
		//Some trigonometry to calculate the player's x and y components for their velocity based on the force and angle given (velocity vector)
		//if (inverted) xVelocity -= (float)Math.cos(Math.toRadians(_degs)) * _vel; 
		xVelocity += (float)Math.cos(Math.toRadians(_degs)) * _vel;
		yVelocity += (float)Math.sqrt(Math.pow(_vel, 2) - Math.pow(xVelocity, 2));
		
		//Add some small random angular drag whenever the player jumps
		if (degs < 0) {
			angularVelocity += (float)Math.random() * -0.5f; 
		} else if (degs == 0) {
			angularVelocity += (float)Math.random() * 1f - 0.5f; 
		} else {
			angularVelocity += (float)Math.random() * 0.5f; 
		}
	}
	
	//Method to rotate the player
	public void rotate(float _degs) {
		//If the player is on the ground and is rotated more than 90 degrees, they must be hitting the ground, so make them bounce to the other side. 
		if (isGrounded && ((degs <= -90 && _degs < 0) || (degs >= 90 && _degs > 0))) {
			angularVelocity = 0f;
			return; 
		}
		
		//Same thing but slightly less constrictive bounds for air movement
		if (!isGrounded && ((degs <= -140 && _degs < 0) || (degs >= 140 && _degs > 0))) {
			return; 
		}
		
		//A transform for the player
		AffineTransform _trans = new AffineTransform(); 
		
		setImage(choice); //To prevent bi-planar distortion when using image projection, keep resetting the image
		degs += _degs; //Set the player's angle
		if (degs <= 0) {
			//Use some calculated anchor points to rotate the player
			//transform.rotate(Math.toRadians(_degs), width / 2 - 10, height - 10);
			transform.rotate(Math.toRadians(_degs), 221, 187 + height/3 - 17f);
			_trans.rotate(Math.toRadians(degs), 221, 187 + height/3 - 17f); 
			armTransform.rotate(Math.toRadians(_degs), getX() + width / 2 - 10, getY() + height - 10); 
		}
		else {
			//Same here but using other anchor points
			//transform.rotate(Math.toRadians(_degs), width / 2 + 20, height - 10);
			transform.rotate(Math.toRadians(_degs), 221 + width/10f - 11f, 187 + height/3 - 17f);
			_trans.rotate(Math.toRadians(degs), 221 + width/10f - 11f, 187 + height/3 - 17f); 
			armTransform.rotate(Math.toRadians(_degs), getX() + width / 2 + 20, getY() + height - 10); 
		}
		
		//Apply the transformations
		//transform.rotate(Math.toRadians(_degs), 0, 0); 
		AffineTransformOp op = new AffineTransformOp(_trans, AffineTransformOp.TYPE_NEAREST_NEIGHBOR); 
		imgPlayer = new ImageIcon(op.filter(getBufferedImageFromIcon(imgPlayer), null)); 
	}
	
	//Simple method to convert from a bufferred image to an icon (good for performance)
	public static BufferedImage getBufferedImageFromIcon(ImageIcon icon) {
        BufferedImage buffer = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(),
                BufferedImage.TYPE_INT_ARGB);
        Graphics g = buffer.getGraphics();
        icon.paintIcon(new JLabel(), g, 0, 0);
        g.dispose();
        return buffer;
    }
	
	//Get the player's collision bounds by using the same anchor points and values as the player's regular rotation function
	public Shape getCollisionBounds() {
		//Intialize a rectangle
		Rectangle2D _colRect = new Rectangle2D.Double(getX()+221, yPos+187, width/10f - 11f, height/3 - 17f);
		
		//Intialize a path2d and add the rectangle to it
		Path2D.Double rectPath = new Path2D.Double(); 
		rectPath.append(_colRect, false); 
		
		//Intialize an affine transform to rotate
		AffineTransform t = new AffineTransform(); 
		
		//Rotate
		if (inverted) {
			if (degs >= 0) {
				//t.rotate(Math.toRadians(-degs), getX() + width / 2 - 10, getY() + height - 10); 
				t.rotate(Math.toRadians(-degs), getX() + 221, getY() + 187 + height/3 - 17f);
			}
			else {
				//t.rotate(Math.toRadians(-degs), getX() + width / 2 + 20, yPos + height - 10); 
				t.rotate(Math.toRadians(-degs), getX() + 221 + width/10f - 11f, getY() + 187 + height/3 - 17f);
			}
		} else {
			if (degs <= 0) {
				//t.rotate(Math.toRadians(degs), getX() + width / 2 - 10, getY() + height - 10);
				t.rotate(Math.toRadians(degs), getX() + 221, getY() + 187 + height/3 - 17f);
			}
			else {
				//t.rotate(Math.toRadians(degs), getX() + width / 2 + 20, yPos + height - 10);
				t.rotate(Math.toRadians(degs), getX() + 221 + width/10f - 11f, getY() + 187 + height/3 - 17f);
			}
		}
		
		//Apply the transformation to the Path2D
		rectPath.transform(t);  
		
		//Return the new shape
		return rectPath; 
	}
	
	//IGNORE the many comments - they are different quadratic models for calculating knockback when the player hits the ground, and are added/removed/commented as needed. 
	public void ground() {
		if (isGrounded) return; 
		
		isGrounded = true;  
		
		//angularVelocity += xVelocity * angularLandingForceMultiplier;
		//angularVelocity += ((0 - angularVelocity) * 700f + 0.2f) * 0.15f + yVelocity/8f;
		
		//System.out.println(degs + " " + (((degs + 0.1f)) * 0.15f * yVelocity/8f) + " " + ((degs + 0.2f + angularVelocity/4f) * 0.15f + yVelocity/8f)); 
		
		if (degs <= 0) angularVelocity += (degs + 0.2f + angularVelocity/4f) * 0.15f + yVelocity/8f; 
		else angularVelocity += (degs + 0.2f - angularVelocity/4f) * 0.15f - yVelocity/8f; 
		//angularVelocity -= ((degs + 0.2f) * angularVelocity/4f) * 0.15f * yVelocity/4f * 2f;
		//angularVelocity += ((degs + 0.1f) * angularVelocity * xVelocity * yVelocity)/200f; 
		
		//System.out.println(angularVelocity);
		
		//yVelocity = -yVelocity * 1.1f;
		//isGrounded = false; 
		
		//System.out.println(angularVelocity);
	}
	
	//Method for resolving collisions
	public void collision() {
		//My (very bad) approach is just to invert the player's velocity
		angularVelocity = -angularVelocity/4f;
		
		xVelocity = -xVelocity; 
		yVelocity = -yVelocity; 
	}
		
	//Method that sets the player's preferred image based on an integer choice
	public void setImage(int _choice) {
		choice = _choice; 
		imgPlayer = images[_choice]; 
		if (width != 0) return; 
		width = imgPlayer.getIconWidth(); 
		height = imgPlayer.getIconHeight(); 
	}
	
	//Method SAME as the collision getter but with values adjusted for getting the player's arm instead
	public Shape getArm() { 
		Rectangle2D armRect = new Rectangle2D.Double(getX()+221+15, yPos+187+65, width/10f - 6f, 16f);
		
		Path2D.Double rectPath = new Path2D.Double(); 
		rectPath.append(armRect, false); 
		
		AffineTransform aTransform = new AffineTransform(); 
		
		if (inverted) {
			if (degs >= 0) {
				aTransform.rotate(Math.toRadians(-degs), getX() + 221, getY() + 187 + height/3 - 17f);
			}
			else {
				aTransform.rotate(Math.toRadians(-degs), getX() + 221 + width/10f - 11f, getY() + 187 + height/3 - 17f);
			}
		} else {
			if (degs <= 0) {
				aTransform.rotate(Math.toRadians(degs), getX() + 221, getY() + 187 + height/3 - 17f);
			}
			else {
				aTransform.rotate(Math.toRadians(degs), getX() + 221 + width/10f - 11f, getY() + 187 + height/3 - 17f);
			}
		}
		
		aTransform.rotate(Math.toRadians(armDegs), getX()+221+15, yPos+187+65 + 16f/2f); 
		
		rectPath.transform(aTransform); 
		
		return rectPath; 
	}
	
	//To rotate the arm, simply add that rotation amount to it's respective variable
	public void rotateArm(float _amount) {
		armDegs += _amount; 
	}
	
	//Calculate a vector (line) for shooting
	public Path2D shoot() {
		//Have a range, y endpoint (based on trigonometry), base offset, etc. 
		int _range = 5000; 
		float _y = (float)(Math.tan(Math.toRadians(armDegs)) * _range); 
		float baseX = getX()+221+15; 
		float baseY = yPos+187+65 + 16f/2f; 
		
		//Create a line starting at that position and ending at the straight endpoint
		Line2D line = new Line2D.Double(baseX + width/10f - 6f + offset, baseY, baseX + _range + width/10f - 6f + offset, baseY); 
		
		//Create a path, add line to said path
		Path2D path = new Path2D.Double(); 
		path.append(line, false);
		
		//Create affine transform
		AffineTransform aTransform = new AffineTransform(); 
		
		//Rotate the affine transform using the EXACT same values as the collision and regular rotation anchor points
		if (inverted) {
			if (degs >= 0) {
				aTransform.rotate(Math.toRadians(-degs), getX() + 221, getY() + 187 + height/3 - 17f);
			}
			else {
				aTransform.rotate(Math.toRadians(-degs), getX() + 221 + width/10f - 11f, getY() + 187 + height/3 - 17f);
			}
		} else {
			if (degs <= 0) {
				aTransform.rotate(Math.toRadians(degs), getX() + 221, getY() + 187 + height/3 - 17f);
			}
			else {
				aTransform.rotate(Math.toRadians(degs), getX() + 221 + width/10f - 11f, getY() + 187 + height/3 - 17f);
			}
		}
		
		//Apply the rotation
		//aTransform.translate(-offset, 0f); 
		aTransform.rotate(Math.toRadians(armDegs), getX()+221+15, yPos+187+65 + 16f/2f);
		aTransform.translate(-offset, 0f);
		
		//Add the rotation to the path 2d
		path.transform(aTransform); 
		
		//Since the bullet has been shot, the arm is no longer rotating
		armRotating = false; 
		
		return path; 
	}
	
	//Check if the player is grounded
	public boolean grounded() {
		return isGrounded; 
	}
	
	//Gets the player's preferred current image
	public ImageIcon getImage() {
		return imgPlayer; 
	}
	
	//Get the player's rotation
	public float getRot() {
		return degs; 
	}
	
	//Set the player's location
	public void setLocation(int x, int y) {
		xPos = x; 
		yPos = y; 
	}
	
	//Method that takes an integer and sets the y position to that integer
	public void setY(int y) {
		yPos = y; 
	}
	
	//Method that returns the x position with the camera offset relative to the player
	public int getX() {
		return xPos + (int)offset; 
		//return xPos; 
	}
	
	//Returns the y position
	public int getY() {
		return yPos; 
	}
	
	public void moveX(Float _x) {
		xPos += _x; 
	}
	
	public void moveY(Float _y) {
		yPos += _y; 
	}
	
	public void setOffset(float _val) {
		offset = _val; 
	}
	
	public int getXWithoutOffset() {
		return xPos; 
	}
	
	//Invert the player on the right by flipping the image and some booleans
	public void changeDir() {
		transform = AffineTransform.getScaleInstance(-1, 1); 
		transform.translate(-imgPlayer.getIconWidth()+28f, 0); 
		
		inverted = !inverted; 
		
		//colTransform = AffineTransform.getScaleInstance(-1, 1); 
		transform.translate(-10f, 0); 
	}
	
	//Get the y velocity
	public float getYVelocity() {
		return yVelocity; 
	}
	
	//Set the arm to start rotating
	public void startArmRotation() {
		if (armRotating) return; 
		
		armDegs = 90; 
		armRotating = true; 
	}
	
	//Check if arm is rotating
	public boolean isArmRotating() {
		return armRotating; 
	}
	
	//Get the arm's rotation amount
	public float getArmRot() {
		return armDegs; 
	}
	
	//Choose a random character from the player image's array
	public void chooseRandomCharacter() {
		//int _choice = 1+(int)(Math.random()*images.length-2);
		Random rand = new Random(); 
		int choice = rand.nextInt(images.length-1)+1; 
		setImage(choice);
		//System.out.println(choice);
	}
	
	//Resolve shootings using basic quadratic equations
	public void shot() {
		xVelocity += (Math.abs(xVelocity)+0.4f)*-1f;
		xVelocity += Math.abs(angularVelocity+1f)*-1f; 
		xVelocity = clamp(xVelocity, -15f, 15f); 
		yVelocity += (Math.abs(yVelocity)+1f)*1f; 
		isGrounded = false; 
		
		//System.out.println((Math.abs(xVelocity)+0.2f)*-1f);
	}
	
	//Basic clamp function for non-linear interpolation
	public static float clamp(float val, float min, float max) {
	    return Math.max(min, Math.min(max, val));
	}
}
