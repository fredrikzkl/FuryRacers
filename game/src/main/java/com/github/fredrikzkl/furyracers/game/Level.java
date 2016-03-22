package com.github.fredrikzkl.furyracers.game;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Vector2f;

public class Level {
	private CourseHandler course;

	int tileWidth, tileHeight;
	int mapWidthTiles, mapHeightTiles;

	float mapWidthPixels, mapHeightPixels;

	private Vector2f startCoordinates, timerCoordinates;
	private int propsLayer, backgroundLayer, roadLayer;

	public float slope, constant;
	private float xPos, yPos;
	private boolean isLeftTileLineCrossed, isRightTileLineCrossed, isTopTileLineCrossed, isBottomTileLineCrossed;
	private float yOfTileEndX, yOfTileStartX, xOfTileEndY, xOfTileStartY;

	// --------------------------------//

	public Level(CourseHandler course) {
		this.course = course;

		roadLayer = course.data.getLayerIndex("road");
		propsLayer = course.data.getLayerIndex("props");
		backgroundLayer = course.data.getLayerIndex("background");
		tileWidth = course.data.getTileWidth();
		tileHeight = course.data.getTileHeight();
		mapWidthTiles = course.data.getWidth();
		mapHeightTiles = course.data.getHeight();
		mapHeightPixels = course.data.getTileHeight() * course.data.getHeight();
		mapWidthPixels = course.data.getTileWidth() * course.data.getWidth();
		// ---------------------------

		musicControl();
		determineStartPosition();
	}

	private void musicControl() {
		course.soundTrack.loop();
		course.soundTrack.setVolume((float) 0.4);

	}

