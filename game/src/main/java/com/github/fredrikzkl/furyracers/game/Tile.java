package com.github.fredrikzkl.furyracers.game;

import java.util.HashMap;
import java.util.Map;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

public class Tile {
	/*
	 * TODO Map tiles to spritesheet tiles
	 */
	enum Tiles {
		BG1(0, 0), BG2(1, 0), BG3(2, 0), BG4(3, 0), BG5(4, 0), BG6(5, 0), BG7(6, 0), BG8(7, 0), BG9(8, 0), BG10(9,
				0), BG11(10, 0), BG12(11, 0), BG13(12, 0), BG14(13, 0), BG15(0, 1), BG16(1, 1), BG17(2, 1), BG18(3,
						1), BG19(4, 1), BG20(5, 1), BG21(6, 1), BG22(7, 1), BG23(8, 1), BG24(9, 1), BG25(10,
								1), BG26(11, 1), BG27(12, 1), BG28(13, 1),
		BG29(0,2),BG30(1,2),BG31(2,2),BG32(3,2),BG33(4,2),BG34(5,2),BG35(6,2),BG36(7,2),BG37(8,2),BG38(9,2),BG39(10,2), BG40(11,2), BG41(12,2), BG42(13,2);

		int x, y;

		Tiles(int x, int y) {
			this.x = x;
			this.y = y;
		}

	}

	static SpriteSheet sheet;
	public static Map<Integer, Tiles> tiles;
	public static int size = 32;

	public static void init() {

		Image bg;
		try {
			bg = new Image("Sprites/bg.png");
			sheet = new SpriteSheet(bg, size, size);
		} catch (SlickException e) {
			e.printStackTrace();
		}
		tiles = new HashMap<>();
		
		/*
		 * TODO Map tiles to spritesheet tiles
		 */
		for (int id = 1; id <= Tiles.values().length; id++) {
			tiles.put(id, Tiles.values()[id - 1]);
		}
		
	}

	public static Image resolveTile(int id) {
		Tiles tile = tiles.get(id);
		if (tile == null) {
			tile = Tiles.BG1;
		}
		return sheet.getSubImage(tile.x * size, tile.y * size, size, size);
	}
}
