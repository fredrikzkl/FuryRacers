package com.github.fredrikzkl.furyracers.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;

import com.github.fredrikzkl.furyracers.Application;
import com.github.fredrikzkl.furyracers.Fonts;
import com.github.fredrikzkl.furyracers.Sounds;

public class ScoreBoard {

	private Image results, highscores;
	float resultPosX, resultPosY, highScorePosX, highScorePosY;

	private boolean toMenuTimerSet, returnToMenuTimerDone;
	private long toMenuRealTimer;
	private String toMenuTimer = "";
	private int timeToMenu = 10;
	private float headerSize, textSize, margin;
	private int textTimer = 0;
	int scoreBoardFontHeight;
	
	float screenWidth;
	int origScreenWidth;; 
	float scalingValue;

	public ArrayList<Car> cars;
	public ArrayList<Player> players;
	
	private boolean closedPlayed, movePlayed;

	public ScoreBoard(List<Car> cars2, List<Player> players2) {

		this.cars = (ArrayList<Car>) cars2;
		this.players = (ArrayList<Player>) players2;
		
		returnToMenuTimerDone = false;
		
		screenWidth = Application.screenSize.width;
		origScreenWidth = 1366; 
		scalingValue = (screenWidth*1.3f) / origScreenWidth;

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

		movePlayed = closedPlayed = false;
		
	}

	public void drawScoreBoard() {
		
		float resultsBoardWidth = results.getWidth() * scalingValue;
		float highscoreBoardWidth = highscores.getWidth() * scalingValue;
		float speed = 4f;
		margin = resultsBoardWidth / 10;
		float endXposResults = screenWidth / 3 - resultsBoardWidth / 2;
		float endXposHighscore = screenWidth / 2 - highscoreBoardWidth/4;
		
		drawBoardBackground();
		
		if(!movePlayed){
			Sounds.scoreboardMove.play();
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
				Sounds.scoreboardClose.play();
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
		Fonts.scoreBoardHeader.drawString(x - (headerSize * 1.5f), Application.screenSize.height - headerSize, toMenuTimer);

		if (timeToMenu - elapsed == 0) {
			returnToMenuTimerDone = true;
		}
	}

	private void printHighScores(float endHighScorePosX) {
		
		Fonts.scoreBoardHeader.drawString(highScorePosX + margin, highScorePosY + margin, "High Scores:", Fonts.headerColor);

	}

	private void printScores(float endXposResults) {
	
		drawResultsHeader();
		drawRaceResults();

		drawTotalScoreHeader();
		drawTotalScores();

		textTimer++;
	}
	
	private void drawRaceResults(){
		
		ArrayList<Car> sortedCars = cars;
		Collections.sort(sortedCars);
		
		for (int i = sortedCars.size() - 1; i >= 0; i--) {
			Fonts.scoreBoardText.drawString(resultPosX + margin, resultPosY + margin + scoreBoardFontHeight,
					"Player " + sortedCars.get(i).getPlayerNr() + ": " + sortedCars.get(i).getTimeElapsed() + " Score: "
							+ "+" + (i + 1));
			scoreBoardFontHeight += scoreBoardFontHeight;
		}
	}
	
	private void drawResultsHeader(){
		
		Fonts.scoreBoardHeader.drawString(resultPosX + margin, resultPosY + margin, "Results:", Fonts.headerColor);
		scoreBoardFontHeight = Fonts.scoreBoardHeader.getHeight();
	}
	
	private void drawTotalScoreHeader(){
		
		Fonts.scoreBoardHeader.drawString(resultPosX + margin, resultPosY + margin + headerSize + scoreBoardFontHeight, "Total Score:",
				Fonts.headerColor);
		scoreBoardFontHeight += scoreBoardFontHeight;
	}
	
	private void drawTotalScores(){
		
		ArrayList<Player> sortedPlayers = players;
		Collections.sort(sortedPlayers);

		for (int i = 0; i < sortedPlayers.size(); i++) {
			
			String usernameAndScores = sortedPlayers.get(i).getUsername() + ": " + sortedPlayers.get(i).getScore();
			Fonts.scoreBoardText.drawString(resultPosX + margin, resultPosY + margin + scoreBoardFontHeight,
					usernameAndScores);
			scoreBoardFontHeight += scoreBoardFontHeight;
		}
	}
	
	public boolean isReturnToMenuTimerDone() {
		return returnToMenuTimerDone;
	}

}
