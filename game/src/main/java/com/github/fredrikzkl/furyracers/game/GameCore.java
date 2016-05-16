package com.github.fredrikzkl.furyracers.game;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.websocket.EncodeException;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.RoundedRectangle;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import com.github.fredrikzkl.furyracers.Application;
import com.github.fredrikzkl.furyracers.assets.Fonts;
import com.github.fredrikzkl.furyracers.assets.Sounds;
import com.github.fredrikzkl.furyracers.car.Car;
import com.github.fredrikzkl.furyracers.car.CarProperties;
import com.github.fredrikzkl.furyracers.network.GameSession;

public class GameCore extends BasicGameState {

	public final static int 
	maxLaps = 3, menuID = 0;
	
	private int 
	screenWidth, screenHeight;

	public float 
	initalZoom, zoom,
	biggest, infoFontSize; 

	private long 
	startTimeCountdown, nanoSecondsElapsed, 
	currentTimeCountDown, secondsElapsed,
	startGoSignalTime, goSignalTimeElapsed, secondsLeft;

	private boolean 
	raceStarted, countdownStarted, startGoSignal, goSignal, 
	raceFinished, threePlayed, twoPlayed, onePlayed, goPlayed;
	
	public static boolean 
	finalRoundSaid, crowdFinishedPlayed;

	private String IP;
	
	public static List<Car> cars;
	public List<Player> players;
	private Camera camera;
	private Level level;
	private ScoreBoard scoreboard;
 

	public void init(GameContainer container, StateBasedGame sbg) throws SlickException {
		System.out.println("IP: " + IP);
	}

	public void gameStart(CourseHandler course, List<Player> players) throws SlickException {

		GameSession.setGameState(getID());
		Application.setInMenu(false);
		level = new Level(course);
		camera = new Camera(0, 0, level);
		initVariables();
		createCars(players);
		camera.setZoom((float) 0.3);
		scoreboard = new ScoreBoard(cars, players);
	}

	public void update(GameContainer container, StateBasedGame game, int deltaTime) throws SlickException {

		checkForKeyboardInput(container, game);
		startCountdown();
		checkCountdown();
		updateCars(container, game, deltaTime);
		checkDistances();
		camera.zoomLogic();
		camera.updateCamCoordinates();
	}

	public void render(GameContainer container, StateBasedGame sbg, Graphics g) throws SlickException {

		relocateCam(g);
		drawPlayerInfo(g);
		countdown(g);
		drawIp();
		if (raceFinished)
			scoreboard.drawScoreBoard();
		if (scoreboard.isReturnToMenuTimerDone()) {
			returnToMenu(container, sbg);
		}
	}

	public void createCars(List<Player> players) throws SlickException {

		this.players = players;

		for (Player player : players) {
			createCar(player.getPlayerNr(), player.getId(), player.getSelect());
		}
	}
	
	private void drawIp(){
		
		int stringWidth = Fonts.infoFont.getWidth(IP);
		Fonts.infoFont.drawString(screenWidth - stringWidth, 10, IP);
	}

	private void startCountdown() {

		if (!countdownStarted) {
			startTimeCountdown = System.nanoTime();
			countdownStarted = true;
		}
	}

	private void countdown(Graphics g) {

		if (!raceStarted) {
			countdownAnnouncer();
			String secondsLeftString = "" + secondsLeft;
			drawCountdown(secondsLeftString);
			
		} else if (startGoSignal) {
			startGoSignalTime = System.currentTimeMillis();
			startGoSignal = false;
			goSignal = true;
		}

		if (goSignal) {
			long currentTime = System.currentTimeMillis();
			goSignalTimeElapsed = currentTime - startGoSignalTime;
			if (goSignalTimeElapsed < 1500) {
				drawCountdown("RACE!");
				if (!goPlayed) {
					Sounds.go.play();
					goPlayed = true;
				}
			} else {
				goSignal = false;
			}
		}
	}
	
	private void drawCountdown(String string){
		
		Color countdownColor = new Color(221, 0, 0);
		int stringWidth = Fonts.header.getWidth(string);
		float margin = screenHeight/10;
		float startX = screenWidth / 2 - stringWidth/2 + margin;
		float startY = screenHeight / 2 - margin;
		
		Fonts.header.drawString(startX, startY, string, countdownColor);
	}
	private void countdownAnnouncer(){
		
		if (secondsLeft > 2) {
			if (!threePlayed) {
				Sounds.three.play();
				threePlayed = true;
			}
		} else if (secondsLeft > 1) {
			if (!twoPlayed) {
				Sounds.two.play();
				twoPlayed = true;
			}
		} else if (secondsLeft > 0) {
			if (!onePlayed) {
				Sounds.one.play();
				onePlayed = true;
			}
		}
	}
	

