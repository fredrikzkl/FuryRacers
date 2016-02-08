package com.github.fredrikzkl.furyracers.game;

import java.awt.Font;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Point;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.tiled.TiledMap;

import com.github.fredrikzkl.furyracers.Application;

public class GameCore extends BasicGame {

	Image p1car = null;

	Circle point1, point2, point3, point4;
	
	ArrayList<Rectangle> collisionTiles = new ArrayList<Rectangle>();
	
	TiledMap map;

	public Level level = null;

	boolean reverseKeyIsDown, throttleKeyIsDown, leftKeyIsDown, rightKeyIsDown, usingKeyboard = false;
	
	int topSpeed = 520; // pixels per second
	float acceleration = 320; // pixels per second^2
	float deAcceleration = (float) 300;
	int currentSpeed = 0;
	int angleChangePerUpdate = 1;
	
	int windowHeight = Application.WINDOW_HEIGHT;
	int windowWidth = Application.WINDOW_WIDTH;
	
	int tileWidth;
	int tileHeight;

	float maxPixelMovementPerUpdate = topSpeed/(float)Application.FPS;
	float currentPixelMovementPerUpdate = currentSpeed/(float)Application.FPS;
	float pixelAccelerationPerUpdate = acceleration/(float)Math.pow(Application.FPS,2);
	float pixelDeAccelerationPerUpdate = deAcceleration/(float)Math.pow(Application.FPS,2);

	float turningCircumferance = (360/angleChangePerUpdate) * maxPixelMovementPerUpdate;
	float turningRadius = (float) (turningCircumferance / 2 * Math.PI);
	float carSize = (float) 0.5;
	int movementDegrees = 0;

	Vector2f topLeftCornerMap = new Vector2f();
	Vector2f newTopLeftCornerMap = new Vector2f();
	Vector2f vectorMovement = new Vector2f();
	
	float carCollisionPointBackLeftX, carCollisionPointBackLeftY, carCollisionPointBackRightX, carCollisionPointBackRightY, carCollisionPointFrontLeftX, carCollisionPointFrontLeftY, carCollisionPointFrontRightX, carCollisionPointFrontRightY; 
	
	boolean xDirectionAllowed = true;
	boolean yDirectionAllowed = true;
	
	Font font;
	TrueTypeFont ttf;
	
	float radDeg = 0;

	private int tilePosXCollisionPointBackLeft;

	private int tilePosYCollisionPointBackLeft;

	private int tilePosXCollisionPointBackRight;

	private int tilePosYCollisionPointBackRight;

	private int tilePosXCollisionPointFrontLeft;

	private int tilePosYCollisionPointFrontLeft;

	private int tilePosXCollisionPointFrontRight;

	private int tilePosYCollisionPointFrontRight;

	private float mapPosCarCollisionPointBackLeftX;

	private float mapPosCarCollisionPointBackRightX;

	private float mapPosCarCollisionPointFrontLeftX;

	private float mapPosCarCollisionPointFrontRightX;

	private float mapPosCarCollisionPointBackLeftY;

	private float mapPosCarCollisionPointBackRightY;

	private float mapPosCarCollisionPointFrontLeftY;

	private float mapPosCarCollisionPointFrontRightY;

	private float newMapPosCarCollisionPointBackLeftX;

	private float newMapPosCarCollisionPointBackRightX;

	private float newMapPosCarCollisionPointFrontLeftX;

	private float newMapPosCarCollisionPointFrontRightX;

	private float newMapPosCarCollisionPointBackLeftY;

	private float newMapPosCarCollisionPointBackRightY;

	private float newMapPosCarCollisionPointFrontLeftY;

	private float newMapPosCarCollisionPointFrontRightY;

	private int newTilePosXCollisionPointBackLeft;

	private int newTilePosYCollisionPointBackLeft;

	private int newTilePosXCollisionPointBackRight;

	private int newTilePosYCollisionPointBackRight;

	private int newTilePosXCollisionPointFrontLeft;

	private int newTilePosYCollisionPointFrontLeft;

	private int newTilePosXCollisionPointFrontRight;

	private int newTilePosYCollisionPointFrontRight;
	
	private float positionerX;
	
	private float positionerY;

	public GameCore(String title) {
		super(title);
	}

