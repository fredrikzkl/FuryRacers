package com.github.fredrikzkl.furyracers.game;

public class Player {
	private String id;
	private String username;
	private int playerNr;
	private int score = 0;
	
	private boolean ready = false;;
	private boolean carChosen = false;
	
	public int maxX = 2; 
	public int maxY = 4;
	
	private int xSel = 0;
	private int ySel = 0;
	private int select = 0;
	
	public Player(String id, int playerNr){
		this.id=id;
		this.playerNr=playerNr;
		
		setySel(playerNr -1);
	}

	public boolean equals(Object o){
		return ((Player) o).getId().equals(getId());
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
	}

	public boolean isCarChosen() {
		return carChosen;
	}

	public void setCarChosen(boolean carChosen) {
		this.carChosen = carChosen;
	}
	
	
}
