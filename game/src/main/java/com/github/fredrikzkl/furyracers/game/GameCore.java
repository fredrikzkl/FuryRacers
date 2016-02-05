package com.github.fredrikzkl.furyracers.game;

import java.awt.event.KeyEvent;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.tiled.TiledMap;

import com.github.fredrikzkl.furyracers.Application;

public class GameCore extends BasicGame {

	Image p1car = null;
	
	TiledMap map;

	public Level level = null;

	boolean reverseKeyIsDown, throttleKeyIsDown, leftKeyIsDown, rightKeyIsDown, usingKeyboard = false;
	
	int topSpeed = 520; // pixels per second
	float acceleration = 320; // pixels per second^2
	float deAcceleration = (float) 300;
	int currentSpeed = 0;
	int angleChangePerUpdate = 1;

	float maxPixelMovementPerUpdate = topSpeed/(float)Application.FPS;
	float currentPixelMovementPerUpdate = currentSpeed/(float)Application.FPS;
	float pixelAccelerationPerUpdate = acceleration/(float)Math.pow(Application.FPS,2);
	float pixelDeAccelerationPerUpdate = deAcceleration/(float)Math.pow(Application.FPS,2);

	float turningCircumferance = (360/angleChangePerUpdate) * maxPixelMovementPerUpdate;
	float turningRadius = (float) (turningCircumferance / 2 * Math.PI);
	float carSize = (float) 0.5;
	int movementDegrees = 0;

	Vector2f position = new Vector2f();
	Vector2f unitCirclePos = new Vector2f();
	
	float radDeg = 0;

	public GameCore(String title) {
		super(title);
	}

	public void init(GameContainer arg0) throws SlickException {

		//level = new Level(1);
		//sprite = new SpriteSheet("Sprites/fr_mustang_red.png", 100, 100);
		map = new TiledMap("Maps/go.tmx");
		p1car = new Image("Sprites//fr_mustang_red.png");
	}

	public void update(GameContainer container, int arg1) throws SlickException {

		int edgeLayer = map.getLayerIndex("Edges");
		map.getTileId(1, 2, edgeLayer);
		
		Input input = container.getInput();

		reactToControlls(input);

		radDeg = (float) Math.toRadians(movementDegrees);
		
		unitCirclePos.x = (float) (Math.cos(radDeg))*currentPixelMovementPerUpdate;
		unitCirclePos.y = (float) (Math.sin(radDeg))*currentPixelMovementPerUpdate;
		
		position.x += unitCirclePos.x;
		position.y += unitCirclePos.y;	
	}

	public void render(GameContainer container, Graphics g)
			throws SlickException {
		map.render((int)position.x, (int)position.y, 0,0, 20, 20);
		//level.render(g);
		p1car.draw(640,360 ,carSize);
		p1car.setCenterOfRotation(16, 32);
		p1car.setRotation(movementDegrees);
	}

	public void reactToControlls(Input input) {
		
		if(input != null){
			usingKeyboard = true;
		}
		if(usingKeyboard){
			reactToKeyboard(input);
		}
		
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