	public void init(GameContainer arg0) throws SlickException {
		
		map = new TiledMap("Maps/go.tmx");
		p1car = new Image("Sprites//fr_mustang_red.png");
		tileHeight  = map.getTileHeight();
		tileWidth = map.getTileWidth();
		point1 = new Circle(0,0,2);
		point2 = new Circle(0,0,2);
		point3 = new Circle(0,0,2);
		point4 = new Circle(0,0,2);
		font = new Font("Verdana", Font.BOLD, 20);
	    ttf = new TrueTypeFont(font, true);
	    
	    /*newMapPosCarCollisionPointBackLeftX = (windowWidth/2) - (float) (Math.cos(radDeg+0.8))*24;
	    newMapPosCarCollisionPointBackLeftY = (windowHeight/2) - (float) (Math.sin(radDeg+0.8))*24;
	    newMapPosCarCollisionPointBackRightX = (windowWidth/2) - (float) (Math.cos(radDeg-0.8))*24;
	    newMapPosCarCollisionPointBackRightY = (windowHeight/2) - (float) (Math.sin(radDeg-0.8))*24;
	    newMapPosCarCollisionPointFrontLeftX = (windowWidth/2) + (float) (Math.cos(radDeg+0.35))*50;
	    newMapPosCarCollisionPointFrontLeftY = (windowHeight/2) + (float) (Math.sin(radDeg+0.35))*50;
	    newMapPosCarCollisionPointFrontRightX = (windowWidth/2) + (float) (Math.cos(radDeg-0.35))*50;
	    newMapPosCarCollisionPointFrontRightY = (windowHeight/2) + (float) (Math.sin(radDeg-0.35))*50;*/
	}

