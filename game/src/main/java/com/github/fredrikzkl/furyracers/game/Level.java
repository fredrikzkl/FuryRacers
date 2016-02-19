package com.github.fredrikzkl.furyracers.game;

import java.util.ArrayList;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.tiled.TiledMap;

import com.github.fredrikzkl.furyracers.Application;

public class Level {

	public final String Dpath = "Maps/a";
	public String path = Dpath;

	public TiledMap map = null;
	public int tileWidth;
	public int tileHeight;
	int roadLayer;
	int mapWidth;
	int mapHeight;
	
	float distanceWidth;
	float distanceHeight;
	
	private Vector2f startCoordinates;
	private int propsLayer;
	public float getTileEndX() {
		return tileEndX;
	}

	public float getTileEndY() {
		return tileEndY;
	}

	public float getTileStartX() {
		return tileStartX;
	}

	public float getTileStartY() {
		return tileStartY;
	}

	public float getLineIntersectsRight() {
		return carIntersectsRight;
	}

	public float getLineIntersectsLeft() {
		return carIntersectsLeft;
	}

	public float getLineIntersectsBottom() {
		return carIntersectsBottom;
	}

	public float prevPosX;
	public float prevPosY;
	public float tileEndX;
	public float tileEndY;
	public float tileStartX;
	public float tileStartY;
	public float slope;
	public float constant;
	public float carIntersectsRight;
	public float carIntersectsLeft;
	public float carIntersectsBottom;
	private float carIntersectsTop;
	private float xPos;
	private float yPos;
	private boolean leftTileIsObstacle;
	private boolean rightTileIsObstacle;
	private boolean topTileIsObstacle;
	private boolean bottomTileIsObstacle;
	private boolean carMovingLeft;
	private boolean carMovingRight;
	private boolean carMovingTop;
	private boolean carMovingBottom;
	private boolean carMovingUp;
	private boolean carMovingDown;
	private boolean isLeftTileLineCrossed;
	private boolean isRightTileLineCrossed;
	private boolean isTopTileLineCrossed;
	private boolean isBottomTileLineCrossed;
	
	public Level(int id) {
		
		path = Dpath + id + ".tmx";
		System.out.println("Course loaded:" + path.substring(5));
		
		try {
			map = new TiledMap(path);
		} catch (SlickException e) {
			System.out.println("Error loading map");
		}
		//TODO
		roadLayer = map.getLayerIndex("road");
		propsLayer = map.getLayerIndex("props");
		tileWidth = map.getTileWidth();
		tileHeight = map.getTileHeight();
		mapWidth = map.getWidth();
		mapHeight = map.getHeight();
		distanceHeight = map.getTileHeight() * map.getHeight();
		distanceWidth = map.getTileWidth() * map.getWidth();
		//---------------------------
		
		determineStartPosition();
	}

	public float getPrevPosX() {
		return prevPosX;
	}

	public float getPrevPosY() {
		return prevPosY;
	}

	private void determineStartPosition() {
		for(int x = 0; x<map.getWidth();x++){
			for(int y = 0/tileHeight; y<map.getHeight(); y++){
				if(map.getTileProperty(map.getTileId(x, y, 1), "startPos", "-1").equals("start")) {
					startCoordinates = new Vector2f(x*tileWidth,y*tileHeight);
				}
			}
		}
	}

	public void render(Graphics g, Vector2f tilePos, Camera camera) {
		int tilePosX = (int) tilePos.x;
		int tilePosY = (int) tilePos.y;
		map.render(0,0, 0, 0, 
				(int)(camera.getSize().x-camera.getX())/map.getTileWidth()+1,
				(int)(camera.getSize().y-camera.getY())/map.getTileHeight()+1);
	}
	
	public int getTileWidth() {
		return tileWidth;
	}

	public int getTileHeight() {
		return tileHeight;
	}
	
	public String getTileType(int xTile, int yTile, int passedCheckpoints){
		int tileIDroad = map.getTileId(xTile, yTile, roadLayer);
		int tileIDprops = map.getTileId(xTile, yTile, propsLayer);
		
			
		/*if(tileIDroad == 0){
			return "";
		}*/
		
		if(map.getTileProperty(tileIDroad, "checkpoint", "-1").equals("1") && passedCheckpoints == 0) {
			return "checkpoint1";
		}else if(map.getTileProperty(tileIDroad, "checkpoint", "-1").equals("2") && passedCheckpoints == 1) {
			return "checkpoint2";
		}else if(map.getTileProperty(tileIDroad, "checkpoint", "-1").equals("3") && passedCheckpoints == 2) {
			return "checkpoint3";
		}else if(map.getTileProperty(tileIDroad, "goal", "-1").equals("finish") && passedCheckpoints == 3) {
			return "finishLine";
		}else if(map.getTileProperty(tileIDprops, "collision", "-1").equals("1")) {
			
			return "collisionObstacle";
		}
		return "openRoad";
	}
	
