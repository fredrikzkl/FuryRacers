package com.github.fredrikzkl.furyracers.menu;

import java.util.ArrayList;
import java.util.List;

import com.github.fredrikzkl.furyracers.Application;

public class ParallaxBackground {
	
	private final int SCREENWIDTH = Application.screenSize.width;
	
	private Layer layer1;
	private Layer layer1sub;
	
	private Layer layer2;
	private Layer layer2sub;
	
	private Layer layer3;
	private Layer layer3sub;
	
	private Layer layer4;
	private Layer layer4sub;
	
	private Layer layer5;
	private Layer layer5sub;
	
	private Layer moon;
	
	private List<Layer> layers;
	private float scaleValue = 1;
	
	public ParallaxBackground(){
		layers = new ArrayList<Layer>();

		float speed = 0.02f*6f;
		layer1 = new Layer("city1",0,0,speed);
		layer1sub = new Layer("city1",SCREENWIDTH,0,speed);
		
		determineScaling(layer1);
		
		speed = 0.01f*6f;
		layer2 = new Layer("city2",0,0,speed);
		layer2sub = new Layer("city2",SCREENWIDTH,0,speed);
		
		speed = 0.009f*4f;
		layer3 = new Layer("hills",0,0,speed);
		float trueLength = layer3.getImg().getWidth()*scaleValue;
		layer3sub = new Layer("hills",(int)trueLength,0,speed);
		
		speed = 0.007f*4f;
		layer4 = new Layer("stars2",0,0,speed);
		layer4sub = new Layer("stars2",(int)trueLength,0,speed);
		
		speed = 0.009f;
		layer5 = new Layer("stars1",0,0,speed);
		layer5sub = new Layer("stars1",(int)trueLength,0,speed);
		
		moon = new Layer("moon",0,0,0);

		layers.add(layer5);
		layers.add(layer5sub);

		layers.add(layer4);
		layers.add(layer4sub);
		
		layers.add(moon);
		
		layers.add(layer3);
		layers.add(layer3sub);
		
		layers.add(layer2);
		layers.add(layer2sub);
		
		layers.add(layer1);
		layers.add(layer1sub);
	}
	
	private void determineScaling(Layer lay) {
		float temp = (float)SCREENWIDTH/lay.getImg().getWidth();
		if(temp>scaleValue)
			scaleValue = temp;
	}

	public void draw(){
		for(Layer lay:layers){
			lay.draw(scaleValue);
		}
	}
	
	public void tick(){
		for(Layer lay:layers){
			lay.setX(lay.getX()-lay.getSpeed());
			lay.outOfFrame(scaleValue);
		}
	}
	
}
