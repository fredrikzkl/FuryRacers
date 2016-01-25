package com.github.fredrikzkl.furyracers.network;

public class Player {
	private String id;
	private String username;
	private int score = 0;
	
	private Player(String id){
		this.id=id;
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
	
	
}
