package com.github.fredrikzkl.furyracers.game;

import java.util.ArrayList;
import java.util.List;

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
	private List<Vector2f> check1,check2,check3;
	
	
	public Level(int id) {
		
		path = Dpath + id + ".tmx";
		System.out.println("Course loaded:" + path.substring(5));
		
		try {
			map = new TiledMap(path);
		} catch (SlickException e) {
			System.out.println("Error loading map");
		}
		//TODO
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
		//---------------------------
		
		determineStartPosition();
		check1 = new ArrayList<Vector2f>();
		check2 = new ArrayList<Vector2f>();
		check3 = new ArrayList<Vector2f>();
		determineCheckpoints();
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
	
	private void determineCheckpoints(){
		for(int x = 0; x<map.getWidth();x++){
			for(int y = 0/tileHeight; y<map.getHeight(); y++){
				
				if(map.getTileProperty(map.getTileId(x, y, 1), "checkpoint", "-1").equals("1")) {
					check1.add(new Vector2f(x*tileWidth,y*tileHeight));
				}
				if(map.getTileProperty(map.getTileId(x, y, 1), "checkpoint", "-1").equals("2")) {
					check1.add(new Vector2f(x*tileWidth,y*tileHeight));
				}
				if(map.getTileProperty(map.getTileId(x, y, 1), "checkpoint", "-1").equals("3")) {
					check1.add(new Vector2f(x*tileWidth,y*tileHeight));
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
	

	public List<Vector2f> getCheck1() {
		return check1;
	}

	
	public List<Vector2f> getCheck2() {
		return check2;
	}

	
	public List<Vector2f> getCheck3() {
		return check3;
	}
	
	
	
}