	public ArrayList<String> whichSideHasBeenCrossed(int tileX, int tileY, float xPos, float yPos, float xVector, float yVector){
		ArrayList<String> stopCarMovement = new ArrayList<String>();
		
		leftTileIsObstacle = isCollisionObstacle(tileX-1, tileY);
		rightTileIsObstacle = isCollisionObstacle(tileX+1, tileY);
		topTileIsObstacle = isCollisionObstacle(tileX, tileY-1);
		bottomTileIsObstacle = isCollisionObstacle(tileX, tileY+1);
		
		carMovingLeft = (xVector < 0);
		carMovingRight = (xVector > 0);
		carMovingUp = (yVector < 0);
		carMovingDown = (yVector > 0);
		
		prevPosX = xPos - xVector;
		prevPosY = yPos - yVector;
		
		this.xPos = xPos;
		this.yPos = yPos;
		
		tileStartX = tileX * tileWidth;
		tileStartY = tileY * tileHeight;
		
		tileEndX = (tileX+1)*tileWidth - 1;
		tileEndY = (tileY+1)*tileHeight - 1;
		
		slope = yVector/xVector;
		
		constant = prevPosY - slope*prevPosX;
		
		carIntersectsRight = slope*tileEndX + constant; // y = ax + c
		carIntersectsLeft = slope*tileStartX + constant;
												
		carIntersectsBottom = (tileEndY - constant)/slope; // x = (y-c)/a
		carIntersectsTop = (tileStartY - constant)/slope;
		
		isLeftTileLineCrossed = isTileLineCrossed(tileStartY, tileEndY, carIntersectsLeft);
		isRightTileLineCrossed = isTileLineCrossed(tileStartY, tileEndY, carIntersectsRight);
		isTopTileLineCrossed = isTileLineCrossed(tileStartX, tileEndX, carIntersectsTop);
		isBottomTileLineCrossed = isTileLineCrossed(tileStartX, tileEndX, carIntersectsBottom);
		
		if(isRightTileLineCrossed){
			if(carMovingLeft){
				if(rightTileIsObstacle) {
					if(carMovingUp){
						if(bottomTileIsObstacle){ 
							stopCarMovement.add("yMovNeg");
							stopCarMovement.add("xMovNeg");
							return stopCarMovement;
						}else{
							stopCarMovement.add("yMovNeg");
							stopCarMovement.add("");
							return stopCarMovement;
						}
					}else if(carMovingDown){
						if(topTileIsObstacle){ 
							stopCarMovement.add("yMovPos");
							stopCarMovement.add("xMovNeg");
							return stopCarMovement;
						}else{
							stopCarMovement.add("yMovNeg");
							stopCarMovement.add("");
							return stopCarMovement;
						}
					}
				}else{
					stopCarMovement.add("xMovNeg");
					stopCarMovement.add("");
					return stopCarMovement;
				}
			}
		}
		
		if(isLeftTileLineCrossed){
			if(carMovingRight){
				if(leftTileIsObstacle) {
					if(carMovingUp){
						if(bottomTileIsObstacle){
							stopCarMovement.add("yMovNeg");
							stopCarMovement.add("xMovPos");
							return stopCarMovement;
						}else{
							stopCarMovement.add("yMovNeg");
							stopCarMovement.add("");
							return stopCarMovement;
						}
					}else if(carMovingDown){
						if(topTileIsObstacle){
							stopCarMovement.add("yMovPos");
							stopCarMovement.add("xMovPos");
							return stopCarMovement;
						}
					}
				}else{
					stopCarMovement.add("xMovPos");
					stopCarMovement.add("");
					return stopCarMovement;
				}
			}
		}
		
		if(isBottomTileLineCrossed){
			if(carMovingUp){
				if(bottomTileIsObstacle) {
					if(carMovingLeft){
						if(rightTileIsObstacle){
							stopCarMovement.add("yMovNeg");
							stopCarMovement.add("xMovNeg");
							return stopCarMovement;
						}else{
							stopCarMovement.add("xMovNeg");
							stopCarMovement.add("");
							return stopCarMovement;
						}
					}else if(carMovingRight){
						if(topTileIsObstacle){
							stopCarMovement.add("yMovNeg");
							stopCarMovement.add("xMovPos");
							return stopCarMovement;
						}
					}
				}else{
					stopCarMovement.add("yMovNeg");
					stopCarMovement.add("");
					return stopCarMovement;
				}
			}
		}
		
		if(isTopTileLineCrossed){
			if(carMovingDown){
				if(topTileIsObstacle) {
					if(carMovingRight){
						if(rightTileIsObstacle){
							stopCarMovement.add("yMovPos");
							stopCarMovement.add("xMovNeg");
							return stopCarMovement;
						}else{
							stopCarMovement.add("xMovNeg");
							stopCarMovement.add("");
							return stopCarMovement;
						}
					}else if(carMovingLeft){
						if(topTileIsObstacle){
							stopCarMovement.add("yMovPos");
							stopCarMovement.add("xMovNeg");
							return stopCarMovement;
						}
					}
				}else{
					stopCarMovement.add("yMovPos");
					stopCarMovement.add("");
					return stopCarMovement;
				}
			}
		}
		
		stopCarMovement.add("");
		stopCarMovement.add("");
		return stopCarMovement;
	}
		
	public boolean isCollisionObstacle(int tileX, int tileY){
		
		int tileId = map.getTileId(tileX, tileY, propsLayer);
		return map.getTileProperty(tileId, "collision", "-1").equals("1");
	}
	
	public boolean isTileLineCrossed(float tileStartY2, float tileEndY2, float carCrossedAt){
		return (tileStartY2-2 <= carCrossedAt && carCrossedAt <= tileEndY2+2);
	}
	

	public float getyPos() {
		return yPos;
	}

	public float getxPos() {
		return xPos;
	}

	public float getLineIntersectsTop() {
		return carIntersectsTop;
	}

	public float getDistanceWidth() {
		return distanceWidth;
	}

	public float getDistanceHeight() {
		return distanceHeight;
	}

	public Vector2f getStartCoordinates() {
		return startCoordinates;
	}
	
	public boolean isTopLineCrossed(){
		return isTopTileLineCrossed;
	}
}
