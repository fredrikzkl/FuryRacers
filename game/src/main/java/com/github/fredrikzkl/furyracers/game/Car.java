package com.github.fredrikzkl.furyracers.game;

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


public class Car implements Comparable<Car>,Runnable {

	
	int playerNr;

	private int laps, collisionSlowdownConstant = 4,  
			centerOfRotationYOffset = 26,
			maxLaps = 3, passedChekpoints,time;
	
	private long startTime, nanoSecondsElapsed, 
	secondsElapsed,minutesElapsed, tenthsOfASecondElapsed,
	currentTime = 0;
	
	private boolean offRoad, raceStarted, finishedRace, startClock;
	private boolean paused;
	
	private float topSpeed, currentSpeed, radDeg;
	
	float[] collisionBoxPoints;
	
	private String tileType, timeElapsed;
	public String id;
	
	private Image sprite;
	private CarProperties stats;
	private Level level; 
	private Polygon collisionBox;
	Vector2f position;

	private Vector2f movementVector;
	private Controlls controlls;
	
	public Car(CarProperties stats, String id, int playerNr, float startX, float startY, Level level){
		this.stats = stats;
		this.id = id;
		this.playerNr = playerNr;
		this.level = level;
		position = new Vector2f(startX,startY);
		controlls = new Controlls(this, stats);
		
		initVariables();
		getCarSprite();
		
	}
	
	private void initVariables(){
		time = 0;
		paused = true;
		passedChekpoints = 0;
		laps = 0;
		offRoad = false;
		raceStarted = false; 
		finishedRace = false; 
		startClock = false;
		topSpeed = stats.topSpeed;
		collisionBoxPoints  = new float[4];
		collisionBox = new Polygon(collisionBoxPoints);
		movementVector = new Vector2f();
	}
	
	private void getCarSprite(){
		
		try {
			sprite  = new Image(stats.imageFile);
		} catch (SlickException e) {
			System.out.println("Could not find image file " + stats.imageFile);
			e.printStackTrace();
		}
	}
	
	
	public void update(GameContainer container, StateBasedGame game, int deltaTime)throws SlickException{
		
		Input input = container.getInput();
		currentSpeed = controlls.getCurrentSpeed();
		controlls.reactToControlls(input, deltaTime,paused);
		rePositionCar(deltaTime);
		checkForEdgeOfMap();
		checkForCheckpoint();
		checkForCollision();
		checkForOffRoad();
		checkRaceTime();
		
	}
	
	public void rePositionCar(int deltaTime){
		
		radDeg = (float) Math.toRadians(controlls.getMovementDegrees());
		float currentSpeed = controlls.getCurrentSpeed();
		
		movementVector.x = (float) Math.cos(radDeg)*currentSpeed*deltaTime/1000;
		movementVector.y = (float) Math.sin(radDeg)*currentSpeed*deltaTime/1000;
		
		position.x += movementVector.x;
		position.y += movementVector.y;	
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
		
		if(laps == 1){
			finishedRace = true;
			stats.deAcceleration = 250;
			controlls.throttleKeyUp();
			controlls.leftKeyUp();
			controlls.rightKeyUp();
			setTime((int) (minutesElapsed+ secondsElapsed + tenthsOfASecondElapsed));
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
					controlls.changeTopSpeed(0.5f);
					controlls.changeCurrentSpeed(0.5f);
					offRoad = true;
					break;
				}
			}else {
				pointsNotOffRoad++;
			}
		}
		
		if(offRoad && pointsNotOffRoad == 4){
			controlls.changeTopSpeed(2);
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
		
		float deltaDeAcceleration = controlls.getDeltaDeAcceleration();
		
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
		
		float carRotation = controlls.getMovementDegrees(); 
		sprite.setCenterOfRotation(0, centerOfRotationYOffset);
		sprite.draw(position.x, position.y, stats.carSize);
		sprite.setRotation(carRotation);
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
			paused = false;
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
	
	public void buttonDown(String data){
		controlls.disableKeyboardInput();
        switch(data){
        	case "0": controlls.reverseKeyDown();break;
        	case "1": controlls.throttleKeyDown();break;
        	case "2": controlls.rightKeyDown();break;
        	case "3": controlls.leftKeyDown();
        }
	}
	
	public void buttonUp(String data){
		switch(data){
			case "0": controlls.reverseKeyUp();break;
			case "1": controlls.throttleKeyUp();break;
			case "2": controlls.rightKeyUp();break;
			case "3": controlls.leftKeyUp();
		}
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
	
	public Vector2f getPosition() {
		return position;
	}

	public void run(GameContainer container, StateBasedGame game, int deltaTime)throws SlickException{
		
		try{
			Input input = container.getInput();
			currentSpeed = controlls.getCurrentSpeed();
			controlls.reactToControlls(input, deltaTime, paused);
			rePositionCar(deltaTime);
			checkForEdgeOfMap();
			checkForCheckpoint();
			checkForCollision();
			checkForOffRoad();
			checkRaceTime();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	
	

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}
	
	
	@Override
	public int compareTo(Car o) {
		return -(Integer.compare(this.getTime(), o.getTime()));
	}
	
	
}
