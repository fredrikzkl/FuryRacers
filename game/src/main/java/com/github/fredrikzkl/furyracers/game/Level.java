package com.github.fredrikzkl.furyracers.game;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.tiled.TiledMap;

import com.github.fredrikzkl.furyracers.Application;

public class Level {

	public final String Dpath = "Maps/a";
	public String path = Dpath;

	public TiledMap map = null;
	public int tileWidth;
	public int tileHeight;
	int edgeLayer;
	int mapWidth;
	int mapHeight;
	
	float distanceWidth;
	float distanceHeight;
	
	private Vector2f startCoordinates;
	
	public Level(int id) {
		
		path = Dpath + id + ".tmx";
		System.out.println("Course loaded:" + path.substring(5));
		
		try {
			map = new TiledMap(path);
		} catch (SlickException e) {
			System.out.println("Error loading map");
		}
		
		edgeLayer = map.getLayerIndex("road");
		tileWidth = map.getTileWidth();
		tileHeight = map.getTileHeight();
		mapWidth = map.getWidth();
		mapHeight = map.getHeight();
		
		/*for(int i = 0; i < mapWidth; i++){
			for(int j = 0; j < mapHeight; j++){
				map.setTileId(i,j,edgeLayer,777);
			}
		}*/
		
		distanceHeight = map.getTileHeight() * map.getHeight();
		distanceWidth = map.getTileWidth() * map.getWidth();
		
	

		for(int x = 0; x<map.getWidth();x++){
			for(int y = 0/tileHeight; y<map.getHeight(); y++){
				if(map.getTileProperty(map.getTileId(x, y, 1), "startPos", "-1").equals("start")) {
					startCoordinates = new Vector2f(x*tileWidth,y*tileHeight);
				}
			}
		}
		
		

			
	}

	public void render(Graphics g, Vector2f tilePos) {
		int tilePosX = (int) tilePos.x;
		int tilePosY = (int) tilePos.y;
		map.render(0,0, 0, 0, (int)Application.screenSize.getWidth()/map.getTileWidth() + tilePosX,
				(int)Application.screenSize.getHeight()/map.getTileHeight() + tilePosY);
	}
	
	public int getTileWidth() {
		return tileWidth;
	}

	public int getTileHeight() {
		return tileHeight;
	}
	
	public boolean getTileType(int xTile, int yTile){
		int tileID = map.getTileId(xTile, yTile, edgeLayer);
			
		if(tileID != 0){
			return false;
		}
		return true;
	}

	public float getDistanceWidth() {
		return distanceWidth;
	}

	public float getDistanceHeight() {
		return distanceHeight;
	}
	

	public Vector2f getStartCoordinates() {
		return startCoordinates;
	}
	
	
	
}
