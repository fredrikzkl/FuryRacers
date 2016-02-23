package com.github.fredrikzkl.furyracers;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class Layer {
	private String path = "/Sprites/background/";
	private Image img;
	private float x,y;
	private float speed;
	
	private float gap;
	
	public Layer(String fileName,int x, int y, float speed){
		try {
			img = new Image(path + fileName + ".png");
		} catch (SlickException e) {
			e.printStackTrace();
		}
		
		this.x = x;
		this.y = y;
		
		this.speed = speed;
	}
	
	
	
	public void draw(float scalingValue){
		img.draw(x,y,scalingValue);
		float imgX = img.getWidth()*scalingValue;
		float imgY = img.getHeight()*scalingValue;
		cropLoop(imgX,imgY).draw(0-imgX,0);;
	}
	
	public void outOfFrame(){
		/*
		if(getX() < (0 - Application.screenSize.width)){
			setX(Application.screenSize.width-gap);
		}
		*/
		if(getX() < (0 - Application.screenSize.width)){
			
		}
	}
	
	
	public Image cropLoop(float height, float width){
		int deltaX = 0-Application.screenSize.width;
		Image subIma = img.getSubImage(deltaX, (int)height, (int)width, (int)height);
		return subIma;
	}

	public Image getImg() {
		return img;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getSpeed() {
		return speed;
	}
	
	
}
