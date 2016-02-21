package com.github.fredrikzkl.furyracers.game;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public enum CarProperties {
	
	mustang("mustang", "medium", "Sprites/fr_mustang_red.png", 300,480,100, 105, 75,110,1);
	
	
	public String name;
	public String type;
	public String imageFile;
	public float topSpeed;
	public float reverseTopSpeed;
	public float acceleration;
	public float reverseAcceleration;
	public float deAcceleration;
	public float handling;
	public float weight;
	
	CarProperties(String name, String type, String imageFile, float reverseTopSpeed,float topSpeed,
			float acceleration, float reverseAcceleration, float deAcceleration, float handling, float weight){
		this.name = name;
		this.type = type;
		this.imageFile = imageFile;
		
		this.topSpeed = topSpeed;
		this.reverseTopSpeed = reverseTopSpeed;
		this.reverseAcceleration = reverseAcceleration;
		this.deAcceleration = deAcceleration;
		this.acceleration = acceleration;
		this.handling = handling;
		this.weight = weight;
		
		
	}
	
	
}
