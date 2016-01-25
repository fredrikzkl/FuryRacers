package com.github.fredrikzkl.furyracers.network;


import javax.websocket.*;

import com.github.fredrikzkl.furyracers.Furyracers.App;

import java.io.IOException;

public class WebsocketClient {
	//Istedet for app, skal det være vår "application" klasse
	private GameSession gameSession = App.getGameSession();
	
	public void onOpen(Session session) throws IOException, EncodeException{
		gameSession.onOpen(session);
	}
	public void onMessage(Session session, String message) throws IOException, EncodeException{
		gameSession.onMessage(session,message);
	}
	
	
}
