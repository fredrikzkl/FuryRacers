package com.github.fredrikzkl.furyracers.game;

import java.util.ArrayList;

public class CarCollision extends Level {
	
	Car car;
	
	public CarCollision(Car car){
		super();
		this.car = car;
	}
	
	public ArrayList<String> whichDirectionToStop(int tileX, int tileY, float xPos, float yPos, float xVector, float yVector){
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
		
		if(isRightTileLineCrossed && carMovingLeft){
			if(rightTileIsObstacle) {
				if(carMovingUp){
					if(bottomTileIsObstacle){ 
						stopCarMovement.add("negativeY");
						stopCarMovement.add("negativeX");
						return stopCarMovement;
					}else{
						stopCarMovement.add("negativeY");
						stopCarMovement.add("");
						return stopCarMovement;
					}
				}else if(carMovingDown){
					if(topTileIsObstacle){ 
						stopCarMovement.add("positiveY");
						stopCarMovement.add("negativeX");
						return stopCarMovement;
					}else{
						stopCarMovement.add("negativeY");
						stopCarMovement.add("");
						return stopCarMovement;
					}
				}
			}else{
				stopCarMovement.add("negativeX");
				stopCarMovement.add("");
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
						stopCarMovement.add("");
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
				stopCarMovement.add("");
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
						stopCarMovement.add("");
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
				stopCarMovement.add("");
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
						stopCarMovement.add("");
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
				stopCarMovement.add("");
				return stopCarMovement;
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

}
