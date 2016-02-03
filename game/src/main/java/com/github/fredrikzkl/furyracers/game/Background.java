package com.github.fredrikzkl.furyracers.game;

import java.awt.Rectangle;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

@SuppressWarnings("serial")
public class Background extends Rectangle {

	public int id;
	public Image image;

	public Background(int x, int y, int id) {
		setBounds(new Rectangle(x, y, Tile.size, Tile.size));
		this.id = id;
		image = Tile.resolveTile(id);
	}

	public void render(Graphics g) {
		g.drawImage(image, x*Tile.size, y*Tile.size);
	}

}
