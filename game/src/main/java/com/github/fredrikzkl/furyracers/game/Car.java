package com.github.fredrikzkl.furyracers.game;

import java.sql.Time;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.GameContainer;

public class Car {
	public String name;
	public String type;
	public Image sprite;

	public CarProperties stats;
	public String id;
	
	boolean reverseKeyIsDown, throttleKeyIsDown, leftKeyIsDown, rightKeyIsDown, usingKeyboard, finishedRace = false;
	boolean startClock = false;
	
	String side = "error";
	
	public Time duration;
	float currentSpeed = 0;
	float movementDegrees = 0;
	float radDeg = 0;
	
	private Level level;
	private int playerNr;
	int collisionSlowdownConstant = 4, centerOfRotationYOffset = 26, maxLaps = 3;
	
	Polygon collisionBox;
	float[] collisionBoxPoints;
	Vector2f position; 
	Vector2f movementVector = new Vector2f();

	private int passedChekpoints = 0;
	String tileType = null;
	long currentTime;
	private int laps;
	private long startTime, nanoSecondsElapsed, secondsElapsed,minutesElapsed, tenthsOfASecondElapsed = 0;
	private String timeElapsed = "";
	private float deltaAngleChange, deltaDeAcceleration;
	private boolean offRoad, raceStarted = false;
	private float topSpeed;
	
	
	public Car(CarProperties stats, String id, int playerNr, float startX, float startY, Level level){
		this.stats = stats;
		this.id = id;
		this.playerNr = playerNr;
		this.level = level;
		laps = 0;
		
		topSpeed = stats.topSpeed;
		
		try {
			sprite  = new Image(stats.imageFile);
		} catch (SlickException e) {
			System.out.println("Could not find image file " + stats.imageFile);
			e.printStackTrace();
		}
		
		position = new Vector2f(startX,startY);
		collisionBoxPoints  = new float[4];
		collisionBox = new Polygon(collisionBoxPoints);
	}
	
	public void update(GameContainer container, StateBasedGame game, int deltaTime)throws SlickException{
		Input input = container.getInput();
		checkIfRaceStarted();
		reactToControlls(input, deltaTime);
		rePositionCar(deltaTime);
		checkForEdgeOfMap();
		checkForCheckpoint();
		checkForCollision();
		checkForOffRoad();
		checkRaceTime();
	}
	
	public void rePositionCar(int deltaTime){
		
		radDeg = (float) Math.toRadians(movementDegrees);
		
		movementVector.x = (float) Math.cos(radDeg)*currentSpeed*deltaTime/1000;
		movementVector.y = (float) Math.sin(radDeg)*currentSpeed*deltaTime/1000;
		
		position.x += movementVector.x;
		position.y += movementVector.y;	
	}
	
	public void checkIfRaceStarted(){
		
		if(!raceStarted){
			stats.topSpeed = 0;
			
		}else{
			stats.topSpeed = topSpeed;
		}
	}
	
	
	public void checkForEdgeOfMap(){
		
		float[] colBoxPoints = collisionBox.getPoints();
		
		for(int i = 0; i < colBoxPoints.length; i+=2){
			
			if(colBoxPoints[i] < 5 || colBoxPoints[i] > level.getMapWidthPixels()-5)
				position.x -= movementVector.x;
			
			if(colBoxPoints[i+1] < 5 || colBoxPoints[i+1] > level.getMapHeightPixels()-5)
				position.y -= movementVector.y;
		}
	}
	
	public void checkForCheckpoint(){

		int tilePosX = (int) (position.x/level.getTileWidth());
		int tilePosY = (int) (position.y/level.getTileHeight());
		
		tileType = level.getTileType(tilePosX, tilePosY, passedChekpoints);
		
		switch(tileType){
			case "checkpoint1": passedChekpoints++; break;
			case "checkpoint2": passedChekpoints++; break;
			case "checkpoint3": passedChekpoints++; break;
			case "lap": laps++; passedChekpoints = 0;	
		}	
		
		if(laps == 3){
			finishedRace = true;
		}
	}
	
	public void checkForOffRoad(){

		float[] colBoxPoints = collisionBox.getPoints();
		float xPos;
		float yPos;
		int pointsNotOffRoad = 0;
		
		for(int i = 0; i < colBoxPoints.length; i+=2){
			xPos = colBoxPoints[i];
			yPos = colBoxPoints[i+1];
			
			if(level.offRoad(xPos, yPos)){
				if(!offRoad){
					stats.topSpeed /= 2.5;
					currentSpeed /= 2;
					offRoad = true;
					break;
				}
			}else {
				pointsNotOffRoad++;
			}
		}
		
		if(offRoad && pointsNotOffRoad == 4){
			stats.topSpeed *= 2.5;
			offRoad = false;
		}
	}
	
