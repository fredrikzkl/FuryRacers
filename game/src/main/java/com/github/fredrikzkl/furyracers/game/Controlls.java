package com.github.fredrikzkl.furyracers.game;

public class Controlls {

	private float deltaAngleChange, deltaDeAcceleration, topSpeed, acceleration, deAcceleration;
	private CarProperties stats;
	boolean 
	reverseKeyIsDown, throttleKeyIsDown, leftKeyIsDown, 
	rightKeyIsDown, usingKeyboard, ignoreNextRight, 
	ignoreNextLeft = false;

	float currentSpeed, movementDegrees = 0;

	public Controlls(CarProperties stats) {

		this.stats = stats;
		topSpeed = stats.topSpeed;
		acceleration = stats.acceleration;
		deAcceleration = stats.deAcceleration;
	
	}
	
	public String getTurningDirection(){
		
		if(leftKeyIsDown){
			return "positive";
		}
		
		if(rightKeyIsDown){
			return "negative";
		}
		
		return "neutral";
	}

	public void reactToControlls(int deltaTime, boolean paused) {
		if (!paused) {

			deltaDeAcceleration = deAcceleration * deltaTime / 1000;
			if (throttleKeyIsDown && currentSpeed < topSpeed) {

				currentSpeed += acceleration * deltaTime / 1000;
			} else if (reverseKeyIsDown && currentSpeed > -stats.reverseTopSpeed) {

				currentSpeed -= stats.reverseAcceleration * deltaTime / 1000;
			} else if (currentSpeed < -deAcceleration) {

				currentSpeed += deltaDeAcceleration;
			} else if (currentSpeed > -deAcceleration && currentSpeed < 0) {

				currentSpeed = 0;
			} else if (currentSpeed > deAcceleration) {

				currentSpeed -= deltaDeAcceleration;
			} else if (currentSpeed > 0 && currentSpeed < deAcceleration) {

				currentSpeed = 0;
			} else {
				deltaDeAcceleration = 0;
			}
			
			if (currentSpeed != 0) {
				deltaAngleChange = 0;
				if (leftKeyIsDown) {
					deltaAngleChange = stats.handling * deltaTime / 1000;
					movementDegrees -= deltaAngleChange;
				} else if (rightKeyIsDown) {
					deltaAngleChange = stats.handling * deltaTime / 1000;
					movementDegrees += deltaAngleChange;
				}
			}
		}
	}
	
	void changeDeAcceleration(float constant){
		
		deAcceleration *= constant;
	}
	
	void resetDeAcceleration(){
		deAcceleration = stats.deAcceleration;
	}
	
	public void changeCurrentSpeed(float changeConstant){
		currentSpeed *= changeConstant;
	}
	
	public void changeTopSpeed(float changeConstant){
		topSpeed *= changeConstant;
	}
	
	void resetTopSpeed(){
		
		topSpeed = stats.topSpeed;
	}

	public void throttleKeyDown() {
		throttleKeyIsDown = true;
		reverseKeyUp();
	}

	public void leftKeyDown() {
		if(!ignoreNextLeft){
			leftKeyIsDown = true;
			rightKeyIsDown = false;
		}
		
		ignoreNextLeft = false;
	}

	public void rightKeyDown() {
		if(!ignoreNextRight){
			rightKeyIsDown = true;
			leftKeyIsDown = false;
		}
		ignoreNextRight = false;
	}

	public void rightKeyUp() {
		
		if(!rightKeyIsDown){
			ignoreNextRight = true;
		}else{
			rightKeyIsDown = false;
			ignoreNextRight = false;
		}
	}

	public void reverseKeyDown() {
		reverseKeyIsDown = true;
		throttleKeyUp();
	}

	public void leftKeyUp() {
		
		if(!leftKeyIsDown){
			ignoreNextLeft = true;
		}else{
			leftKeyIsDown = false;
			ignoreNextLeft = false;
		}
	}


	public void reverseKeyUp() {
		reverseKeyIsDown = false;
	}

	public void throttleKeyUp() {
		throttleKeyIsDown = false;
		
	}

	public void disableKeyboardInput() {
		usingKeyboard = false;
	}

	public void activateKeyboardInput() {
		usingKeyboard = true;
	}

	public float getCurrentSpeed() {

		return currentSpeed;
	}

	public float getDeltaDeAcceleration() {
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
