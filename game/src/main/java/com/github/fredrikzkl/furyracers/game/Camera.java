package com.github.fredrikzkl.furyracers.game;

import org.newdawn.slick.Graphics;




public class Camera {
	
	private float x,y;
	private Level level;
	
	public Camera(float startX, float startY, Level level){
		this.level = level;
		x = startX;
		y= startY;
	}
	
	public void update(float posX, float posY) {
		x = -posX;
		y = -posY;
		
		if(posX<0)
			x = 0;
		
		if(posY <0)
			y = 0;
		
	
		//level.map.getWidth()*level.map.getTileWidth()
		
	
		
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

	public void zoom(Graphics g, float i) {
		g.scale(i, i);
	}

	
	
	
}