	public void update(GameContainer container, int arg1) throws SlickException {

		Input input = container.getInput();

		reactToControlls(input);
		
		int edgeLayer = map.getLayerIndex("Edges");

		radDeg = (float) Math.toRadians(movementDegrees);
		
		carCollisionPointBackLeftX = (windowWidth/2) - (float) (Math.cos(radDeg+0.8))*24;
		carCollisionPointBackLeftY = (windowHeight/2) - (float) (Math.sin(radDeg+0.8))*24;
		carCollisionPointBackRightX = (windowWidth/2) - (float) (Math.cos(radDeg-0.8))*24;
		carCollisionPointBackRightY = (windowHeight/2) - (float) (Math.sin(radDeg-0.8))*24;
		carCollisionPointFrontLeftX = (windowWidth/2) + (float) (Math.cos(radDeg+0.35))*50;
		carCollisionPointFrontLeftY = (windowHeight/2) + (float) (Math.sin(radDeg+0.35))*50;
		carCollisionPointFrontRightX = (windowWidth/2) + (float) (Math.cos(radDeg-0.35))*50;
		carCollisionPointFrontRightY = (windowHeight/2) + (float) (Math.sin(radDeg-0.35))*50;
		
		vectorMovement.x = (float) (Math.cos(radDeg))*currentPixelMovementPerUpdate;
		vectorMovement.y = (float) (Math.sin(radDeg))*currentPixelMovementPerUpdate;
		
		positionerX -= vectorMovement.x;
		positionerY -= vectorMovement.y; // -= fordi at det i realiteten er kartet som beveger seg ikke bilen. Bilen beveger seg motsatt av kartet, derfor -.
		
		newMapPosCarCollisionPointBackLeftX = positionerX + carCollisionPointBackLeftX; 
		newMapPosCarCollisionPointBackRightX = positionerX + carCollisionPointBackRightX;
		newMapPosCarCollisionPointFrontLeftX = positionerX + carCollisionPointFrontLeftX;
		newMapPosCarCollisionPointFrontRightX = positionerX + carCollisionPointFrontRightX;
		
		newMapPosCarCollisionPointBackLeftY = positionerY + carCollisionPointBackLeftY;
		newMapPosCarCollisionPointBackRightY = positionerY + carCollisionPointBackRightY;
		newMapPosCarCollisionPointFrontLeftY = positionerY + carCollisionPointFrontLeftY;
		newMapPosCarCollisionPointFrontRightY = positionerY + carCollisionPointFrontRightY;
		
		newTilePosXCollisionPointBackLeft = (int) (newMapPosCarCollisionPointBackLeftX/tileWidth);
		newTilePosYCollisionPointBackLeft = (int) (newMapPosCarCollisionPointBackLeftY/tileHeight);
		newTilePosXCollisionPointBackRight = (int) (newMapPosCarCollisionPointBackRightX/tileWidth);
		newTilePosYCollisionPointBackRight = (int) (newMapPosCarCollisionPointBackRightY/tileHeight);
		
		newTilePosXCollisionPointFrontLeft = (int) (newMapPosCarCollisionPointFrontLeftX/tileWidth);
		newTilePosYCollisionPointFrontLeft = (int) (newMapPosCarCollisionPointFrontLeftY/tileHeight);
		newTilePosXCollisionPointFrontRight = (int) (newMapPosCarCollisionPointFrontRightX/tileWidth);
		newTilePosYCollisionPointFrontRight = (int) (newMapPosCarCollisionPointFrontRightY/tileHeight);
		
		
	if(map.getTileId(newTilePosXCollisionPointFrontLeft,newTilePosYCollisionPointFrontLeft, edgeLayer) == 55){
			
			currentPixelMovementPerUpdate *= 0.1;
			
			
			
		
			
			if( newTilePosXCollisionPointFrontLeft - tilePosXCollisionPointFrontLeft < 0){
			
			}else if(newTilePosXCollisionPointFrontLeft - tilePosXCollisionPointFrontLeft > 0){
				
				
				
			}else{
				tilePosXCollisionPointFrontLeft = newTilePosXCollisionPointFrontLeft;
				mapPosCarCollisionPointFrontLeftX = newMapPosCarCollisionPointFrontLeftX;
			}
			if(newTilePosYCollisionPointBackLeft - tilePosYCollisionPointBackLeft < 0){
				
				/*if(Math.cos(radDeg) > 0 && Math.sin(radDeg) < 0){
					movementDegrees = 5;
				}else if(Math.sin(radDeg) < 0 && Math.cos(radDeg) < 0){
					movementDegrees = 175;
				}*/
			}else if(newTilePosYCollisionPointFrontLeft - tilePosYCollisionPointFrontLeft > 0){
				
				/*if(Math.cos(radDeg) < 0 && Math.sin(radDeg) > 0){
					movementDegrees = -175;
				}else if(Math.sin(radDeg) > 0 && Math.cos(radDeg) > 0){
					movementDegrees = -5;
				}*/
			}else{
				tilePosYCollisionPointFrontLeft = newTilePosYCollisionPointFrontLeft;
				mapPosCarCollisionPointFrontLeftY = newMapPosCarCollisionPointFrontLeftY;
			}
		}else  if(map.getTileId(newTilePosXCollisionPointFrontRight,newTilePosYCollisionPointFrontRight, edgeLayer) == 55){}
		else{
			tilePosXCollisionPointBackLeft = newTilePosXCollisionPointBackLeft;
			tilePosYCollisionPointBackLeft = newTilePosYCollisionPointBackLeft;
			mapPosCarCollisionPointBackLeftY = newMapPosCarCollisionPointBackLeftY;
			mapPosCarCollisionPointBackLeftX = newMapPosCarCollisionPointBackLeftX;
			
			tilePosXCollisionPointFrontLeft = newTilePosXCollisionPointFrontLeft;
			tilePosYCollisionPointFrontLeft = newTilePosYCollisionPointFrontLeft;
			mapPosCarCollisionPointFrontLeftY = newMapPosCarCollisionPointFrontLeftY;
			mapPosCarCollisionPointFrontLeftX = newMapPosCarCollisionPointFrontLeftX;
		}
		
		topLeftCornerMap.x += vectorMovement.x;
		topLeftCornerMap.y += vectorMovement.y;
		
		
		/*if(yDirectionAllowed){
			topLeftCornerMap.y = newTopLeftCornerMap.y;
			currentCarTilePosX = newCarTilePosX;
		}
		if(xDirectionAllowed){
			topLeftCornerMap.x = newTopLeftCornerMap.x;
			currentCarTilePosY = newCarTilePosY;
		}*/
		
		
		/*System.out.println("carTileX: " + newCarTilePosX);
		System.out.println("carTileY: " + newCarTilePosY);*/
	}
	/*public void createCollisionTiles(){
		
		int edgeLayer = map.getLayerIndex("Edges");
		for(int i = 0; i < map.getWidth(); i+=tileWidth){
			for(int j = 0; j < map.getHeight(); j += tileHeight){
				if(map.getTileId(i, j, edgeLayer)==55){
					Rectangle tile = new Rectangle(i, j, tileWidth, tileHeight);
					collisionTiles.add(tile);
				}
			}
		}
	}*/

