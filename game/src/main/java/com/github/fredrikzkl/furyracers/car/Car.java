package com.github.fredrikzkl.furyracers.car;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javax.websocket.EncodeException;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.GameContainer;

import com.github.fredrikzkl.furyracers.assets.Sounds;
import com.github.fredrikzkl.furyracers.game.GameCore;
import com.github.fredrikzkl.furyracers.game.Level;
import com.github.fredrikzkl.furyracers.network.GameSession;

public class Car implements Comparable<Car> {
	
	private static final int
	maxLaps = GameCore.maxLaps; 
	
	private int 
	playerNr, laps, passedChekpoints, 
	time, originalCarWidth, originalCarLength;
	
	private long 
	startTime, nanoSecondsElapsed, 
	secondsElapsed, minutesElapsed, 
	tenthsOfASecondElapsed, currentTime,
	totalTenthsOfSeconds;
	
	private boolean 
	offRoad, finishedRace, startClock, preventMovement;
	
	private float 
	currentSpeed, radDeg, centerOfRotationYOffset,
	carLength, carWidth, centerOfRotationY;
	
	float[] collisionBoxPoints;

	private String 
	tileType, timeElapsed, 
	id, username;
	
	private Vector2f 
	movementVector, position, startPos;

	private ArrayList<String> 
	stoppingDirections;
	
	public boolean deAcceleratingSoundPlayed = false;
	
	private CarProperties stats;
	private Level level;
	private CollisionBox colBox;
	private CollisionHandler collision;
	public Controlls controlls;

	public Car(CarProperties stats, String id, int playerNr, Vector2f startArea, Level level) {
		
		originalCarLength = stats.carImage.getWidth();
		originalCarWidth = stats.carImage.getHeight()/2;
		
		carLength = originalCarLength * stats.carSize;
		carWidth = originalCarWidth * stats.carSize;
		
		centerOfRotationYOffset = stats.carImage.getHeight()/2 * stats.carSize;
		
		this.stats = stats;
		this.id = id;
		this.playerNr = playerNr;
		this.level = level;
		
		initVariables();
		detStartPos(startArea);
		
		position = new Vector2f(startPos);
		collision = new CollisionHandler(this);
		colBox = new CollisionBox(this);
		controlls = new Controlls(stats);
	}
	
	private void detStartPos(Vector2f carStartPos){
		
		Car previousCar = GameCore.getCar(playerNr-1);
		float spaceBetweenCars = originalCarWidth/4;
		carStartPos.x -= carLength;

		if(previousCar != null){
			float prevCarStartY = previousCar.getStartPos().y;
			float prevCarEndY = prevCarStartY + previousCar.getCarWidth();
			carStartPos.y = prevCarEndY + spaceBetweenCars;
		}
		
		startPos = new Vector2f(carStartPos);
	}

	private void initVariables() {
		time = passedChekpoints = laps = 0;
		currentTime = 0;
		preventMovement = true;
		offRoad = false;
		username = "";
		finishedRace = false;
		startClock = false;
		movementVector = new Vector2f();
		stoppingDirections = new ArrayList<String>();
	}

	public void update(GameContainer container, StateBasedGame game, int deltaTime) throws SlickException, IOException, EncodeException {

		controlls.reactToControlls(deltaTime, preventMovement);
		rePositionCar(deltaTime);
		checkForEdgeOfMap();
		checkForCheckpoint();
		collision.checkForCollision();
		checkForOffRoad();
		checkRaceTime();
		carSounds();
	}

	public void rePositionCar(int deltaTime) {

		radDeg = (float) Math.toRadians(controlls.getMovementDegrees());
		currentSpeed = controlls.getCurrentSpeed();

		movementVector.x = (float) Math.cos(radDeg) * currentSpeed * deltaTime/1000;
		movementVector.y = (float) Math.sin(radDeg) * currentSpeed * deltaTime/1000;

		position.x += movementVector.x;
		position.y += movementVector.y;
	}

