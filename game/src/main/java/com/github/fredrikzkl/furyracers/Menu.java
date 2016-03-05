package com.github.fredrikzkl.furyracers;

import java.awt.Font;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.ResourceLoader;

import com.github.fredrikzkl.furyracers.game.CourseHandler;
import com.github.fredrikzkl.furyracers.game.GameCore;
import com.github.fredrikzkl.furyracers.game.Player;
import com.github.fredrikzkl.furyracers.network.GameSession;

public class Menu extends BasicGameState {

	private GameCore core;

	Font regularFont;
	TrueTypeFont ip;
	
	private int mapSelected;
	private CourseHandler course;

	private final int ICONSIZE = 128;

	private static String controllerIP = null;
	private String version = null;
	private TrueTypeFont header;
	private TrueTypeFont regularText;
	private TrueTypeFont consoleText;
	private float consoleSize = 15f;

	private Color headerColor = new Color(221, 0, 0);
	private Image icons, cars, controllerQR, nextLevelBorder;

	private int tick;
	private double seconds;
	// used to count seconds
	double duration, last;

	private String countDown;
	private int counter;
	private int secondsToNextGame;
	double allReadyTimestamp;

	QRgenerator QR = new QRgenerator();

	public List<String> console;
	public List<Player> players;

	// --------------//
	private ParallaxBackground background;
	// --------------//
	private Music music;
	private Sound car_select;
	private Sound select_car;
	private Sound spray;
	private Sound playerJoin;
	private Sound playerReady;
	private Sound deSelect;
	private Sound peep;
	private Sound getReady;
	
	private boolean getReadySaid;

	public Menu(GameCore game) {
		core = game;
	}

	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		
		
