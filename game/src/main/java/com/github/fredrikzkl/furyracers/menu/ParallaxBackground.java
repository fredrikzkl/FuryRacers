package com.github.fredrikzkl.furyracers.menu;

import java.util.ArrayList;
import java.util.List;

import com.github.fredrikzkl.furyracers.Application;
import com.github.fredrikzkl.furyracers.assets.Sprites;

class ParallaxBackground {
	
	private final int SCREENWIDTH = Application.screenSize.width;
	
	private List<Layer> layers;
	
	ParallaxBackground(){
		
		layers = new ArrayList<Layer>();
		
		int subLayerStartX = SCREENWIDTH;
		
		float speed = 0.009f;
		Layer layer5 = new Layer(Sprites.stars1,0,0,speed);
		Layer layer5sub = new Layer(Sprites.stars1,subLayerStartX,0,speed);
		
		speed = 0.028f;
		Layer layer4 = new Layer(Sprites.stars2,0,0,speed);
		Layer layer4sub = new Layer(Sprites.stars2,subLayerStartX,0,speed);
		
		speed = 0.036f;
		Layer layer3 = new Layer(Sprites.hills,0,0,speed);
		Layer layer3sub = new Layer(Sprites.hills,subLayerStartX,0,speed);

		speed = 0.06f;
		Layer layer2 = new Layer(Sprites.city2,0,0,speed);
		Layer layer2sub = new Layer(Sprites.city2,subLayerStartX,0,speed);
		
		speed = 0.12f;
		Layer layer1 = new Layer(Sprites.city1,0,0,speed);
		Layer layer1sub = new Layer(Sprites.city1,subLayerStartX,0,speed);
		
		Layer moon = new Layer(Sprites.moon,0,0,0);

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

	void draw(){
		for(Layer layer : layers){
			tick(layer);
			layer.draw();
		}
	}
	
	private void tick(Layer layer){
		
		float newXpos = layer.getX() - layer.getSpeed(); 
		layer.setX(newXpos);
	}
	
}
