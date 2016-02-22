package com.github.fredrikzkl.furyracers.game;

import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import com.github.fredrikzkl.furyracers.Application;
import com.github.fredrikzkl.furyracers.network.GameSession;

public class GameCore extends BasicGameState {
	
	private String IP = "";
	
	Image p1car = null;
	SpriteSheet sprite;
	Circle dot;
	Circle center;
	
	Image redMustang;
	Image blueMustang;
	Image greenMustang;
	Image yellowMustang;

	public static Camera camera;
	public Level level = null;

	public float initalZoom = (float) 1; //TODO
	public float zoom = (float) 1;
	
	Font font;
	TrueTypeFont ttf;
	
	
	public List<Car> cars;
	public List<Player> players;
	
	public Vector2f longestDistance;
	public Vector2f smallestDistance;
	public Vector2f deltaDistance;
	public Vector2f closestEdge;
	
	
	public Vector2f tilePos; 
	public int cameraMargin = 250;
	
	float biggest = 0;
	
	private boolean keyboardPlayerOne, keyboardPlayerTwo;

	public GameCore(int state) {
		
	}

	public void init(GameContainer container, StateBasedGame sbg) throws SlickException {
		Application.setInMenu(false);
		System.out.println("IP: " + IP);
		cars = new ArrayList<Car>();
		
		longestDistance = new Vector2f();
		smallestDistance = new Vector2f();
		deltaDistance = new Vector2f();
		closestEdge = new Vector2f();
		tilePos = new Vector2f();
		
		keyboardPlayerOne = false;
		keyboardPlayerOne = false;
		
		font = new Font("Verdana", Font.BOLD, 20);
		ttf = new TrueTypeFont(font, true);
		
		center = new Circle(0,0,1);
		 
	}
	
	public void gameStart(int levelNr, List<Player> players2) throws SlickException{
		Application.setInMenu(false);
		level = new Level(levelNr);
		camera = new Camera(0,0,level,this);
		
		players = players2;
		for(Player player:players){
			createPlayer(player.getPlayerNr(),player.getId(), player.getSelect());
		}
		//For cinematic effect, bruh
    	zoom = (float) 0.3;
	}

	public void update(GameContainer container , StateBasedGame game, int deltaTime) throws SlickException {
		GameSession.setGameState(game.getCurrentStateID());
		checkForKeyboardInput(container, game);
		for(Car cars: cars){
			cars.update(container, game, deltaTime);
		}
				
		checkDistances();
		zoomLogic();
		camera.update(((deltaDistance.x/2+closestEdge.x)-Application.screenSize.width/2)*zoom,
					  ((deltaDistance.y/2+closestEdge.y)-Application.screenSize.height/2)*zoom);
	}

	public void render(GameContainer container, StateBasedGame sbg, Graphics g)
			throws SlickException {
		
		//--------------------------------------------------------------------------//
		camera.zoom(g,(float) zoom);//Crasher om verdien <=0 	
		g.translate(camera.getX(), camera.getY()); //Start of camera
		
		
		level.render(g,tilePos,camera);
		for(Car car: cars){
			car.render(g);
		}
		
		
		g.translate(-camera.getX(), -camera.getY()); //End of camera
		camera.zoom(g,(float) 1/zoom);
		//--------------------------------------------------------------------------//
		
		for(Car car: cars){

			ttf.drawString(car.getPosition().x+camera.getX()-25, car.getPosition().y+camera.getY()-30, car.getTimeElapsed());
		}
		ttf.drawString(Application.screenSize.width-300, 0, IP);//Ip addresene øverst i venstre corner
	}
	
	private void zoomLogic() {
		float deltaX = (float) (deltaDistance.x/(Application.screenSize.width/1.98)); //Høyere deleverdi gir mindre margin
		float deltaY = (float) (deltaDistance.y/(Application.screenSize.height/1.92)); 
		float temp = zoom;
		boolean zoomLim = true;
		
		
		if(deltaY>deltaX)
			biggest=deltaY;
		else
			biggest=deltaX;
		
		temp = initalZoom /(biggest);
		
		if(camera.getSize().x - camera.getX() >= level.distanceWidth && zoom>temp &&camera.getX()>=0 || 
		   camera.getSize().y - camera.getY() >= level.distanceHeight && zoom>temp &&camera.getY()>=0)
			zoomLim = false;
		
		if(zoomLim){
			if(temp > initalZoom){
				zoom = (float) initalZoom;
			}else{
				zoom = temp;
			}
		}
	}
	
	private void checkDistances() {
		longestDistance.x = 1;
		longestDistance.y = 1;
		smallestDistance.x = level.getDistanceWidth();
		smallestDistance.y = level.getDistanceHeight();
		closestEdge.x = level.getDistanceWidth();
		closestEdge.y = level.getDistanceHeight();
		
		
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
			
			if(cars.get(i).position.x<closestEdge.x){
				closestEdge.x = cars.get(i).position.x;
			}
			if(cars.get(i).position.y<closestEdge.y){
				closestEdge.y = cars.get(i).position.y;
			}
				
		}
		deltaDistance.x = longestDistance.x - smallestDistance.x;
		deltaDistance.y = longestDistance.y - smallestDistance.y;
		
		tilePos.x = (int)(longestDistance.x/level.getTileWidth());
		tilePos.y = (int)(longestDistance.y/level.getTileHeight());
	}
	
	public void checkForKeyboardInput(GameContainer container, StateBasedGame game) throws SlickException{
		Input input = container.getInput();
		if(input.isKeyDown(Input.KEY_A) && !keyboardPlayerOne){
			int amountOfPlayers = cars.size();
			createPlayer(amountOfPlayers+1,"keyboardPlayer",1);
			cars.get(amountOfPlayers).activateKeyboardInput();
			keyboardPlayerOne = true;
		}
		
		if(input.isKeyDown(Input.KEY_B) && !keyboardPlayerTwo){
			int amountOfPlayers = cars.size();
			createPlayer(amountOfPlayers+1,"keyboardPlayerTwo",1);
			cars.get(amountOfPlayers).activateKeyboardInput();
			keyboardPlayerTwo = true;
		}
		
	    if(input.isKeyPressed(Input.KEY_N)){
	    	game.getState(1).init(container, game);
	    	game.enterState(1);
	    }
	    
	}
	
	public void createPlayer(int nr, String id, int playerChoice) throws SlickException{
		
		CarProperties temp = CarProperties.values()[playerChoice];
		
		cars.add(new Car(temp,id,nr,
				level.getStartCoordinates().x-(level.tileWidth*4),
				level.getStartCoordinates().y-(level.tileHeight*4),
				level));
	}
	
	public void setIP(String ip) {
		IP = ip + "/fury";
	}

	public int getID() {
		return 1;
	}

	public float getZoom() {
		return zoom;
	}

}
