package com.github.fredrikzkl.furyracers.game;

import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

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
	
	Image redMustang;
	Image blueMustang;
	Image greenMustang;
	Image yellowMustang;

	public static Camera camera;
	public Level level = null;

	public float zoom = (float) 1; //TODO
	
	Font font;
	TrueTypeFont ttf;
	
	public Car p1;
	public Car p2;
	public Car p3;
	public Car p4;
	public List<Car> cars;

	public GameCore(String title) {
		super(title);
	}
	
	

	public void init(GameContainer container) throws SlickException {
		cars = new ArrayList<Car>();
		redMustang = new Image("Sprites/fr_mustang_red.png");
		blueMustang = new Image("Sprites/fr_mustang_blue.png");
		
		
		font = new Font("Verdana", Font.BOLD, 20);
		ttf = new TrueTypeFont(font, true);
		 
		level = new Level(1);

		camera = new Camera(Application.VIEW_HEIGHT/2,Application.VIEW_WIDTH/2,level);
		
		
	}

	public void update(GameContainer container, int deltaTime) throws SlickException {
		for(Car cars: cars){
			cars.update(container, deltaTime);
		}
		
		//camera.update((position.x*zoom - Application.VIEW_WIDTH/2)/zoom, (position.y*zoom - Application.VIEW_HEIGHT/2)/zoom);
	}

	public void render(GameContainer container, Graphics g)
			throws SlickException {
		//g.translate(camera.getX()*zoom, camera.getY()*zoom); //Start of camera
		camera.zoom(g,(float) zoom);//Crasher om verdien <=0 
		level.render(g);
		for(Car cars: cars){
			cars.render();
		}
		//g.translate(-camera.getX()*zoom, -camera.getY()*zoom); //End of camera
	}
	
	public void createPlayer(int nr, String id) throws SlickException{
		if(nr == 1){
			p1 = new Car(id, "medium", 1,redMustang,480,100,75,110,1, level);
			cars.add(p1);
		}
		if(nr == 2){
			p2 = new Car(id, "medium", 2,blueMustang,480,100,75,110,1, level);
			cars.add(p2);
		}
		
		
	}


	
}
