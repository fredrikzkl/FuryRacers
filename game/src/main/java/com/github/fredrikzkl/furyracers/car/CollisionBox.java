package com.github.fredrikzkl.furyracers.car;

import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Vector2f;

public class CollisionBox {
	
	private static final int amountLength = 5;
	private static final int amountWidth = 3;
	private Car car;
	private float carWidth;
	private float carLength;
	private Polygon collisionBox;
	public boolean bool = false;
	
	public CollisionBox(Car car){
		this.car = car;
		carWidth = car.getCarWidth();
		carLength = car.getCarLength();
		
		collisionBox = new Polygon();
	}
	
	public void generatePoints(){
		
		float centerOfRotationX = car.getPosition().x;
		float centerOfRotationY  = car.getPosition().y + car.getCenterOfRotationYOffset();
		float radDeg = car.getRotationRad();
		collisionBox = new Polygon();
		collisionBox.setClosed(true);

		float backRightX = centerOfRotationX + (float)(Math.cos(radDeg-Math.PI/2)*carWidth/2),
			  backRightY = centerOfRotationY + (float)(Math.sin(radDeg-Math.PI/2)*carWidth/2);
		float backLeftX = centerOfRotationX + (float)(Math.cos(radDeg+Math.PI/2)*carWidth/2),
			  backLeftY = centerOfRotationY + (float)(Math.sin(radDeg+Math.PI/2)*carWidth/2);
		
		Vector2f backRight = new Vector2f(backRightX, backRightY);
		Vector2f backLeft = new Vector2f(backLeftX, backLeftY);
		
		Vector2f frontLeft = pointsLeftOfCar(backLeft, radDeg);
		Vector2f frontRight = pointsTopOfCar(frontLeft, radDeg);
		
		pointsRightOfCar(frontRight, radDeg);
		pointsBackOfCar(backRight, radDeg);
	}
	
	private Vector2f pointsLeftOfCar(Vector2f backLeft, float radDeg){
		
		Vector2f newPoint = new Vector2f(0,0);
		
		for(int i = 0; i < amountLength; i++){
			
			newPoint.x = (float) (backLeft.x +  Math.cos(radDeg)*carLength*i/(amountLength-1));
			newPoint.y = (float) (backLeft.y +  Math.sin(radDeg)*carLength*i/(amountLength-1));
			
			collisionBox.addPoint(newPoint.x, newPoint.y);
		}
		
		return newPoint;
		
	}
	
	private Vector2f pointsTopOfCar(Vector2f frontLeft,float radDeg){
		
		Vector2f newPoint = new Vector2f(0,0);
		
		for(int i = 1; i < amountWidth; i++){
			
			 newPoint.x = (float) (frontLeft.x +  Math.cos(radDeg-Math.PI/2)*carWidth*i/(amountWidth-1));
			 newPoint.y = (float) (frontLeft.y +  Math.sin(radDeg-Math.PI/2)*carWidth*i/(amountWidth-1));
			
			collisionBox.addPoint(newPoint.x, newPoint.y);
		}
		
		return newPoint;
	}
	
	private void pointsRightOfCar(Vector2f frontRight, float radDeg){
		
		Vector2f newPoint = new Vector2f(0,0); 
		
		for(int i = 1; i < amountLength; i++){
						
			newPoint.x = (float) (frontRight.x +  Math.cos(radDeg+Math.PI)*carLength*i/(amountLength-1));
			newPoint.y = (float) (frontRight.y +  Math.sin(radDeg+Math.PI)*carLength*i/(amountLength-1));
			
			collisionBox.addPoint(newPoint.x, newPoint.y);
		}
	}
	
	private void pointsBackOfCar(Vector2f backRight, float radDeg){
		
		Vector2f newPoint = new Vector2f(0,0);

		for(int i = 1; i < amountWidth-1; i++){
			
			newPoint.x = (float) (backRight.x +  Math.cos(radDeg+Math.PI/2)*carWidth*i/(amountWidth-1));
			newPoint.y = (float) (backRight.y +  Math.sin(radDeg+Math.PI/2)*carWidth*i/(amountWidth-1));
				
			collisionBox.addPoint(newPoint.x, newPoint.y);
		}
	}
	
	float[] getPoints(){
		
		return collisionBox.getPoints();
	}
	
	Polygon getBox(){
		return collisionBox;
	}

}
