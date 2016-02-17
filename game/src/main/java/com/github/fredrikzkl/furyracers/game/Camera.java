package com.github.fredrikzkl.furyracers.game;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Vector2f;

import com.github.fredrikzkl.furyracers.Application;

public class Camera {
	
	private float x,y;
	private Level level;
	private GameCore game;
	
	private Vector2f size;
	
	float edgeX, edgeY;
	
	public Camera(float startX, float startY, Level level, GameCore game){
		this.level = level;
		this.game = game;
		x = startX;
		y= startY;
		
		size = new Vector2f();
		
		edgeX = (float) (level.map.getWidth()*level.map.getTileWidth() - Application.screenSize.getWidth());
		edgeY = (float) (level.map.getHeight()*level.map.getTileHeight() - Application.screenSize.getHeight());
	}
	
	public void update(float posX, float posY) {
		size.x = Application.screenSize.width / game.getZoom();
		size.y = Application.screenSize.height / game.getZoom();
		
		x = -posX;
		y = -posY;
		
		if(posX<0)
			x = 0;
		
		if(posY <0)
			y = 0;
		
		if(size.x + posX > level.distanceWidth){
			x = -(level.distanceWidth - size.x);
		}
		
		if(size.y + posY > level.distanceHeight){
			y = -(level.distanceHeight - size.y);
		}
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

	public Vector2f getSize() {
		return size;
	}
	
	
}

