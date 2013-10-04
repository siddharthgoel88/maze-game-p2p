package org.ds.p2p.impl;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Map;

import org.ds.p2p.GameState;
import org.ds.p2p.Player;

public class PlayerUtils {

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

	public static String getIP4Adress() {
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