package com.github.fredrikzkl.furyracers.game;

import java.sql.Time;

import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.GameContainer;

public class Car {
	public String name;
	public String type;
	
	public Image sprite;
	public Time duration;
	
	public float topSpeed;
	public float reverseTopSpeed;
	public float acceleration;
	public float reverseAcceleration;
	public float deAcceleration;
	public float handling;
	public float weight;
	
	boolean reverseKeyIsDown, throttleKeyIsDown, leftKeyIsDown, rightKeyIsDown, usingKeyboard, stopClock = false;
	boolean startClock = true;
	
	
	float currentSpeed = 0;
	float movementDegrees = 0;
	float radDeg = 0;
	
	float carSize = (float) 0.4;
	
	private Level level;
	private int tilePosX, tilePosY;
	private int playerNr;
	
	Vector2f position;
	Vector2f unitCirclePos = new Vector2f();

	private int passedChekpoints = 0;
	int tileType;
	long startTime;
	long endTime;
	private int laps = 0;
	
	public Car(String name, String type, int playerNr, Image sprite, float startX, float startY, float reverseTopSpeed,float topSpeed,
			float acceleration, float reverseAcceleration, float deAcceleration, float handling, float weight, Level level){
	
		this.name = name;
		this.type = type;
		this.sprite = sprite;
		this.topSpeed = topSpeed;
		this.reverseTopSpeed = reverseTopSpeed;
		this.reverseAcceleration = reverseAcceleration;
		this.deAcceleration = deAcceleration;
		this.acceleration = acceleration;
		this.playerNr = playerNr;
		this.handling = handling;
		this.weight = weight;
		this.level = level;
		
		position = new Vector2f(startX,startY);
	}
	
	public void update(GameContainer container, StateBasedGame game, int deltaTime)throws SlickException{
		Input input = container.getInput();
		reactToControlls(input, deltaTime);
		
		rePositionCar(deltaTime);
		checkForEdgeOfMap();
		checkTilePosition();
		
		/*boolean slowDown = level.getTileType(tilePosX, tilePosY);
		
		if(slowDown){
			currentSpeed = topSpeed/2;
		}*/
	}
	
	public void rePositionCar(int deltaTime){
		
		radDeg = (float) Math.toRadians(movementDegrees);
		
		checkIfRaceStarted();
		
		unitCirclePos.x = (float) (Math.cos(radDeg))*currentSpeed*deltaTime/1000;
		unitCirclePos.y = (float) (Math.sin(radDeg))*currentSpeed*deltaTime/1000;
		
		position.x += unitCirclePos.x;
		position.y += unitCirclePos.y;	
	}
	
	public void checkIfRaceStarted(){
		if(currentSpeed > 0 && startClock){
			startTime = System.nanoTime();
			startClock = false;
		}
		
		if(stopClock){
			endTime = System.nanoTime();
			stopClock = false;
			System.out.println("final time " + (endTime - startTime)/100000000);
		}
		
		
	}
	
	public void checkForEdgeOfMap(){
		
		if(position.x < 0 || position.x > level.getDistanceWidth()){
			position.x -= unitCirclePos.x;
		}
		
		if(position.y < 0 || position.y > level.getDistanceHeight()){
			position.y -= unitCirclePos.y;
		}
	}
	
	public void checkTilePosition(){

		tilePosX = (int) (position.x/level.getTileWidth());
		tilePosY = (int) (position.y/level.getTileHeight());
		
		tileType = level.getTileType(tilePosX, tilePosY, passedChekpoints);
		
		switch(tileType){
			case 1: passedChekpoints++; System.out.println("checkpoint1"); break;
			case 2: passedChekpoints++; System.out.println("checkpoint2");break;
			case 3: passedChekpoints++; System.out.println("checkpoint3");break;
			case 4: stopClock = true; break;
		}
	}
	
	public void render() {
		sprite.draw(position.x, position.y, carSize);
		sprite.setCenterOfRotation(carSize*16, carSize*32);
		sprite.setRotation(movementDegrees);
	}

	public Vector2f getPosition() {
		return position;
	}
	
	public void buttonDown(String data){
		disableKeyboardInput();
        switch(data){
        	case "1": throttleKeyDown();break;
        	case "2": rightKeyDown();break;
        	case "3": leftKeyDown();break;
        }
	}
	
	public void buttonUp(String data){
		switch(data){
			case "1": throttleKeyUp();break;
			case "2": rightKeyUp();break;
			case "3": leftKeyUp();break;
		}
	}
	
	public void reactToControlls(Input input, int deltaTime) {
		
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
			if(currentSpeed > -reverseTopSpeed) {
				currentSpeed -= reverseAcceleration*deltaTime/1000;
			}
		} else{
			if(currentSpeed < -reverseAcceleration) {
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
		
		if(playerNr == 1){
			reactToArrowKeys(input);
		}else if(playerNr == 2){
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
