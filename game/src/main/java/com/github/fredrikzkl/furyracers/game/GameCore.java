package com.github.fredrikzkl.furyracers.game;

import java.awt.Font;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
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
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.ResourceLoader;
import com.github.fredrikzkl.furyracers.Application;
import com.github.fredrikzkl.furyracers.network.GameSession;

public class GameCore extends BasicGameState {
	
	private String IP = "";
	
	private final int menuID = 0; 
	

	public static Camera camera;
	public Level level = null;

	public float initalZoom, zoom = 1;
	
	Font font;
	TrueTypeFont ttf;
	Image subMapPic, mapPic;
	
	public List<Car> cars;
	public List<Player> players;

	private int cameraMargin = 250, screenWidth, screenHeight;
	
	float biggest = 0;
	
	private boolean keyboardPlayerOne, keyboardPlayerTwo;

	private long startTimeCountdown, nanoSecondsElapsed, 
				 currentTimeCountDown, secondsElapsed;

	private boolean raceStarted, countdownStarted,
					startGoSignal, goSignal;

	private long startGoSignalTime, goSignalTimeElapsed, secondsLeft;
	
	
	private boolean raceFinished = false;
	private Image results, highscores;
	float resultPosX, resultPosY, highScorePosX, highScorePosY;
	private Color headerColor = new Color(221, 0, 0);
	private TrueTypeFont scoreBoardHeader;
	private TrueTypeFont scoreBoardText;
	private float headerSize,textSize;
	private int textTimer = 0;
	int scoreBoardLength;
	

	private TrueTypeFont countDownFont;

	private static Sound drivingSound;

	public void init(GameContainer container, StateBasedGame sbg) throws SlickException {
		System.out.println("IP: " + IP);
	}
	
	public void gameStart(int levelNr, List<Player> players) throws SlickException{

		initSounds();
		GameSession.setGameState(getID());
		Application.setInMenu(false);
		level = new Level(levelNr);
		camera = new Camera(0,0,level);
		initVariables();
		createPlayers(players);
		camera.setZoom((float)0.3);
		
		addFonts();
	}

	public void update(GameContainer container , StateBasedGame game, int deltaTime) throws SlickException {
		
		checkForKeyboardInput(container, game);
		startCountdown();
		checkCountdown();
		updateCars(container, game, deltaTime);
		checkDistances();
		camera.zoomLogic();
		camera.updateCamCoordinates();
		
	}

	public void render(GameContainer container, StateBasedGame sbg, Graphics g)
			throws SlickException {
		
		relocateCam(g);
		drawCarTimes();
		drawCountdown(g);
		ttf.drawString(screenWidth-300, 10, IP);//Ip addresene øverst i venstre corner
		
		if(raceFinished)
		drawScoreBoard();
	}
	
	
	public void createPlayers(List<Player> players) throws SlickException{
		
		this.players = players;
		
		for(Player player:players){
			createPlayer(player.getPlayerNr(),player.getId(), player.getSelect());
		}
	}
	
