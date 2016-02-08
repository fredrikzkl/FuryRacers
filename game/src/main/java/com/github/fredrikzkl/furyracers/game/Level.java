package com.github.fredrikzkl.furyracers.game;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.tiled.TiledMap;

import com.github.fredrikzkl.furyracers.Application;

public class Level {

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
		
		
	}


	public void render(Graphics g, int tileX, int tileY) {
		
		map.render(0,0, 0, 0, (int)Application.screenSize.getWidth()/map.getTileWidth()+tileX,
				(int)Application.screenSize.getHeight()/map.getTileHeight()+tileY);
	
	}

}
