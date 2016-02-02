package com.github.fredrikzkl.furyracers.game;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import org.newdawn.slick.Image;

public class Tile {
	
	public static int[] blank = {-1,-1};
	
	public static int size = 32;


	public static BufferedImage background, road, offroad;
	
	public Tile(){
		try{
			Tile.background = ImageIO.read(new File("bg.png"));
		}catch(Exception e){
			System.out.println("Error loading image for background");
		}
		
	}
}
