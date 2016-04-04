package com.github.fredrikzkl.furyracers.game;

import java.io.IOException;

import javax.websocket.EncodeException;

import org.newdawn.slick.Color;

import com.github.fredrikzkl.furyracers.network.GameSession;

public class Player implements Comparable<Player>{
	
	private final String 
	RED = "#ffe6e6", GREEN = "#d6f5d6", 
	BLUE = "#e6f5ff", YELLOW = "#ffffcc";
	
	private final int AMOUNT_OF_CARTYPES = 3;
	private final int AMOUNT_OF_CARCOLORS= 4;
	public final int maxX = AMOUNT_OF_CARTYPES; 
	public final int maxY = AMOUNT_OF_CARCOLORS;
	
	private String id;
	private String username;
	private Color rgbRED, rgbGREEN, rgbBLUE, rgbYELLOW;
	private Color carColor;
	private int playerNr;
	private int score = 0;
	
	private boolean ready = false;;
	private boolean carChosen = false;
	
	private int xSel = 0;
	private int ySel = 0;
	private int select = 0;
	
	public Player(String id, int playerNr) throws IOException, EncodeException{
		
		this.id = id;
		this.playerNr = playerNr;
		
		rgbRED = new Color(252f, 0f, 0f, 0.4f);
		rgbBLUE = new Color(0f, 0f, 250f, 0.3f);
		rgbGREEN = new Color(0f,255f, 0f, 0.5f);
		rgbYELLOW = new Color(255f,255f,0f,0.5f);
		
		setySel(playerNr -1);
		setxSel(playerNr-1);
	}

	public boolean equals(Player o){
		return o.getId().equals(getId());
	}
	
	public int hashCode(){
		return getId().hashCode();
	}
	
	public void setxSel(int xSel) throws IOException, EncodeException {
		if(xSel > maxX-1){
			this.xSel = 0;
		}else if(xSel<0){
			this.xSel = maxX-1;
		}else{
			this.xSel = xSel;
		}
		
		setCarModelToController(this.xSel);
	}

	public void setySel(int ySel) throws IOException, EncodeException {
		if(ySel >= maxY){
			this.ySel = 0;
		}else if(ySel<0){
			this.ySel = maxY-1;
		}else{
			this.ySel = ySel;
		}
		
		setCarColorToController(this.ySel);
	}

	private void setCarColorToController(int ySel) throws IOException, EncodeException{
		
		switch(ySel){
			
			case 0: 
				GameSession.carColorToController(id, RED);
				carColor = rgbRED;
				break;
			case 1:
				GameSession.carColorToController(id, BLUE);
				carColor = rgbBLUE;
				break;
			case 2:
				GameSession.carColorToController(id, GREEN);
				carColor = rgbGREEN;
				break;
			case 3:
				GameSession.carColorToController(id, YELLOW);
				carColor = rgbYELLOW;
				break;
		}
	}
	
	private void setCarModelToController(int xSel) throws IOException, EncodeException{
		
		switch(xSel){
			
			case 0: 
				GameSession.carModelToController(id, "Mustang");
				break;
			case 1:
				GameSession.carModelToController(id, "Camaro");
				break;
			case 2:
				GameSession.carModelToController(id, "VelociRaptor");
				break;
		}
	}
	
	@Override
	public int compareTo(Player o) {
		return Integer.compare(this.getScore(), o.getScore());
	}
	
	Color getCarColor(){
		
		return carColor;
	}
	
	public boolean isCarChosen() {
		return carChosen;
	}

	public void setCarChosen(boolean carChosen) {
		this.carChosen = carChosen;
	}
	
	public boolean isReady() {
		return ready;
	}

	public void setReady(boolean ready) {
		this.ready = ready;
	}

	public int getSelect() {
		return select;
	}

	public void setSelect(int select) {
		this.select = select;
	}

	public int getxSel() {
		return xSel;
	}

	public int getySel() {
		return ySel;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void buttonDown(String data) {
		getCar().controlls.disableKeyboardInput();
		switch (data) {
		case "0":
			getCar().controlls.reverseKeyDown();
			break;
		case "1":
			getCar().controlls.throttleKeyDown();
			break;
		case "2":
			getCar().controlls.rightKeyDown();
			break;
		case "3":
			getCar().controlls.leftKeyDown();
		}
	}

	public void buttonUp(String data) {
		switch (data) {
		case "0":
			getCar().controlls.reverseKeyUp();
			break;
		case "1":
			getCar().controlls.throttleKeyUp();
			break;
		case "2":
			getCar().controlls.rightKeyUp();
			break;
		case "3":
			getCar().controlls.leftKeyUp();
		}
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getId() {
		return id;
	}

	public int getScore() {
		return score;
	}

	public int getPlayerNr() {
		return playerNr;
	}
	
	public String toString(){
		return "Player " + getPlayerNr() + " ID:'" + getId() +"'";
		
	}
	
	public Car getCar(){
		return GameCore.cars.get(playerNr-1);
	}
}
