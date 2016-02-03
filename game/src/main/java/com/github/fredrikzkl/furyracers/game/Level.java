package com.github.fredrikzkl.furyracers.game;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.tiled.TiledMap;

import com.github.fredrikzkl.furyracers.Application;

public class Level {

	public Background[][] bg;
	public Background[][] road;
	public Background[][] solid;

	public final String Dpath = "Maps/testMap";
	public String path = Dpath;

	public TiledMap map = null;

	public Level(int id) {
		path = Dpath + id + ".tmx";
		System.out.println(path);

		try {
			map = new TiledMap(path, false);
		} catch (SlickException e) {
			System.out.println("Error loading map");
		}
		
		bg = new Background[map.getWidth()][map.getHeight()];
		Tile.init();
		loadCourse();

	}

	public void loadCourse() {
		int background = map.getLayerIndex("background");
		int roads = map.getLayerIndex("roads");
		int solids = map.getLayerIndex("solids");

		for (int x = 0; x < bg.length; x++) {
			for (int y = 0; y < bg[0].length; y++) {
				bg[x][y] = new Background(x, y, map.getTileId(x, y, background));
			}
		}
		/*
		for (int x = 0; x < bg.length; x++) {
			for (int y = 0; y < bg[0].length; y++) {
				road[x][y] = new Background(x, y, map.getTileId(x, y, roads));
			}
		}

		for (int x = 0; x < bg.length; x++) {
			for (int y = 0; y < bg[0].length; y++) {
				solid[x][y] = new Background(x, y, map.getTileId(x, y, solids));
			}
		}
		*/
	}

	public void render(Graphics g) {
		for (int x = 0; x < bg.length; x++) {
			for (int y = 0; y < bg[0].length; y++) {
				bg[x][y].render(g);
			}
		}
	}

}
