package com.github.fredrikzkl.furyracers.network;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.websocket.DeploymentException;
import javax.websocket.EncodeException;
import javax.websocket.Session;

import org.glassfish.tyrus.client.ClientManager;
import org.newdawn.slick.SlickException;

import com.github.fredrikzkl.furyracers.Application;
import com.github.fredrikzkl.furyracers.Menu;
import com.github.fredrikzkl.furyracers.game.Car;
import com.github.fredrikzkl.furyracers.game.GameCore;
import com.github.fredrikzkl.furyracers.game.Player;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GameSession {

	private final GameCore game;
	private Menu menu;
	private static Session backend;
	private int playerNumber;

	private static ArrayList<Player> players;

	private static int gameState;

	public GameSession(GameCore game, Menu menu) throws DeploymentException {
		this.game = game;
		this.menu = menu;
		players = new ArrayList<Player>();
		playerNumber = 1;
	}

	public void connect() throws URISyntaxException, IOException, DeploymentException {
		ClientManager client = ClientManager.createClient();
		client.connectToServer(WebsocketClient.class, new URI("ws://localhost:3001/ws"));
	}

	public void onOpen(Session session) throws IOException, EncodeException {
		backend = session;
		
		sendToBackend("identify", "game");
		sendToBackend("get ip", "");
	}

	private static void sendToBackend(String action, String data) throws IOException, EncodeException {
		backend.getBasicRemote().sendObject(Json.createObjectBuilder()
				.add("action", action)
				.add("data", data).build());
	}
	
	
	private static void sendToClient(String recieverId, String action, String data) throws IOException, EncodeException{
		 JsonObject message = Json.createObjectBuilder()
		     .add("to", recieverId)
		     .add("action", "pass through")
		     .add("data", Json.createObjectBuilder()
		         .add("action", action)
		         .add("data", data)).build();
		 
		backend.getBasicRemote().sendObject(message);
	}
	
	public static void toggleRumbling(String id) throws IOException, EncodeException{
		sendToClient(id, "rumble", "");
	}
	
	public static void carColorToController(String recieverId, String colorCode) throws IOException, EncodeException{
		sendToClient(recieverId, "set color", colorCode);
	}
	
	public static void carModelToController(String recieverId, String carModel) throws IOException, EncodeException{
		sendToClient(recieverId, "set carModel", carModel);
	}

	public void onMessage(Session session, String message) throws IOException, EncodeException, SlickException {
		JsonReader jsonReader = Json.createReader(new StringReader(message));
		JsonObject jsonObj = jsonReader.readObject();
		jsonReader.close();

		if (!(jsonObj.containsKey("action") && jsonObj.containsKey("data"))) {
			return;
		}

		String action = jsonObj.getString("action");

		switch (action) {
		
		case "set username":{
			
			action = "get username";
			
		}
		case "get username": {
			JsonArray client = jsonObj.getJsonArray("data");

			String id = client.get(0).toString();
			String username = client.get(1).toString();
			
			for(Player player : players ){
				if(player.getId().equals(id)){
					player.setUsername(username);
				}
			}
			break;
		}
		
		case "added client": {
			menu.printConsole("New player connecting...");
			String id = jsonObj.getString("data");
			addPlayer(id, players.size()+1);
			break;
		}

		case "dropped client": {
			String id = jsonObj.getString("data");
			int spot = 0;
			for (Player player : players) {
				if (player.getId().equals(id)) {
					spot = player.getPlayerNr();
				}
			}
			players.remove(spot-1);
			break;
		}

		case "play as": {
			
			String id = jsonObj.getString("from");
			String data = jsonObj.getString("data");
			
			if (data.equals(""))
				break;

			sendToBackend("get username", id);

			break;
		}

		case "buttonDown": {
			if (!jsonObj.containsKey("from"))
				return;
			
			String data = jsonObj.getJsonNumber("data").toString();
			String from = jsonObj.getString("from");
			buttonDown(from, data);
			
			break;
		}
		case "buttonUp": {
			
			if (!jsonObj.containsKey("from"))
				return;
			
			String data = jsonObj.getJsonNumber("data").toString();
			String from = jsonObj.getString("from");
			buttonUp(from, data);

			break;
		}

		case "get ip": {
			String ip = jsonObj.getJsonString("data").toString();
			game.setIP(ip);
			menu.setIP(ip);
		}
	}
}
	private void buttonDown(String from, String data) throws IOException, EncodeException, SlickException{

		boolean playerExists = false;
		for (Player player : players) {

			if (player.getId().equals(from)) {
				playerExists = true;
				if (getGameState() == 0) {
					menu.buttonDown(data, player.getPlayerNr());
				}else{
					player.getCar().buttonDown(data);
				}
			}
		}
		
		if(!playerExists){
			menu.printConsole( from + " doesn't exist as a player! Adding to plyerlist..");
			addPlayer(from, players.size()+1);
		}
	}
	
	private void buttonUp(String from, String data){
		
		for (Player player : players) {
			if (player.getId().equals(from)) {
				if (getGameState() == 1) { 
					player.getCar().buttonUp(data);
				}
			}
		}
	}

	public void closeConnection() throws IOException, EncodeException{
		sendToBackend("disconnect", "");
	}

	private void addPlayer(String id, int playerNr) throws IOException, EncodeException, SlickException {
		if (players.size() > 4) {
			menu.printConsole("The game is full!");
			printPlayers();
		} else {
			players.add(new Player(id, playerNr));
			sendToBackend("get username", id);
			menu.updatePlayerList(players);
			menu.printConsole(id + " joined the game! Assigned as player: " + playerNr);
		}
	}

	private void printPlayers() {
		int count = 0;
		System.out.println("--Player list--");
		for (Player player : players) {
			System.out.println(players.get(count));
			count++;
		}
	}

	public static int getGameState() {
		return gameState;
	}

	public static void setGameState(int gameState) {
		GameSession.gameState = gameState;
	}

}
