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

	public float zoom = (float) 1.6; //TODO
	
	Font font;
	TrueTypeFont ttf;
	
	public Car p1;
	public Car p2;
	public Car p3;
	public Car p4;
	public List<Car> cars;
	
	public Vector2f longestDistance;
	private boolean keyboardPlayer;

	public GameCore(String title) {
		super(title);
	}
	
	

	public void init(GameContainer container) throws SlickException {
		cars = new ArrayList<Car>();
		redMustang = new Image("Sprites/fr_mustang_red.png");
		blueMustang = new Image("Sprites/fr_mustang_blue.png");
		greenMustang = new Image("Sprites/fr_mustang_green.png");
		yellowMustang = new Image("Sprites/fr_mustang_yellow.png");
		longestDistance = new Vector2f();
		
		font = new Font("Verdana", Font.BOLD, 20);
		ttf = new TrueTypeFont(font, true);
		 
		level = new Level(1);

		camera = new Camera(Application.VIEW_HEIGHT/2,Application.VIEW_WIDTH/2,level);
		
		
	}

	public void update(GameContainer container, int deltaTime) throws SlickException {
		checkForKeyboardInput(container);
		for(Car cars: cars){
			cars.update(container, deltaTime);
		}
				
		longestDistance.x = 0;
		longestDistance.y = 0;
		for(int x = 0; x<cars.size();x++){
			if(cars.get(x).position.x >longestDistance.x){
				longestDistance.x = cars.get(x).position.x;
			}
			if(cars.get(x).position.y>longestDistance.y){
				longestDistance.y = cars.get(x).position.y;
 			}
		}
		
		zoomLogic();
		camera.update(longestDistance.x,longestDistance.y);
	}

	



	public void render(GameContainer container, Graphics g)
			throws SlickException {
		g.translate(camera.getX(), camera.getY()); //Start of camera
		camera.zoom(g,(float) zoom);//Crasher om verdien <=0 
		
		level.render(g);
		for(Car cars: cars){
			cars.render();
		}
		g.translate(-camera.getX(), -camera.getY()); //End of camera
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
		if(nr == 3){
			p3 = new Car(id, "medium", 3,greenMustang,480,100,75,110,1, level);
			cars.add(p3);
		}
		if(nr ==4){
			p4 = new Car(id, "medium", 4,yellowMustang,480,100,75,110,1, level);
			cars.add(p4);
		}
		
	}
	
	private void zoomLogic() {
		if(longestDistance.x  > 100 || longestDistance.y > 100){
			if(zoom>1)
				zoom = (float) (zoom -0.0003);
		}else{
			if(zoom<1.6)
				zoom = (float) (zoom -0.0003);
		}
	}
	
	public void checkForKeyboardInput(GameContainer container) throws SlickException{
		Input input = container.getInput();
		if(input.isKeyDown(Input.KEY_A) && !keyboardPlayer){
			int amountOfPlayers = cars.size();
			createPlayer(amountOfPlayers+1,"futureReference");
			cars.get(amountOfPlayers).activateKeyboardInput();
			keyboardPlayer = true;
		}
	}


	
}
