package com.github.fredrikzkl.furyracers.game;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import javax.websocket.EncodeException;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.GameContainer;

import com.github.fredrikzkl.furyracers.network.GameSession;

public class Car implements Comparable<Car> {
	
	private static final int 
	originalCarWidth = 64, originalCarLength = 128, maxLaps = GameCore.maxLaps; 
	
	private int 
	playerNr, laps, passedChekpoints, time;
	
	private long 
	startTime, nanoSecondsElapsed, 
	secondsElapsed, minutesElapsed, 
	tenthsOfASecondElapsed, currentTime,
	totalTenthsOfSeconds;
	
	private boolean 
	offRoad, finishedRace, startClock, paused;
	
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

	private Sound 
	finalRound, crowdFinish,
	checkpointSound, lapSound, still,
	topSpeedSound;
	
	private Image sprite;
	private CarProperties stats;
	private Level level;
	private CollisionBox colBox;
	private CollisionHandler collision;
	Controlls controlls;

	public Car(CarProperties stats, String id, int playerNr, float startX, float startY, Level level) {
		
		colBox = new CollisionBox(this);
		controlls = new Controlls(stats);
		collision = new CollisionHandler(this);
		carLength = originalCarLength * stats.carSize;
		carWidth = originalCarWidth * stats.carSize;
		
		this.stats = stats;
		this.id = id;
		this.playerNr = playerNr;
		this.level = level;
		
		getCarSprite();
		initVariables();
		initSounds();
		detStartPos(startX, startY);
		position = new Vector2f(startPos);
	}
	
	private void detStartPos(float startX, float startY){
		
		Car previousCar = GameCore.getCar(playerNr-1);
		float spaceBetweenCars = originalCarWidth/4;
		startX -= carLength;

		if(previousCar != null){
			
			float prevCarStartY = previousCar.getStartPos().y;
			float prevCarEndY = prevCarStartY + previousCar.getCarWidth();
			startY = prevCarEndY + spaceBetweenCars;
		}
		
		startPos = new Vector2f(startX, startY);
	}

	private void initVariables() {
		time = passedChekpoints = laps = 0;
		currentTime = 0;
		paused = true;
		offRoad = false;
		username = "";
		finishedRace = false;
		startClock = false;
		movementVector = new Vector2f();
		centerOfRotationYOffset = originalCarWidth*stats.carSize;
		stoppingDirections = new ArrayList<String>();
	}

	private void getCarSprite() {

		try {
			sprite = new Image(stats.imageFile);
		} catch (SlickException e) {
			System.out.println("Could not find image file " + stats.imageFile);
			e.printStackTrace();
		}
	}

	public void update(GameContainer container, StateBasedGame game, int deltaTime) throws SlickException, IOException, EncodeException {

		currentSpeed = controlls.getCurrentSpeed();
		controlls.reactToControlls(deltaTime, paused);
		rePositionCar(deltaTime);
		checkForEdgeOfMap();
		checkForCheckpoint();
		collision.checkForCollision();
		checkForOffRoad();
		checkRaceTime();
		sounds();
	}

	public void rePositionCar(int deltaTime) {

		radDeg = (float) Math.toRadians(controlls.getMovementDegrees());
		float currentSpeed = controlls.getCurrentSpeed();

		movementVector.x = (float) Math.cos(radDeg) * currentSpeed * deltaTime / 1000;
		movementVector.y = (float) Math.sin(radDeg) * currentSpeed * deltaTime / 1000;

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
				if (!checkpointSound.playing())
					checkpointSound.play();
				break;
			case "checkpoint2":
				passedChekpoints++;
				if (!checkpointSound.playing())
					checkpointSound.play();
				break;
			case "checkpoint3":
				passedChekpoints++;
				if (!checkpointSound.playing())
					checkpointSound.play();
				break;
			case "lap":
				if(!lapSound.playing() && laps != 3)
					lapSound.play();
				laps++;
				passedChekpoints = 0;
		}
		if (laps == maxLaps-1) {
			if (!GameCore.finalRoundSaid) {
				finalRound.play();
				GameCore.finalRoundSaid = true;
			}
		}

