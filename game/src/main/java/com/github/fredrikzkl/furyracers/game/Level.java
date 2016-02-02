package com.github.fredrikzkl.furyracers.game;



import java.awt.Rectangle;

import org.newdawn.slick.SlickException;

import org.newdawn.slick.tiled.TiledMap;

import com.github.fredrikzkl.furyracers.Application;

public class Level {
	
	public Background[][] bg = new Background[Application.HEIGHT][Application.WIDTH];
	
	public final String Dpath = "Maps/testMap";
	public String path = Dpath;
	
	public TiledMap map = null;
	
	public Level (int id){
		path = Dpath  + Integer.toString(id) + ".tmx";
		System.out.println(path);
		
		try{
			map = new TiledMap(path,false);
		}catch(SlickException e){
			System.out.println("Error loading map");
		}
		
		for(int x=0; x < bg.length ;x++){
			for(int y = 0 ; y<bg[0].length; y++){
				bg[x][y]=new Background(new Rectangle(
						x*Tile.size,
						y*Tile.size,
						Tile.size,
						Tile.size),
						Tile.blank);
			}
		}
	}
	
	public void loadCourse(){
		int background = map.getLayerIndex("background");
		int solids = map.getLayerIndex("solids");
		
		for(int x=0; x < bg.length ;x++){
			for(int y = 0 ; y<bg[0].length; y++){
				
				if(map.getTileId(x, y, background) == 1){
					bg[x][y].id = Tile.road;
				}
					
			}
		}
	}
	
	
	
}
