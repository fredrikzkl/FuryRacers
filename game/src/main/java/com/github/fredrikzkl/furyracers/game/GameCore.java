package com.github.fredrikzkl.furyracers.game;

import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Vector2f;

import com.github.fredrikzkl.furyracers.Application;

public class GameCore extends BasicGame {

	Image p1car = null;
	SpriteSheet sprite;

	public static Camera camera;
	public Level level = null;

	boolean reverseKeyIsDown, throttleKeyIsDown, leftKeyIsDown, rightKeyIsDown, usingKeyboard = false;

	float topSpeed = 480; // pixels per second
	float acceleration = 100; // pixels per second
	float deAcceleration = 75;
	float currentSpeed = 0;
	float handling = 110; // degrees per second
	int angleChangePerUpdate = 1;

	float maxPixelMovementPerUpdate = topSpeed/(float)Application.FPS;

	float turningCircumferance = (360/angleChangePerUpdate) * maxPixelMovementPerUpdate;
	float turningRadius = (float) (turningCircumferance / 2 * Math.PI);
	float carSize = (float) 0.4;
	float movementDegrees = 0;

	Vector2f position = new Vector2f();
	Vector2f unitCirclePos = new Vector2f();
	
	public float zoom = (float) 1; //TODO

	float radDeg = 0;

	private int tilePosX;
	private int tilePosY;

	public GameCore(String title) {
		super(title);
	}

	public void init(GameContainer container) throws SlickException {

		level = new Level(1);
		p1car = new Image("Sprites/fr_mustang_red.png");

		position.x = Application.VIEW_HEIGHT/2; //TODO
		position.y = Application.VIEW_WIDTH/2;//TODO

		camera = new Camera(Application.VIEW_HEIGHT/2,Application.VIEW_WIDTH/2,level);
	}

	public void update(GameContainer container, int deltaTime) throws SlickException {

		Input input = container.getInput();

		reactToControlls(input, deltaTime);

		radDeg = (float) Math.toRadians(movementDegrees);
		
		unitCirclePos.x = (float) (Math.cos(radDeg))*currentSpeed*deltaTime/1000;
		unitCirclePos.y = (float) (Math.sin(radDeg))*currentSpeed*deltaTime/1000;
		
		position.x += unitCirclePos.x;
		position.y += unitCirclePos.y;	
		
		tilePosX = (int) (position.x/16);
		tilePosY = (int) (position.y/16);
		
		camera.update((position.x*zoom - Application.VIEW_WIDTH/2)/zoom, (position.y*zoom - Application.VIEW_HEIGHT/2)/zoom);
	}

	public void render(GameContainer container, Graphics g)
			throws SlickException {

		g.translate(camera.getX()*zoom, camera.getY()*zoom); //Start of camera
		camera.zoom(g,(float) zoom);//Crasher om verdien <=0 
		
		level.render(g, tilePosX, tilePosY );
		p1car.draw(position.x, position.y, carSize);
		p1car.setCenterOfRotation(16, 32); 	//TODO Hard coding:
		p1car.setRotation(movementDegrees);
		
		g.translate(-camera.getX()*zoom, -camera.getY()*zoom); //End of camera
	}

	public void reactToControlls(Input input, int deltaTime) {

		if(input != null){
			usingKeyboard = true;
		}
		if(usingKeyboard){
			reactToKeyboard(input);
		}

		if(throttleKeyIsDown ) {
			if(currentSpeed < topSpeed) {
				currentSpeed += acceleration*deltaTime/1000;
			}
		} else{
			if(currentSpeed > deAcceleration) {
				currentSpeed -= deAcceleration*deltaTime/1000;
			}else if(currentSpeed > 0){
				currentSpeed = 0;
			}
		}

		if(reverseKeyIsDown) {
			if(currentSpeed > -topSpeed) {
				currentSpeed -= deAcceleration*deltaTime/1000;
			}
		} else{
			if(currentSpeed < -acceleration) {
				currentSpeed += deAcceleration*deltaTime/1000;
			}else if(currentSpeed < 0){
				currentSpeed = 0;
			}
		}

		if(currentSpeed != 0){
			if(leftKeyIsDown){
				movementDegrees -= handling*deltaTime/1000;
			}else if(rightKeyIsDown){
				movementDegrees += handling*deltaTime/1000;
			}
		}
	}

	public void reactToKeyboard(Input input){

		if(input.isKeyDown(Input.KEY_UP)) {
            throttleKeyDown();
	    }else {
	    	throttleKeyUp();
	    }

		if(input.isKeyDown(Input.KEY_DOWN)){
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

	public float getZoom() {
		return zoom;
	}

	public void setZoom(float zoom) {
		this.zoom = zoom;
	}
	
	
	
}
