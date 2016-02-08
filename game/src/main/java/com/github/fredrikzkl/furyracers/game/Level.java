package com.github.fredrikzkl.furyracers.game;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.tiled.TiledMap;

import com.github.fredrikzkl.furyracers.Application;

public class Level {

	public Background[][] bg;
	public Road[][] road;
	public Background[][] solid;

	public final String Dpath = "Maps/a";
	public String path = Dpath;

	public TiledMap map = null;

	public Level(int id) {
		path = Dpath + id + ".tmx";
		System.out.println(path);
		
		
		try {
			map = new TiledMap(path);
		} catch (SlickException e) {
			System.out.println("Error loading map");
		}
		
		
		/*
		bg = new Background[map.getWidth()][map.getHeight()];
		road = new Road[map.getWidth()][map.getHeight()];
		Tile.init();
		loadCourse();
		*/
	}

	public void loadCourse() {
		
		int background = map.getLayerIndex("background");
		int roads = map.getLayerIndex("road");
		int solids = map.getLayerIndex("solids");
		
	
		for (int x = 0; x < bg.length; x++) {
			
			for (int y = 0; y < bg[0].length; y++) {
				
				bg[x][y] = new Background(x, y, map.getTileId(x, y, background));
			}
		}
		
		
		/*
		for (int x = 0; x < road.length; x++) {
			for (int y = 0; y < road[0].length; y++) {
				road[x][y] = new Road(x, y, map.getTileId(x, y, roads));
			}
		}

		for (int x = 0; x < bg.length; x++) {
			for (int y = 0; y < bg[0].length; y++) {
				solid[x][y] = new Background(x, y, map.getTileId(x, y, solids));
			}
		}
		*/
	}

	public void render(Graphics g, int tileX, int tileY) {
		
		map.render(0,0, 0, 0, (int)Application.screenSize.getWidth()/map.getTileWidth()+tileX,
				(int)Application.screenSize.getHeight()/map.getTileHeight()+tileY);
		
		/*
		for (int x = 0; x < bg.length; x++) {
			for (int y = 0; y < bg[0].length; y++) {
				bg[x][y].render(g);
			}
		}
		*/
	}

}
