package com.github.fredrikzkl.furyracers;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Graphics;

public class ParallaxBackground {
	
	private final int SCREENWIDTH = Application.screenSize.width;
	
	private Layer layer1;
	private Layer layer1sub;
	
	private Layer layer2;
	private Layer layer2sub;
	
	private Layer layer3;
	private Layer layer3sub;
	
	private List<Layer> layers;
	private float scaleValue = 1;
	
	public ParallaxBackground(){
		layers = new ArrayList<Layer>();

		float speed1 = 0.02f;
		layer1 = new Layer("city1",0,0,speed1);
		//layer1sub = new Layer("city1",SCREENWIDTH,0,speed1);
		determineScaling(layer1);
		
		
		float speed2 = 0.01f;
		layer2 = new Layer("city2",0,0,speed2);
		//layer2sub = new Layer("city2",SCREENWIDTH,0,speed2);
		
		/*
		float speed3 = 1f;
		layer3 = new Layer("hills",0,0,speed3);
		float gap = SCREENWIDTH - (layer3.getImg().getWidth() * scaleValue);
		layer3sub = new Layer("hills",SCREENWIDTH,0,speed3);
		
		
		layers.add(layer3);
		layers.add(layer3sub);
		*/
		layers.add(layer2);
		//layers.add(layer2sub);
		
		layers.add(layer1);
		//layers.add(layer1sub);
		
	}
	
	private void determineScaling(Layer lay) {
		int temp = Application.screenSize.width/lay.getImg().getWidth();
		if(temp>scaleValue)
			scaleValue = temp;
	}

	public void draw(Graphics g){
		for(Layer lay:layers){
			lay.draw(scaleValue);
		}
		
	}
	
	public void tick(){
		
		for(Layer lay:layers){
			lay.setX(lay.getX()-lay.getSpeed());
			lay.outOfFrame();
		}
		 
		
		
	}
	
}
