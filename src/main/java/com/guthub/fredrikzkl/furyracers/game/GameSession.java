package com.guthub.fredrikzkl.furyracers.game;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.websocket.DeploymentException;
import javax.websocket.EncodeException;
import javax.websocket.Session;
import com.github.fredrikzkl.furyracers.network.Gamecore;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;

public class GameSession {

	
	private final Gamecore game;
	
	private Session backend;
	
	private String player1;
	private String player2;
	private String player3;
	private String player4;
	
	private String player1Username = "Player 1";
	private String player2Username = "Player 2";
	private String player3Username = "Player 3";
	private String player4Username = "Player 4";
	
	public GameSession(Gamecore game) throws DeploymentException {
		this.game = game;
	}
	
	public void onOpen(Session session)throws IOException, EncodeException {
		backend = session;
		player1 = "";
		player2= "";
		player3= "";
		player4= "";
		
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
	}
	
	public String getplayer1Username(){
		return player1Username;
	}
	public String getplayer2Username(){
		return player2Username;
	}
	public String getplayer3Username(){
		return player3Username;
	}
	public String getplayer4Username(){
		return player4Username;
	}

	
	
}
