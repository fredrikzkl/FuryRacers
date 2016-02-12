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
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import com.github.fredrikzkl.furyracers.Application;

public class GameCore extends BasicGameState {
	
	private String IP = "";
	
	Image p1car = null;
	SpriteSheet sprite;
	
	Image redMustang;
	Image blueMustang;
	Image greenMustang;
	Image yellowMustang;

	public static Camera camera;
	public Level level = null;

	public float initalZoom = (float) 1.2; //TODO
	public float zoom = (float) 1;
	
	Font font;
	TrueTypeFont ttf;
	
	public Car p1;
	public Car p2;
	public Car p3;
	public Car p4;
	public List<Car> cars;
	
	public Vector2f longestDistance;
	public Vector2f smallestDistance;
	public Vector2f deltaDistance;
	public Vector2f tilePos; 
	public int cameraMargin = 250;
	
	float biggest = 0;
	
	
	private boolean keyboardPlayer;

	public GameCore(int state) {
		
	}

	public void init(GameContainer container, StateBasedGame sbg) throws SlickException {
		
		cars = new ArrayList<Car>();
		
		redMustang = new Image("Sprites/fr_mustang_red.png");
		blueMustang = new Image("Sprites/fr_mustang_blue.png");
		greenMustang = new Image("Sprites/fr_mustang_green.png");
		yellowMustang = new Image("Sprites/fr_mustang_yellow.png");
		
		longestDistance = new Vector2f();
		smallestDistance = new Vector2f();
		deltaDistance = new Vector2f();
		tilePos = new Vector2f();
		
		font = new Font("Verdana", Font.BOLD, 20);
		ttf = new TrueTypeFont(font, true);
		 
		level = new Level(1);

		camera = new Camera(0,0,level);
	}

	public void update(GameContainer container , StateBasedGame sbg, int deltaTime) throws SlickException {
		checkForKeyboardInput(container);
		for(Car cars: cars){
			cars.update(container, sbg, deltaTime);
		}
				
		checkDistances();
		//zoomLogic();
		
		camera.update(smallestDistance.x-cameraMargin ,smallestDistance.y-cameraMargin);
	}

	public void render(GameContainer container, StateBasedGame sbg, Graphics g)
			throws SlickException {
		g.translate(camera.getX(), camera.getY()); //Start of camera
		//camera.zoom(g,(float) zoom);//Crasher om verdien <=0 
		
		level.render(g,tilePos);
		for(Car cars: cars){
			cars.render();
		}
		ttf.drawString(50,50, "X: " + deltaDistance.x + " Y: " + deltaDistance.y);
		ttf.drawString(50,100,"Biggest: " + biggest);
		
		g.translate(-camera.getX(), -camera.getY()); //End of camera
		
		ttf.drawString(Application.screenSize.width-250, Application.screenSize.height-30, IP); //Ip addresene nederst i venstre corner
		
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
		if(nr == 4){
			p4 = new Car(id, "medium", 4,yellowMustang,480,100,75,110,1, level);
			cars.add(p4);
		}
		
	}
	
	private void zoomLogic() {
		float deltaX = deltaDistance.x/700;
		float deltaY = deltaDistance.y/330;
		float temp = zoom;
		
		if(deltaY>deltaX)
			biggest=deltaY;
		else
			biggest=deltaX;
		temp = initalZoom/ (biggest);
		
		if(temp > 1.2){
			zoom = (float) 1.2;
			System.out.println(zoom);
		}else if(temp < 0.3){
			zoom = (float) 0.3;
		}else{
			zoom = temp;
		}
	}
	
	private void checkDistances() {
		longestDistance.x = 1;
		longestDistance.y = 1;
		smallestDistance.x = level.getDistanceWidth();
		smallestDistance.y = level.getDistanceHeight();
		
		for(int i = 0; i<cars.size();i++){
			if(cars.get(i).position.x >longestDistance.x){
				longestDistance.x = cars.get(i).position.x;
			}
			if(cars.get(i).position.x<smallestDistance.x){
				smallestDistance.x = cars.get(i).position.x;
			}
			if(cars.get(i).position.y>longestDistance.y){
				longestDistance.y = cars.get(i).position.y;
 			}
			if(cars.get(i).position.y<smallestDistance.y){
				smallestDistance.y = cars.get(i).position.y;
			}
		}
		deltaDistance.x = longestDistance.x - smallestDistance.x;
		deltaDistance.y = longestDistance.y - smallestDistance.y;
		
		tilePos.x = (int)(longestDistance.x/level.getTileWidth());
		tilePos.y = (int)(longestDistance.y/level.getTileHeight());
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

	public int getID() {
	
		return 1;
	}

	public void setIP(String iP) {
		IP = iP;
	}

}