	private void determineStartPosition() {
		for (int x = 0; x < course.data.getWidth(); x++) {
			for (int y = 0; y < course.data.getHeight(); y++) {
				int tileIDroad = course.data.getTileId(x, y, roadLayer);
				if (course.data.getTileProperty(tileIDroad, "startPos", "-1")
						.equals("start")) {
					System.out.println("X: " + x + " Y: " + y);
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

	public int getTileWidth() {
		return tileWidth;
	}

	public int getTileHeight() {
		return tileHeight;
	}

	public boolean offRoad(float xPos, float yPos) {

		int tileX = (int) (xPos / tileWidth), tileY = (int) (yPos / tileHeight),

				tileIDroad = course.data.getTileId(tileX, tileY, roadLayer);

		if (tileIDroad != 0) {
			return false;
		}

		return true;
	}

	public String getTileType(int xTile, int yTile, int passedCheckpoints) {
		int tileIDroad = course.data.getTileId(xTile, yTile, roadLayer);

		if (course.data.getTileProperty(tileIDroad, "checkpoint", "-1").equals("1") && passedCheckpoints == 0) {
			return "checkpoint1";
		} else if (course.data.getTileProperty(tileIDroad, "checkpoint", "-1").equals("2") && passedCheckpoints == 1) {
			return "checkpoint2";
		} else if (course.data.getTileProperty(tileIDroad, "checkpoint", "-1").equals("3") && passedCheckpoints == 2) {
			return "checkpoint3";
		} else if (course.data.getTileProperty(tileIDroad, "goal", "-1").equals("finish") && passedCheckpoints == 3) {
			return "lap";
		}
		return "openRoad";
	}

	public boolean collision(float xPos, float yPos) {

		int tileX = (int) (xPos / tileWidth), tileY = (int) (yPos / tileHeight),
				tileIDprops = course.data.getTileId(tileX, tileY, propsLayer);

		boolean isColliding = course.data.getTileProperty(tileIDprops, "collision", "-1").equals("1");

		return isColliding;
	}

	public ArrayList<String> whichDirectionToStop(float xCarPos, float yCarPos, float xVector, float yVector) {

		ArrayList<String> stopCarMovement = new ArrayList<String>();

		int tileX = (int) (xCarPos / tileWidth), tileY = (int) (yCarPos / tileHeight);

		boolean leftTileIsObstacle = isCollisionObstacle(tileX - 1, tileY),
				rightTileIsObstacle = isCollisionObstacle(tileX + 1, tileY),
				topTileIsObstacle = isCollisionObstacle(tileX, tileY - 1),
				bottomTileIsObstacle = isCollisionObstacle(tileX, tileY + 1);

		boolean carMovingLeft = (xVector < 0), carMovingRight = (xVector > 0), carMovingUp = (yVector < 0),
				carMovingDown = (yVector > 0);

		int tileStartX = tileX * tileWidth, tileStartY = tileY * tileHeight;
		int tileEndX = (tileX + 1) * tileWidth - 1, tileEndY = (tileY + 1) * tileHeight - 1;

		float slope = yVector / xVector;
		float constant = yCarPos - slope * xCarPos; // c = y - ax

		intersectionPointsOfLine(slope, constant, tileStartX, tileStartY, tileEndX, tileEndY);
		checkIntersectionsWithTile(tileStartX, tileStartY, tileEndX, tileEndY);

		if (isRightTileLineCrossed && carMovingLeft) {
			if (rightTileIsObstacle) {
				if (carMovingUp) {
					if (bottomTileIsObstacle) {
						stopCarMovement.add("negativeY");
						stopCarMovement.add("negativeX");
						return stopCarMovement;
					} else {
						stopCarMovement.add("negativeY");
						return stopCarMovement;
					}
				} else if (carMovingDown) {
					if (topTileIsObstacle) {
						stopCarMovement.add("positiveY");
						stopCarMovement.add("negativeX");
						return stopCarMovement;
					}else{
						stopCarMovement.add("positiveY");
						return stopCarMovement;
					}
				}
			}else{
				stopCarMovement.add("negativeX");
				return stopCarMovement;
			}
		}
		
		if(isLeftTileLineCrossed && carMovingRight){
			if(leftTileIsObstacle) {
				if(carMovingUp){
					if(bottomTileIsObstacle){
						stopCarMovement.add("negativeY");
						stopCarMovement.add("positiveX");
						return stopCarMovement;
					}else{
						stopCarMovement.add("negativeY");
						return stopCarMovement;
					}
				}else if(carMovingDown){
					if(topTileIsObstacle){
						stopCarMovement.add("positiveY");
						stopCarMovement.add("positiveX");
						return stopCarMovement;
					}
				}
			}else{
				stopCarMovement.add("positiveX");
				return stopCarMovement;
			}
		}
		
		if(isBottomTileLineCrossed && carMovingUp){
			if(bottomTileIsObstacle){
				if(carMovingLeft){
					if(rightTileIsObstacle){
						stopCarMovement.add("negativeY");
						stopCarMovement.add("negativeX");
						return stopCarMovement;
					}else{
						stopCarMovement.add("negativeX");
						return stopCarMovement;
					}
				}else if(carMovingRight){
					if(topTileIsObstacle){
						stopCarMovement.add("negativeY");
						stopCarMovement.add("positiveX");
						return stopCarMovement;
					}
				}
			}else{
				stopCarMovement.add("negativeY");
				return stopCarMovement;
			}
		}
		
		if(isTopTileLineCrossed && carMovingDown){
			if(topTileIsObstacle) {
				if(carMovingRight){
					if(rightTileIsObstacle){
						stopCarMovement.add("positiveY");
						stopCarMovement.add("negativeX");
						return stopCarMovement;
					}else{
						stopCarMovement.add("negativeX");
						return stopCarMovement;
					}
				}else if(carMovingLeft){
					if(topTileIsObstacle){
						stopCarMovement.add("positiveY");
						stopCarMovement.add("negativeX");
						return stopCarMovement;
					}
				}
			}else{
				stopCarMovement.add("positiveY");
				return stopCarMovement;
			}
		}
		
		return stopCarMovement;
	}
	
	
	public void intersectionPointsOfLine(float slope, float constant, int tileStartX, int tileStartY, int tileEndX, int tileEndY){
		yOfTileEndX = slope*tileEndX + constant; // y = ax + c
		yOfTileStartX = slope*tileStartX + constant;
												
		xOfTileEndY = (tileEndY - constant)/slope; // x = (y-c)/a
		xOfTileStartY = (tileStartY - constant)/slope;
	}
	
	
	public void checkIntersectionsWithTile(int tileStartX, int tileStartY,int tileEndX, int tileEndY){

		isLeftTileLineCrossed = isTileLineCrossed(tileStartY, tileEndY, yOfTileStartX);
		isRightTileLineCrossed = isTileLineCrossed(tileStartY, tileEndY, yOfTileEndX);
		isTopTileLineCrossed = isTileLineCrossed(tileStartX, tileEndX, xOfTileStartY);
		isBottomTileLineCrossed = isTileLineCrossed(tileStartX, tileEndX, xOfTileEndY);
	}

	public boolean isCollisionObstacle(int tileX, int tileY) {

		int tileId = course.data.getTileId(tileX, tileY, propsLayer);
		return course.data.getTileProperty(tileId, "collision", "-1").equals("1");
	}

	public boolean isTileLineCrossed(float tileStart, float tileEnd, float carCrossedAt) {
		return (tileStart - 2 <= carCrossedAt && carCrossedAt <= tileEnd + 2);
	}

	public float getyPos() {
		return yPos;
	}

	public float getxPos() {
		return xPos;
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

	public boolean isTopLineCrossed() {
		return isTopTileLineCrossed;
	}

	public Vector2f getTimerCoordinates() {
		return timerCoordinates;
	}

	public void setTimerCoordinates(Vector2f timerCoordinates) {
		this.timerCoordinates = timerCoordinates;
	}

}
