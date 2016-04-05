package com.github.fredrikzkl.furyracers;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class Sprites {
	
	public static Image icons, cars, controllerQR, nextLevelBorder, resultsBoard, highscoresBoard;

	public static void init(){
		
		try {
			icons = new Image("Sprites/menu/menu_sheet.png");
			cars = new Image("Sprites/menu/carSheet.png");
			controllerQR = new Image("QRcode/controllerQR.JPG");
			nextLevelBorder = new Image("Sprites/UI/nextLevelBorder.png");
			resultsBoard = new Image("Sprites/UI/border.png");
			highscoresBoard = new Image("Sprites/UI/border.png");
			
		} catch (RuntimeException e) {
			System.out.println("Sprite/image ERROR: " + e);
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
}
