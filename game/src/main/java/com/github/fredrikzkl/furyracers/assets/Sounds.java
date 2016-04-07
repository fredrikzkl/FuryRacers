package com.github.fredrikzkl.furyracers.assets;

import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;

public class Sounds {
	
	public static Music music;
	public static Sound car_select;
	public static Sound select_car;
	public static Sound spray;
	public static Sound playerJoin;
	public static Sound playerReady;
	public static Sound deSelect;
	public static Sound peep;
	public static Sound getReady;
	public static Sound three, two, one, go;
	public static Sound 
	finalRound, crowdFinish,
	checkpoint, lap, still,
	topSpeed;
	public static Sound scoreboardClose;
	public static Sound scoreboardMove;
	
	public static void initialize(){
		
		try {
			String path = "Sound/";
			car_select = new Sound(path + "car_select.ogg");
			select_car = new Sound(path + "select_car.ogg");
			spray = new Sound(path + "spray.ogg");
			playerJoin = new Sound(path + "playerJoin.ogg");
			playerReady = new Sound(path + "ready.ogg");
			deSelect = new Sound(path + "deselect.ogg");
			peep = new Sound(path + "countdown.ogg");
			getReady = new Sound(path + "announcer/getReady.ogg");
			three = new Sound(path + "announcer/three.ogg");
			two = new Sound(path + "announcer/two.ogg");
			one = new Sound(path + "announcer/one.ogg");
			go = new Sound(path + "announcer/race!.ogg");
			finalRound = new Sound(path +"announcer/finalRound.ogg");
			crowdFinish = new Sound(path +"crowdFinish.ogg");
			checkpoint = new Sound(path +"checkpoint.ogg");
			lap = new Sound(path +"lap.ogg");
			still = new Sound(path + "carSounds/still.ogg");
			topSpeed = new Sound(path + "carSounds/speed.ogg");
			scoreboardClose = new Sound(path + "scoreBoard/closed.ogg");
			scoreboardMove = new Sound(path + "scoreBoard/move.ogg");
		} catch (SlickException e) {
			System.out.println("Could not load sound file" + e);
			e.printStackTrace();
		}
	}

}
