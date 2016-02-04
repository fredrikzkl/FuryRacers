package com.github.fredrikzkl.furyracers.game;

public class Camera {
	
	private float x,y;
	
	public Camera(float startX, float startY){
		x = startX;
		y= startY;
	}
	
	public void update(float posX, float posY) {
		x = -posX;
		y = -posY;
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

	
	
	
}

