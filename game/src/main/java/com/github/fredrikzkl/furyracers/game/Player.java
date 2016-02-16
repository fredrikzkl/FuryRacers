package com.github.fredrikzkl.furyracers.game;

public class Player {
	private String id;
	private String username;
	private int playerNr;
	private int score = 0;
	
	public Player(String id, int playerNr){
		this.id=id;
		this.playerNr=playerNr;
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
}
