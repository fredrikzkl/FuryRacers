package com.github.fredrikzkl.furyracers.network;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.websocket.DeploymentException;
import javax.websocket.EncodeException;
import javax.websocket.Session;

import org.glassfish.tyrus.client.ClientManager;

import com.github.fredrikzkl.furyracers.game.GameCore;
import com.github.fredrikzkl.furyracers.game.Player;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

public class GameSession {

	private final GameCore game;
	
	private Session backend;
	
	private String player1;
    private String player2;
    private String player1Username = "Player 1";
    private String player2Username = "Player 2";
	
	public GameSession(GameCore game) throws DeploymentException {
		this.game = game;
	}
	
	public void connect() throws URISyntaxException, IOException, DeploymentException {
		ClientManager client = ClientManager.createClient();
        client.connectToServer(WebsocketClient.class, new URI("ws://localhost:3001/ws"));
	}
	
	public void onOpen(Session session)throws IOException, EncodeException {
		System.out.println("onOpen");
		backend = session;
		sendToBackend("identify", "game");
	}

	private void sendToBackend(String action, String data) throws IOException, EncodeException {
		backend.getBasicRemote().sendObject(Json.createObjectBuilder()
				.add("action", action)
				.add("data", data)
				.build());
	}
	
	public void onMessage(Session session, String message)throws IOException, EncodeException {

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
                
                System.out.println("username");

                String id = client.get(0).toString();
                String username = client.get(1).toString();

                if (player1.equals(id)) {
                    player1Username = username;
                } else if (player2.equals(id)) {
                    player2Username = username;
                }

                break;
            }

            case "dropped client": {
                String id = jsonObj.getString("data");

                if (player1.equals(id)) {
                    player1 = "";
                } else if (player2.equals(id)) {
                    player2 = "";
                }

                System.out.println("oisann, droppad");

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
                
                switch(data){
                	case "1": game.onThrottle();break;
                	case "2": game.rightKeyDown();System.out.println("rightDown");break;
                	case "3": game.leftKeyDown();System.out.println("leftDown");break;
                }

                if (!jsonObj.containsKey("from")) {
                    return;
                }

                String from = jsonObj.getString("from");

                break;
            }
            case "buttonUp":{
            	String data = jsonObj.getJsonNumber("data").toString();
            	switch(data){
            		case "1": game.offThrottle();break;
            		case "2": game.rightKeyUp(); System.out.println("rightUp");break;
            		case "3": game.leftKeyUp();System.out.println("leftUp");break;
            	}
            	 if (!jsonObj.containsKey("from")) {
                     return;
                 }

                 String from = jsonObj.getString("from");

                 break;
            }
        }
    }

        //Skal fylles ut med messages fra controller (switch)
	}
	
	//Metoder som mangler:
	//Sende informasjon til kontroller
	
	
	
	
