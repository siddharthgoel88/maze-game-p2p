package org.ds.p2p.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.ds.p2p.Bootstrapper;
import org.ds.p2p.GameState;
import org.ds.p2p.GameStateFactory;
import org.ds.p2p.Nominator;
import org.ds.p2p.PeerProperties;
import org.ds.p2p.Player;
import org.ds.p2p.MovePlayers;

public class P2Player {
	
	static PeerProperties peerProp = new PeerProperties();
	GameState state;
	Player gamePlayer;
	
	public static PeerProperties getPeerProp(){
		return peerProp;
	}
	
	public static void main(String[] args) {
		P2Player player = new P2Player();
		player.init(args);
	}
	
	public void init(String[] args){
		if(args.length == 2){
			if(!initGame(args[0] , args[1])){
				System.err.println("Game could not be initialized! Please try again later!");
			}
		}else{
			System.err.println("Game could not be initialized due to insufficient Parameters.");
		}
		if(peerProp.isPrimary()){
			try{
				System.out.println("Primary sleeping for 20 secs.");
				Thread.sleep(20000);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		playGame();	
	}

	private void playGame() {
		Registry registry = RegistryManager.getPrimaryRegistry();
		MovePlayers movePlayerStub = null;
		
		try {
			movePlayerStub = (MovePlayers) registry.lookup("move");
		} catch (Exception e1) {
			e1.printStackTrace();
		} 
		
		try{
			Map<String, Object> movePlMap = movePlayerStub.move(gamePlayer.getId(), "x");
			printState((GameState) movePlMap.get("currentState"));
		}catch(RemoteException re){
			re.printStackTrace();
		}
		
		while(true){
			BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
		    String move = null;
			try {
				move = bufferRead.readLine().toLowerCase();
			} catch (IOException e) {
				e.printStackTrace();
			}
		try {
			if(move.matches("[asdwx]")){
				Map<String,Object> moveResult = movePlayerStub.move(gamePlayer.getId(), move);
				if( !Boolean.valueOf((String) moveResult.get("isSuccessful")) ){
					System.out.println("Player move invalid.");
				}else{
					printState((GameState) moveResult.get("currentState"));
				}
			}else if(move.equals("k")){
				movePlayerStub.move(gamePlayer.getId(), move);
				System.out.println("Game stopped voluntarily. Please play again.");
			}else{
				System.out.println("Invalid Move!");
			}
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	private boolean initGame(String primaryIP , String name) {
		boolean isPrimary = false;
		String playerUUID = UUID.randomUUID().toString();
		String machineIp = getIP4Adress();
		peerProp.setMyIP(machineIp);
		
        if(primaryIP.equals(machineIp)){
        	Registry registry = null;
        	
        	try {
        		registry = RegistryManager.initRegistry(1099);
        		RegistryManager.setPrimaryRegistry(registry);
        		BootstrapperImpl bootstrapper = new BootstrapperImpl();
        		MovePlayersImpl movePlayers = new MovePlayersImpl();
        		registry.bind("bootstrapper", (Bootstrapper)UnicastRemoteObject.exportObject( bootstrapper , 0));
        		registry.bind("move", (MovePlayers) UnicastRemoteObject.exportObject( movePlayers , 0));
        		peerProp.setPrimary(true);
        		System.out.println("You are the game initiater.\nPlease enter size of board and total number of treasures: ");
        		BufferedReader boardSize = new BufferedReader(new InputStreamReader(System.in));
        		BufferedReader numTreasure = new BufferedReader(new InputStreamReader(System.in));
        		state = GameStateFactory.getGameState();
        		state.setBoardSize(Integer.parseInt(boardSize.readLine().toString()));
        		state.setTotalNumTreasures(Integer.parseInt(numTreasure.readLine().toString()));
        		state.initializeGame();
        		peerProp.setInitTime(System.currentTimeMillis() + 20000);
        		gamePlayer = new Player(name , playerUUID);
        		gamePlayer.setPlayerDispId('A');
        		state.getPlayers().put(playerUUID, gamePlayer);
        		peerProp.getPrimaryProperties().put("machineIP", machineIp);
        		peerProp.getPrimaryProperties().put("port", "1099");
        		state.setNumPlayers(1);
        		System.out.println("\nPrimary is ready !");
        		isPrimary = true;
        	}catch(Exception createException){
        		System.out.println("\nPrimary server already exists on this machine.");
        	}
        }

		if(!isPrimary){
			NominatorImpl nominator = new NominatorImpl();
			try {
				Registry registry = LocateRegistry.getRegistry(primaryIP, 1099);
				RegistryManager.setPrimaryRegistry(registry);
				registry.bind(playerUUID ,(Nominator) UnicastRemoteObject.exportObject( nominator, 0));
				Map<String,String> playerProps = new HashMap<String, String>();
				Bootstrapper bootstrap = (Bootstrapper) registry.lookup("bootstrapper");
				peerProp.getPrimaryProperties().put("ip", primaryIP);
				peerProp.getPrimaryProperties().put("port", 1099);
				playerProps.put("uuid", playerUUID);
				playerProps.put("machineIP" , machineIp);
				playerProps.put("name" , name);
				Map<String, Object> props = bootstrap.bootstrap(playerProps);
				gamePlayer = new Player(name , playerUUID);
				gamePlayer.setPlayerDispId((props.get("playerDispId")).toString().charAt(0));
				Long waitTime = (Long) props.get("waitTime");
				System.out.println("Expected waiting time:" + waitTime/1000);
				Thread.sleep(waitTime);
			} catch (Exception cannotContact) {
				cannotContact.printStackTrace();
				return false;
			}
		}
	  return true;
	}
	
	public static void printState(GameState initState) {
		org.ds.p2p.Square[][] square = initState.getGameBoard();
		Map<String,Player> players = initState.getPlayers();
		int boardsize = initState.getBoardSize();
				
		for (int i=0 ; i < boardsize ; i++){
			System.out.println("\n");
			for(int j=0 ; j < boardsize ; j++){
				if(!square[i][j].isFree()){
					Player curPlayer = players.get(square[i][j].getUserId());
					System.out.print(curPlayer.getPlayerDispId() + "(" + curPlayer.getNumTreasures() + ")" + "\t");
				}else{
					System.out.print(square[i][j].getNumTreasures() + "\t");
				}
			}
		}
		
		for(int i=0;i++<5;)
			System.out.println("");
	}
	
	public String getIP4Adress(){
		try {
			Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
			while (netInterfaces.hasMoreElements()) {
	            NetworkInterface iface = netInterfaces.nextElement();    
	            Enumeration<InetAddress> addresses = iface.getInetAddresses();
	            while(addresses.hasMoreElements()) {
	                InetAddress addr = addresses.nextElement();
	                if(addr instanceof Inet4Address && !addr.isAnyLocalAddress()){
	                	return addr.getHostAddress();
	                }
	            }
			}    
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return null;
	}
}