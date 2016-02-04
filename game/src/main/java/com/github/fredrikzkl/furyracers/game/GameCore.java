package com.github.fredrikzkl.furyracers.game;

import java.awt.event.KeyEvent;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Vector2f;

import com.github.fredrikzkl.furyracers.Application;

public class GameCore extends BasicGame {

	Image p1car = null;

	public Level level = null;

	boolean reverseKeyIsDown, throttleKeyIsDown, leftKeyIsDown, rightKeyIsDown, usingRemoteControllers = false;
	
	int topSpeed = 480;
	int acceleration = 6;
	int deAcceleration = 3;
	int currentSpeed = 0;
	int angleChangePerUpdate = 1;

	float maxPixelMovementPerUpdate = topSpeed/Application.FPS;
	float currentPixelMovementPerUpdate = currentSpeed/Application.FPS;
	float pixelAccelerationPerUpdate = acceleration/(float)Application.FPS;
	float pixelDeAccelerationPerUpdate = deAcceleration/(float)Application.FPS;

	float turningCircumferance = (360/angleChangePerUpdate) * maxPixelMovementPerUpdate;
	float turningRadius = (float) (turningCircumferance / 2 * Math.PI);
	float carSize = (float) 0.5;
	int movementDegrees = 0;

	Vector2f position = new Vector2f();
	Vector2f unitCirclePos = new Vector2f();
	
	float radDeg = 0;

	public GameCore(String title) {
		super(title);
	}

	public void init(GameContainer arg0) throws SlickException {

		level = new Level(1);
		//sprite = new SpriteSheet("Sprites/fr_mustang_red.png", 100, 100);
		p1car = new Image("Sprites/fr_mustang_red.png");
		System.out.println("top pixel/second: " + topSpeed);
		System.out.println("current pixel/second: " + currentSpeed);
		System.out.println("pixel/second^2:" + acceleration);
		System.out.println("-pixel/second^2: " + deAcceleration);
	}

	public void update(GameContainer container, int arg1) throws SlickException {

		Input input = container.getInput();

		reactToControlls(input);

		radDeg = (float) Math.toRadians(movementDegrees);
		
		unitCirclePos.x = (float) (Math.cos(radDeg))*currentPixelMovementPerUpdate;
		unitCirclePos.y = (float) (Math.sin(radDeg))*currentPixelMovementPerUpdate;
		
		position.x += unitCirclePos.x;
		position.y += unitCirclePos.y;	
	}

	public void render(GameContainer container, Graphics g)
			throws SlickException {
		level.render(g);
		p1car.draw(position.x, position.y,carSize);
		p1car.setCenterOfRotation(16, 32);
		p1car.setRotation(movementDegrees);
	}

	public void reactToControlls(Input input) {
		
		if(input == null){
			usingRemoteControllers = false;
		}
		if(!usingRemoteControllers){
			reactToKeyboard(input);
		}
		
		if(throttleKeyIsDown ) {
			if(currentPixelMovementPerUpdate < maxPixelMovementPerUpdate) {
				currentPixelMovementPerUpdate += pixelAccelerationPerUpdate;
			}
		} else{
			if(currentPixelMovementPerUpdate > pixelDeAccelerationPerUpdate) {
				currentPixelMovementPerUpdate -= pixelDeAccelerationPerUpdate;
			}else if(currentPixelMovementPerUpdate > 0){
				currentPixelMovementPerUpdate = 0;
			}
		}
		
		if(reverseKeyIsDown) {
			if(currentPixelMovementPerUpdate > -maxPixelMovementPerUpdate) {
				currentPixelMovementPerUpdate -= pixelDeAccelerationPerUpdate;
			}
		} else{
			if(currentPixelMovementPerUpdate < -pixelDeAccelerationPerUpdate) {
				currentPixelMovementPerUpdate += pixelDeAccelerationPerUpdate;
			}else if(currentPixelMovementPerUpdate < 0){
				currentPixelMovementPerUpdate = 0;
			}
		}
		
		if(currentPixelMovementPerUpdate != 0){
			if(leftKeyIsDown){
				movementDegrees -= angleChangePerUpdate;
			}else if(rightKeyIsDown){
				movementDegrees += angleChangePerUpdate;
			}
		}
	}
	
	public void reactToKeyboard(Input input){
		
		if(input.isKeyDown(Input.KEY_UP)) {
            throttleKeyDown();
	    }else {
	    	throttleKeyUp();
	    }
		
		if(input.isKeyDown(Input.KEY_DOWN)){
			reverseKeyDown();
		}else{
			reverseKeyUp();
		}
		
		if(input.isKeyDown(Input.KEY_LEFT)){
			leftKeyDown();
		}else{
			leftKeyUp();
		}
		
		if(input.isKeyDown(Input.KEY_RIGHT)){
			rightKeyDown();
		}else{
			rightKeyUp();
		}
	}
	
	public void throttleKeyDown() {
		throttleKeyIsDown = true;
		//System.out.println("throttleDown");
	}

	public void leftKeyDown() {
		leftKeyIsDown = true;
	}

	public void rightKeyDown() {
		rightKeyIsDown = true;
	}
	
	public void reverseKeyDown() {
		reverseKeyIsDown = true;
	}

	public void leftKeyUp() {
		leftKeyIsDown = false;
	}

	public void rightKeyUp() {
		rightKeyIsDown = false;
	}
	
	public void reverseKeyUp() {
		reverseKeyIsDown = false;
	}
	
	public void throttleKeyUp() {
		throttleKeyIsDown = false;
		//System.out.println("throttleUp");
	}
	
	public void setUsingRemoteControllers(){
		usingRemoteControllers = true;
	}
}