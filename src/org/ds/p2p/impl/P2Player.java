package org.ds.p2p.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.ds.p2p.BackupUpdates;
import org.ds.p2p.Bootstrapper;
import org.ds.p2p.ClientHeartBeat;
import org.ds.p2p.GameEndCheck;
import org.ds.p2p.GameEndCheckThread;
import org.ds.p2p.GameState;
import org.ds.p2p.GameStateFactory;
import org.ds.p2p.HeartBeatThread;
import org.ds.p2p.MovePlayers;
import org.ds.p2p.PeerProperties;
import org.ds.p2p.Player;
import org.ds.p2p.PrimaryStatus;

public class P2Player {
	
	static PeerProperties peerProp = new PeerProperties();
	GameState state;
	static Player gamePlayer;
	static MovePlayers movePlayerStub = null;
	
	public static PeerProperties getPeerProp(){
		return peerProp;
	}
	
	public static void setPeerProp(PeerProperties props){
		peerProp = props;
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
				System.out.println("Sending peer props");
				updateBackupPeerProps();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		initHeartBeat();
		initGameEndCheck();
		if(peerProp.isBackup())
			initPrimaryPoll();
		playGame();	
	}

	public static void initPrimaryPoll() {
		PrimaryStatus primaryStatus = null;
		
		try
		{
			primaryStatus = (PrimaryStatus) RegistryManager.getPrimaryRegistry().lookup("primaryStatus");
		}
		catch(Exception e)
		{
			System.out.println("Issues in polling primary");
			e.printStackTrace();
		}
		
		PrimaryStatusChecker psc = new PrimaryStatusChecker();
		psc.setPrimaryStatus(primaryStatus);
		Thread t = new Thread(psc);
		t.start();
	}

	private void updateBackupPeerProps() {
		try{
			Registry reg = LocateRegistry.getRegistry((String) peerProp.getSecondaryPeerIp().get("ip") , Integer.parseInt((String)peerProp.getSecondaryPeerIp().get("port")));
			BackupUpdates bkp = (BackupUpdates) reg.lookup("updateBackup");
			bkp.updatePeerProps(peerProp);
		}catch(Exception e){
			System.out.println("No other player has joined. Please try again later.");
			System.exit(6);
			e.printStackTrace();
		}	
	}

	private void initGameEndCheck() {
		GameEndCheckThread gmc = new GameEndCheckThread();
		Thread t = new Thread(gmc);
		t.start();
	}

	public static void initHeartBeat() {
		HeartBeatThread hbt = new HeartBeatThread();
		hbt.setPlayer(gamePlayer);
		Thread t = new Thread(hbt);
		t.start();
		
		if(peerProp.isPrimary()){
			System.out.println("Initiated Heart beat checks.");
			Thread heartBeatChecker = new Thread(new HeartBeatChecker());
    		heartBeatChecker.start();
		}
		
	}

