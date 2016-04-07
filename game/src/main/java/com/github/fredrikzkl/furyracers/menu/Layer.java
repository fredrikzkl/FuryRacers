package com.github.fredrikzkl.furyracers.menu;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import com.github.fredrikzkl.furyracers.Application;

public class Layer {
	private String path = "/Sprites/background/";
	private Image img;
	private float x,y;
	private float speed;
	
	
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
	}
	
	public void outOfFrame(float scalingValue){
		if(getX()+img.getWidth()*scalingValue < 0){
			setX(x + Application.screenSize.width*2);
		}
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
