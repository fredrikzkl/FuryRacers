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
	int roadLayer;
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
		//TODO
		roadLayer = map.getLayerIndex("road");
		tileWidth = map.getTileWidth();
		tileHeight = map.getTileHeight();
		mapWidth = map.getWidth();
		mapHeight = map.getHeight();

		distanceHeight = map.getTileHeight() * map.getHeight();
		distanceWidth = map.getTileWidth() * map.getWidth();
		//---------------------------
		
		determineStartPosition();
		
	}

	private void determineStartPosition() {
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
	
	public int getTileType(int xTile, int yTile, int passedCheckpoints){
		int tileID = map.getTileId(xTile, yTile, roadLayer);
			
		if(tileID == 0){
			return 0;
		}
		
		if(map.getTileProperty(tileID, "checkpoint", "-1").equals("1") && passedCheckpoints == 0) {
			return 1;
		}else if(map.getTileProperty(tileID, "checkpoint", "-1").equals("2") && passedCheckpoints == 1) {
			return 2;
		}else if(map.getTileProperty(tileID, "checkpoint", "-1").equals("3") && passedCheckpoints == 2) {
			return 3;
		}else if(map.getTileProperty(tileID, "goal", "-1").equals("finish") && passedCheckpoints == 3) {
			return 4;
		}
		return 5;
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
