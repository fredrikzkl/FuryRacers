package com.github.fredrikzkl.furyracers.game;

import java.awt.Color;
import java.awt.Rectangle;

import org.newdawn.slick.Graphics;

import com.github.fredrikzkl.furyracers.Application;

public class Background extends Rectangle {

	public int[] id = {-1,-1};
	
	public Background(Rectangle rect, int id[]){
		setBounds(rect);
		this.id = id;
	}
	
	public void render(Graphics g){
		g.drawImage(Tile.background, 
				x- (float)Application.oX,
				y - (float)Application.oY,
				x + width - (float)Application.oX,
				y + height - (float)Application.oY,
				(float)id[0] * Tile.size,
				(float)id[1] * Tile.size,
				(float)id[0] * Tile.size + Tile.size,
				(float)id[1] * Tile.size + Tile.size,
				Color.GREEN
				);
	}

}
