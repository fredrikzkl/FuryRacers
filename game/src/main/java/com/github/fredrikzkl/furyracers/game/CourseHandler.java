package com.github.fredrikzkl.furyracers.game;

import java.io.File;
import java.nio.file.DirectoryStream;
import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Image;

public class CourseHandler {
	
	private String directory;
	private File f;
	private int level;
	private int layers;
	
	private List<Image> mapImages;
	
	public CourseHandler(int level){
		this.level = level;
		directory  = "/Maps/course" + level + "/";
		f = new File(directory);
		mapImages = new ArrayList<Image>();
		
		
		//layers = amountOfLayers(f);
		System.out.println(directory);
		System.out.println("LAYERS: " + layers);
		
		
	}
	
	private int amountOfLayers(File f){
		return f.listFiles().length;
	}
	
}
