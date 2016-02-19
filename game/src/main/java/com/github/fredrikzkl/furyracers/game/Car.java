package com.github.fredrikzkl.furyracers.game;

import java.sql.Time;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.GameContainer;

public class Car {
	public String name;
	public String type;
	
	public Image sprite;
	public Time duration;
	public Circle dot;
	
	public float topSpeed;
	public float reverseTopSpeed;
	public float acceleration;
	public float reverseAcceleration;
	public float deAcceleration;
	public float handling;
	public float weight;
	
	boolean reverseKeyIsDown, throttleKeyIsDown, leftKeyIsDown, rightKeyIsDown, usingKeyboard, finishedRace = false;
	boolean startClock = true;
	
	String side = "error";
	
	float currentSpeed = 0;
	float movementDegrees = 0;
	float radDeg = 0;
	
	float carSize = (float) 0.4;
	
	private Level level;
	private int tilePosX, tilePosY;
	private int playerNr;

	Vector2f position;
	Vector2f movementVector = new Vector2f();

	private int passedChekpoints = 0;
	String tileType = null;
	long startTime = 0;
	long currentTime;
	private int laps = 0;
	private long nanoSecondsElapsed = 0;
	private long secondsElapsed = 0;
	private long minutesElapsed = 0;
	private long minuteConverter = 0;
	private long milliSecondsElapsed = 0;
	private String timeElapsed;
	
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
		checkRaceTime();
		
		/*boolean slowDown = level.getTileType(tilePosX, tilePosY);
		
		if(slowDown){
			currentSpeed = topSpeed/2;
		}*/
	}
	
	public void rePositionCar(int deltaTime){
		
		radDeg = (float) Math.toRadians(movementDegrees);
		
		movementVector.x = (float) (Math.cos(radDeg))*currentSpeed*deltaTime/1000;
		movementVector.y = (float) (Math.sin(radDeg))*currentSpeed*deltaTime/1000;
		
		position.x += movementVector.x;
		position.y += movementVector.y;	
	}
	
	public void checkRaceTime(){
		if(currentSpeed > 0 && startClock){
			startTime = System.nanoTime();
			startClock = false;
		}
		
		if(startTime != 0 && !finishedRace){
			currentTime = System.nanoTime();
			nanoSecondsElapsed = currentTime - startTime;
			minutesElapsed = TimeUnit.NANOSECONDS.toMinutes(nanoSecondsElapsed);
			secondsElapsed = TimeUnit.NANOSECONDS.toSeconds(nanoSecondsElapsed) - 60*minutesElapsed;
			milliSecondsElapsed = TimeUnit.NANOSECONDS.toMillis(nanoSecondsElapsed) - TimeUnit.NANOSECONDS.toSeconds(nanoSecondsElapsed)*1000;
			
			timeElapsed = minutesElapsed + ":" + secondsElapsed + ":" + milliSecondsElapsed;
		}
	}
	
	public void checkForEdgeOfMap(){
		
		if(position.x < 0 || position.x > level.getDistanceWidth()){
			position.x -= movementVector.x;
		}
		
		if(position.y < 0 || position.y > level.getDistanceHeight()){
			position.y -= movementVector.y;
		}
	}
	
	public void checkTilePosition(){

		tilePosX = (int) (position.x/level.getTileWidth());
		tilePosY = (int) (position.y/level.getTileHeight());
		
		tileType = level.getTileType(tilePosX, tilePosY, passedChekpoints);
		
		ArrayList<String> siden = new ArrayList<String>();
		siden.add("");
		siden.add("");
		switch(tileType){
			case "checkpoint1": passedChekpoints++; break;
			case "checkpoint2": passedChekpoints++; break;
			case "checkpoint3": passedChekpoints++; break;
			case "finishLine": finishedRace = true; break;
			case "collisionObstacle": 
				siden = level.whichSideHasBeenCrossed(tilePosX, tilePosY, 
												position.x, position.y, 
												movementVector.x, movementVector.y);
												if(!siden.get(0).equals(side)){System.out.println("startTileX: " + level.getTileStartX() + " startTileY: " + level.getTileStartY());
												System.out.println(level.isTopLineCrossed());
												System.out.println("EndTileX: " + level.getTileEndX() + " EndTileY: " + level.getTileEndY());
												System.out.println("startX: " + level.getxPos()+ " startY: " + level.getyPos());
												System.out.println("prevX: " + level.getPrevPosX()+ " prevY: " + level.getPrevPosY());
												System.out.println("bottom: " + level.getLineIntersectsBottom()+ " top: " + level.getLineIntersectsTop());
												System.out.println("left: " + level.getLineIntersectsLeft()+ " right: " + level.getLineIntersectsRight());
												
												System.out.println("alleged: " + siden);
												side = siden.get(0);
												}
				
		}	
		
		switch(siden.get(0)){
			case "xMovPos": position.x -= movementVector.x; break;
			case "xMovNeg": position.x -= movementVector.x; break;
			case "yMovPos": position.y -= movementVector.y; break;
			case "yMovNeg": position.y -= movementVector.y; break;
		}
		
		switch(siden.get(1)){
			case "xMovPos": position.x -= movementVector.x; break;
			case "xMovNeg": position.x -= movementVector.x; break;
			case "yMovPos": position.y -= movementVector.y; break;
			case "yMovNeg": position.y -= movementVector.y; break;
		}
	}
	
	public void render() {
		sprite.setCenterOfRotation(0, 32);
		sprite.draw(position.x, position.y, carSize);
		
		sprite.setRotation(movementDegrees);
	}

	public Vector2f getPosition() {
		return position;
	}
	
	public void buttonDown(String data){
		disableKeyboardInput();
        switch(data){
        	case "0": reverseKeyDown();break;
        	case "1": throttleKeyDown();break;
        	case "2": rightKeyDown();break;
        	case "3": leftKeyDown();
        }
	}
	
	public void buttonUp(String data){
		switch(data){
			case "0": reverseKeyUp();break;
			case "1": throttleKeyUp();break;
			case "2": rightKeyUp();break;
			case "3": leftKeyUp();
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
		}else{
			if(currentSpeed > 0) {
				currentSpeed -= deAcceleration*deltaTime/1000;
			}else if(!reverseKeyIsDown && currentSpeed > 0){
				currentSpeed = 0;
			}
		}

		if(reverseKeyIsDown) {
			if(currentSpeed > -reverseTopSpeed) {
				currentSpeed -= reverseAcceleration*deltaTime/1000;
			}
		}else{
			if(currentSpeed < 0) {
				currentSpeed += deAcceleration*deltaTime/1000;
			}else if(!throttleKeyIsDown && currentSpeed < 0){
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
	
	public String getTimeElapsed(){
		return timeElapsed;
	}
	
	public int getPlayerNr() {
		return playerNr;
	}
	
	public Image getImage(){
		return sprite;
	}
}
