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

	private static String IP = null;
	private String version = null;
	private TrueTypeFont header;
	private TrueTypeFont regularText;
	private TrueTypeFont consoleText;
	private float consoleSize = 15f;

	private Color headerColor = new Color(221, 0, 0);
	private Image icons, cars;

	private int tick;
	private double seconds;
	// used to count seconds
	double duration, last;

	private String countDown;
	private int counter;
	private int secondsToNextGame = 5;
	private boolean allReady = true;
	double allReadyTimestamp = -1;

	public List<String> console;
	public List<Player> players;

	// --------------//
	private Music music;
	private Sound car_select;
	private Sound select_car;
	private Sound spray;
	private Sound playerJoin;
	private Sound playerReady;
	private Sound deSelect;
	private Sound peep;

	public Menu(int state, GameCore game) {
		core = game;
	}

	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		Application.setInMenu(true);
		players = new ArrayList<Player>();
		console = new ArrayList<String>();
		console.add("Welcome to FuryRacers! Version: " + version);

		regularFont = new Font("Verdana", Font.BOLD, 20);
		ip = new TrueTypeFont(regularFont, true);

		counter = secondsToNextGame;
		countDown = Integer.toString(counter);
		duration = last = System.nanoTime();

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

		try {
			icons = new Image("Sprites/menu_sheet.png");
			cars = new Image("Sprites/carSheet.png");
		} catch (RuntimeException e) {
			printConsole("ERROR! Sprite sheet not found!");
		}

		music = new Music("Sound/menu.ogg");
		music.loop();
		music.setVolume((float) 0.4);
		
		initSounds();
	}


	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {

		// TODO
		ip.drawString(Application.screenSize.width - 300, 0, IP);
		// ip.drawString(Application.screenSize.width-125,20,version);

		header.drawString(Application.screenSize.width / 3, 50, "Fury Racers", headerColor);

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

		header.drawString(Application.screenSize.width - 125, Application.screenSize.height - 75, countDown);

		String consoleTxt;
		for (int i = console.size(); i > 0; i--) {
			consoleText.drawString(0, Application.screenSize.height - (consoleSize * (console.size() - i + 1)),
					console.get(i - 1));// Draws the console
		}

		drawPlayerIcons(container, game, g);
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

	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		GameSession.setGameState(game.getCurrentStateID());

		tick++;
		duration = System.nanoTime() - last;
		seconds += duration / 1_000_000_000.0f;

		allReady = true;
		if (players.size() > 0) {
			for (Player player : players) {
				if (!player.isReady()) {
					allReady = false;
				}
			}
			if (allReady && allReadyTimestamp < 0) {
				allReadyTimestamp = seconds;
				printConsole("Everyone is ready, the game will begin shortly!");
			}
		} else {
			allReady = false;
		}

		if (allReady) {
			secondsToNextGame = (int) (allReadyTimestamp + counter - seconds);
			if (secondsToNextGame <= 0) {
				startGame(game);
			}
			
			countDown = String.valueOf(secondsToNextGame);
		} else {
			allReadyTimestamp = -1;
			countDown = String.valueOf(counter);
		}

		Input mouse = container.getInput();
		if (mouse.isMouseButtonDown(0)) {

		}

		last = System.nanoTime();
	}

	private void startGame(StateBasedGame game) throws SlickException {
		music.stop();
		game.enterState(1);
		core.gameStart(1, players);
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
						if (players.get(i).isReady()){
							players.get(i).setReady(false);
							deSelect.play();
						}else{
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

	private void determinePlayerChoice(Player player) {
		int car = player.getxSel();
		int color = player.getySel();
		player.setSelect(car*player.maxY + color);
	}

	public int getID() {
		return 0;
	}

	public void setIP(String ip) {
		IP = ip + "/fury";
	}

	public void setVersion(String version) {
		this.version = version;
	}

	private void initSounds() {
		try {
			String path = "Sound/";
			car_select = new Sound(path + "car_select.ogg");
			select_car = new Sound(path +"select_car.ogg");
			spray = new Sound(path + "spray.ogg");
			playerJoin = new Sound(path + "playerJoin.ogg");
			playerReady = new Sound(path + "ready.ogg");
			deSelect = new Sound(path + "deselect.ogg");
			peep = new Sound(path + "countdown.ogg");
		} catch (SlickException e) {
			System.out.println("Could not load sound file" + e);
			e.printStackTrace();
		}
		
		
		
	}

}
