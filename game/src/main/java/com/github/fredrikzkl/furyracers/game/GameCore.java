package com.github.fredrikzkl.furyracers.game;

import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import com.github.fredrikzkl.furyracers.Application;
import com.github.fredrikzkl.furyracers.network.GameSession;

public class GameCore extends BasicGameState {
	
	private String IP = "";
	
	Image p1car = null;
	SpriteSheet sprite;
	Circle center;
	
	Image redMustang;
	Image blueMustang;
	Image greenMustang;
	Image yellowMustang;

	public static Camera camera;
	public Level level = null;

	public float initalZoom = (float) 1; //TODO
	public float zoom = (float) 1;
	
	Font font;
	TrueTypeFont ttf;
	
	public List<Car> cars;
	public List<Player> players;
	
	public float randomHighStartValue;

	public int cameraMargin = 250;
	
	float biggest = 0;
	
	private boolean keyboardPlayerOne, keyboardPlayerTwo;

	private long startTimeCountdown, nanoSecondsElapsed, currentTimeCountDown, secondsElapsed;

	private boolean raceStarted, countdownStarted;

	private long secondsLeft;

	public void init(GameContainer container, StateBasedGame sbg) throws SlickException {
		
		Application.setInMenu(false);
		System.out.println("IP: " + IP);
	}
	
	public void gameStart(int levelNr, List<Player> players) throws SlickException{
		Application.setInMenu(false);
		
		level = new Level(levelNr);
		camera = new Camera(0,0,level);
		
		initVariables();
		
		this.players = players;
		for(Player player:players){
			createPlayer(player.getPlayerNr(),player.getId(), player.getSelect());
		}
		
		camera.setZoom((float)0.3);
		GameSession.setGameState(getID());
	}

	public void update(GameContainer container , StateBasedGame game, int deltaTime) throws SlickException {
		
		checkForKeyboardInput(container, game);
		startCountdown();
		checkCountdown();		
		for(Car cars: cars){
			cars.update(container, game, deltaTime);
		}
				
		checkDistances();
		camera.zoomLogic();
		camera.updateCamCoordinates();
		
	}

	public void render(GameContainer container, StateBasedGame sbg, Graphics g)
			throws SlickException {
		
		relocateCam(g);
		
		drawCarTimes();
		
		
		ttf.drawString(Application.screenSize.width-300, 0, IP);//Ip addresene øverst i venstre corner
		
		if(!raceStarted)
			ttf.drawString(150, 150, "" + secondsLeft);
	}
	
	public void startCountdown(){
		
		if(!countdownStarted){
			startTimeCountdown = System.nanoTime();
			countdownStarted = true;
		}
	}
	
	public void drawCarTimes(){
		for(Car car: cars)
			ttf.drawString(car.getPosition().x+camera.getX()-25, car.getPosition().y+camera.getY()-30, car.getTimeElapsed());
		
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
		if(input.isKeyDown(Input.KEY_A) && !keyboardPlayerOne){
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
		}
		
	    if(input.isKeyPressed(Input.KEY_N)){
	    	game.getState(1).init(container, game);
	    	game.enterState(1);
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
		
		level.render(g,camera);
		for(Car car: cars){
			car.render(g);
		}
		
		g.translate(-camera.getX(), -camera.getY()); //End of camera
		camera.zoom(g, 1/camera.getZoom());
	}
	public void initVariables(){
		
		cars = new ArrayList<Car>();
		
		raceStarted = false;
		countdownStarted = false;
		
		keyboardPlayerOne = false;
		keyboardPlayerOne = false;
		
		font = new Font("Verdana", Font.BOLD, 20);
		ttf = new TrueTypeFont(font, true);
		
		center = new Circle(0,0,1);
		randomHighStartValue = 999;
	}
	
	public void setIP(String ip) {
		IP = ip + "/fury";
	}

	public int getID() {
		return 1;
	}

	public float getZoom() {
		return zoom;
	}

}
