package com.github.fredrikzkl.furyracers.game;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

public class GameCore extends BasicGame {

	Image carSprites = null;
	SpriteSheet sprite;
	
	boolean throttleKeyIsDown = false;
	boolean leftKeyIsDown = false;
	boolean rightKeyIsDown = false;
	
	int handling = 2;

	int topSpeed = 5;
	float currentSpeed = 0;
	float acceleration = (float) 0.1;
	float deAcceleration = (float) 0.05;
	
	float carSize = (float) 0.5;
	
	float movementDegrees = 0;
	
	float xPos = 140;
	float yPos = 140;
	
	public GameCore(String title) {
		super(title);
	}
	
	public void init(GameContainer arg0) throws SlickException {
		sprite = new SpriteSheet("Sprites/car.png", 100, 100);
		carSprites = new Image("Sprites/car.png");
	}
	
	public void update(GameContainer container, int arg1) throws SlickException {
		Input input = container.getInput();
		
		reactToKeyboardInput(input);
		
		float radDeg = (float) Math.toRadians(movementDegrees);

		float deltaX = (float) (Math.cos(radDeg))*currentSpeed;
		float deltaY = (float) (Math.sin(radDeg))*currentSpeed;
		
		xPos += deltaX;
		yPos += deltaY;	
	}

	public void render(GameContainer container, Graphics g) throws SlickException {
		carSprites.getSubImage(1,0,140,140).draw(xPos, yPos, carSize);
	}
	
	public void onThrottle(){
		throttleKeyIsDown = true;
	}
	
	public void offThrottle(){
		throttleKeyIsDown = false;
	}
	
	public void leftKeyDown(){
		leftKeyIsDown = true;
	}
	
	public void rightKeyDown(){
		rightKeyIsDown = true;
	}
	
	public void leftKeyUp(){
		leftKeyIsDown = false;
	}
	
	public void rightKeyUp(){
		rightKeyIsDown = false;
	}
	
	public void reactToKeyboardInput(Input input){
		
		/*if(input.isKeyDown(Input.KEY_UP)){
			onThrottle();
		}else{
			offThrottle();
		}*/
		
		if(throttleKeyIsDown){
			if(currentSpeed<topSpeed){
				currentSpeed += acceleration; 
			}
		}else{
			if(currentSpeed>0){
				currentSpeed -= deAcceleration;
			}
			else{
				currentSpeed = 0;
			}
		}
	
		if(leftKeyIsDown){
			movementDegrees -= handling;
		}
		if(rightKeyIsDown){
			movementDegrees += handling;
		}
	}
}