		if (laps == maxLaps) {
			if (!GameCore.crowdFinishedPlayed) {
				crowdFinish.play();
				GameCore.crowdFinishedPlayed = true;
			}
			finishedRace = true;
			controlls.throttleKeyUp();
			controlls.leftKeyUp();
			controlls.rightKeyUp();
			setTime((int) totalTenthsOfSeconds);
		}
	}

	public void checkForOffRoad() throws IOException, EncodeException {

		float[] colBoxPoints = colBox.getPoints();
		float xPos, yPos;
		int 
		pointsNotOffRoad = 0,
		amountOfPointsXY = colBoxPoints.length;
		
		for(int i = 0; i < amountOfPointsXY; i+=2){
			
			xPos = colBoxPoints[i];
			yPos = colBoxPoints[i + 1];

			if (level.offRoad(xPos, yPos)) {
				if (!offRoad) {
					controlls.changeTopSpeed(0.5f);
					controlls.changeCurrentSpeed(0.5f);
					offRoad = true;
					GameSession.toggleRumbling(id);
					break;
				}
			} else {
				pointsNotOffRoad++;
			}
		}
		
		if(offRoad && pointsNotOffRoad == amountOfPointsXY/2){
			controlls.changeTopSpeed(2);
			offRoad = false;
			GameSession.toggleRumbling(id);
		}
	}
	
	Vector2f getTurningDirectionVector(){
		
		String turningDirection = controlls.getTurningDirection();
		float deltaAngleChange = controlls.getDeltaAngleChange();
		float movementDegrees = controlls.getMovementDegrees();
		float angleBeforeCollision;
		Vector2f turningVector = new Vector2f();
		
		if(turningDirection == "positive"){
			angleBeforeCollision = movementDegrees - deltaAngleChange;
			double toRad = Math.toRadians(angleBeforeCollision);
			turningVector.x = (float) Math.cos(toRad-Math.PI/2);
			turningVector.x = (float) Math.sin(toRad-Math.PI/2);
			
			return turningVector;
		}
		
		if(turningDirection == "negative"){
			angleBeforeCollision = movementDegrees + deltaAngleChange;
			double toRad = Math.toRadians(angleBeforeCollision);
			turningVector.x = (float) Math.cos(toRad+Math.PI/2);
			turningVector.x = (float) Math.sin(toRad+Math.PI/2);
			
			return turningVector;
		}
		
		return turningVector;
	}

	public void render(Graphics g) {

		float carRotation = controlls.getMovementDegrees();
		sprite.setCenterOfRotation(0, centerOfRotationYOffset);
		sprite.setRotation(carRotation);
		sprite.draw(position.x, position.y, stats.carSize);
		colBox.generatePoints();
	}
	
	public ArrayList<String> getDirectionsToStop(){
		
		return stoppingDirections;
	}
	
	
	public void checkRaceTime() {

		if (startClock) {
			startTime = System.nanoTime();
			startClock = false;
			paused = false;
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

	public void buttonDown(String data) {
		controlls.disableKeyboardInput();
		switch (data) {
		case "0":
			controlls.reverseKeyDown();
			break;
		case "1":
			controlls.throttleKeyDown();
			break;
		case "2":
			controlls.rightKeyDown();
			break;
		case "3":
			controlls.leftKeyDown();
		}
	}

	public void buttonUp(String data) {
		switch (data) {
		case "0":
			controlls.reverseKeyUp();
			break;
		case "1":
			controlls.throttleKeyUp();
			break;
		case "2":
			controlls.rightKeyUp();
			break;
		case "3":
			controlls.leftKeyUp();
		}
	}

	public String getTimeElapsed() {
		return timeElapsed;
	}

	public int getPlayerNr() {
		return playerNr;
	}

	public Image getImage() {
		return sprite;
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

	public void initSounds() {
		String path = "/Sound/carSounds/";
		String type = ".ogg";
		try {
			finalRound = new Sound("/Sound/announcer/finalRound" + type);
			crowdFinish = new Sound("/Sound/crowdFinish" + type);
			checkpointSound = new Sound("/Sound/checkpoint" + type);
			lapSound = new Sound("/Sound/lap" + type);

			still = new Sound(path + "still" + type);
			topSpeedSound = new Sound(path + "speed" + type);
		} catch (SlickException e) {
			System.out.println("ERROR! Could not load car sounds!");
		}
	}
	
	public boolean deAcceleratingSoundPlayed = false;
	
	private void sounds() {

		if(currentSpeed < 1 && !topSpeedSound.playing()){
			if(!still.playing())
				still.play();
		}
		if(controlls.throttleKeyIsDown){
			if(!topSpeedSound.playing()){
				still.stop();
				topSpeedSound.play();
				deAcceleratingSoundPlayed = false;
			}
		}else{
			topSpeedSound.stop();
			if(!still.playing())
				still.play();
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
	
	CollisionBox getCollisionBox(){
		return colBox;
	}
}
