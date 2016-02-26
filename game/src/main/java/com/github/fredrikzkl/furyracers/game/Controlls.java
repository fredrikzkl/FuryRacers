package com.github.fredrikzkl.furyracers.game;

import org.newdawn.slick.Input;

public class Controlls {
	
	private float deltaAngleChange, deltaDeAcceleration;
	private CarProperties stats;
	private Car car;
	boolean reverseKeyIsDown, throttleKeyIsDown, 
			leftKeyIsDown, rightKeyIsDown, 
			usingKeyboard =  false;
	
	float currentSpeed, movementDegrees = 0;
	
	public Controlls(Car car, CarProperties stats){
		
		this.stats = stats;
		this.car = car;
	}
	
public void reactToControlls(Input input, int deltaTime) {
		
		if(usingKeyboard){
			reactToKeyboard(input);
		}

		deltaDeAcceleration = stats.deAcceleration*deltaTime/1000;
		if(throttleKeyIsDown && currentSpeed < stats.topSpeed) {
			
				currentSpeed += stats.acceleration*deltaTime/1000;
		}else if(reverseKeyIsDown && currentSpeed > -stats.reverseTopSpeed) {
	
				currentSpeed -= stats.reverseAcceleration*deltaTime/1000;
		}else if(currentSpeed < -stats.deAcceleration) {
				
			currentSpeed += deltaDeAcceleration;
		}else if(currentSpeed > -stats.deAcceleration && currentSpeed < 0){
			
			currentSpeed = 0;
		}else if(currentSpeed > stats.deAcceleration) {
				
			currentSpeed -= deltaDeAcceleration;
		}else if(currentSpeed > 0 && currentSpeed < stats.deAcceleration){
			
			currentSpeed = 0;
		}else{
			deltaDeAcceleration = 0;
		}
		
		if(currentSpeed != 0){
			deltaAngleChange = 0;
			if(leftKeyIsDown){
				deltaAngleChange = stats.handling*deltaTime/1000;
				movementDegrees -= deltaAngleChange;
			}else if(rightKeyIsDown){
				deltaAngleChange = stats.handling*deltaTime/1000;
				movementDegrees += deltaAngleChange;
			}
		}
	}
	
	public void reactToKeyboard(Input input){
		
		if(car.playerNr == 1){
			reactToArrowKeys(input);
		}else if(car.playerNr == 2){
			reactToWasdKeys(input);
		}
	}

	public void reactToArrowKeys(Input input){
		
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
	
	public void reactToWasdKeys(Input input){
		
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
	
	public void changeCurrentSpeed(float changeConstant){
		currentSpeed *= changeConstant;
	}
	
	public void changeTopSpeed(float changeConstant){
		stats.topSpeed *= changeConstant;
	}

	public void throttleKeyDown() {
		throttleKeyIsDown = true;
		reverseKeyUp();
	}

	public void leftKeyDown() {
		leftKeyIsDown = true;
		rightKeyUp();
	}

	public void rightKeyDown() {
		rightKeyIsDown = true;
		leftKeyUp();
	}

	public void reverseKeyDown() {
		reverseKeyIsDown = true;
		throttleKeyUp();
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
	
	
	public float getCurrentSpeed(){
		return currentSpeed;
	}
	
	public float getDeltaDeAcceleration(){
		return deltaDeAcceleration;
	}
	
	public float getDeltaAngleChange(){
		return deltaAngleChange;
	}
	
	public float getMovementDegrees(){
		return movementDegrees;
	}
	
	public void setMovementDegrees(float movementDegrees){
		this.movementDegrees = movementDegrees;
	}
	

}
