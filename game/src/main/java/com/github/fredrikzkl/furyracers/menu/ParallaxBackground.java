package com.github.fredrikzkl.furyracers.menu;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Image;

import com.github.fredrikzkl.furyracers.Application;
import com.github.fredrikzkl.furyracers.assets.Sprites;

class ParallaxBackground {
	
	private final int SCREENWIDTH = Application.screenSize.width;
	
	private List<Layer> layers;
	
	ParallaxBackground(){
		
		layers = new ArrayList<Layer>();
		
		final int subLayerStartX = SCREENWIDTH;
		
		final float frontLayerSpeed = 0.12f;
		
		addLayer(Sprites.stars1, 0, 0);
		addLayer(Sprites.stars2, 0, 0);
		addLayer(Sprites.moon,0,0);
		addLayer(Sprites.hills, subLayerStartX, frontLayerSpeed/4);
		addLayer(Sprites.city2, subLayerStartX, frontLayerSpeed/2);
		addLayer(Sprites.city1, subLayerStartX, frontLayerSpeed);
	}

	private void addLayer(Image sprite, int xOffset, float speed){
		
		Layer layer = new Layer(sprite, 0, 0, speed);
		layers.add(layer);
		
		if(speed != 0){
			Layer layerCopy = new Layer(sprite, xOffset, 0, speed);
			layers.add(layerCopy);
		}
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