	public void render(GameContainer container, Graphics g)
			throws SlickException {
		map.render((int)topLeftCornerMap.x, (int)topLeftCornerMap.y, 0,0, tileWidth,tileWidth);
		yDirectionAllowed = true;
		xDirectionAllowed = true;
		//level.render(g);
		p1car.draw(1280/2-16,720/2-32,carSize);
		p1car.setCenterOfRotation(16, 32);
		p1car.setRotation(movementDegrees);
		
		point1.setCenterX(carCollisionPointBackLeftX);
		point1.setCenterY(carCollisionPointBackLeftY);
		point2.setCenterX(carCollisionPointBackRightX);
		point2.setCenterY(carCollisionPointBackRightY);
		point3.setCenterX(carCollisionPointFrontLeftX);
		point3.setCenterY(carCollisionPointFrontLeftY);
		point4.setCenterX(carCollisionPointFrontRightX);
		point4.setCenterY(carCollisionPointFrontRightY);
		
		g.setColor(Color.cyan);
		g.fill(point1);
		g.draw(point1);
		g.setColor(Color.red);
		g.fill(point2);
		g.draw(point2);
		g.setColor(Color.blue);
		g.fill(point3);
		g.draw(point3);
		g.setColor(Color.yellow);
		g.fill(point4);
		g.draw(point4);
		//g.draw(point1);
		 //ttf.drawString(1280/2, 150, "carX: " + carX + " carY: "+ carY);
		 //ttf.drawString(1280/2, 100, "carTilePosx: "+ currentCarTilePosX + " carTilePosY: " + currentCarTilePosY);
		//ttf.drawString(1280/2, 150, "tileBackLeftX: " + tilePosXCollisionPointBackLeft + " tileBackLeftY: "+ tilePosYCollisionPointBackLeft);
		ttf.drawString(1280/2, 150, "tileFrontLeftX: " + tilePosXCollisionPointFrontLeft + " tileFrontLeftY: "+ tilePosYCollisionPointFrontLeft);
		ttf.drawString(1280/2, 100, "vectorX: " + vectorMovement.x); 
		ttf.drawString(1280/2, 75, "vectorY: " + vectorMovement.y);
		ttf.drawString(1280/2, 50, "positionX: " + mapPosCarCollisionPointBackLeftX + " position.y: " + mapPosCarCollisionPointBackLeftY);
	}

	public void reactToControlls(Input input) {
		
		if(input != null){
			usingKeyboard = true;
		}
		if(usingKeyboard){
			reactToKeyboard(input);
		}
		
		/*for(Rectangle collisionTile : collisionTiles){
			if(midpoint.intersects(collisionTile)){
				System.out.println("Collision!");
			}
		}*/
		
		if(throttleKeyIsDown ) {
			if(currentPixelMovementPerUpdate < maxPixelMovementPerUpdate) {
				currentPixelMovementPerUpdate += pixelAccelerationPerUpdate;
			}
		} else{
			if(currentPixelMovementPerUpdate > pixelDeAccelerationPerUpdate) {
				currentPixelMovementPerUpdate -= pixelDeAccelerationPerUpdate;
			}else if(currentPixelMovementPerUpdate > 0){
				currentPixelMovementPerUpdate = 0;
			}
		}
		
		if(reverseKeyIsDown) {
			if(currentPixelMovementPerUpdate > -maxPixelMovementPerUpdate) {
				currentPixelMovementPerUpdate -= pixelDeAccelerationPerUpdate;
			}
		} else{
			if(currentPixelMovementPerUpdate < -pixelDeAccelerationPerUpdate) {
				currentPixelMovementPerUpdate += pixelDeAccelerationPerUpdate;
			}else if(currentPixelMovementPerUpdate < 0){
				currentPixelMovementPerUpdate = 0;
			}
		}
		
		if(currentPixelMovementPerUpdate != 0){
			if(leftKeyIsDown){
				movementDegrees -= angleChangePerUpdate;
			}else if(rightKeyIsDown){
				movementDegrees += angleChangePerUpdate;
			}
		}
	}
	
	public void reactToKeyboard(Input input){
		
		if(input.isKeyDown(Input.KEY_DOWN)) {
            throttleKeyDown();
	    }else {
	    	throttleKeyUp();
	    }
		
		if(input.isKeyDown(Input.KEY_UP)){
			reverseKeyDown();
		}else{
			reverseKeyUp();
		}
		
		if(input.isKeyDown(Input.KEY_LEFT)){
			leftKeyDown();
		}else{
			leftKeyUp();
		}
		
		if(input.isKeyDown(Input.KEY_RIGHT)){
			rightKeyDown();
		}else{
			rightKeyUp();
		}
	}
	
	public void throttleKeyDown() {
		throttleKeyIsDown = true;
	}

	public void leftKeyDown() {
		leftKeyIsDown = true;
	}

	public void rightKeyDown() {
		rightKeyIsDown = true;
	}
	
	public void reverseKeyDown() {
		reverseKeyIsDown = true;
	}

	public void leftKeyUp() {
		leftKeyIsDown = false;
	}

	public void rightKeyUp() {
		rightKeyIsDown = false;
	}
	
	public void reverseKeyUp() {
		reverseKeyIsDown = false;
	}
	
	public void throttleKeyUp() {
		throttleKeyIsDown = false;
	}
	
	public void disableKeyboardInput(){
		usingKeyboard = false;
	}
	
	public void activateKeyboardInput(){
		usingKeyboard = true;
	}
}