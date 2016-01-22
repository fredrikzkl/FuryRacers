package com.guthub.fredrikzkl.furyracers.game;


import javax.websocket.*;
import java.io.IOException;

public class WebsocketClient {
	//Istedet for app, skal det være vår "application" klasse
	private GameSession gameSession = App.getGameSession();
	
	public void onOpen(Session session){
		gameSession.onOpen(session);
	}
	public void onMessage(Session session, String message){
		gameSession.onMessage(session,message);
	}
	
	
}