	public void addFonts() {

		InputStream inputStream;
		float countdownFontSize = 76f;

		try {
			inputStream = ResourceLoader.getResourceAsStream("Font/Timber.ttf");
			Font timberFont = Font.createFont(Font.TRUETYPE_FONT, inputStream);

			timberFont = timberFont.deriveFont(countdownFontSize);
			
			countDownFont = new TrueTypeFont(timberFont, true);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void startCountdown(){
		
		if(!countdownStarted){
			startTimeCountdown = System.nanoTime();
			countdownStarted = true;
		}
	}
	
	public void drawCountdown(Graphics g){
		
		Color countdownColor = new Color(221, 0, 0);
		
		if(!raceStarted){
			countDownFont.drawString(screenWidth/2, screenHeight/2-150, "" + secondsLeft, countdownColor);
		}else if(startGoSignal){
			startGoSignalTime = System.currentTimeMillis();
			startGoSignal = false;
			goSignal = true;
		}

		if(goSignal){
			long currentTime = System.currentTimeMillis();
			goSignalTimeElapsed = currentTime - startGoSignalTime;
			if(goSignalTimeElapsed < 1500){
				countDownFont.drawString(screenWidth/2-50, screenHeight/2-150, "RACE!", countdownColor);
			}else{
				goSignal = false;
			}
		}
	}
	
	public void drawCarTimes(){
		
		for(int i = 0; i < cars.size(); i++){
			
			float yOffSet = 20;
			float yNextPlayerOffSet = yOffSet*4;
			float startY = screenHeight/10 + (yNextPlayerOffSet*i);
			float startX = screenWidth/40;
			
			ttf.drawString(startX, startY, "Player" + cars.get(i).getPlayerNr()+":");
			ttf.drawString(startX, startY + yOffSet,"Lap "  + cars.get(i).getLaps() + "/3");
			ttf.drawString(startX, startY + yOffSet*2, ""+cars.get(i).getTimeElapsed());
		}
	}
	
	public void updateCars(GameContainer container, StateBasedGame game, int deltaTime) throws SlickException{
		
		int carsFinished = 0;
		
		for(Car car: cars){
			car.update(container, game, deltaTime);
			if(car.finishedRace())
				carsFinished++;
		}
		
		if(carsFinished == cars.size()){
			raceFinished = true;
			//returnToMenu(container , game);
		}
	}
	
	private void drawScoreBoard() {
		float speed = 1;
		float maxX = Application.screenSize.width;
		float midWay = maxX/2;
		float marginX = midWay/5;
		float scalingValue = (marginX*3)/results.getWidth();
		
		results.draw(resultPosX-results.getWidth(),resultPosY,scalingValue);
		highscores.draw(highScorePosX+highscores.getWidth(),highScorePosY,scalingValue);
		
		if(resultPosX<marginX*3.5){
			resultPosX += speed;
		}else{
			printScores(marginX);
		}
		if(highScorePosX>(midWay-(marginX*1.3))){
			highScorePosX -= speed;
		}else{
			printHighScores(marginX);
		}
		
		
		
	}
	
	private void printHighScores(float marginX) {
		float headerPosX = (marginX*6.4f);
		float headerPosY = resultPosY*1.4f;
		
		scoreBoardHeader.drawString(headerPosX, headerPosY, "High Scores:",headerColor);
		
	}

	private void printScores(float marginX) {
		float headerPosX = (marginX*1.4f);
		float headerPosY = resultPosY*1.4f;
		
		scoreBoardHeader.drawString(headerPosX, headerPosY, "Results:",headerColor);
		scoreBoardLength = (int) headerSize;
		ArrayList<Car> sortedCars = (ArrayList<Car>) cars;
		Collections.sort(sortedCars);

		
		for(int i = sortedCars.size()-1; i >= 0 ;i--){
			scoreBoardText.drawString(headerPosX, headerPosY+scoreBoardLength, 
					"Player " + sortedCars.get(i).getPlayerNr() + ": " +
					sortedCars.get(i).getTimeElapsed() + " Score: " + "+" +(i+1));
			scoreBoardLength+=textSize;
		}
		
		scoreBoardHeader.drawString(headerPosX, headerPosY+headerSize+scoreBoardLength, "Total Score:",headerColor);
		scoreBoardLength += headerSize*2;
		
		ArrayList<Player> sortedPlayers = (ArrayList<Player>) players;
		Collections.sort(sortedPlayers);
		
		for(int i = 0; i<sortedPlayers.size();i++){
			scoreBoardText.drawString(headerPosX, headerPosY+scoreBoardLength, 
					sortedPlayers.get(i).getId() + ": " + sortedPlayers.get(i).getScore());
			scoreBoardLength+=textSize;

		}
		
		
		textTimer++;
	}

	private void initSounds() {
		try {
			String path = "Sound/";
			drivingSound = new Sound(path + "driving.wav");
			
		} catch (SlickException e) {
			System.out.println("Could not load sound file" + e);
			e.printStackTrace();
		}
	}
	
	public static Sound getDrivingSound(){
		return drivingSound;
	}
	
	public void returnToMenu(GameContainer container, StateBasedGame game) throws SlickException{
		Application.closeConnection();
		Application.createGameSession();
		game.getState(menuID).init(container, game);
		game.enterState(menuID);
	}
	
	public void checkCountdown(){
		if(!raceStarted){
			currentTimeCountDown = System.nanoTime();
			nanoSecondsElapsed = currentTimeCountDown - startTimeCountdown;
			secondsElapsed = TimeUnit.NANOSECONDS.toSeconds(nanoSecondsElapsed);
			secondsLeft = 3 - secondsElapsed;
			
			if(secondsLeft == 0){
				startRace();
			}
		}
	}
	
	public void startRace(){
		
		for(Car car : cars)
			car.startClock();
		
		raceStarted = true;
	}
	
	private void checkDistances() {
		
		Vector2f longestDistance = new Vector2f();
		Vector2f shortestDistance = new Vector2f(level.getMapWidthPixels(), level.getMapHeightPixels());
		Vector2f closestEdge = new Vector2f(level.getMapWidthPixels(), level.getMapHeightPixels());
		
		for(Car car : cars){
			if(car.position.x >longestDistance.x){
				longestDistance.x = car.position.x;
			}
			if(car.position.x < shortestDistance.x){
				shortestDistance.x = car.position.x;
			}
			if(car.position.y > longestDistance.y){
				longestDistance.y = car.position.y;
 			}
			if(car.position.y < shortestDistance.y){
				shortestDistance.y = car.position.y;
			}
			
			if(car.position.x < closestEdge.x){
				closestEdge.x = car.position.x;
			}
			if(car.position.y < closestEdge.y){
				closestEdge.y = car.position.y;
			}
				
		}
		
		Vector2f deltaDistance = new Vector2f();
		
		deltaDistance.x = longestDistance.x - shortestDistance.x;
		deltaDistance.y = longestDistance.y - shortestDistance.y;
		
		camera.setDeltaDistances(deltaDistance);
		camera.setClosestEdge(closestEdge);
	}
	
	public void checkForKeyboardInput(GameContainer container, StateBasedGame game) throws SlickException{
		Input input = container.getInput();
		/*if(input.isKeyDown(Input.KEY_A) && !keyboardPlayerOne){
			int amountOfPlayers = cars.size();
			createPlayer(amountOfPlayers+1,"keyboardPlayer",1);
			cars.get(amountOfPlayers).activateKeyboardInput();
			keyboardPlayerOne = true;
		}
		
		if(input.isKeyDown(Input.KEY_B) && !keyboardPlayerTwo){
			int amountOfPlayers = cars.size();
			createPlayer(amountOfPlayers+1,"keyboardPlayerTwo",1);
			cars.get(amountOfPlayers).activateKeyboardInput();
			keyboardPlayerTwo = true;
		}*/
		
	    if(input.isKeyPressed(Input.KEY_R)){
	    	returnToMenu(container, game);
	    }
	    
	}
	
	public void createPlayer(int nr, String id, int playerChoice) throws SlickException{
		
		CarProperties temp = CarProperties.values()[playerChoice];
		
		cars.add(new Car(temp,id,nr,
				level.getStartCoordinates().x-(level.tileWidth*4),
				level.getStartCoordinates().y-(level.tileHeight*4),
				level));
	}
	
	public void relocateCam(Graphics g){
		camera.zoom(g, camera.getZoom());//Crasher om verdien <=0 	
		g.translate(camera.getX(), camera.getY()); //Start of camera
		
		g.drawImage(subMapPic, 0, 0);
		for(Car car: cars){
			car.render(g);
		}
		g.drawImage(mapPic,0,0);
		
		g.translate(-camera.getX(), -camera.getY()); //End of camera
		camera.zoom(g, 1/camera.getZoom());
	}
	public void initVariables(){
		
		cars = new ArrayList<Car>();
		
		raceStarted = false;
		countdownStarted = false;
		
		startGoSignal = true;
		
		keyboardPlayerOne = false;
		keyboardPlayerOne = false;
		
		try {
			subMapPic = new Image("Maps/course1sub.png");
			mapPic = new Image("Maps/course1.png");
		} catch (SlickException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		font = new Font("Verdana", Font.BOLD, 20);
		ttf = new TrueTypeFont(font, true);
		
		screenWidth = Application.screenSize.width;
		screenHeight = Application.screenSize.height;
		
		headerSize = 30f;
		textSize = 24f;
		InputStream inputStream;
		try {
			inputStream = ResourceLoader.getResourceAsStream("Font/Orbitron-Regular.ttf");
			Font awtFont1 = Font.createFont(Font.TRUETYPE_FONT, inputStream);
			Font awtFont2;

			awtFont1 = awtFont1.deriveFont(headerSize); // set font size
			awtFont2 = awtFont1.deriveFont(textSize);
			
			scoreBoardHeader = new TrueTypeFont(awtFont1, true);
			scoreBoardText = new TrueTypeFont(awtFont2, true);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String path = "/Sprites/UI/";
		try {
			 results = new Image(path + "border.png");
			 highscores = new Image(path + "border.png");
		} catch (SlickException e) {
			e.printStackTrace();
		}
		
		resultPosX = 0-(results.getWidth() * 2);
		highScorePosX = (float) (Application.screenSize.width + (results.getWidth() / 1.3));
		resultPosY =  Application.screenSize.height/10;
		highScorePosY = Application.screenSize.height/10;
		
	}
	
	public void setIP(String ip) {
		IP = ip + "/furyracers";
	}

	public int getID() {
		return 1;
	}

	public float getZoom() {
		return zoom;
	}

}
