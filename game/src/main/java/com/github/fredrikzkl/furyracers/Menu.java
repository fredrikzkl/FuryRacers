package com.github.fredrikzkl.furyracers;

import java.awt.Font;

import java.io.InputStream;

import java.util.ArrayList;
import java.util.List;



import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.ResourceLoader;

import com.github.fredrikzkl.furyracers.game.GameCore;
import com.github.fredrikzkl.furyracers.game.Player;
import com.github.fredrikzkl.furyracers.network.GameSession;


public class Menu extends BasicGameState {

	private GameCore core;

	Font regularFont;
	TrueTypeFont ip;

	private final int ICONSIZE = 128;

	private static String controllerIP = null;
	private String version = null;
	private TrueTypeFont header;
	private TrueTypeFont regularText;
	private TrueTypeFont consoleText;
	private float consoleSize = 15f;

	private Color headerColor = new Color(221, 0, 0);
	private Image icons, cars, controllerQR;

	private int tick;
	private double seconds;
	// used to count seconds
	double duration, last;

	private String countDown;
	private int counter;
	private int secondsToNextGame = 3;
	private boolean allReady = true;
	double allReadyTimestamp = -1;
	
	QRgenerator QR = new QRgenerator();

	public List<String> console;
	public List<Player> players;

	// --------------//
	private Music music;

	public Menu(GameCore game) {
		core = game;
	}

	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		QR.genQR(controllerIP);
		
		Application.setInMenu(true);
		players = new ArrayList<Player>();
		console = new ArrayList<String>();
		console.add("Welcome to FuryRacers! Version: " + version);

		addFonts();
		counter = secondsToNextGame;
		countDown = Integer.toString(counter);
		duration = last = System.nanoTime();

		getImages();

		music = new Music("Sound/menu.ogg");
		music.setVolume(0.5f);
		music.loop();

	}
	
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		
		GameSession.setGameState(game.getCurrentStateID());
		tick++;
		duration = System.nanoTime() - last;
		seconds += duration / 1_000_000_000.0f;
		
		readyCheck(game);

		last = System.nanoTime();
	}

	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {

		header.drawString(Application.screenSize.width / 3, 50, "Fury Racers", headerColor);
		
		drawGameInfo(g);
		drawBacksideInfo();
		drawPlayerIcons(container, game, g);
		g.drawImage(controllerQR, 150, 150);
		
	}

	private void drawPlayerIcons(GameContainer container, StateBasedGame game, Graphics g) {
		// Cars - tegner ingenting om spiller ikke finnes
		for (int i = 0; i < players.size(); i++) {
			g.drawImage(cars.getSubImage(players.get(i).getxSel() * ICONSIZE, players.get(i).getySel() * ICONSIZE, ICONSIZE, ICONSIZE),
					(float) (Application.screenSize.width / 3.8 + (i * 160)), Application.screenSize.height / 4);
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
	
	public boolean allPlayersAreReady(){
		
		if (players.size() > 0) {
			for (Player player : players) 
				if (!player.isReady()) 
					return false;
			
		}else{
			return false;
		}
		
		return true;
	}
	
	public void readyCheck(StateBasedGame game) throws SlickException{
		
		if (allPlayersAreReady()) {

			secondsToNextGame = (int) (allReadyTimestamp + counter - seconds);
			if (secondsToNextGame <= 0)
				startGame(game);
			
			countDown = String.valueOf(secondsToNextGame);
		}else{
			allReadyTimestamp = -1;
			countDown = String.valueOf(counter);
		}

		if (allReadyTimestamp < 0) {
			allReadyTimestamp = seconds;
			printConsole("Everyone is ready, the game will begin shortly!");
		}
	}

	private void startGame(StateBasedGame game) throws SlickException {
		game.enterState(1);
		core.gameStart(1, players);
	}

	public void updatePlayerList(ArrayList<Player> list) {
		players = list;
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
						if (players.get(i).isReady())
							players.get(i).setReady(false);
						else
							players.get(i).setReady(true);
					} else {
						players.get(i).setCarChosen(true);
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
						} else {
							players.get(i).setxSel(players.get(i).getxSel() + 1);
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
						} else {
							players.get(i).setxSel(players.get(i).getxSel() - 1);
						}
					}
				}
			}
			break;
		}
	}
	
	
	public void addFonts(){
		
		regularFont = new Font("Verdana", Font.BOLD, 20);
		InputStream inputStream;
		
		try {
			inputStream = ResourceLoader.getResourceAsStream("Font/Orbitron-Regular.ttf");
			Font awtFont1 = Font.createFont(Font.TRUETYPE_FONT, inputStream);

			inputStream = ResourceLoader.getResourceAsStream("Font/Orbitron-Regular.ttf");
			Font awtFont2 = Font.createFont(Font.TRUETYPE_FONT, inputStream);

			inputStream = ResourceLoader.getResourceAsStream("Font/Orbitron-Regular.ttf");
			Font awtFont3 = Font.createFont(Font.TRUETYPE_FONT, inputStream);

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
	
	public void getImages(){
		try {
			icons = new Image("Sprites/menu_sheet.png");
			cars = new Image("Sprites/carSheet.png");
			controllerQR = new Image("QRcode/controllerQR.JPG");
		} catch (RuntimeException e) {
			printConsole("ERROR! Sprite sheet not found!");
		} catch (SlickException e) {
			
			e.printStackTrace();
			
		}
	}
	
	public void drawGameInfo(Graphics g){
		
		//countdown
		header.drawString(Application.screenSize.width - 125, Application.screenSize.height - 75, countDown);
		
		String blinkingText;
		if (players.isEmpty())
			blinkingText = "Need at least 1 player to begin!";
		else
			blinkingText = "Ready up and the game will begin";
		if (allReady && !players.isEmpty())
			blinkingText = "Game is starting";

		// Blinking text
		if (Math.sin(tick / 500) > 0) {
			regularText.drawString((float) (Application.screenSize.width / 2.8 - blinkingText.length()),
					Application.screenSize.height / 2, blinkingText);
		}
	}
	
	public void drawBacksideInfo(){
		for (int i = console.size(); i > 0; i--) {
			consoleText.drawString(0, Application.screenSize.height - (consoleSize * (console.size() - i + 1)),
					console.get(i - 1));// Draws the console
		}
	}

	
	public int getID() {
		return 0;
	}

	public void setIP(String ip) {
		controllerIP = "http://" + ip + "/fury";
	}

	public void setVersion(String version) {
		this.version = version;
	}

}
