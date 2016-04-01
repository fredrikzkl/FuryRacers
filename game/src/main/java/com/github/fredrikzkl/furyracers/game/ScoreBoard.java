package com.github.fredrikzkl.furyracers.game;

import java.awt.Font;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.util.ResourceLoader;

import com.github.fredrikzkl.furyracers.Application;

public class ScoreBoard {

	private Image results, highscores;
	float resultPosX, resultPosY, highScorePosX, highScorePosY;

	private boolean toMenuTimerSet, returnToMenuTimerDone;
	private long toMenuRealTimer;
	private String toMenuTimer = "";
	private int timeToMenu = 10;

	private TrueTypeFont scoreBoardHeader;
	private TrueTypeFont scoreBoardText;
	private float headerSize, textSize, marginX;
	private int textTimer = 0;
	int scoreBoardLength;
	
	float screenWidth;
	int origScreenWidth;; 
	float scalingValue;

	private Color headerColor = new Color(221, 0, 0);

	public ArrayList<Car> cars;
	public ArrayList<Player> players;
	
	//-----------------------------------//
	private Sound close;
	private Sound move;
	private boolean closedPlayed, movePlayed;

	public ScoreBoard(List<Car> cars2, List<Player> players2) {

		this.cars = (ArrayList<Car>) cars2;
		this.players = (ArrayList<Player>) players2;
		
		returnToMenuTimerDone = false;
		headerSize = 30f;
		textSize = 24f;
		
		screenWidth = Application.screenSize.width;
		origScreenWidth = 1366; 
		scalingValue = (screenWidth*1.3f) / origScreenWidth;

		InputStream inputStream;
		try {
			inputStream = ResourceLoader.getResourceAsStream("Font/Orbitron-Regular.ttf");
			Font awtFont1 = Font.createFont(Font.TRUETYPE_FONT, inputStream);
			Font awtFont2;

			awtFont1 = awtFont1.deriveFont(headerSize); // set font size
			awtFont2 = awtFont1.deriveFont(textSize);

			scoreBoardHeader = new TrueTypeFont(awtFont1, true);
			scoreBoardText = new TrueTypeFont(awtFont2, true);

		} catch (Exception e) {
			e.printStackTrace();
		}

		String path = "/Sprites/UI/";
		try {
			results = new Image(path + "border.png");
			highscores = new Image(path + "border.png");
		} catch (SlickException e) {
			e.printStackTrace();
		}

		resultPosX = - (results.getWidth() * 2);
		highScorePosX = (float) (screenWidth + (results.getWidth() / 1.3));
		resultPosY = Application.screenSize.height / 10;
		highScorePosY = Application.screenSize.height / 10;
		
		initSounds();
		movePlayed = closedPlayed = false;
		
	}

	public void drawScoreBoard() {
		
		float resultsBoardWidth = results.getWidth() * scalingValue;
		float highscoreBoardWidth = highscores.getWidth() * scalingValue;
		float speed = 4f;
		marginX = resultsBoardWidth / 10;
		float endXposResults = screenWidth / 3 - resultsBoardWidth / 2;
		float endXposHighscore = screenWidth / 2 - highscoreBoardWidth/4;
		
		drawBoardBackground();
		
		if(!movePlayed){
			move.play();
			movePlayed = true;
		}
		
		if (resultPosX < endXposResults) {
			resultPosX += speed;
			highScorePosX -= speed;
		} else {
			printScores(endXposResults);
			printHighScores(endXposHighscore);
			printTimer(screenWidth);
			if(!closedPlayed){
				close.play();
				closedPlayed = true;
			}
		}

	}

	private void drawBoardBackground(){
		
		results.draw(resultPosX, resultPosY, scalingValue);
		highscores.draw(highScorePosX, highScorePosY, scalingValue);
	}
	private void printTimer(float x) {
		if (!toMenuTimerSet) {
			toMenuRealTimer = System.nanoTime();
			toMenuTimerSet = true;
		}
		long currentTime = System.nanoTime();
		long elapsed = TimeUnit.NANOSECONDS.toSeconds(currentTime - toMenuRealTimer);
		toMenuTimer = "" + (timeToMenu - elapsed);
		scoreBoardHeader.drawString(x - (headerSize * 1.5f), Application.screenSize.height - headerSize, toMenuTimer);

		if (timeToMenu - elapsed == 0) {
			returnToMenuTimerDone = true;
		}
	}

	private void printHighScores(float endHighScorePosX) {
		
		scoreBoardHeader.drawString(highScorePosX + marginX, highScorePosY + marginX, "High Scores:", headerColor);

	}

	private void printScores(float endXposResults) {
	

		scoreBoardHeader.drawString(resultPosX + marginX, resultPosY + marginX, "Results:", headerColor);
		scoreBoardLength = (int) headerSize;
		
		ArrayList<Car> sortedCars = cars;
		Collections.sort(sortedCars);

		for (int i = sortedCars.size() - 1; i >= 0; i--) {
			scoreBoardText.drawString(resultPosX + marginX, resultPosY + marginX + scoreBoardLength,
					"Player " + sortedCars.get(i).getPlayerNr() + ": " + sortedCars.get(i).getTimeElapsed() + " Score: "
							+ "+" + (i + 1));
			scoreBoardLength += textSize;
		}

		scoreBoardHeader.drawString(resultPosX + marginX, resultPosY + marginX + headerSize + scoreBoardLength, "Total Score:",
				headerColor);
		scoreBoardLength += headerSize * 2;

		ArrayList<Player> sortedPlayers = players;
		Collections.sort(sortedPlayers);

		for (int i = 0; i < sortedPlayers.size(); i++) {
			scoreBoardText.drawString(resultPosX + marginX, resultPosY + marginX + scoreBoardLength,
					sortedPlayers.get(i).getUsername() + ": " + sortedPlayers.get(i).getScore());
			scoreBoardLength += textSize;

		}

		textTimer++;
	}
	
	
	public boolean isReturnToMenuTimerDone() {
		return returnToMenuTimerDone;
	}
	
	public void initSounds(){
		String path = "/Sound/scoreBoard/";
		
		try {
			close = new Sound(path + "closed.ogg");
			move = new Sound(path + "move.ogg");
		} catch (SlickException e) {
			System.out.println("Could not load scoreboard soundfiles!");
			e.printStackTrace();
		} 
	}

}
