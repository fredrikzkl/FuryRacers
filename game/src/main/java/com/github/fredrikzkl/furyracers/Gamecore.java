package com.github.fredrikzkl.furyracers;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

public class GameCore extends BasicGame {

	Image carSprites = null;
	SpriteSheet sprite;
	
	int handling = 2;
	
	int topSpeed = 5;
	
	float speed = 0;
	float acceleration = (float) 0.1;
	float deAcceleration = (float) 0.05;
	
	float carSize = (float) 0.5;
	
	float movementDegrees = 0;
	
	float xPos = 140;
	float yPos = 140;
	
	public GameCore(String title) {
		super(title);
	}
	
	public void init(GameContainer arg0) throws SlickException {
		sprite = new SpriteSheet("Sprites/car.png", 100, 100);
		carSprites = new Image("Sprites/car.png");
	}
	
	public void update(GameContainer container, int arg1) throws SlickException {
		Input input = container.getInput();
		
		if(input.isKeyDown(Input.KEY_SPACE)){
			if(speed<topSpeed){
				speed += acceleration; 
			}
		}else{
			if(speed>0){
				speed -= deAcceleration;
			}
			else{
				speed = 0;
			}
		}
	
		if(input.isKeyDown(Input.KEY_RIGHT)){
			movementDegrees -= handling;
		}
		if(input.isKeyDown(Input.KEY_LEFT)){
			movementDegrees += handling;
		}
		
		float radDeg = (float) Math.toRadians(movementDegrees);

		float deltaX = (float) (Math.cos(radDeg))*speed;
		float deltaY = (float) (Math.sin(radDeg))*speed;
		
		xPos += deltaX;
		yPos += deltaY;	
	}

	public void render(GameContainer container, Graphics g) throws SlickException {
		carSprites.getSubImage(1,0,140,140).draw(xPos, yPos, carSize);
	}

	public static void main(String[] args) throws SlickException{
		AppGameContainer app = new AppGameContainer(new GameCore("Test"));
		
		app.setDisplayMode(1280, 720, false);
		app.setAlwaysRender(true);
		app.setTargetFrameRate(60);
		app.start();
	}
}