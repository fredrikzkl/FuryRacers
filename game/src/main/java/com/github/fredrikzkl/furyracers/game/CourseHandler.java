package com.github.fredrikzkl.furyracers.game;

import java.io.File;
import java.nio.file.DirectoryStream;
import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Image;
import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.tiled.TiledMap;

public class CourseHandler {
	
	private String directory;

	private int level;
	
	public Image subLayer, topLayer;
	public Music soundTrack;
	public TiledMap data;
	
	public CourseHandler(int level){
		this.level = level;
		directory  = "/Maps/course" + level + "/";
		importAssets();
		if(subLayer != null && topLayer != null && soundTrack != null &&  data != null){
			System.out.println("Course" + level + " sucsessfully loaded!");
		}
	}

	private void importAssets() {
		try {
			subLayer = new Image(directory + "1.png");
			topLayer = new Image(directory + "2.png");
			soundTrack = new Music(directory + "soundTrack.ogg");
			data = new TiledMap(directory + "data.tmx");
		} catch (SlickException e) {
			System.out.println("Could not load level " + level + "properly! Jumping to next map..." + e);
			subLayer = null;
			topLayer = null;
			soundTrack = null;
			data = null;
		}
		
	}

	
	
	
	
}
