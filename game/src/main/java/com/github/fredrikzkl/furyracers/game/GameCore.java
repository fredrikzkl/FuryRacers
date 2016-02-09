package com.github.fredrikzkl.furyracers.game;

import java.awt.Font;

import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.geom.Vector2f;

import com.github.fredrikzkl.furyracers.Application;

public class GameCore extends BasicGame {

	Image p1car = null;
	SpriteSheet sprite;

	public static Camera camera;
	public Level level = null;

	public float zoom = (float) 1; //TODO
	
	Font font;
	TrueTypeFont ttf;
	
	public Car p1;
	public Car p2;
	public Car p3;
	public Car p4;

	public GameCore(String title) {
		super(title);
	}
	
	

	public void init(GameContainer container) throws SlickException {

		font = new Font("Verdana", Font.BOLD, 20);
		ttf = new TrueTypeFont(font, true);
		 
		level = new Level(1);
		
		p1car = new Image("Sprites/fr_mustang_red.png");
		Image blueCar = new Image("Sprites/fr_mustang_blue.png");
		
		p2 = new Car("mustang", "medium",blueCar,
				480,100,75,110,1, level);

		camera = new Camera(Application.VIEW_HEIGHT/2,Application.VIEW_WIDTH/2,level);
	}

	public void update(GameContainer container, int deltaTime) throws SlickException {
		
		p2.update(container, deltaTime);
		//camera.update((position.x*zoom - Application.VIEW_WIDTH/2)/zoom, (position.y*zoom - Application.VIEW_HEIGHT/2)/zoom);
	}

	public void render(GameContainer container, Graphics g)
			throws SlickException {
		//g.translate(camera.getX()*zoom, camera.getY()*zoom); //Start of camera
		camera.zoom(g,(float) zoom);//Crasher om verdien <=0 
		level.render(g);
		p2.render();
		//g.translate(-camera.getX()*zoom, -camera.getY()*zoom); //End of camera
	}


	
}
