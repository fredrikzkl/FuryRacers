package com.github.fredrikzkl.furyracers.game;

import java.awt.Font;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.websocket.EncodeException;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.RoundedRectangle;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.GameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.Transition;
import org.newdawn.slick.util.ResourceLoader;

import com.github.fredrikzkl.furyracers.Application;
import com.github.fredrikzkl.furyracers.network.GameSession;

public class GameCore extends BasicGameState {

	private String IP = "";

	private final int menuID = 0;

	public static Camera camera;
	public Level level = null;
	private ScoreBoard scoreboard;

	public float initalZoom, zoom = 1;

	Font font;
	TrueTypeFont infoFont;
	Image subMapPic, mapPic, side;

	public static List<Car> cars;
	public List<Player> players;

	private int screenWidth, screenHeight;

	float biggest = 0;

	private boolean keyboardPlayerOne, keyboardPlayerTwo;

	private long startTimeCountdown, nanoSecondsElapsed, currentTimeCountDown, secondsElapsed;

	private boolean raceStarted, countdownStarted, startGoSignal, goSignal;

	private long startGoSignalTime, goSignalTimeElapsed, secondsLeft;
	private boolean raceFinished;
	private TrueTypeFont countDownFont;
	public Sound three, two, one, go;
	private boolean threePlayed, twoPlayed, onePlayed, goPlayed;

	private float infoFontSize;
	public static boolean finalRoundSaid, crowdFinishedPlayed;

	public void init(GameContainer container, StateBasedGame sbg) throws SlickException {
		System.out.println("IP: " + IP);
	}

	public void gameStart(CourseHandler course, List<Player> players) throws SlickException {

		initSounds();
		GameSession.setGameState(getID());
		Application.setInMenu(false);
		level = new Level(course);
		camera = new Camera(0, 0, level);
		initVariables();
		createPlayers(players);
		camera.setZoom((float) 0.3);

		scoreboard = new ScoreBoard(cars, players);
		addFonts();
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
		drawCarTimes(g);
		countdown(g);
		int stringWidth = infoFont.getWidth(IP);
		infoFont.drawString(screenWidth - stringWidth, 10, IP);// Ip addresene øverst i

		if (raceFinished)
			scoreboard.drawScoreBoard();
		if (scoreboard.isReturnToMenuTimerDone()) {
			returnToMenu(container, sbg);
		}

	}

	public void createPlayers(List<Player> players) throws SlickException {

		this.players = players;

		for (Player player : players) {
			createPlayer(player.getPlayerNr(), player.getId(), player.getSelect());
		}
	}

	public void addFonts() {

		InputStream inputStream;
		float countdownFontSize = 70f;
		infoFontSize = 20f;

		try {
			inputStream = ResourceLoader.getResourceAsStream("Font/Orbitron-Regular.ttf");
			Font timberFont = Font.createFont(Font.TRUETYPE_FONT, inputStream);

			timberFont = timberFont.deriveFont(countdownFontSize);
			countDownFont = new TrueTypeFont(timberFont, true);
			
			timberFont = timberFont.deriveFont(infoFontSize);
			infoFont = new TrueTypeFont(timberFont, true);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void startCountdown() {

		if (!countdownStarted) {
			startTimeCountdown = System.nanoTime();
			countdownStarted = true;
		}
	}

	public void countdown(Graphics g) {

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
					go.play();
					goPlayed = true;
				}
			} else {
				goSignal = false;
			}
		}
	}
	
	private void drawCountdown(String string){
		
		Color countdownColor = new Color(221, 0, 0);
		int stringWidth = countDownFont.getWidth(string);
		float margin = screenHeight/10;
		float startX = screenWidth / 2 - stringWidth/2 + margin;
		float startY = screenHeight / 2 - margin;
		
		countDownFont.drawString(startX, startY, string, countdownColor);
	}
	private void countdownAnnouncer(){
		
		if (secondsLeft > 2) {
			if (!threePlayed) {
				three.play();
				threePlayed = true;
			}
		} else if (secondsLeft > 1) {
			if (!twoPlayed) {
				two.play();
				twoPlayed = true;
			}
		} else if (secondsLeft > 0) {
			if (!onePlayed) {
				one.play();
				onePlayed = true;
			}
		}
	}
	

	public void drawCarTimes(Graphics g) {

		for (int i = 0; i < cars.size(); i++) {

			float margin = screenWidth/160,
				  nextLineYOffSet = infoFontSize,
				  yNextPlayerOffSet = nextLineYOffSet * 4,
				  startY = screenHeight / 10 + (yNextPlayerOffSet * i),
				  startX =  screenWidth / 40,
				  cornerRadius = 4f;
			
			Color carColor = players.get(i).getCarColor();
			String username = players.get(i).getUsername(),
				   laps = "Lap " + cars.get(i).getLaps() + "/3";
			
			int usernameLength = infoFont.getWidth(username),
			    lapsLength = infoFont.getWidth(laps),
			    fontHeight = infoFont.getHeight();
			
			float infoBoxWidth = infoBoxWidth(usernameLength, lapsLength) + margin*2,
				  infoBoxHeight = fontHeight * 2 + margin*2;
			
			RoundedRectangle box = new RoundedRectangle(startX-margin, startY-margin, infoBoxWidth, infoBoxHeight, cornerRadius);
			g.setColor(carColor);
			g.fill(box);
			infoFont.drawString(startX, startY, username);
			infoFont.drawString(startX, startY + nextLineYOffSet, laps);
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
	

	private void initSounds() {
		try {
			three = new Sound("/Sound/announcer/three.ogg");
			two = new Sound("/Sound/announcer/two.ogg");
			one = new Sound("/Sound/announcer/one.ogg");
			go = new Sound("/Sound/announcer/race!.ogg");
		} catch (SlickException e) { 
			System.out.println("ERROR: Could not load announcer files!" + e);
		}

	}

	public void returnToMenu(GameContainer container, StateBasedGame game) throws SlickException {
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

	public void createPlayer(int nr, String id, int playerChoice) throws SlickException {

		CarProperties temp = CarProperties.values()[playerChoice];

		cars.add(new Car(temp, id, nr, level.getStartCoordinates().x - (level.tileWidth * 4),
				level.getStartCoordinates().y - (level.tileHeight * 4), level));
	}

	public void relocateCam(Graphics g) {
		camera.zoom(g, camera.getZoom());// Crasher om verdien <=0
		g.translate(camera.getX(), camera.getY()); // Start of camera


		level.drawCars(g, cars);

		g.translate(-camera.getX(), -camera.getY()); // End of camera
		camera.zoom(g, 1 / camera.getZoom());
	}

	public void initVariables() {
		threePlayed = twoPlayed = onePlayed = goPlayed = finalRoundSaid = crowdFinishedPlayed = false;
		raceFinished = false;
		cars = new ArrayList<Car>();

		raceStarted = false;
		countdownStarted = false;

		startGoSignal = true;

		keyboardPlayerOne = false;
		keyboardPlayerOne = false;

		font = new Font("Verdana", Font.BOLD, 20);
		infoFont = new TrueTypeFont(font, true);

		screenWidth = Application.screenSize.width;
		screenHeight = Application.screenSize.height;

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

	public void setFinalRoundSaid(boolean finalRoundSaid) {
		this.finalRoundSaid = finalRoundSaid;
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
