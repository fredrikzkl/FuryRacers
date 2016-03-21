package com.github.fredrikzkl.furyracers.game;

import org.newdawn.slick.Color;

import com.github.fredrikzkl.furyracers.network.GameSession;

public class Player implements Comparable<Player>{
	private String id;
	private String username;
	private String RED = "#ffe6e6", GREEN = "#d6f5d6", BLUE = "#e6f5ff", YELLOW="#ffffcc";
	private Color rgbRED, rgbGREEN, rgbBLUE, rgbYELLOW;
	private Color carColor;
	private int playerNr;
	private int score = 0;
	
	private boolean ready = false;;
	private boolean carChosen = false;
	
	//Select variables
	public int maxX = 3; 
	public int maxY = 4;
	private int xSel = 0;
	private int ySel = 0;
	private int select = 0;
	
	public Player(String id, int playerNr){
		this.id=id;
		this.playerNr=playerNr;
		
		setySel(playerNr -1);
		
		rgbRED = new Color(252f, 0f, 0f, 0.4f);
		rgbBLUE = new Color(0f, 0f, 250f, 0.3f);
		rgbGREEN = new Color(0f,255f, 0f, 0.5f);
		rgbYELLOW = new Color(255f,255f,0f,0.5f);
	}

	public boolean equals(Player o){
		return o.getId().equals(getId());
	}
	
	public int hashCode(){
		return getId().hashCode();
	}
	
	public String getUsername() {
		return username;
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
	public void setxSel(int xSel) {
		if(xSel > maxX-1){
			this.xSel = 0;
		}else if(xSel<0){
			this.xSel = maxX-1;
		}else{
			this.xSel = xSel;
		}
	}

	public void setySel(int ySel) {
		if(ySel >= maxY){
			this.ySel = 0;
		}else if(ySel<0){
			this.ySel = maxY-1;
		}else{
			this.ySel = ySel;
		}
		
		setCarColorToController(this.ySel);
	}

	private void setCarColorToController(int ySel){
		
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
	
	public Color getCarColor(){
		
		switch(ySel){
		
		case 0: 
		
			return rgbRED;
		case 1:
	
			return rgbBLUE;
		case 2:
	
			return rgbGREEN;

		case 3:
	
			return carColor = rgbYELLOW;

		}
		
		return rgbRED;
		
	}
	
	public boolean isCarChosen() {
		return carChosen;
	}

	public void setCarChosen(boolean carChosen) {
		this.carChosen = carChosen;
	}
	
	@Override
	public int compareTo(Player o) {
		return Integer.compare(this.getScore(), o.getScore());
	}
	
	
}
