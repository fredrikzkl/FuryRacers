package com.github.fredrikzkl.furyracers.car;


public class Controlls {

	private int
	stopLockLeft, stopLockRight;
	
	private float 
	deltaAngleChange, deltaDeAcceleration, 
	topSpeed, acceleration, deAcceleration;
	
	boolean 
	reverseKeyIsDown, throttleKeyIsDown, leftKeyIsDown, 
	rightKeyIsDown, usingKeyboard, ignoreNextRight, 
	ignoreNextLeft;

	float 
	currentSpeed, movementDegrees = 0;
	
	private CarProperties stats;

	public Controlls(CarProperties stats) {

		this.stats = stats;
		topSpeed = stats.topSpeed;
		acceleration = stats.acceleration;
		deAcceleration = stats.deAcceleration;
		stopLockLeft = 0;
		stopLockRight = 0; 
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

	public void reactToControlls(int deltaTime, boolean preventMovement) {
		if (!preventMovement) {

			deltaDeAcceleration = deAcceleration * deltaTime/1000;
			if (throttleKeyIsDown && currentSpeed < topSpeed) {

				currentSpeed += acceleration * deltaTime/1000;
			} else if (reverseKeyIsDown && currentSpeed > -stats.reverseTopSpeed) {

				currentSpeed -= stats.reverseAcceleration * deltaTime/1000;
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
					deltaAngleChange = stats.handling * deltaTime/1000;
					movementDegrees -= deltaAngleChange;
				} else if (rightKeyIsDown) {
					deltaAngleChange = stats.handling * deltaTime/1000;
					movementDegrees += deltaAngleChange;
				}
			}
		}
	}
	
	void changeDeAcceleration(float constant){
		
		deAcceleration *= constant;
	}
	
	public void resetDeAcceleration(){
		deAcceleration = stats.deAcceleration;
	}
	
	public void changeCurrentSpeed(float changeConstant){
		currentSpeed *= changeConstant;
	}
	
	public void changeTopSpeed(float changeConstant){
		topSpeed *= changeConstant;
	}
	
	public void resetTopSpeed(){
		
		topSpeed = stats.topSpeed;
	}

	public void throttleKeyDown() {
		throttleKeyIsDown = true;
		reverseKeyUp();
	}

	public void rightKeyDown() {
		if(!ignoreNextRight){
			rightKeyIsDown = true;
		}
		ignoreNextRight = false;
	}

	public void rightKeyUp() {
		
		if(!rightKeyIsDown){
			stopLockRight++;
			if(stopLockRight < 1){
				ignoreNextRight = true;
			}else{
				stopLockRight = 0;
			}
		}else{
			rightKeyIsDown = false;
			ignoreNextRight = false;
		}
	}

	public void reverseKeyDown() {
		reverseKeyIsDown = true;
		throttleKeyUp();
	}
	
	public void leftKeyDown() {
		if(!ignoreNextLeft){
			leftKeyIsDown = true;
		}
		ignoreNextLeft = false;
	}

	public void leftKeyUp() {
		
		if(!leftKeyIsDown){
			stopLockLeft++;
			if(stopLockLeft < 1){
				ignoreNextLeft = true;
			}else{
				stopLockLeft = 0;
			}
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