	private void drawPlayerInfo(Graphics g) {
		
		float 
		margin = screenWidth/160,
		fontHeight = Fonts.infoFont.getHeight(),
		startX =  screenWidth / 40,
		infoBoxHeight = fontHeight*2 + margin*2,
		cornerRadius = 4f;

		for (int i = 0; i < cars.size(); i++) {
			
			Color 
			carColor = players.get(i).getCarColor();
			
			String 
			username = players.get(i).getUsername(),
			laps = "Lap " + cars.get(i).getLaps() + "/" + maxLaps;
			
			int 
			usernameLength = Fonts.infoFont.getWidth(username),
			lapsLength = Fonts.infoFont.getWidth(laps);
			
			float 
		    infoBoxWidth = infoBoxWidth(usernameLength, lapsLength) + margin*2,
			startY = screenHeight / 10 + (infoBoxHeight + margin*2) * i,
			
			usernameStartY = startY + margin,
			lapsStartY = usernameStartY + margin/2 + fontHeight;
			
			RoundedRectangle infoBox = new RoundedRectangle(startX, startY, infoBoxWidth, infoBoxHeight, cornerRadius);
			g.setColor(carColor);
			g.fill(infoBox);
			Fonts.infoFont.drawString(startX+margin, usernameStartY, username);
			
			Fonts.infoFont.drawString(startX+margin, lapsStartY, laps);
		}

	}
	private int infoBoxWidth(int usernameLength, int lapsLength ){
		if(usernameLength < lapsLength){
			return lapsLength;
		}
		
		return usernameLength;
	}

	public void updateCars(GameContainer container, StateBasedGame game, int deltaTime) throws SlickException {

		int carsFinished = 0;

		for (Car car : cars) {
			try {
				car.update(container, game, deltaTime);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (EncodeException e) {
				e.printStackTrace();
			}
			if (car.finishedRace())
				carsFinished++;
		}
		
		if(carsFinished == cars.size()){
			raceFinished = true;
		}
	}

	public void returnToMenu(GameContainer container, StateBasedGame game) throws SlickException {
		
		for(Car car : cars){
			car.controlls.resetTopSpeed();
			car.controlls.resetDeAcceleration();
			car.rumbleController(false);
		}
		
		Application.closeConnection();
		Application.createGameSession();
		
		game.getState(menuID).init(container, game);
		game.enterState(menuID);
	}

	public void checkCountdown() {
		if (!raceStarted) {
			currentTimeCountDown = System.nanoTime();
			nanoSecondsElapsed = currentTimeCountDown - startTimeCountdown;
			secondsElapsed = TimeUnit.NANOSECONDS.toSeconds(nanoSecondsElapsed);
			secondsLeft = 3 - secondsElapsed;

			if (secondsLeft <= 0) {
				startRace();
			}
		}
	}

	public void startRace() {
		for (Car car : cars)
			car.startClock();
		raceStarted = true;
	}

	private void checkDistances() {

		Vector2f longestDistance = new Vector2f();
		Vector2f shortestDistance = new Vector2f(level.getMapWidthPixels(), level.getMapHeightPixels());
		Vector2f closestEdge = new Vector2f(level.getMapWidthPixels(), level.getMapHeightPixels());

		for (Car car : cars) {
			if (car.getPosition().x > longestDistance.x) {
				longestDistance.x = car.getPosition().x;
			}
			if (car.getPosition().x < shortestDistance.x) {
				shortestDistance.x = car.getPosition().x;
			}
			if (car.getPosition().y > longestDistance.y) {
				longestDistance.y = car.getPosition().y;
			}
			if (car.getPosition().y < shortestDistance.y) {
				shortestDistance.y = car.getPosition().y;
			}
			if (car.getPosition().x < closestEdge.x) {
				closestEdge.x = car.getPosition().x;
			}
			if (car.getPosition().y < closestEdge.y) {
				closestEdge.y = car.getPosition().y;
			}
		}

		Vector2f deltaDistance = new Vector2f();

		deltaDistance.x = longestDistance.x - shortestDistance.x;
		deltaDistance.y = longestDistance.y - shortestDistance.y;

		camera.setDeltaDistances(deltaDistance);
		camera.setClosestEdge(closestEdge);
	}

	public void checkForKeyboardInput(GameContainer container, StateBasedGame game) throws SlickException {
		Input input = container.getInput();

		if (input.isKeyPressed(Input.KEY_R)) {
			returnToMenu(container, game);
		}
		
		if (input.isKeyPressed(Input.KEY_H)) {
			scoreboard.drawScoreBoard();
		}
	}

	public void createCar(int nr, String id, int playerChoice) throws SlickException {

		CarProperties stats = CarProperties.values()[playerChoice];
		
		Vector2f startArea = new Vector2f();

		startArea.x = level.getStartCoordinates().x - level.getTileWidth() * 4;
		startArea.y = level.getStartCoordinates().y - level.getTileHeight() * 4;
		
		cars.add(new Car(stats, id, nr, startArea, level));
	}

	public void relocateCam(Graphics g) {
		camera.zoom(g, camera.getZoom());// Crasher om verdien <=0
		g.translate(camera.getX(), camera.getY()); // Start of camera

		level.drawCars(g, cars);

		g.translate(-camera.getX(), -camera.getY()); // End of camera
		camera.zoom(g, 1 / camera.getZoom());
	}

	public void initVariables() {
		threePlayed = twoPlayed = onePlayed = 
		goPlayed = finalRoundSaid = 
		crowdFinishedPlayed = raceFinished = 
		raceStarted = countdownStarted = false;
		
		startGoSignal = true;
		
		screenWidth = Application.screenSize.width;
		screenHeight = Application.screenSize.height;
		
		zoom = 1;
		biggest = 0;
			
		cars = new ArrayList<Car>();
	}

	public void setIP(String ip) {
		IP = ip + "/furyracers";
	}

	public float getZoom() {
		return zoom;
	}

	public boolean isFinalRoundSaid() {
		return finalRoundSaid;
	}
	
	public static Car getCar(int playerNr){
		
		if(playerNr < 1){
			return null;
		}
		
		return cars.get(playerNr-1);
	}
	
	public int getID() {
		return 1;
	}

}