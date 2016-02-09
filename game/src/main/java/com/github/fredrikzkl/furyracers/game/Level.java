package com.github.fredrikzkl.furyracers.game;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.tiled.TiledMap;
import com.github.fredrikzkl.furyracers.Application;

public class Level {

	public final String Dpath = "Maps/a";
	public String path = Dpath;

	public TiledMap map = null;
	public int tileWidth;
	public int tileHeight;
	int edgeLayer;
	
	public Level(int id) {
		
		path = Dpath + id + ".tmx";
		System.out.println(path);
		
		try {
			map = new TiledMap(path);
		} catch (SlickException e) {
			System.out.println("Error loading map");
		}
		
		int edgeLayer = map.getLayerIndex("Edges");
		tileWidth = map.getTileWidth();
		tileHeight = map.getTileHeight();
	}

	public void render(Graphics g, int tileX, int tileY) {
		
		map.render(0,0, 0, 0, (int)Application.screenSize.getWidth()/map.getTileWidth()+tileX,
				(int)Application.screenSize.getHeight()/map.getTileHeight()+tileY);
	}
	
	public int getTileWidth() {
		return tileWidth;
	}

	public int getTileHeight() {
		return tileHeight;
	}
	
	public int getTileType()

}
