package com.github.fredrikzkl.furyracers;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.geom.RectangularShape;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.ResourceLoader;

import com.github.fredrikzkl.furyracers.game.Player;

public class Menu extends BasicGameState{
	
	Font regularFont;
	TrueTypeFont ip;

	private static String IP = null;
	private String version = null;
	private TrueTypeFont header;
	private TrueTypeFont regularText;
	private TrueTypeFont consoleText;
	private float consoleSize = 15f;
	
	
	private Color headerColor = new Color(221, 0, 0);
	private Image icons, cars;
	private boolean p1,p2,p3,p4 = false;
	
	private int tick;
	private double seconds;
	// used to count seconds
	double duration, last;
	
	private String countDown;
	private int counter;
	private int secondsToNextGame = 30;
	
	
	public List<String> console;
	public List<Player> players;
	
	public Menu(int state){
		
	}
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
		players = new ArrayList<Player>();
		console  = new ArrayList<String>();
		console.add("Welcome to FuryRacers! Version: " + version);
		
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
	        
	        inputStream = ResourceLoader.getResourceAsStream("Font/Orbitron-Regular.ttf");
	        Font awtFont3 = Font.createFont(Font.TRUETYPE_FONT, inputStream);
	       
	        awtFont1 = awtFont1.deriveFont(60f); // set font size
	        awtFont2 = awtFont1.deriveFont(24f);
	        awtFont3 = awtFont1.deriveFont(consoleSize);
	        
	        header = new TrueTypeFont(awtFont1, true);
	        regularText = new TrueTypeFont(awtFont2, true);
	        consoleText = new TrueTypeFont(awtFont3, true);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }   
		
	    try{
		icons = new Image("Sprites/menu_sheet.png");
		cars = new Image("Sprites/menu_cars.png");
	    }catch(RuntimeException e){
	    	printConsole("ERROR! Sprite sheet not found!");
	    }
	    
	}

	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException {
		
		//TODO
		ip.drawString(Application.screenSize.width-300, 0, IP);
		//ip.drawString(Application.screenSize.width-125,20,version);
		
		header.drawString(Application.screenSize.width/3, 50, "Fury Racers", headerColor);
		
		//Blinking text
		if(Math.sin(tick/400)>0){
			regularText.drawString(Application.screenSize.width/3, Application.screenSize.height/2, "Need at least 1 player to begin!");
		}
		
		header.drawString(Application.screenSize.width-125, Application.screenSize.height -75, countDown);
		
		String consoleTxt;
		for(int i = console.size(); i>0; i--){
			consoleText.drawString(0, 
					Application.screenSize.height-(consoleSize*(console.size()-i+1)), 
					console.get(i-1));// Draws the console
		}
		
		drawPlayerIcons(container, game, g);
	}


	private void drawPlayerIcons(GameContainer container, StateBasedGame game, Graphics g) {
		
		if(p1){
			g.drawImage(cars.getSubImage(128, 0, 128, 128), (float)(Application.screenSize.width/3.8 + (0*160)) ,Application.screenSize.height/4);
		}
		
		if(p2){
			g.drawImage(cars.getSubImage(128, 128, 128, 128), (float)(Application.screenSize.width/3.8 + (1*160)) ,Application.screenSize.height/4);
		}
		
		if(p3){
			g.drawImage(cars.getSubImage(128, 256, 128, 128), (float)(Application.screenSize.width/3.8 + (2*160)) ,Application.screenSize.height/4);
		}
		
//		(float)(Application.screenSize.width/3.8 + (0*160)) , (float)Application.screenSize.height/4,
		
		//Bordersene
		for(int i = 0; i<4 ; i++){
			icons.draw((float)(Application.screenSize.width/3.8 + (i*160)) ,Application.screenSize.height/4);
			
		}
		
		
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

	public void createPlayer(int nr, String id, ArrayList<Player> list) throws SlickException{
		players = list;
		if(nr == 1)
			p1 = true;
		if(nr == 2)
			p2 = true;
		if(nr == 3)
			p3 = true;
	}
	
	public void printConsole(String text){
		if(console.size()>4){
			console.remove(0);
		}else{
			console.add(text);
		}
	}
	
	public int getID() {
		return 0;
	}
	public void setIP(String ip) {
		IP = ip + "/fury";
	}
	public void setVersion(String version) {
		this.version = version;
	}
	
	
}