	public void checkForEdgeOfMap() {

		float[] 
		colBoxPoints = colBox.getPoints();
		int 
		safetyMargin = 7,
		startOfMapX = safetyMargin, 
		startOfMapY = safetyMargin;

		for (int i = 0; i < colBoxPoints.length; i += 2) {

			if (colBoxPoints[i] < startOfMapX || colBoxPoints[i] > level.getMapWidthPixels() - startOfMapX)
				position.x -= movementVector.x;

			if (colBoxPoints[i + 1] < startOfMapY || colBoxPoints[i + 1] > level.getMapHeightPixels() - startOfMapY)
				position.y -= movementVector.y;
		}
	}

	public void checkForCheckpoint() {

		int tilePosX = (int) (position.x / level.getTileWidth());
		int tilePosY = (int) (position.y / level.getTileHeight());

		tileType = level.getTileType(tilePosX, tilePosY, passedChekpoints);
		
		switch (tileType) {
			case "checkpoint1":
				passedChekpoints++;
				if (!Sounds.checkpoint.playing())
					Sounds.checkpoint.play();
				break;
			case "checkpoint2":
				passedChekpoints++;
				if (!Sounds.checkpoint.playing())
					Sounds.checkpoint.play();
				break;
			case "checkpoint3":
				passedChekpoints++;
				if (!Sounds.checkpoint.playing())
					Sounds.checkpoint.play();
				break;
			case "lap":
				if(!Sounds.lap.playing() && laps != 3)
					Sounds.lap.play();
				laps++;
				passedChekpoints = 0;
		}
		
		if (laps == maxLaps-1) {
			if (!GameCore.finalRoundSaid) {
				Sounds.finalRound.play();
				GameCore.finalRoundSaid = true;
			}
		}

		if (laps == maxLaps) {
			if (!GameCore.crowdFinishedPlayed) {
				Sounds.crowdFinish.play();
				GameCore.crowdFinishedPlayed = true;
			}
			finishedRace = true;
			controlls.throttleKeyUp();
			controlls.leftKeyUp();
			controlls.rightKeyUp();
			controlls.changeDeAcceleration(1.04f);
			setTime((int) totalTenthsOfSeconds);
		}
	}

	public void checkForOffRoad() {

		float[] 
		colBoxPoints = colBox.getPoints();
		
		float 
		xPos, yPos;
		
		int 
		pointsNotOffRoad = 0,
		amountOfXpointsAndYpoints = colBoxPoints.length;
		
		for(int i = 0; i < amountOfXpointsAndYpoints; i+=2){
			
			xPos = colBoxPoints[i];
			yPos = colBoxPoints[i + 1];

			if (level.offRoad(xPos, yPos)) {
				if (!offRoad) {
					controlls.changeTopSpeed(0.5f);
					controlls.changeCurrentSpeed(0.5f);
					offRoad = true;
					rumbleController(true);
					break;
				}
			} else {
				pointsNotOffRoad++;
			}
		}
		
		if(offRoad && pointsNotOffRoad == amountOfXpointsAndYpoints/2){
			controlls.changeTopSpeed(2);
			offRoad = false;
			rumbleController(false);
		}
	}
	