	public void checkForCollision(){

		ArrayList<String> directionsToStop;
		float[] colBoxPoints = collisionBox.getPoints();
		float xPos;
		float yPos;
		int stoppedDirections = 0;
		
		for(int i = 0; i < colBoxPoints.length; i+=2){
			
			xPos = colBoxPoints[i];
			yPos = colBoxPoints[i+1];
			
			if(level.collision(xPos, yPos) && stoppedDirections !=2){
				directionsToStop = level.whichDirectionToStop(xPos, yPos, movementVector.x, movementVector.y);
				stopCarDirection(directionsToStop);
				if(directionsToStop.size() == 2)
					break;
				stoppedDirections++;
			}
		}
	}
	
	public void stopCarDirection(ArrayList<String> directionsToStop){

		/*if(leftKeyIsDown){
			movementDegrees += deltaAngleChange*1.1;
		}else if(rightKeyIsDown){
			movementDegrees -= deltaAngleChange*1.1;
		}*/
		
		deAccelerate(collisionSlowdownConstant);
		
		for(String directionToStop : directionsToStop){
			
			switch(directionToStop){
				case "positiveX": /*if(movementVector.x > 0)*/ position.x -= movementVector.x; break;
				case "negativeX": /*if(movementVector.x < 0)*/position.x -= movementVector.x; break;
				case "positiveY": /*if(movementVector.y > 0)*/position.y -= movementVector.y; break;
				case "negativeY": /*if(movementVector.y < 0)*/position.y -= movementVector.y;break;
			}
		}
	}
	
	public void deAccelerate(int slowdownConstant){
		
		if(currentSpeed < -stats.deAcceleration) {
			
			currentSpeed += deltaDeAcceleration*slowdownConstant;
		}else if(currentSpeed > -stats.deAcceleration && currentSpeed < 0){
		
			currentSpeed = 0;
		}else if(currentSpeed > stats.deAcceleration) {
		
			currentSpeed -= deltaDeAcceleration*slowdownConstant;
		}else if(currentSpeed > 0 && currentSpeed < stats.deAcceleration){
		
			currentSpeed = 0;
		}
	}
	public void render(Graphics g) {
		sprite.setCenterOfRotation(0, centerOfRotationYOffset);
		sprite.draw(position.x, position.y, stats.carSize);
		sprite.setRotation(movementDegrees);
		collisionBox = new Polygon();
		collisionBox.setClosed(true);
		generateCollisionBoxPoints();
	}
	
	public void generateCollisionBoxPoints(){
		
		int carLength = 52;
		int carWidth = 15;
		
		float centerOfRotationX = position.x;
		float centerOfRotationY = position.y + centerOfRotationYOffset;
		
		float backLeftX = (float)(centerOfRotationX+ Math.cos(radDeg+Math.PI/2)*carWidth);
		float backLeftY = (float)((centerOfRotationY) + Math.sin(radDeg+Math.PI/2)*carWidth);
		float frontLeftX = (float)(backLeftX + Math.cos(radDeg)*carLength);
		float frontLeftY = (float)(backLeftY + Math.sin(radDeg)*carLength);
		
		float backRightX = (float)(centerOfRotationX + Math.cos(radDeg-Math.PI/2)*carWidth);
		float backRightY = (float)((centerOfRotationY) + Math.sin(radDeg-Math.PI/2)*carWidth);
		float frontRightX = (float)(backRightX + Math.cos(radDeg)*carLength);
		float frontRightY = (float)(backRightY + Math.sin(radDeg)*carLength);
		
		collisionBox.addPoint(backLeftX, backLeftY);
		collisionBox.addPoint(frontLeftX, frontLeftY);
		collisionBox.addPoint(frontRightX, frontRightY);
		collisionBox.addPoint(backRightX, backRightY);
	}
	
	public void checkRaceTime(){
		
		if(startClock){
			startTime = System.nanoTime();
			startClock = false;
			raceStarted = true;
		}
		
		if(startTime != 0 && !finishedRace){
			currentTime = System.nanoTime();
			nanoSecondsElapsed = currentTime - startTime;
			minutesElapsed = TimeUnit.NANOSECONDS.toMinutes(nanoSecondsElapsed);
			secondsElapsed = TimeUnit.NANOSECONDS.toSeconds(nanoSecondsElapsed) - 60*minutesElapsed;
			tenthsOfASecondElapsed = TimeUnit.NANOSECONDS.toMillis(nanoSecondsElapsed)/100 - TimeUnit.NANOSECONDS.toSeconds(nanoSecondsElapsed)*10;
			
			timeElapsed = minutesElapsed + ":" + secondsElapsed + ":" + tenthsOfASecondElapsed;
		}
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
	
	public String getTimeElapsed(){
		return timeElapsed;
	}
	
	public int getPlayerNr() {
		return playerNr;
	}
	
	public Image getImage(){
		return sprite;
	}
	
	public void startClock(){
		startClock = true;
	}
	
	public int getLaps(){
		if(laps < maxLaps)
			return laps+1;
		return maxLaps;
	}
	
	public boolean finishedRace(){
		return finishedRace;
	}
	
}
