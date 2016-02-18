package com.github.fredrikzkl.furyracers;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.geom.RectangularShape;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.ResourceLoader;

public class Menu extends BasicGameState{
	
	Font regularFont;
	TrueTypeFont ip;

	private static String IP = null;
	private TrueTypeFont header;
	private TrueTypeFont regularText;
	
	
	private Color headerColor = new Color(221, 0, 0);
	private Image icons;
	
	private int tick;
	private double seconds;
	// used to count seconds
	double duration, last;
	
	private String countDown;
	private int counter;
	private int secondsToNextGame = 30;
	
	public Menu(int state){
		
	}
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
		
		
		regularFont = new Font("Verdana", Font.BOLD, 20);
		ip = new TrueTypeFont(regularFont, true);
		
		
		counter = secondsToNextGame;
		countDown = Integer.toString(counter);
		duration = last = System.nanoTime();
		
		InputStream inputStream;
	    try {
	        inputStream = ResourceLoader.getResourceAsStream("Font/Orbitron-Regular.ttf");
	        Font awtFont1 = Font.createFont(Font.TRUETYPE_FONT, inputStream);
	        
	        inputStream = ResourceLoader.getResourceAsStream("Font/Orbitron-Regular.ttf");
	        Font awtFont2 = Font.createFont(Font.TRUETYPE_FONT, inputStream);
	        
	       
	     
	        awtFont1 = awtFont1.deriveFont(60f); // set font size
	        awtFont2 = awtFont1.deriveFont(24f);
	        
	        header = new TrueTypeFont(awtFont1, true);
	        regularText = new TrueTypeFont(awtFont2, true);
	   
	             
	    } catch (Exception e) {
	        e.printStackTrace();
	    }   
		
		icons = new Image("Sprites/menu_sheet.png");
	}

	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException {
		
		//TODO
		ip.drawString(Application.screenSize.width-300, 0, IP);
		
		header.drawString(Application.screenSize.width/3, 50, "Fury Racers", headerColor);
		
		//Bordersene
		for(int i = 0; i<4 ; i++){
			icons.draw((float)(Application.screenSize.width/3.8 + (i*160)) ,Application.screenSize.height/4);
		}
		
		//Blinking text
		if(Math.sin(tick/400)>0){
			regularText.drawString(Application.screenSize.width/3, Application.screenSize.height/2, "Need at least 1 player to begin!");
		}
		
		header.drawString(Application.screenSize.width-125, Application.screenSize.height -75, countDown);
		
		
	}


	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {
		tick++;
		duration = System.nanoTime() - last;
		seconds += duration/1_000_000_000.0f;
		
		
		if(secondsToNextGame - seconds>=0){
			counter = (int) (secondsToNextGame - seconds);
			countDown = String.valueOf(counter);
		}else{
			countDown = String.valueOf(0);
		}
		
		
		Input mouse = container.getInput();
		if(mouse.isMouseButtonDown(0)){
			game.enterState(1);
		}
		
		last = System.nanoTime();
		
	}

	public int getID() {
		return 0;
	}
	public void setIP(String ip) {
		IP = ip + "/fury";
	}
}
