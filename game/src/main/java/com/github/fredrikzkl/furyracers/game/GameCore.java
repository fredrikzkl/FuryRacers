package com.github.fredrikzkl.furyracers.game;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Vector2f;

public class GameCore extends BasicGame {

	Image p1car = null;

	public Level level = null;

	boolean throttleKeyIsDown = false;
	boolean leftKeyIsDown = false;
	boolean rightKeyIsDown = false;

	int handling = 1;

	int topSpeed = 2;
	float currentSpeed = 0;
	float acceleration = (float) 0.05;
	float deAcceleration = (float) 0.025;

	float carSize = (float) 0.5;

	int movementDegrees = 0;

	Vector2f position = new Vector2f();
	Vector2f unitCirclePos = new Vector2f();
	
	float radDeg = 0;

	public GameCore(String title) {
		super(title);
	}

	public void init(GameContainer arg0) throws SlickException {

		level = new Level(1);
		//sprite = new SpriteSheet("Sprites/fr_mustang_red.png", 100, 100);

		p1car = new Image("Sprites/fr_mustang_red.png");
	}

	public void update(GameContainer container, int arg1) throws SlickException {

		Input input = container.getInput();

		reactToKeyboardInput(input);

		radDeg = (float) Math.toRadians(movementDegrees);
		
		unitCirclePos.x = (float) (Math.cos(radDeg))*currentSpeed;
		unitCirclePos.y = (float) (Math.sin(radDeg))*currentSpeed;
		
		position.x += unitCirclePos.x;
		position.y += unitCirclePos.y;	
	}

	

	public void render(GameContainer container, Graphics g)
			throws SlickException {
		level.render(g);
		p1car.draw(position.x, position.y,carSize);
		p1car.setCenterOfRotation(32, 32);
		p1car.setRotation(movementDegrees);

	}

	public void throttleKeyDown() {
		throttleKeyIsDown = true;
	}

	public void throttleKeyUp() {
		throttleKeyIsDown = false;
	}

	public void leftKeyDown() {
		leftKeyIsDown = true;
	}

	public void rightKeyDown() {
		rightKeyIsDown = true;
	}

	public void leftKeyUp() {
		leftKeyIsDown = false;
	}

	public void rightKeyUp() {
		rightKeyIsDown = false;
	}

	public void reactToKeyboardInput(Input input) {

		if(throttleKeyIsDown) {
			if(currentSpeed < topSpeed) {
				currentSpeed += acceleration;
			}
		} else {
			if(currentSpeed > 0) {
				currentSpeed -= deAcceleration;
			}else {
				currentSpeed = 0;
			}
		}
		
		if(currentSpeed > 0){
			if(leftKeyIsDown){
				movementDegrees -= handling;
			}else if(rightKeyIsDown){
				movementDegrees += handling;
			}
		}
	}
}