	public void rumbleController(boolean rumbleController) {
			
		try {
			if(rumbleController){
				GameSession.rumbleControllerOn(id);
			}else{
				GameSession.rumbleControllerOff(id);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (EncodeException e) {
			e.printStackTrace();
		}
	}
	
	Vector2f getTurningDirectionVector(){
		
		String turningDirection = controlls.getTurningDirection();
		float deltaAngleChange = controlls.getDeltaAngleChange();
		float degreesRotated = controlls.getMovementDegrees();
		float angleBeforeCollision;
		Vector2f turningVector = new Vector2f();
		
		if(turningDirection == "positive"){
			angleBeforeCollision = degreesRotated - deltaAngleChange;
			double toRad = Math.toRadians(angleBeforeCollision);
			turningVector.x = (float) Math.cos(toRad+Math.PI/2);
			turningVector.x = (float) Math.sin(toRad+Math.PI/2);
			
			return turningVector;
		}
		
		if(turningDirection == "negative"){
			angleBeforeCollision = degreesRotated + deltaAngleChange;
			double toRad = Math.toRadians(angleBeforeCollision);
			turningVector.x = (float) Math.cos(toRad-Math.PI/2);
			turningVector.x = (float) Math.sin(toRad-Math.PI/2);
			
			return turningVector;
		}
		
		return turningVector;
	}

	public void render(Graphics g) {

		float carRotation = controlls.getMovementDegrees();
		stats.carImage.setCenterOfRotation(0, centerOfRotationYOffset);
		stats.carImage.setRotation(carRotation);
		stats.carImage.draw(position.x, position.y, stats.carSize);
		colBox.generatePoints();
	}
	
	public ArrayList<String> getDirectionsToStop(){
		
		return stoppingDirections;
	}
	
	private void checkRaceTime() {

		if (startClock) {
			startTime = System.nanoTime();
			startClock = false;
			preventMovement = false;
		}

		if (startTime != 0 && !finishedRace) {
			currentTime = System.nanoTime();
			nanoSecondsElapsed = currentTime - startTime;
			minutesElapsed = TimeUnit.NANOSECONDS.toMinutes(nanoSecondsElapsed);
			secondsElapsed = TimeUnit.NANOSECONDS.toSeconds(nanoSecondsElapsed) - 60 * minutesElapsed;
			totalTenthsOfSeconds = TimeUnit.NANOSECONDS.toMillis(nanoSecondsElapsed) / 100;
			tenthsOfASecondElapsed = totalTenthsOfSeconds
					- TimeUnit.NANOSECONDS.toSeconds(nanoSecondsElapsed) * 10;

			timeElapsed = minutesElapsed + ":" + secondsElapsed + ":" + tenthsOfASecondElapsed;
		}
	}

	public String getTimeElapsed() {
		return timeElapsed;
	}

	public int getPlayerNr() {
		return playerNr;
	}

	public Image getImage() {
		return stats.carImage;
	}

	public void startClock() {
		startClock = true;
	}

	public int getLaps() {
		if (laps < maxLaps)
			return laps + 1;
		return maxLaps;
	}

	public boolean finishedRace() {
		return finishedRace;
	}

	public Vector2f getPosition() {
		return position;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int totalTime) {
		this.time = totalTime;
	}

	@Override
	public int compareTo(Car o) {
		return -(Integer.compare(this.getTime(), o.getTime()));
	}
	
	
	private void carSounds() {

		if(currentSpeed < 1 && !Sounds.topSpeed.playing()){
			if(!Sounds.still.playing())
				Sounds.still.play();
		}
		if(controlls.throttleKeyIsDown){
			if(!Sounds.topSpeed.playing()){
				Sounds.still.stop();
				Sounds.topSpeed.play();
				deAcceleratingSoundPlayed = false;
			}
		}else{
			Sounds.topSpeed.stop();
			if(!Sounds.still.playing())
				Sounds.still.play();
		}
	}
	
	public void setUsername(String username){
		this.username = username;
	}
	
	public String getUsername(){
		return username;
	}
	
	public Vector2f getMovementVector(){
		return movementVector;
	}
	
	public Vector2f getStartPos(){
		return startPos;
	}
	
	float getCenterOfRotationYOffset(){
		return centerOfRotationYOffset;
	}
	
	float getCarLength(){
		return carLength;
	}
	
	float getCarWidth(){
		return carWidth;
	}
	
	float getCenterOfRotationY(){
		return centerOfRotationY;
	}
	
	float getRotationRad(){
		return radDeg;
	}
	
	public CollisionBox getCollisionBox(){
		return colBox;
	}
	
	public void setOffroad(boolean isOffroad){
		offRoad = isOffroad;
	}
}
