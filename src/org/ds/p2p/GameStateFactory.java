package org.ds.p2p;

public class GameStateFactory {
	public static GameState state;
	
	public static GameState getGameState(){
		if(state == null){
			state = new GameState();
		}
		return state;
	}

	public static void setState(GameState state) {
		GameStateFactory.state = state;
	}
}
