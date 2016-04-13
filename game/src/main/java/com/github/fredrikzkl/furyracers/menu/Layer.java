package com.github.fredrikzkl.furyracers.menu;

import org.newdawn.slick.Image;

import com.github.fredrikzkl.furyracers.Application;

public class Layer {
	
	private Image img;
	private float x,y;
	private float speed;
	private float scaleValue;
	private float SCREENWIDTH = Application.screenSize.width;
	private float outOfFrameX;
	
	 Layer(Image spriteName,int x, int y, float speed){
	
		img = spriteName;
		
		this.x = x;
		this.y = y;
		
		this.speed = speed;
		
		scaleValue = (float)SCREENWIDTH/img.getWidth();
		
		outOfFrameX = - img.getWidth()*scaleValue;
	}
	
	public void draw(){
		img.draw(x,y, scaleValue);
	}
	
	private boolean outOfFrame(float x){
		if(x < outOfFrameX){
			return true;
		}
		return false;
	}
	
	public Image getImg() {
		return img;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		
		if(outOfFrame(x)){
			this.x = SCREENWIDTH;
			return;
		}
		this.x = x;
	}

	public float getSpeed() {
		return speed;
	}
	
}
