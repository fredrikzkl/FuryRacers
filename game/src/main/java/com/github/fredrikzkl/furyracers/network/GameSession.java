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
		System.out.println("onOpen");
		backend = session;
		
		sendToBackend("identify", "game");
		sendToBackend("get ip", "");
	}

	private static void sendToBackend(String action, String data) throws IOException, EncodeException {
		backend.getBasicRemote().sendObject(Json.createObjectBuilder().add("action", action).add("data", data).build());
	}
	
	public static Map<String, String> config = null;
	
	private static void sendToClient(String action, String recieverId, String data){
		 JsonBuilderFactory factory = Json.createBuilderFactory(config);
		 JsonObject value = factory.createObjectBuilder()
		     .add("to", recieverId)
		     .add("action", "pass through")
		     .add("data", factory.createObjectBuilder()
		         .add("action", action)
		         .add("data", data)).build();
		 try {
			backend.getBasicRemote().sendObject(value);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (EncodeException e) {
			e.printStackTrace();
		}
	}
	
	public static void toggleRumbling(String id) throws IOException, EncodeException{
		sendToClient("rumble", id, "");
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
			System.out.println(id);
			
			System.out.println("from" + players.get(0).getId());
			
			for(int i = 0; i < players.size(); i++){
				if(players.get(i).getId().equals(id)){
					System.out.println(username);
					players.get(i).setUsername(username);
				}
			}
			break;
		}
		
		case "added client": {
			System.out.println("New player connecting...");
			menu.printConsole("New player connecting...");
			String id = jsonObj.getString("data");
			addPlayer(id);
			break;
		}

		case "dropped client": {
			String id = jsonObj.getString("data");
			int spot = 0;
			String username = id;
			for (Player player : players) {
				if (player.getId().equals(id)) {
					spot = player.getPlayerNr();
					username = player.getUsername();
				}
			}
			players.remove(spot-1);
			System.out.println("Player " + username + " dropped! Player slot " + spot + " removed");
			break;
		}

		case "play as": {
			String id = jsonObj.getString("from");
			String data = jsonObj.getString("data");

			if (data.equals("")) {
				System.out.println("data.equals('')");

				break;
			}
			
			System.out.println("GAAAAY");

			sendToBackend("get username", id);

			break;
		}

		case "buttonDown": {
			String data = jsonObj.getJsonNumber("data").toString();
			String from = jsonObj.getString("from");

			if (!checkIfPlayerExist(from)) {

				for (int i = 0; i < players.size(); i++) {

					if (players.get(i).getId().equals(from)) {
						if (getGameState() == 0) {
							menu.buttonDown(data, players.get(i).getPlayerNr());
						}else{
							for (Car car : game.cars) {
								if (from.equals(car.id)) {
									car.buttonDown(data);
								}
							}
						}
					}
				}
			}

			if (!jsonObj.containsKey("from")) {
				return;
			}
			from = jsonObj.getString("from");
			break;
		}
		case "buttonUp": {
			String data = jsonObj.getJsonNumber("data").toString();
			String from = jsonObj.getString("from");

			for (int i = 0; i < players.size(); i++) {
				if (players.get(i).getId().equals(from)) {
					if (getGameState() == 1) { // Checks if the game is in the
						for (Car car : game.cars) {
							if (from.equals(car.id)) {
								car.buttonUp(data);
								//car.disableKeyboardInput();
							}
						}

					}

				}
			}

			if (!jsonObj.containsKey("from")) {
				return;
			}
			from = jsonObj.getString("from");
			break;
		}

		case "get ip": {
			String ip = jsonObj.getJsonString("data").toString();
			game.setIP(ip);
			menu.setIP(ip);

		}
	}
}

	public void closeConnection() throws IOException, EncodeException{
		sendToBackend("disconnect", "");
	}
	private boolean checkIfPlayerExist(String from) throws IOException, EncodeException, SlickException {
		boolean notExist = true;
		for (Player player : players) {
			if (player.getId().equals(from)) {
				notExist = false;
			}
		}
		if (notExist) {
			System.out.println("Player: '" + from + "' doesnt exist as a player! Added to plyerlist..");
			menu.printConsole("Player: '" + from + "' doesnt exist as a player! Adding to plyerlist..");
			addPlayer(from);
		}

		return notExist;
	}

	private void addPlayer(String id) throws IOException, EncodeException, SlickException {
		if (players.size() > 4) {
			System.out.println("The game is full!");
			menu.printConsole("The game is full!");
			printPlayers();
		} else {
			players.add(new Player(id, playerNumber));
			sendToBackend("get username", id);
			menu.updatePlayerList(players);
			menu.printConsole("Player '" + id + "' joined the game! Assigned as player: " + playerNumber);
			playerNumber++;
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
