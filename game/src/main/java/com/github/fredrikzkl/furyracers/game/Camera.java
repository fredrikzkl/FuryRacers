package com.github.fredrikzkl.furyracers.game;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Vector2f;

import com.github.fredrikzkl.furyracers.Application;

public class Camera {
	
	private float x,y;
	private Level level;
	private GameCore game;
	public float initialZoom = (float) 1; //TODO
	public float zoom = (float) 1;
	private Vector2f size;
	float edgeX, edgeY;
	float biggest = 0;
	Vector2f deltaDistances = new Vector2f();
	Vector2f closestEdge = new Vector2f();
	
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
		size.x = Application.screenSize.width / zoom;
		size.y = Application.screenSize.height / zoom;
		
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
	
	public void zoomLogic() {
		
		float deltaX = (float) (deltaDistances.x/(Application.screenSize.width/1.98)); //Høyere deleverdi gir mindre margin
		float deltaY = (float) (deltaDistances.y/(Application.screenSize.height/1.92)); 
		float temp = zoom;
		boolean zoomLim = true;
		
		
		if(deltaY>deltaX)
			biggest=deltaY;
		else
			biggest=deltaX;
		
		temp = initialZoom /(biggest);
		
		if(getSize().x - getX() >= level.distanceWidth && zoom>temp && getX()>=0 || 
		   getSize().y - getY() >= level.distanceHeight && zoom>temp && getY()>=0)
			zoomLim = false;
		
		if(zoomLim){
			if(temp > initialZoom){
				zoom = (float) initialZoom;
			}else{
				zoom = temp;
			}
		}
	}
	
	public void updateCamCoordinates(){
		
		float x = ((deltaDistances.x/2+closestEdge.x)-Application.screenSize.width/2)*zoom;
		float y = ((deltaDistances.y/2+closestEdge.y)-Application.screenSize.height/2)*zoom;
		
		update(x,y);
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}


	public void zoom(Graphics g, float zoom) {
		g.scale(zoom, zoom);
	}

	public Vector2f getSize() {
		return size;
	}
	
	public void setInitialZoom(float initialZoom) {
		this.initialZoom = initialZoom;
	}

	public void setZoom(float zoom) {
		this.zoom = zoom;
	}
	
	public float getZoom(){
		return zoom;
	}
	
	public void setDeltaDistances(Vector2f deltaDistances){
		
		this.deltaDistances = deltaDistances;
	}
	
	public void setClosestEdge(Vector2f closestEdge){
		
		this.closestEdge = closestEdge;
	}
}

