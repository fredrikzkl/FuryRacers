package com.github.fredrikzkl.furyracers.game;

import org.newdawn.slick.Graphics;

import com.github.fredrikzkl.furyracers.Application;




public class Camera {
	
	private float x,y;
	private Level level;
	
	float edgeX, edgeY;
	
	public Camera(float startX, float startY, Level level){
		this.level = level;
		x = startX;
		y= startY;
		
		
		
		edgeX = (float) (level.map.getWidth()*level.map.getTileWidth() - Application.screenSize.getWidth());
		edgeY = (float) (level.map.getHeight()*level.map.getTileHeight() - Application.screenSize.getHeight());
	}
	
	public void update(float posX, float posY) {
		x = -posX;
		y = -posY;
		
		
		if(posX<0)
			x = 0;
		
		if(posY <0)
			y = 0;
		
		if(-x > edgeX)
			x = -(edgeX);
		
		if(-y > edgeY)
			y = -(edgeY);
		
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}


	public void zoom(Graphics g, float i) {
		g.scale(i, i);
	}

	
	
	
}

