package com.github.fredrikzkl.furyracers.Furyracers;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

public class GameCore extends BasicGame {
	
	private Image spriteMew = null;
	private SpriteSheet car = null;
	private Image carSheet = null;

	public GameCore(String title) {
		super(title);
		
	}
	
	public void init(GameContainer container) throws SlickException {
		spriteMew = new Image("Sprites/Mew.png");
		carSheet = new Image("Sprites/car.png");
		car = new SpriteSheet(carSheet, 32, 32);
		
	}
	
	public void update(GameContainer container, int delta) throws SlickException {
	
		
	}

	public void render(GameContainer container, Graphics g) throws SlickException {
		
		g.drawString("Hello, World!", 50, 50);
		
		
		spriteMew.draw(200,250,45,45);
		car.startUse();
		car.getSubImage(1, 0).draw(200, 200);
		car.endUse();
	}

	public static void main(String[] args) throws SlickException{
		AppGameContainer app = new AppGameContainer(new GameCore("Setup Test"));
		
		app.setDisplayMode(800, 600, false);
		app.setAlwaysRender(true);
		
		app.start();
		
	}
	

	

}
