package com.github.fredrikzkl.furyracers.game;

public class Atile {
	
	public int width, height, xCoordinate, yCoordinate, mapPositionX, mapPositionY, tileType;
	
	public Atile(int mapPositionX, int mapPositionY){
		
		this.width = width;
		this.height = height;
		this.xCoordinate = xCoordinate;
		this.yCoordinate = yCoordinate;
		this.tileType = tileType;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getxCoordinate() {
		return xCoordinate;
	}

	public void setxCoordinate(int xCoordinate) {
		this.xCoordinate = xCoordinate;
	}

	public int getyCoordinate() {
		return yCoordinate;
	}

	public void setyCoordinate(int yCoordinate) {
		this.yCoordinate = yCoordinate;
	}

	public int getMapPositionX() {
		return mapPositionX;
	}

	public void setMapPositionX(int mapPositionX) {
		this.mapPositionX = mapPositionX;
	}

	public int getMapPositionY() {
		return mapPositionY;
	}

	public void setMapPositionY(int mapPositionY) {
		this.mapPositionY = mapPositionY;
	}

}