		QR.genQR(controllerIP);
		Application.setInMenu(true);
		initVariables();
		addFonts();
		getImages();
		initSounds();
		
	}

	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		GameSession.setGameState(getID());
		setTime();
		readyCheck(game);
	}
	
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		
		background.draw();
		background.tick();
		drawHeader();
		drawGameInfo(g);
		drawBacksideInfo();
		drawPlayerIcons(container, game, g);
		drawQRcode(g);
		drawNextLevelInfo();
	}
	
	public void initVariables() throws SlickException{
		players = new ArrayList<Player>();
		console = new ArrayList<String>();
		console.add("Welcome to FuryRacers! Version: " + version);
		
		mapSelected = randomMap();
		course = new CourseHandler(mapSelected);
		
		allReadyTimestamp = 0;
		secondsToNextGame = 5;
		counter = secondsToNextGame;
		countDown = Integer.toString(counter);
		duration = last = System.nanoTime();
		
		background = new ParallaxBackground();
		music = new Music("Sound/menu.ogg");
		music.loop();
		music.setVolume((float) 0.4);
		getReadySaid = false;
	}
	
	public void setTime(){
		
		tick++;
		duration = System.nanoTime() - last;
		seconds += duration / 1_000_000_000.0f;
		last = System.nanoTime();
	}

	public void drawQRcode(Graphics g){

		g.drawImage(controllerQR, Application.screenSize.width - controllerQR.getWidth()-50, 50);
	}
	
	public void drawHeader(){
		
		header.drawString(Application.screenSize.width / 3, 50, "Fury Racers", headerColor);
	}

	private void drawPlayerIcons(GameContainer container, StateBasedGame game, Graphics g) {

		// Cars - tegner svart om spiller ikke finnes
		for (int i = 0; i < 4; i++) {
			if (i < players.size()) {
				g.drawImage(
						cars.getSubImage(players.get(i).getxSel() * ICONSIZE, players.get(i).getySel() * ICONSIZE,
								ICONSIZE, ICONSIZE),
						(float) (Application.screenSize.width / 3.8 + (i * 160)), Application.screenSize.height / 4);
			}else{
				g.drawImage(icons.getSubImage(256, 0, 128, 128),(float) (Application.screenSize.width / 3.8 + (i * 160)), Application.screenSize.height / 4);
			}
		}

		// Bordersene
		for (int i = 0; i < 4; i++) {
			if (i < players.size() && players.get(i).isReady()) {
				g.drawImage(icons.getSubImage(128, 0, 128, 128),
						(float) (Application.screenSize.width / 3.8 + (i * 160)), Application.screenSize.height / 4);
				continue;
			}
			g.drawImage(icons.getSubImage(0, 0, 128, 128), (float) (Application.screenSize.width / 3.8 + (i * 160)),
					Application.screenSize.height / 4);
		}
	}

	public boolean allPlayersAreReady() {

		if (players.size() > 0) {
			for (Player player : players)
				if (!player.isReady())
					return false;
		} else {
			return false;
		}

		return true;
	}

	public void readyCheck(StateBasedGame game) throws SlickException {

		if (allPlayersAreReady()) {

			if (allReadyTimestamp < 0) {
				allReadyTimestamp = seconds;
				printConsole("Everyone is ready, the game will begin shortly!");
				if(!getReadySaid){
					getReady.play();
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
		music.stop();
		core.gameStart(course, players);
		game.enterState(1);
	}

	public void updatePlayerList(ArrayList<Player> list) {
		players = list;
		playerJoin.play();
	}

	public void printConsole(String text) {
		if (console.size() > 4)
			console.remove(0);
		console.add(text);
	}

	public void buttonDown(String data, int playerNr) {
		switch (data) {
		case "1":
			for (int i = 0; i < players.size(); i++) {
				if (playerNr == i + 1) {
					if (players.get(i).isCarChosen()) {
						if (players.get(i).isReady()) {
							players.get(i).setReady(false);
							deSelect.play();
						} else {
							determinePlayerChoice(players.get(i));
							players.get(i).setReady(true);
							playerReady.play();
						}
					} else {
						players.get(i).setCarChosen(true);
						select_car.play();
					}
				}
			}
			break;
		case "2":
			for (int i = 0; i < players.size(); i++) {
				if (playerNr == i + 1) {
					if (!players.get(i).isReady()) {
						if (players.get(i).isCarChosen()) {
							players.get(i).setySel(players.get(i).getySel() + 1);
							spray.play();
						} else {
							players.get(i).setxSel(players.get(i).getxSel() + 1);
							car_select.play();

						}

					}
				}
			}
			break;
		case "3":
			for (int i = 0; i < players.size(); i++) {
				if (playerNr == i + 1) {
					if (!players.get(i).isReady()) {
						if (players.get(i).isCarChosen()) {
							players.get(i).setySel(players.get(i).getySel() - 1);
							spray.play();
						} else {
							players.get(i).setxSel(players.get(i).getxSel() - 1);
							car_select.play();
						}
					}
				}
			}
			break;
		}
	}

	public void addFonts() {

		regularFont = new Font("Verdana", Font.BOLD, 20);
		InputStream inputStream;

		try {
			inputStream = ResourceLoader.getResourceAsStream("Font/Orbitron-Regular.ttf");
			Font awtFont1 = Font.createFont(Font.TRUETYPE_FONT, inputStream);
			Font awtFont2;
			Font awtFont3;

			awtFont1 = awtFont1.deriveFont(60f); // set font size
			awtFont2 = awtFont1.deriveFont(24f);
			awtFont3 = awtFont1.deriveFont(consoleSize);

			header = new TrueTypeFont(awtFont1, true);
			regularText = new TrueTypeFont(awtFont2, true);
			consoleText = new TrueTypeFont(awtFont3, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void getImages() {
		try {
			icons = new Image("Sprites/menu/menu_sheet.png");
			cars = new Image("Sprites/menu/carSheet.png");
			controllerQR = new Image("QRcode/controllerQR.JPG");
			nextLevelBorder = new Image("Sprites/UI/nextLevelBorder.png");
		} catch (RuntimeException e) {
			printConsole("ERROR! Sprite sheet not found!");
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
	public void drawNextLevelInfo(){
		float screenWidth = Application.screenSize.width;
		float screenHeight = Application.screenSize.height;
		float scaleValue = (float) ((screenWidth/nextLevelBorder.getWidth())/3.5);
		float realXvalue = nextLevelBorder.getWidth()*scaleValue;
		float realYvalue = nextLevelBorder.getHeight()*scaleValue;
		float posX = ((screenWidth/2)-(realXvalue/2));
		float posY = screenHeight-(realYvalue+25);
		
		nextLevelBorder.draw(posX,posY,scaleValue);
		//Draw minimap
		course.minimap.draw(posX,posY,realXvalue,realYvalue);
		//Draw mapname
		regularText.drawString(posX+(realXvalue/20), (posY + (realYvalue/10)), course.mapName);
		//Draw nextLevelString
		consoleText.drawString(posX, (posY - (console.size())-15), "Next course:");
		
	}

	public void drawGameInfo(Graphics g) {

		// countdown
		header.drawString(Application.screenSize.width - 125, Application.screenSize.height - 75, countDown);
		
		String blinkingText;
		if (players.isEmpty())
			blinkingText = "Need at least 1 player to begin!";
		else
			blinkingText = "Ready up and the game will begin";
		if (allPlayersAreReady() && !players.isEmpty())
			blinkingText = "Game is starting";

		// Blinking text
		if (Math.sin(tick / 500) > 0) {
			regularText.drawString((float) (Application.screenSize.width / 2.8 - blinkingText.length()),
					Application.screenSize.height / 2, blinkingText);
		}
		
	}

	public void drawBacksideInfo() {
		for (int i = console.size(); i > 0; i--) {
			consoleText.drawString(0, Application.screenSize.height - (consoleSize * (console.size() - i + 1)),
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

	private void initSounds() {
		try {
			String path = "Sound/";
			car_select = new Sound(path + "car_select.ogg");
			select_car = new Sound(path + "select_car.ogg");
			spray = new Sound(path + "spray.ogg");
			playerJoin = new Sound(path + "playerJoin.ogg");
			playerReady = new Sound(path + "ready.ogg");
			deSelect = new Sound(path + "deselect.ogg");
			peep = new Sound(path + "countdown.ogg");
			getReady = new Sound(path + "/announcer/getReady.ogg");
		} catch (SlickException e) {
			System.out.println("Could not load sound file" + e);
			e.printStackTrace();
		}
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
