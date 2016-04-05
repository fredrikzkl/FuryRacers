package com.github.fredrikzkl.furyracers.game;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.newdawn.slick.Image;
import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.tiled.TiledMap;

public class CourseHandler {
	
	private String directory;

	private int level;
	
	public Image subLayer, topLayer, minimap;
	public Music soundTrack;
	public TiledMap data;
	public String mapName;
	
	private File info;
	private BufferedReader br;
	
	public CourseHandler(int level){
		this.level = level;
		directory  = "Maps/course" + level + "/";
		importAssets();
		
		mapName = readTxtFile(info);
		
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
			info = new File (directory + "info.txt");
			minimap = new Image(directory + "minimap.png");
		} catch (SlickException e) {
			System.out.println("Could not load level " + level + " properly! Jumping to next map..." + e);
			subLayer = null;
			topLayer = null;
			soundTrack = null;
			data = null;
			info = null;
			minimap = null;
		}
		
	}

	private String readTxtFile(File file){
		String name = "FuryRacers Course";
		
		try {
			br = new BufferedReader(new FileReader(info));
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();
			
			while(line != null){
				sb.append(line);
				sb.append(System.lineSeparator());
		        line = br.readLine();
			}
			name = sb.toString();
			br.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return name;
	}
	
	
	
}
