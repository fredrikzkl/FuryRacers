package com.github.fredrikzkl.furyracers.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.github.fredrikzkl.furyracers.Application;
import com.github.fredrikzkl.furyracers.assets.Fonts;
import com.github.fredrikzkl.furyracers.assets.Sounds;
import com.github.fredrikzkl.furyracers.assets.Sprites;
import com.github.fredrikzkl.furyracers.car.Car;

public class ScoreBoard {

	private long 
	toMenuRealTimer;
	
	private int 
	timeToMenu, scoreBoardFontHeight, 
	origScreenWidth;
	
	private boolean 
	closedPlayed, movePlayed, 
	toMenuTimerSet, returnToMenuTimerDone;

	private float 
	resultsBoardWidth, highscoreBoardWidth, 
	endXposResults, screenHeight, margin, 
	endOfResultsY, movementPerUpdate, screenWidth,
	scalingValue, resultPosX, resultPosY, 
	highScorePosX, highScorePosY;
	
	private String toMenuTimer = "";
	
	private ArrayList<Car> cars;
	private ArrayList<Player> players;

	public ScoreBoard(List<Car> cars2, List<Player> players2) {

		this.cars = (ArrayList<Car>) cars2;
		this.players = (ArrayList<Player>) players2;
				
		timeToMenu = 10;
		scoreBoardFontHeight = Fonts.scoreBoardHeader.getHeight();
		screenWidth = Application.screenSize.width;
		screenHeight = Application.screenSize.height;
		origScreenWidth = 1366; 
		scalingValue = (screenWidth*1.4f) / origScreenWidth;
		resultsBoardWidth = Sprites.resultsBoard.getWidth() * scalingValue;
		highscoreBoardWidth = Sprites.highscoresBoard.getWidth() * scalingValue;
		margin = resultsBoardWidth / 10;

		resultPosX = - resultsBoardWidth;
		highScorePosX = screenWidth + highscoreBoardWidth;
		resultPosY = Application.screenSize.height / 10;
		highScorePosY = Application.screenSize.height / 10;
		endXposResults = screenWidth / 2 - resultsBoardWidth;
		movementPerUpdate = screenWidth/342f;

		movePlayed = closedPlayed = returnToMenuTimerDone = false;
	}

	public void drawScoreBoard() {
		
		if(!movePlayed){
			Sounds.scoreboardMove.play();
			movePlayed = true;
		}
		
		drawBoardBackgrounds();
		
		if (resultPosX < endXposResults) {
			resultPosX += movementPerUpdate;
			highScorePosX -= movementPerUpdate*3/2;
		} else {
			drawScores();
			drawHighScoresHeader();
			drawMenuCountdown();
			if(!closedPlayed){
				Sounds.scoreboardClose.play();
				closedPlayed = true;
			}
		}
	}
	
	private void drawBoardBackgrounds(){
		
		Sprites.resultsBoard.draw(resultPosX, resultPosY, scalingValue);
		Sprites.highscoresBoard.draw(highScorePosX, highScorePosY, scalingValue);
	}
	
	private void drawScores() {
		
		drawResultsHeader();
		drawRaceResults();

		drawTotalScoreHeader();
		drawTotalScores();
	}
	
	private void drawResultsHeader(){
		
		Fonts.scoreBoardHeader.drawString(resultPosX + margin, resultPosY + margin, "Results:", Fonts.headerColor);
	}
	
	private void drawRaceResults(){
		
		ArrayList<Car> sortedCars = cars;
		Collections.sort(sortedCars);
		int amountOfCars = sortedCars.size();
		int startIndex = amountOfCars - 1;
		float yPos = resultPosY;
		
		for (int i = startIndex; i > -1; i--) {
			Car car = sortedCars.get(i);
			
			String player = "Player " + car.getPlayerNr() + ": ";
			String time = car.getTimeElapsed() + " Score: +" + (i + 1);
			
			float yOffset = margin + scoreBoardFontHeight *(amountOfCars-i);
			float xPos = resultPosX + margin;
			yPos = resultPosY + yOffset;
			
			Fonts.scoreBoardText.drawString(xPos, yPos, player + time);
		}
		
		endOfResultsY = yPos; 
	}
	
	private void drawTotalScoreHeader(){
		
		float xPos = resultPosX + margin;
		float yPos = endOfResultsY + margin;
		
		Fonts.scoreBoardHeader.drawString(xPos, yPos, "Total Score:", Fonts.headerColor);
	}
	
	private void drawTotalScores(){
		
		ArrayList<Player> sortedPlayers = players;
		Collections.sort(sortedPlayers);
		
		int i = 0;
		float xPos = resultPosX + margin;
		
		for (Player player : sortedPlayers) {
			
			String usernameWithScores = player.getUsername() + ": " + player.getScore();
			float yOffset = margin + scoreBoardFontHeight*(i+1);
			float yPos = endOfResultsY*margin*2 + yOffset;
			
			Fonts.scoreBoardText.drawString(xPos, yPos, usernameWithScores);
			i++;
		}
	}
	
	private void drawHighScoresHeader() {
		
		Fonts.scoreBoardHeader.drawString(highScorePosX + margin, highScorePosY + margin, "High Scores:", Fonts.headerColor);

	}
	
	private void drawMenuCountdown() {
		if (!toMenuTimerSet) {
			toMenuRealTimer = System.nanoTime();
			toMenuTimerSet = true;
		}
		long currentTime = System.nanoTime();
		long elapsed = TimeUnit.NANOSECONDS.toSeconds(currentTime - toMenuRealTimer);
		toMenuTimer = "" + (timeToMenu - elapsed);
		
		int stringWidth = Fonts.scoreBoardHeader.getWidth(toMenuTimer);
		float xPos = screenWidth - stringWidth;
		float yPos = screenHeight - scoreBoardFontHeight;
		
		Fonts.scoreBoardHeader.drawString(xPos, yPos, toMenuTimer);

		if (timeToMenu - elapsed == 0) {
			returnToMenuTimerDone = true;
		}
	}
	
	public boolean isReturnToMenuTimerDone() {
		return returnToMenuTimerDone;
	}
}
