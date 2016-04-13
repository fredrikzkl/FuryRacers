package com.github.fredrikzkl.furyracers.game;

import java.util.List;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Vector2f;

import com.github.fredrikzkl.furyracers.car.Car;

public class Level {
	public static CourseHandler course;

	public static int tileHeight, tileWidth;
	int mapWidthInTiles, mapHeightInTiles;

	float mapWidthPixels, mapHeightPixels;

	private Vector2f startCoordinates, timerCoordinates;
	public static int propsLayer, backgroundLayer, roadLayer;

	public Level(CourseHandler course) {
		this.course = course;

		roadLayer = course.data.getLayerIndex("road");
		propsLayer = course.data.getLayerIndex("props");
		backgroundLayer = course.data.getLayerIndex("background");
		tileWidth = course.data.getTileWidth();
		tileHeight = course.data.getTileHeight();
		mapWidthInTiles = course.data.getWidth();
		mapHeightInTiles = course.data.getHeight();
		mapHeightPixels = course.data.getTileHeight() * course.data.getHeight();
		mapWidthPixels = course.data.getTileWidth() * course.data.getWidth();
	
		musicControl();
		determineStartPosition();
	}

	private void musicControl() {
		course.soundTrack.loop();
		course.soundTrack.setVolume((float) 0.4);

	}

	private void determineStartPosition() {
		for (int x = 0; x < mapWidthInTiles; x++) {
			for (int y = 0; y < mapHeightInTiles; y++) {
				int tileIDroad = course.data.getTileId(x, y, roadLayer);
				if (course.data.getTileProperty(tileIDroad, "startPos", "-1")
						.equals("start")) {
					startCoordinates = new Vector2f(x * tileWidth, y * tileHeight);
					break;
				}
				
			}
		}
		
		if(startCoordinates == null){
			startCoordinates = new Vector2f(0,0);

		}
	}

	public void drawCars(Graphics g, List<Car> cars) {
		g.drawImage(course.subLayer, 0, 0);
		for (Car car : cars) {
			car.render(g);
		}
		g.drawImage(course.topLayer, 0, 0);
	}

	public boolean offRoad(float xPos, float yPos) {

		int tileX = (int) (xPos / tileWidth), tileY = (int) (yPos / tileHeight),
			tileIDroad = course.data.getTileId(tileX, tileY, roadLayer);

		if (tileIDroad != 0)
			return false;

		return true;
	}

	public String getTileType(int xTile, int yTile, int passedCheckpoints) {
		int tileIDroad = course.data.getTileId(xTile, yTile, roadLayer);

		if ( isCheckpoint(tileIDroad, "1") && passedCheckpoints == 0) {
			return "checkpoint1";
		} else if ( isCheckpoint(tileIDroad, "2") && passedCheckpoints == 1) {
			return "checkpoint2";
		} else if ( isCheckpoint(tileIDroad, "3") && passedCheckpoints == 2) {
			return "checkpoint3";
		} else if ( isFinishLine(tileIDroad) && passedCheckpoints == 3) {
			return "lap";
		}
		return "openRoad";
	}
	
	private boolean isCheckpoint(int tileId, String checkpointId){
		
		return course.data.getTileProperty(tileId, "checkpoint", "-1").equals(checkpointId);
	}
	
	private boolean isFinishLine(int tileId){
		return course.data.getTileProperty(tileId, "goal", "-1").equals("finish");
	}

	public float getMapWidthPixels() {
		return mapWidthPixels;
	}

	public float getMapHeightPixels() {
		return mapHeightPixels;
	}

	public Vector2f getStartCoordinates() {
		return startCoordinates;
	}

	public Vector2f getTimerCoordinates() {
		return timerCoordinates;
	}

	public void setTimerCoordinates(Vector2f timerCoordinates) {
		this.timerCoordinates = timerCoordinates;
	}
	
	public int getTileWidth() {
		return tileWidth;
	}

	public int getTileHeight() {
		return tileHeight;
	}

}
