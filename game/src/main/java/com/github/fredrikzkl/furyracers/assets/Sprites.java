package com.github.fredrikzkl.furyracers.assets;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class Sprites {
	
	public static Image 
	icons, cars, controllerQR, nextLevelBorder, resultsBoard, highscoresBoard, 
	mustangR, mustangB, mustangG, mustangY, 
	camaroR, camaroB, camaroG, camaroY, 
	vRaptorR, vRaptorB, vRaptorG, vRaptorY;
	
	
	public static void init(){
		
		try {
			icons = new Image("Sprites/menu/menu_sheet.png");
			cars = new Image("Sprites/menu/carSheet.png");
			controllerQR = new Image("QRcode/controllerQR.JPG");
			nextLevelBorder = new Image("Sprites/UI/nextLevelBorder.png");
			resultsBoard = new Image("Sprites/UI/border.png");
			highscoresBoard = new Image("Sprites/UI/border.png");
			
			mustangR = new Image("Sprites/cars/fr_mustang_red.png");
			mustangG = new Image("Sprites/cars/fr_mustang_green.png");
			mustangB = new Image("Sprites/cars/fr_mustang_blue.png");
			mustangY = new Image("Sprites/cars/fr_mustang_yellow.png");
			
			camaroR = new Image("Sprites/cars/fr_camaro_red.png");
			camaroG = new Image("Sprites/cars/fr_camaro_green.png");
			camaroB = new Image("Sprites/cars/fr_camaro_blue.png");
			camaroY = new Image("Sprites/cars/fr_camaro_yellow.png");
			
			vRaptorR = new Image("Sprites/cars/fr_pickup_red.png");
			vRaptorG = new Image("Sprites/cars/fr_pickup_green.png");
			vRaptorB = new Image("Sprites/cars/fr_pickup_blue.png");
			vRaptorY = new Image("Sprites/cars/fr_pickup_yellow.png");
			
		} catch (RuntimeException e) {
			System.out.println("Sprite/image ERROR: " + e);
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
}
