package com.github.fredrikzkl.furyracers.network;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.websocket.DeploymentException;
import javax.websocket.EncodeException;
import javax.websocket.Session;

import org.glassfish.tyrus.client.ClientManager;

import com.guthub.fredrikzkl.furyracers.game.GameCore;
import com.guthub.fredrikzkl.furyracers.game.Player;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

public class GameSession {

	
	private final GameCore game;
	
	private Session backend;
	
	private Set<Player> players;
	
	public GameSession(GameCore game) throws DeploymentException {
		this.game = game;
		players = new HashSet<>();
	}
	
	public void connect() throws URISyntaxException, IOException, DeploymentException {
		ClientManager client = ClientManager.createClient();
        client.connectToServer(WebsocketClient.class, new URI("ws://localhost:3001/ws"));
	}
	
	public void onOpen(Session session)throws IOException, EncodeException {
		backend = session;
		sendToBackend("Identify", "game");
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
        
        //Skal fylles ut med messages fra controller (switch)
	}
	
	//Metoder som mangler:
	//Sende informasjon til kontroller
	
	
	
	
	
}
