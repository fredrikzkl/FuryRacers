package com.github.fredrikzkl.furyracers.game;

public class Player {
	private String id;
	private String username;
	private int playerNr;
	private int score = 0;
	
	private boolean ready = false;;
	private boolean carChosen = false;
	private int xSel = 0;
	private int ySel;
	private int select = 0;
	
	public Player(String id, int playerNr){
		this.id=id;
		this.playerNr=playerNr;
		ySel = playerNr*128;
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

	public void setxSel(int xSel) {
		this.xSel = xSel;
	}

	public int getySel() {
		return ySel;
	}

	public void setySel(int ySel) {
		this.ySel = ySel;
	}

	public boolean isCarChosen() {
		return carChosen;
	}

	public void setCarChosen(boolean carChosen) {
		this.carChosen = carChosen;
	}
	
	
}
