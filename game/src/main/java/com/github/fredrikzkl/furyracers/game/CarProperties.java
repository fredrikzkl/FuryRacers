package com.github.fredrikzkl.furyracers.game;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public enum CarProperties {
	
	//Mustang
	mustangR("mustang", "medium", "Sprites/fr_mustang_red.png",0.4f, 300,480,100, 105, 75,110,1), // 0
	mustangB("mustang", "medium", "Sprites/fr_mustang_blue.png",0.4f, 300,480,100, 105, 75,110,1), // 1
	mustangG("mustang", "medium", "Sprites/fr_mustang_green.png",0.4f, 300,480,100, 105, 75,110,1), // 2
	mustangY("mustang", "medium", "Sprites/fr_mustang_yellow.png",0.4f, 300,480,100, 105, 75,110,1), // 3
	//Camaro
	CamaroR("camaro", "medium", "Sprites/fr_camaro_red.png",0.45f, 300,480,100, 105, 75,110,1), // 4
	CamaroB("camaro", "medium", "Sprites/fr_camaro_blue.png",0.45f, 300,480,100, 105, 75,110,1), // 5
	CamaroG("camaro", "medium", "Sprites/fr_camaro_green.png",0.45f, 300,480,100, 105, 75,110,1), // 6
	CamaroY("camaro", "medium", "Sprites/fr_camaro_yellow.png",0.45f, 300,480,100, 105, 75,110,1), // 7
	//VelociRaptor
	VRaptorR("velociRapor", "large", "Sprites/fr_pickup_red.png",0.65f, 350,480,70, 100, 80,110,1),
	VRaptorB("velociRapor", "large", "Sprites/fr_pickup_blue.png",0.65f, 350,480,70, 100, 80,110,1),
	VRaptorG("velociRapor", "large", "Sprites/fr_pickup_green.png",0.65f, 350,480,70, 100, 80,110,1),
	VRaptorY("velociRapor", "large", "Sprites/fr_pickup_yellow.png",0.65f, 350,480,70, 100, 80,110,1);
	
	public String name;
	public String type;
	public String imageFile;
	
	public float carSize;
	
	public float topSpeed;
	public float reverseTopSpeed;
	public float acceleration;
	public float reverseAcceleration;
	public float deAcceleration;
	public float handling;
	public float weight;
	
	CarProperties(String name, String type, String imageFile, float size, float reverseTopSpeed,float topSpeed,
			float acceleration, float reverseAcceleration, float deAcceleration, float handling, float weight){
		this.name = name;
		this.type = type;
		this.imageFile = imageFile;
		this.carSize = size;
		this.topSpeed = topSpeed;
		this.reverseTopSpeed = reverseTopSpeed;
		this.reverseAcceleration = reverseAcceleration;
		this.deAcceleration = deAcceleration;
		this.acceleration = acceleration;
		this.handling = handling;
		this.weight = weight;
		
		
	}
	
	
}
