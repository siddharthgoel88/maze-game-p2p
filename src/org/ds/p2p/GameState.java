package org.ds.p2p;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.ds.p2p.Player;
import org.ds.p2p.Square;

public class GameState implements Serializable {
	/**
	 * version 1.0
	 */
	
	private static final long serialVersionUID = 356185318404742910L;
	private int BOARD_SIZE;
	private int totalNumTreasures;
	private Square[][] gameBoard;
	private ArrayList<Player> winner = new ArrayList<Player>();
	private ConcurrentHashMap<String, Player> players = new ConcurrentHashMap<String, Player>();
	private int numPlayers;
	private boolean isGameOn = true;
	
	public GameState(){
		
	}
	
	public Square[][] getGameBoard() {
		return gameBoard;
	}

	public void initializeGame() {
		int randRow, randCol;
		Random random = new Random();
		
		GameStateFactory.getGameState().gameBoard = new Square[BOARD_SIZE][BOARD_SIZE];
		System.out.println("BS: "+BOARD_SIZE+" # treasure:" + totalNumTreasures);
		
		for (int i = 0; i < BOARD_SIZE; i++)
			for (int j = 0; j < BOARD_SIZE; j++)
				gameBoard[i][j] = new Square();

		for (int i = 0; i < totalNumTreasures; i++) {
			randCol = random.nextInt(BOARD_SIZE);
			randRow = random.nextInt(BOARD_SIZE);
			gameBoard[randCol][randRow].numTreasures += 1;
		}

		for (int i = 0; i < BOARD_SIZE; i++) {
			System.out.println("\n");
			for (int j = 0; j < BOARD_SIZE; j++) {
				System.out.print(gameBoard[i][j].numTreasures + " | ");
			}
		}
	}

	public void setBoardSize(int boardSize) {
		this.BOARD_SIZE = boardSize;
	}
	
	public int getBoardSize() {
		return this.BOARD_SIZE;
	}


	public Map<String, Player> getPlayers() {
		return players;
	}

	public boolean initializePlayer(String id) {
		Random random = new Random();
		int randRow = random.nextInt(BOARD_SIZE);
		int randCol = random.nextInt(BOARD_SIZE);

		if (gameBoard[randRow][randCol].numTreasures == 0 && gameBoard[randRow][randCol].isFree ) {
			gameBoard[randRow][randCol].isFree = false;
			gameBoard[randRow][randCol].userId = id;
			players.get(id).currentCol = randCol;
			players.get(id).currentRow = randRow;
			return true;
		}
		return false;
	}
	
	public void cleanUpPlayer(Player player){
		gameBoard[player.currentRow][player.currentCol].isFree = true;
		gameBoard[player.currentRow][player.currentCol].userId = null;
		players.remove(player.id);
	}

	public int getTotalNumTreasures() {
		return totalNumTreasures;
	}

	public void setTotalNumTreasures(int totalNumTreasures) {
		this.totalNumTreasures = totalNumTreasures;
	}

	public ArrayList<Player> getWinner() {
		return winner;
	}
	
	public int getNumPlayers() {
		return numPlayers;
	}

	public void setNumPlayers(int numPlayers) {
		this.numPlayers = numPlayers;
	}


	public boolean isGameOn() {
		return isGameOn;
	}


	public void setGameOn(boolean isGameOn) {
		this.isGameOn = isGameOn;
	}
}
