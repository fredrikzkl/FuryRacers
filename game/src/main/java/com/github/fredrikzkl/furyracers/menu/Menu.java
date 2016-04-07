package com.github.fredrikzkl.furyracers.menu;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.websocket.EncodeException;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import com.github.fredrikzkl.furyracers.Application;
import com.github.fredrikzkl.furyracers.assets.Fonts;
import com.github.fredrikzkl.furyracers.assets.Sounds;
import com.github.fredrikzkl.furyracers.assets.Sprites;
import com.github.fredrikzkl.furyracers.game.CourseHandler;
import com.github.fredrikzkl.furyracers.game.GameCore;
import com.github.fredrikzkl.furyracers.game.Player;
import com.github.fredrikzkl.furyracers.network.GameSession;

public class Menu extends BasicGameState {

	private final int ICONSIZE = 128;
	
	private int 
	screenWidth, screenHeight,
	counter, secondsToNextGame,
	tick, mapSelected;
	
	private float 
	consoleSize, xPosCountdown, yPosCountdown;

	private double 
	seconds, duration, last, 
	allReadyTimestamp;

	private boolean 
	getReadySaid;
	
	private String 
	controllerIP, version, countDown;

	private QRgenerator QR;
	private GameCore core;
	private CourseHandler course;
	public List<String> console;
	public List<Player> players;
	private ParallaxBackground background;

