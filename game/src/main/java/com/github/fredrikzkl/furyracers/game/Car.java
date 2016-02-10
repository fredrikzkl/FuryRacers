package com.github.fredrikzkl.furyracers.game;

import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.GameContainer;

public class Car {
	public String name;
	public String type;
	
	public Image sprite;
	
	public float topSpeed;
	public float acceleration;
	public float deAcceleration;
	public float handling;
	
	public float weight;
	
	boolean reverseKeyIsDown, throttleKeyIsDown, leftKeyIsDown, rightKeyIsDown, usingKeyboard = false;
	
	float currentSpeed = 0;
	float movementDegrees = 0;
	float radDeg = 0;
	
	float carSize = (float) 0.4;
	
	private Level level;
	private int tilePosX, tilePosY;
	
	Vector2f position = new Vector2f();
	Vector2f unitCirclePos = new Vector2f();
	
	public Car(String name, String type, Image sprite,float topSpeed,
			float acceleration, float deAcceleration, float handling, float weight, Level level){
		this.name = name;
		this.type = type;
		this.sprite = sprite;
		this.topSpeed = topSpeed;
		this.acceleration = acceleration;
		this.deAcceleration = deAcceleration;
		this.handling = handling;
		this.weight = weight;
		this.level = level;
	}
	
	public void update(GameContainer container, int deltaTime)throws SlickException{
		Input input = container.getInput();
		reactToControlls(input, deltaTime);
		
		radDeg = (float) Math.toRadians(movementDegrees);
		boolean slowDown = level.getTileType(tilePosX, tilePosY);
		
		unitCirclePos.x = (float) (Math.cos(radDeg))*currentSpeed*deltaTime/1000;
		unitCirclePos.y = (float) (Math.sin(radDeg))*currentSpeed*deltaTime/1000;
		
		position.x += unitCirclePos.x;
		position.y += unitCirclePos.y;	
		
		tilePosX = (int) (position.x/level.getTileWidth());
		tilePosY = (int) (position.y/level.getTileHeight());
		/*
		if(slowDown){
			currentSpeed = topSpeed/2;
		}
		*/
	}
	
	public void render() {
		sprite.draw(position.x, position.y, carSize);
		sprite.setCenterOfRotation(16, 32);
		sprite.setRotation(movementDegrees);
	}
	

	public Vector2f getPosition() {
		return position;
	}
	
	public void buttonDown(String data){
		disableKeyboardInput();
        switch(data){
        	case "1": throttleKeyDown();System.out.println("throttleDown");break;
        	case "2": rightKeyDown();System.out.println("rightDown");break;
        	case "3": leftKeyDown();System.out.println("leftDown");break;
        }
	}
	
	public void buttonUp(String data){
		switch(data){
			case "1": throttleKeyUp();System.out.println("throttleUp");break;
			case "2": rightKeyUp(); System.out.println("rightUp");break;
			case "3": leftKeyUp();System.out.println("leftUp");break;
		}
	}
	
	
	
	
	public void reactToControlls(Input input, int deltaTime) {

		if(input != null){
			usingKeyboard = true;
		}
		if(usingKeyboard){
			reactToKeyboard(input);
		}

		if(throttleKeyIsDown) {
			if(currentSpeed < topSpeed) {
				currentSpeed += acceleration*deltaTime/1000;
			}
		} else{
			if(currentSpeed > deAcceleration) {
				currentSpeed -= deAcceleration*deltaTime/1000;
			}else if(currentSpeed > 0){
				currentSpeed = 0;
			}
		}

		if(reverseKeyIsDown) {
			if(currentSpeed > -topSpeed) {
				currentSpeed -= deAcceleration*deltaTime/1000;
			}
		} else{
			if(currentSpeed < -acceleration) {
				currentSpeed += deAcceleration*deltaTime/1000;
			}else if(currentSpeed < 0){
				currentSpeed = 0;
			}
		}

		if(currentSpeed != 0){
			if(leftKeyIsDown){
				movementDegrees -= handling*deltaTime/1000;
			}else if(rightKeyIsDown){
				movementDegrees += handling*deltaTime/1000;
			}
		}
	}
	
	public void reactToKeyboard(Input input){

		if(input.isKeyDown(Input.KEY_W)) {
            throttleKeyDown();
	    }else {
	    	throttleKeyUp();
	    }

		if(input.isKeyDown(Input.KEY_S)){
			reverseKeyDown();
		}else{
			reverseKeyUp();
		}

		if(input.isKeyDown(Input.KEY_A)){
			leftKeyDown();
		}else{
			leftKeyUp();
		}

		if(input.isKeyDown(Input.KEY_D)){
			rightKeyDown();
		}else{
			rightKeyUp();
		}
	}

	public void throttleKeyDown() {
		throttleKeyIsDown = true;
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
	}

	public void disableKeyboardInput(){
		usingKeyboard = false;
	}

	public void activateKeyboardInput(){
		usingKeyboard = true;
	}

	
	
}
