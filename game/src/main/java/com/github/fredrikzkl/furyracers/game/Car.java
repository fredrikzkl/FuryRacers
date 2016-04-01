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
	
	private int originalCarWidth = 64, originalCarLength = 128, playerNr;
	private int laps, maxLaps = 0, passedChekpoints, time;
	
	private long startTime, nanoSecondsElapsed, 
				 secondsElapsed, minutesElapsed, 
				 tenthsOfASecondElapsed, currentTime = 0;
	
	private boolean offRoad, finishedRace, startClock;
	private boolean paused;
	
	private float currentSpeed, radDeg, centerOfRotationYOffset;
	
	float[] collisionBoxPoints;

	private String tileType, timeElapsed;
	public String id, username = "";

	private Image sprite;
	private CarProperties stats;
	private Level level;
	private Polygon collisionBox;

	private Vector2f movementVector, position, startPos;
	Controlls controlls;

	private float carLength, carWidth, centerOfRotationX, centerOfRotationY;

	private ArrayList<String> stoppingDirections = new ArrayList<String>();

	private Sound finalRound;
	private Sound crowdFinish;
	private Sound checkpointSound;
	private Sound lapSound;
	private Sound still;
	private Sound topSpeedSound;
	private CollisionHandler collision;

	public Car(CarProperties stats, String id, int playerNr, float startX, float startY, Level level) {
		
		controlls = new Controlls(this, stats);
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
		startX -= carLength; //Bilen plasseres bakover etter hvor lang den er.

		if(previousCar != null){
			
			float prevStartY = previousCar.getStartPos().y;
			float closestEdgeY = prevStartY + previousCar.getCarWidth();
			startY = closestEdgeY + spaceBetweenCars;
		}
		
		startPos = new Vector2f(startX, startY);
	}

	private void initVariables() {
		time = 0;
		paused = true;
		passedChekpoints = 0;
		laps = 0;
		offRoad = false;
	
		finishedRace = false;
		startClock = false;
		collisionBoxPoints = new float[16];
		collisionBox = new Polygon(collisionBoxPoints);
		movementVector = new Vector2f();
		centerOfRotationYOffset = originalCarWidth*stats.carSize;
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

		Input input = container.getInput();
		currentSpeed = controlls.getCurrentSpeed();
		controlls.reactToControlls(input, deltaTime, paused);
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

		float[] colBoxPoints = collisionBox.getPoints();
		int safetyMargin = 7,
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
			stats.deAcceleration = 250;
			controlls.throttleKeyUp();
			controlls.leftKeyUp();
			controlls.rightKeyUp();
			setTime((int) (minutesElapsed + secondsElapsed + tenthsOfASecondElapsed));
		}
	}

	public void checkForOffRoad() throws IOException, EncodeException {

		float[] colBoxPoints = collisionBox.getPoints();
		float xPos, yPos;
		int pointsNotOffRoad = 0,
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
	
	public void deAccelerate(int slowdownConstant) {

		float deltaDeAcceleration = controlls.getDeltaDeAcceleration();
		if (currentSpeed < -stats.deAcceleration) {

			currentSpeed += deltaDeAcceleration * slowdownConstant;
		} else if (currentSpeed > -stats.deAcceleration && currentSpeed < 0) {

			currentSpeed = 0;
		} else if (currentSpeed > stats.deAcceleration) {

			currentSpeed -= deltaDeAcceleration * slowdownConstant;
		} else if (currentSpeed > 0 && currentSpeed < stats.deAcceleration) {

			currentSpeed = 0;
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
		sprite.draw(position.x, position.y, stats.carSize);
		sprite.setRotation(carRotation);
		collisionBox = new Polygon();
		collisionBox.setClosed(true);
		generateCollisionBoxPoints();
	}
	
	public ArrayList<String> getDirectionsToStop(){
		
		return stoppingDirections;
	}
	
	public void generateCollisionBoxPoints(){
		
		int colBoxPointsLength = 5;
		int colBoxPointsWidth = 3;
		
		centerOfRotationX = position.x;
		centerOfRotationY = position.y + centerOfRotationYOffset;

		float backRightX = (float)(centerOfRotationX + Math.cos(radDeg-Math.PI/2)*carWidth/2),
			  backRightY = (float)(centerOfRotationY + Math.sin(radDeg-Math.PI/2)*carWidth/2);
		float backLeftX = (float)(centerOfRotationX+ Math.cos(radDeg+Math.PI/2)*carWidth/2),
			  backLeftY = (float)(centerOfRotationY + Math.sin(radDeg+Math.PI/2)*carWidth/2);
		
		Vector2f backRight = new Vector2f(backRightX, backRightY);
		Vector2f backLeft = new Vector2f(backLeftX, backLeftY);
		
		Vector2f frontLeft = colBoxPointsLeftOfCar(backLeft, colBoxPointsLength);
		Vector2f frontRight = colBoxPointsTopOfCar(frontLeft, colBoxPointsWidth);
		
		colBoxPointsRightOfCar(frontRight, colBoxPointsLength);
		colBoxPointsBackOfCar(backRight, colBoxPointsWidth);
	}
	
	private Vector2f colBoxPointsLeftOfCar(Vector2f backLeft, int amountOfPoints){
		
		Vector2f newPoint = new Vector2f(0,0);
		
		for(int i = 0; i < amountOfPoints; i++){
			
			newPoint.x = (float) (backLeft.x +  Math.cos(radDeg)*carLength*i/(amountOfPoints-1));
			newPoint.y = (float) (backLeft.y +  Math.sin(radDeg)*carLength*i/(amountOfPoints-1));
			
			collisionBox.addPoint(newPoint.x, newPoint.y);
		}
		
		return newPoint;
		
	}
	
	private Vector2f colBoxPointsTopOfCar(Vector2f frontLeft, int amountOfPoints){
		
		Vector2f newPoint = new Vector2f(0,0);
		
		for(int i = 1; i < amountOfPoints; i++){
			
			 newPoint.x = (float) (frontLeft.x +  Math.cos(radDeg-Math.PI/2)*carWidth*i/(amountOfPoints-1));
			 newPoint.y = (float) (frontLeft.y +  Math.sin(radDeg-Math.PI/2)*carWidth*i/(amountOfPoints-1));
			
			collisionBox.addPoint(newPoint.x, newPoint.y);
		}
		
		return newPoint;
	}
	
	private void colBoxPointsRightOfCar(Vector2f frontRight, int amountOfPoints){
		
		Vector2f newPoint = new Vector2f(0,0); 
		
		for(int i = 1; i > amountOfPoints; i++){
			
			newPoint.x = (float) (frontRight.x +  Math.cos(-radDeg)*carLength*i/(amountOfPoints-1));
			newPoint.y = (float) (frontRight.y +  Math.sin(-radDeg)*carLength*i/(amountOfPoints-1));
			
			collisionBox.addPoint(newPoint.x, newPoint.y);
		}
	}
	
	private void colBoxPointsBackOfCar(Vector2f backRight, int amountOfPoints){
		
		Vector2f newPoint = new Vector2f(0,0);
		
		for(int i = 1; i < amountOfPoints-1; i++){
			
			newPoint.x = (float) (backRight.x +  Math.cos(radDeg+Math.PI/2)*carWidth*i/(amountOfPoints-1));
			newPoint.y = (float) (backRight.y +  Math.sin(radDeg+Math.PI/2)*carWidth*i/(amountOfPoints-1));
				
			collisionBox.addPoint(newPoint.x, newPoint.y);
		}
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
			tenthsOfASecondElapsed = TimeUnit.NANOSECONDS.toMillis(nanoSecondsElapsed) / 100
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
	
	public float[] getCollisionBoxPoints(){
		
		return collisionBox.getPoints();
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

	public void setTime(int time) {
		this.time = time;
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
	
	public float getCarWidth(){
		return carWidth;
	}
	
}