	public Menu(GameCore game) {
		core = game;
	}

	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		initVariables();
		QR.genQR(controllerIP);
		Application.setInMenu(true);
		GameSession.setGameState(getID());
	}

	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		setKeyboardCommands(container, game);
		setTime();
		readyCheck(game);
	}
	
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		
		background.draw();
		background.tick();
		drawHeader();
		drawMenuInfo(g);
		drawBacksideInfo();
		drawPlayerIcons(container, g);
		drawQRcode(g);
		drawNextLevelInfo();
	}
	
	public void initVariables() throws SlickException{
		
		QR = new QRgenerator();
		
		screenWidth = Application.screenSize.width;
		screenHeight = Application.screenSize.height;
		
		players = new ArrayList<Player>();
		console = new ArrayList<String>();
		console.add("Welcome to FuryRacers! Version: " + version);
		
		mapSelected = randomMap();
		course = new CourseHandler(mapSelected);
		
		xPosCountdown = screenWidth - screenHeight/10;
		yPosCountdown = screenHeight - screenHeight/15;
		consoleSize = 15f;
		allReadyTimestamp = 0;
		secondsToNextGame = 5;
		counter = secondsToNextGame;
		countDown = Integer.toString(counter);
		duration = last = System.nanoTime();
		
		background = new ParallaxBackground();
		Sounds.music = new Music("Sound/menu.ogg");
		Sounds.music.loop();
		Sounds.music.setVolume((float) 0.4);
		getReadySaid = false;
	}
	
	private void setKeyboardCommands(GameContainer container, StateBasedGame game) throws SlickException{
		
		Input input = container.getInput();
		if (input.isKeyPressed(Input.KEY_R)) {
			Application.closeConnection();
			Application.createGameSession();
			init(container, game);
		}
	}
	
	private void setTime(){
		
		tick++;
		duration = System.nanoTime() - last;
		seconds += duration / 1_000_000_000.0f;
		last = System.nanoTime();
	}

	private void drawQRcode(Graphics g){

		int margin = screenWidth/38;
		g.drawImage(Sprites.controllerQR, screenWidth - Sprites.controllerQR.getWidth()- margin, margin);
	}
	
	private void drawHeader(){
		
		String headerString = "Fury Racers";
		int stringLength = Fonts.header.getWidth(headerString);
		
		float xPos = screenWidth/2 - stringLength/2;
		Fonts.header.drawString(xPos, screenHeight/15, "Fury Racers", Fonts.headerColor);
	}

	private void drawPlayerIcons(GameContainer container, Graphics g) {

		float yPos = screenHeight / 4;
		int margin = 32; 
		int maxPlayers = 4;
		float playerIconsLength = maxPlayers * (ICONSIZE + margin);
		float startPlayerIconsX = screenWidth/2 - playerIconsLength/2;

		for (int i = 0; i < 4; i++) {
			float xPos = startPlayerIconsX + i * (ICONSIZE + margin);
			drawCarSprite(xPos, yPos, i, g);
		}
		
		drawBoarder(startPlayerIconsX, yPos, g);
	}
	
	private void drawCarSprite(float xPos, float yPos, int i, Graphics g){
		
		if (i < players.size()) {
			int carSelectNum = players.get(i).getxSel();
			int colorSelectNum = players.get(i).getySel();
			
			g.drawImage( getCarImage(carSelectNum, colorSelectNum), xPos, yPos);
		}else{
			g.drawImage(getEmptyBlackImage(), xPos, yPos);
		}
	}
	
	private void drawBoarder(float startPlayerIcons, float yPos, Graphics g){
		
		int margin = 32; 
		
		for (int i = 0; i < 4; i++) {
			float xPos = startPlayerIcons + i * (ICONSIZE + margin);
			
			if (i < players.size() && players.get(i).isReady()) {
				g.drawImage(Sprites.icons.getSubImage(128, 0, 128, 128), xPos, yPos);
				continue;
			}
			g.drawImage(Sprites.icons.getSubImage(0, 0, 128, 128), xPos, yPos);
		}
	}
	
	private Image getCarImage(int carSelectNum, int colorSelectNum){
		
		Image carImage = Sprites.cars.getSubImage( carSelectNum * ICONSIZE, colorSelectNum * ICONSIZE, ICONSIZE, ICONSIZE); 
		
		return carImage;
	}
	
	private Image getEmptyBlackImage(){
		
		return Sprites.icons.getSubImage(256, 0, 128, 128);
	}

	private boolean allPlayersAreReady() {

		if (players.size() > 0) {
			for (Player player : players)
				if (!player.isReady())
					return false;
		} else {
			return false;
		}

		return true;
	}

	private void readyCheck(StateBasedGame game) throws SlickException {

		if (allPlayersAreReady()) {

			if (allReadyTimestamp < 0) {
				allReadyTimestamp = seconds;
				printConsole("Everyone is ready, the game will begin shortly!");
				if(!getReadySaid){
					Sounds.getReady.play();
					getReadySaid = true;
				}
			}

			secondsToNextGame = (int) (allReadyTimestamp + counter - seconds);
			countDown = String.valueOf(secondsToNextGame);

			if (secondsToNextGame <= 0) {
				startGame(game);
			}

		} else {
			getReadySaid = false;
			allReadyTimestamp = -1;
			countDown = String.valueOf(counter);
		}
	}

	private void startGame(StateBasedGame game) throws SlickException {
		Sounds.music.stop();
		core.gameStart(course, players);
		game.enterState(1);
	}

	public void updatePlayerList(ArrayList<Player> list) {
		players = list;
		Sounds.playerJoin.play();
	}

	public void printConsole(String text) {
		if (console.size() > 4)
			console.remove(0);
		console.add(text);
	}

	public void buttonDown(String data, int playerNr) throws IOException, EncodeException {
		switch (data) {
			
			case "1":
				selectButtonDown(playerNr);
				break;
			case "2":
				rightButtonDown(playerNr);
				break;
			case "3":
				leftButtonDown(playerNr);
				break;
		}
	}
	
	private void selectButtonDown(int playerNr){
		
		for (int i = 0; i < players.size(); i++) {
			if (playerNr == i + 1) {
				Player player = players.get(i);
				if (player.isCarChosen()) {
					if (player.isReady()) {
						player.setReady(false);
						Sounds.deSelect.play();
					} else {
						determinePlayerChoice(player);
						player.setReady(true);
						Sounds.playerReady.play();
					}
				} else {
					player.setCarChosen(true);
					Sounds.select_car.play();
				}
			}
		}
	}
	
	private void rightButtonDown(int playerNr) throws IOException, EncodeException{
		
		for (int i = 0; i < players.size(); i++) {
			if (playerNr == i + 1) {
				Player player = players.get(i);
				if (!player.isReady()) {
					if (player.isCarChosen()) {
						player.setySel(player.getySel() + 1);
						Sounds.spray.play();
					} else {
						player.setxSel(player.getxSel() + 1);
						Sounds.car_select.play();

					}

				}
			}
		}
	}
	
	private void leftButtonDown(int playerNr) throws IOException, EncodeException{
		
		for (int i = 0; i < players.size(); i++) {
			if (playerNr == i + 1) {
				Player player = players.get(i);
				if (!player.isReady()) {
					if (player.isCarChosen()) {
						player.setySel(player.getySel() - 1);
						Sounds.spray.play();
					} else {
						player.setxSel(player.getxSel() - 1);
						Sounds.car_select.play();
					}
				}
			}
		}
	}

	public void drawNextLevelInfo(){

		float scaleValue = (float) ((screenWidth/Sprites.nextLevelBorder.getWidth())/3.5);
		float realXvalue = Sprites.nextLevelBorder.getWidth()*scaleValue;
		float realYvalue = Sprites.nextLevelBorder.getHeight()*scaleValue;
		float posX = ((screenWidth/2)-(realXvalue/2));
		float posY = screenHeight-(realYvalue+25);
		
		Sprites.nextLevelBorder.draw(posX,posY,scaleValue);
		
		course.minimap.draw(posX,posY,realXvalue,realYvalue);
		
		Fonts.regularText.drawString(posX+(realXvalue/20), (posY + (realYvalue/10)), course.mapName);
		//Draw nextLevelString
		Fonts.consoleText.drawString(posX, (posY - (console.size())-15), "Next course:");
		
	}

	private void drawMenuInfo(Graphics g) {

		drawCountdown();
		
		drawBlinkingInfo();
		
	}
	
	private void drawCountdown(){
		
		Fonts.header.drawString(xPosCountdown, yPosCountdown, countDown);
	}
	
	private void drawBlinkingInfo(){
		
		String blinkingText;
		if (players.isEmpty())
			blinkingText = "Need at least 1 player to begin!";
		else
			blinkingText = "Ready up and the game will begin";
		if (allPlayersAreReady() && !players.isEmpty())
			blinkingText = "Game is starting";
		
		int stringLength = Fonts.regularText.getWidth(blinkingText);
		
		float xPos = screenWidth/2 - stringLength/2;
		
		if (Math.sin(tick / 60) > 0) {
			Fonts.regularText.drawString( xPos, screenHeight / 2, blinkingText);
		}
	}

	public void drawBacksideInfo() {
		for (int i = console.size(); i > 0; i--) {
			Fonts.consoleText.drawString(0, screenHeight - (consoleSize * (console.size() - i + 1)),
					console.get(i - 1));// Draws the console
		}
	}

	private void determinePlayerChoice(Player player) {
		int car = player.getxSel();
		int color = player.getySel();
		player.setSelect(car * player.maxY + color);
	}
	
	private int randomMap(){
		File dir = new File("Maps/");
		int numberOfSubfolders = 0;
		File listDir[] = dir.listFiles();
		for (int i = 0; i < listDir.length; i++) {
		    if (listDir[i].isDirectory()) {
		            numberOfSubfolders++;
		        }
		}
		return  1 + (int)(Math.random() * numberOfSubfolders);
	}
	
	public void setIP(String ip) {
		controllerIP = "http://" + ip + "/furyracers";
	}
	
	public void setVersion(String version) {
		this.version = version;
	}
	
	public int getID() {
		return 0;
	}
}
