package com.github.fredrikzkl.furyracers.Furyracers;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
//import org.newdawn.slick.SpriteSheet;

public class GameCore extends BasicGame{
	
	//SpriteSheet sprite;

	public GameCore(String title) {
		super(title);
		// TODO Auto-generated constructor stub
	}

	public void init(GameContainer arg0) throws SlickException {
		
		//sprite = new SpriteSheet("Sprites/car.png", 20,20);
	}

	public void render(GameContainer arg0, Graphics arg1) throws SlickException {
		
		//sprite.getSprite(1, 1).draw(50,50);
		
	}
	
	public void update(GameContainer arg0, int arg1) throws SlickException {
		
		
	}
	
	public static void main(String[] args) throws SlickException {
		AppGameContainer app = new AppGameContainer(new GameCore("Setup Test"));
		
		app.setDisplayMode(800, 600, false);
		app.setAlwaysRender(true);
	}

}
