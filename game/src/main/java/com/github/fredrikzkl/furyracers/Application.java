package com.github.fredrikzkl.furyracers;

import com.github.fredrikzkl.furyracers.game.GameCore;
import com.github.fredrikzkl.furyracers.network.GameSession;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.ScalableGame;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import java.awt.*;

public class Application extends StateBasedGame {
	
	private final static String version = "0.01" + "a";
	
	private final static String gameName = "FuryRacers";
	
	private final static int menuID = 0;
	private final static int GameCore = 1;
	private static GameCore game;
	private static Menu menu;
	private static GameSession gameSession;
	public static Dimension screenSize;
	public static final int HEIGHT = 24;
	public static final int WIDTH = 32;
	public static final int VIEW_HEIGHT = HEIGHT * 16; //TODO
	public static final int VIEW_WIDTH = WIDTH * 16; //TODO
	public static final int FPS = 120;
	
	public static boolean inMenu;

	public Application(String gameName) {
		super(gameName);
		this.addState(menu);
		this.addState(game);
	}
	
	public void initStatesList(GameContainer container) throws SlickException {
		this.getState(menuID).init(container, this);
		this.getState(GameCore).init(container, this);
		this.enterState(menuID);
	}

	public static GameSession getGameSession() {
		return gameSession;
	}

	public static void fatalError(String error) {
		System.out.println(error);
		exit();
	}

	private static void exit() {
		System.out.println("Unable to recover; exiting...");
		System.exit(1);
	}

	private static void createGameSession() {
		try {
			game = new GameCore();
			menu = new Menu(game);
			menu.setVersion(version);
			gameSession = new GameSession(game,menu);
			gameSession.connect();
		} catch (Exception e) {
			fatalError("Could not start websocket server: " + e.getMessage());
		}
	}
	
	private static void startGame() {
		try {
			screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			Display.setResizable(true);
			AppGameContainer app = new AppGameContainer(new ScalableGame(new Application(gameName), (int)screenSize.getWidth(), (int)screenSize.getHeight()));
			app.setDisplayMode((int)screenSize.getWidth(), (int)screenSize.getHeight(), true);

			app.setAlwaysRender(true);
			app.start();
			System.out.println("Launching the game! Setting resolution: " + screenSize.getWidth() + "x" + screenSize.getHeight());
		} catch (SlickException e) {
			fatalError("Could not start FuryRacers: " + e.getMessage());
		}
	}
	
	public static void main(String[] args) {
		createGameSession();
		startGame();
	}

	public static boolean isInMenu() {
		return inMenu;
	}

	public static void setInMenu(boolean inMenu) {
		Application.inMenu = inMenu;
	}
}