	private void playGame() {
		Registry registry = RegistryManager.getPrimaryRegistry();
		
		
		movePlayerStub = getRemoteObj(registry, movePlayerStub); 
		
		noMove();
		
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
					PlayerUtils.printState((GameState) moveResult.get("currentState"));
				}
			}else if(move.equals("k")){
				movePlayerStub.move(gamePlayer.getId(), move);
				System.out.println("Game stopped voluntarily. Please play again.");
				System.exit(2);
			}else{
				System.out.println("Invalid Move!");
			}
			} catch (RemoteException e) {
				try {
					Thread.sleep(000);
				} catch (InterruptedException e1) {
					System.err.println("Move remote could not sleep");
					//e1.printStackTrace();
				}
				registry = RegistryManager.getPrimaryRegistry();
				movePlayerStub = getRemoteObj(registry, movePlayerStub);
				noMove();
				//e.printStackTrace();
			}
		}
	}

	public static void noMove() {
		try{
			Map<String, Object> movePlMap = movePlayerStub.move(gamePlayer.getId(), "x");
			PlayerUtils.printState((GameState) movePlMap.get("currentState"));
		}catch(RemoteException re){
			re.printStackTrace();
		}
	}

	private MovePlayers getRemoteObj(Registry registry,
			MovePlayers movePlayerStub) {
		try {
			movePlayerStub = (MovePlayers) registry.lookup("move");
		} catch (Exception e1) {
			System.out.println("Issues in lookup of move registry");
			//e1.printStackTrace();
		}
		return movePlayerStub;
	}

	private boolean initGame(String primaryIP , String name) {
		boolean isPrimary = false;
		String playerUUID = UUID.randomUUID().toString();
		String machineIp = PlayerUtils.getIP4Adress();
		System.out.println(machineIp);
		peerProp.setMyIP(machineIp);
		
        if(primaryIP.equals(machineIp)){
        	try {
        		isPrimary = initPrimary(name, playerUUID, machineIp);
        	}catch(Exception createException){
        		System.out.println("\nPrimary server already exists on this machine.");
        	}
        }

		if(!isPrimary){
			NominatorImpl nominator = new NominatorImpl();
			try {
				initPlayer(primaryIP, name, playerUUID, machineIp, nominator);
			} catch (Exception cannotContact) {
				cannotContact.printStackTrace();
				return false;
			}
		}
	  return true;
	}

	private void initPlayer(String primaryIP, String name, String playerUUID,String machineIp, NominatorImpl nominator) throws RemoteException , InterruptedException {
		Map<String,String> playerProps = new HashMap<String, String>();
		Bootstrapper bootstrap = null;
		Map<String, Object> props = null;
		
		try{
			Registry registry = LocateRegistry.getRegistry(primaryIP, 1099);
			RegistryManager.setPrimaryRegistry(registry);
			bootstrap = (Bootstrapper) registry.lookup("bootstrapper");
		}catch(Exception e){
			System.out.println("Unable to boot up the network game. Please try again later. (Primary not yet started).");
			System.exit(5);
		}
		
		peerProp.getPrimaryProperties().put("ip", primaryIP); //TODO: see the above todo
		peerProp.getPrimaryProperties().put("port", 1099);
		Integer seed = 1099;
		
		while(true){
			try{
				Registry reg = findLocalRegistryPort(seed);
				RegistryManager.setRegistry(reg);
				System.out.println("Registry initiated on port "+seed);
				break;
			}catch(RemoteException re){
				System.out.println(seed + " Not free");
				seed++;
			}
		}

		playerProps.put("uuid", playerUUID);
		playerProps.put("machineIP" , machineIp);
		playerProps.put("port", seed.toString());
		playerProps.put("name" , name);
		peerProp.setMyIP(machineIp);
		peerProp.setMyRMIport(seed);

		try
		{
			props = bootstrap.bootstrap(playerProps);
		}
		catch(Exception e)
		{
			System.out.println("Issues in bootstrap call. Check /etc/hosts");
		}
		nominator.nominate(props);
		gamePlayer = new Player(name , playerUUID);
		gamePlayer.setPlayerDispId((props.get("playerDispId")).toString().charAt(0));
		Long waitTime = (Long) props.get("waitTime");
		System.out.println("Expected waiting time:" + waitTime/1000 + " seconds.");
		Thread.sleep(waitTime);
	}

	private Registry findLocalRegistryPort(int port) throws RemoteException {
		return LocateRegistry.createRegistry(port);
	}

	private boolean initPrimary(String name, String playerUUID, String machineIp) throws RemoteException, AlreadyBoundException, AccessException , IOException {
		boolean isPrimary;
		Registry registry;
		registry = RegistryManager.initRegistry(1099);
		RegistryManager.setPrimaryRegistry(registry);
		BootstrapperImpl bootstrapper = new BootstrapperImpl();
		ClientHeartBeatImpl heartBeatImpl = new ClientHeartBeatImpl();
		GameEndCheckImpl gameEndCheck = new GameEndCheckImpl();
		PrimaryStatusImpl primaryStatus = new PrimaryStatusImpl();
		MovePlayersImpl movePlayers = new MovePlayersImpl();
		
		registry.bind("bootstrapper", (Bootstrapper)UnicastRemoteObject.exportObject( bootstrapper , 0));
		registry.bind("move", (MovePlayers) UnicastRemoteObject.exportObject( movePlayers , 0));
		registry.bind("heartBeat", (ClientHeartBeat) UnicastRemoteObject.exportObject( heartBeatImpl , 0));
		registry.bind("gameEnd", (GameEndCheck) UnicastRemoteObject.exportObject( gameEndCheck , 0));
		registry.bind("primaryStatus", (PrimaryStatus) UnicastRemoteObject.exportObject( primaryStatus , 0));
		peerProp.setPrimary(true);
		System.out.println("You are the game initiater.\nPlease enter size of board and total number of treasures: ");
		BufferedReader boardSize = new BufferedReader(new InputStreamReader(System.in));
		BufferedReader numTreasure = new BufferedReader(new InputStreamReader(System.in));
		state = GameStateFactory.getGameState();
		String boardString = boardSize.readLine();
		String totalTreasures = numTreasure.readLine();
		
		if(boardString == null || totalTreasures == null){
			state.setBoardSize(5);
			state.setTotalNumTreasures(15);
		}else{
			state.setBoardSize(Integer.parseInt(boardString));
			state.setTotalNumTreasures(Integer.parseInt(totalTreasures));
		}
		
		state.initializeGame();
		peerProp.setInitTime(System.currentTimeMillis() + 20000);
		gamePlayer = new Player(name , playerUUID);
		gamePlayer.setPlayerDispId('A');
		state.getPlayers().put(playerUUID, gamePlayer);
		while(!state.initializePlayer(playerUUID));
		peerProp.getPrimaryProperties().put("ip", machineIp); //TODO: is it right machineIP or ip?
		peerProp.getPrimaryProperties().put("port", "1099");
		peerProp.getPrimaryProperties().put("uuid", playerUUID);
		state.setNumPlayers(1);
		System.out.println("\nPrimary is ready !");
		isPrimary = true;
		return isPrimary;
	}
}