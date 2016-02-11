package com.github.fredrikzkl.furyracers.network;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.websocket.DeploymentException;
import javax.websocket.EncodeException;
import javax.websocket.Session;

import org.glassfish.tyrus.client.ClientManager;
import org.newdawn.slick.SlickException;

import com.github.fredrikzkl.furyracers.game.GameCore;
import com.github.fredrikzkl.furyracers.game.Player;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameSession {

	private final GameCore game;
	
	private Session backend;
	
	private int playerNumber;
	
	private String player1;
    private String player2;
    private String player3;
    private String player4;
    private String player1Username = "Player 1";
    private String player2Username = "Player 2";
    
    private List<Player> players;
	
	public GameSession(GameCore game) throws DeploymentException {
		this.game = game;
		players = new ArrayList<Player>();
		playerNumber = 1;
	}
	
	public void connect() throws URISyntaxException, IOException, DeploymentException {
		ClientManager client = ClientManager.createClient();
        client.connectToServer(WebsocketClient.class, new URI("ws://localhost:3001/ws"));
	}
	
	public void onOpen(Session session)throws IOException, EncodeException {
		System.out.println("onOpen");
		backend = session;
		player1 = "";
		player2 = "";
		sendToBackend("identify", "game");
	}

	private void sendToBackend(String action, String data) throws IOException, EncodeException {
		backend.getBasicRemote().sendObject(Json.createObjectBuilder()
				.add("action", action)
				.add("data", data)
				.build());
	}
	

	
	public void onMessage(Session session, String message)throws IOException, EncodeException, SlickException {
		JsonReader jsonReader = Json.createReader(new StringReader(message));

        JsonObject jsonObj = jsonReader.readObject();
   
        jsonReader.close();
        
        if(!(jsonObj.containsKey("action") && jsonObj.containsKey("data"))){
        	return;
        }
        
        String action = jsonObj.getString("action");

        switch (action) {
            case "get username": {
                JsonArray client = jsonObj.getJsonArray("data");
                
                String id = client.get(0).toString();
                String username = client.get(1).toString();
                
                
                if (player1.equals(id)) {
                    player1Username = username;
                } else if (player2.equals(id)) {
                    player2Username = username;
                }
				
                break;
            }
            
            case "added client": {
            	System.out.println("New player connecting...");
            	String id = jsonObj.getString("data");
            	addPlayer(id);
            	break;
            }

            case "dropped client": {
                String id = jsonObj.getString("data");
                
                if (player1.equals(id)) {
                    player1 = "";
                } else if (player2.equals(id)) {
                    player2 = "";
                }
                

                System.out.println("Player " + id + " dropped!");

                break;
            }

            case "play as": {
                String id = jsonObj.getString("from");
                String data = jsonObj.getString("data");

                if (data.equals("")) {
                   System.out.println("data.equals('')");

                    break;
                }
                
                if (player1.equals("") && data.equals("left")) {
                    player1 = id;
                } else if (player2.equals("") && data.equals("right")) {
                    player2 = id;
                }
                

                sendToBackend("get username", id);

                break;
            }

            case "buttonDown": {	
                String data = jsonObj.getJsonNumber("data").toString();
                
                String from = jsonObj.getString("from");
                
                for(int i = 0; i < players.size(); i++){
                	if(players.get(i).getId().equals(from)){
                		if(players.get(i).getPlayerNr() == 1)game.p1.buttonDown(data);
                		if(players.get(i).getPlayerNr() == 2)game.p2.buttonDown(data);
                		if(players.get(i).getPlayerNr() == 3)game.p2.buttonDown(data);
                		if(players.get(i).getPlayerNr() == 4)game.p2.buttonDown(data);
                	}
                }
                if (!jsonObj.containsKey("from")) {
                    return;
                }
                from = jsonObj.getString("from");
                break;
            }
            case "buttonUp":{
            	String data = jsonObj.getJsonNumber("data").toString();
            	String from = jsonObj.getString("from");
            	
            	for(int i = 0; i < players.size(); i++){
                	if(players.get(i).getId().equals(from)){
                		if(players.get(i).getPlayerNr() == 1)game.p1.buttonUp(data);
                		if(players.get(i).getPlayerNr() == 2)game.p2.buttonUp(data);
                		if(players.get(i).getPlayerNr() == 3)game.p2.buttonUp(data);
                		if(players.get(i).getPlayerNr() == 4)game.p2.buttonUp(data);
                	}
                }
                if (!jsonObj.containsKey("from")) {
                     return;
                }
                from = jsonObj.getString("from");
                break;
            }
        }
    }

	private void checkIfPlayerExist(String from) throws IOException, EncodeException, SlickException {
		boolean notExist = true;
		for(Player player:players){
			if(from == player.getId()){
				notExist = false;
			}
		}
		if(notExist){
			System.out.println("Player: '" + from + "' doesnt exist as a player! Added to plyerlist..");
			addPlayer(from);
		}
		
	}
	
	private void addPlayer(String id) throws IOException, EncodeException, SlickException{
		if(players.size() > 4){
			System.out.println("The game is full!");
		}else{
	    	players.add(new Player(id, playerNumber));
	    	sendToBackend("get username", id);
	    	game.createPlayer(playerNumber, id);
	    	System.out.println("Player '" + id + "' successfully added to the game! Assigned as player: " + playerNumber);
	    	playerNumber++;
		}
    	
	}
	
	private void printPlayers() {
		int count = 0;
		for(Player player : players){
        	System.out.println(players.get(count));
        	count++;
        }
	}

        
	}
	

	
	
	
	
