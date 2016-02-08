package com.github.fredrikzkl.furyracers;

import com.github.fredrikzkl.furyracers.game.GameCore;
import com.github.fredrikzkl.furyracers.game.Level;

import com.github.fredrikzkl.furyracers.network.GameSession;

import org.lwjgl.opengl.Display;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.ScalableGame;
import org.newdawn.slick.SlickException;

import java.awt.*;

public class Application {
	private static GameCore game;
	private static GameSession gameSession;
	
	public static Dimension screenSize;

	public static final int HEIGHT = 24;
	public static final int WIDTH = 32;
	public static final int VIEW_HEIGHT = HEIGHT * 16; //TODO
	public static final int VIEW_WIDTH = WIDTH * 16; //TODO
	public static final int FPS = 120;

	private Application() {
		game = new GameCore("Fury");
		createGameSession();
		startGame();
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

	public static void main(String[] args) {
		new Application();
	}

	private void createGameSession() {
		try {
			gameSession = new GameSession(game);
			gameSession.connect();
		} catch (Exception e) {
			fatalError("Could not start websocket server: " + e.getMessage());
		}
	}
	private void startGame() {
		
		
		try {
			screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			//Display.setResizable(true);
			AppGameContainer app = new AppGameContainer(new ScalableGame(game, (int)screenSize.getWidth(), (int)screenSize.getHeight()));
			app.setDisplayMode((int)screenSize.getWidth(), (int)screenSize.getHeight(), true);
			app.setTargetFrameRate(FPS);
			// app.setMouseGrabbed(true);
			app.setAlwaysRender(true);
			app.start();
		} catch (SlickException e) {
			fatalError("Could not start FuryRacers: " + e.getMessage());
		}
	}
}