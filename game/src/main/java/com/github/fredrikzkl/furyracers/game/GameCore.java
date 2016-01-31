package com.github.fredrikzkl.furyracers.game;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Vector2f;

public class GameCore extends BasicGame {

	Image p1car = null;
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
	
	Vector2f position = new Vector2f();
	Vector2f angle = new Vector2f();
	
	float radDeg;
	
	public GameCore(String title) {
		super(title);
	}
	
	public void init(GameContainer arg0) throws SlickException {
		sprite = new SpriteSheet("Sprites/car.png", 100, 100);
		p1car = new Image("Sprites/fr_mustang_red.png");
		
		
		
	}
	
	public void update(GameContainer container, int arg1) throws SlickException {
		
		
		Input input = container.getInput();
		
		reactToKeyboardInput(input);
		
		radDeg = (float) Math.toRadians(movementDegrees);
		
		angle.x = (float) (Math.cos(radDeg))*currentSpeed;
		angle.y = (float) (Math.sin(radDeg))*currentSpeed;
		
		position.x += angle.x;
		position.y += angle.y;	
	}

	public void render(GameContainer container, Graphics g) throws SlickException {
		p1car.draw(position.x, position.y, carSize);
		p1car.setRotation(movementDegrees);
		
		
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