package ChatServerClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JOptionPane;

public class ServerSocketFlow extends Thread{
	
	Map<String, PrintWriter> map;
	String clientScreenName;
    Socket listeningSocket;
    BufferedReader serverBufferedReader;
    PrintWriter serverPrintWriter;
    
    public ServerSocketFlow(Socket socket, Map<String, PrintWriter> map) {
    	this.listeningSocket = socket;
    	this.map = map;
    }
	
    public void chatStarting() throws IOException {
    	// starting and name submit
		while (true) {
			serverPrintWriter.println(ChatStatus.SCREEN_NAME_SUBMIT);
			clientScreenName = serverBufferedReader.readLine();
			if (clientScreenName == null) {
				return;
			}
			if (!map.containsKey(clientScreenName)) {
				map.put(clientScreenName, serverPrintWriter);
				System.out.println(clientScreenName + " joined.");
				refreshClientList(map);
				break;
			}
		}
		serverPrintWriter.println(ChatStatus.SCREEN_NAME_SUBMIT_OK);
		for (Entry<String, PrintWriter> entry : map.entrySet()) {
			entry.getValue().println(
					ChatStatus.SCREEN_NAME_JOIN + " " + clientScreenName);
			entry.getValue().println(
					ChatStatus.USER_NAME_LIST + " " + map.keySet());
		}
    }
    
    public void chatContinue_End() throws IOException {
    	// broadcast and exit
		while (true) {
			String input = serverBufferedReader.readLine();
			if (input == null) {
				return;
			} else if (input.equals(ChatStatus.SCREEN_NAME_EXIT)) {
				map.remove(clientScreenName);
				System.out.println(clientScreenName + " exited.");
				refreshClientList(map);
				for (Entry<String, PrintWriter> entry : map.entrySet()) {
					entry.getValue().println(
							ChatStatus.SCREEN_NAME_EXIT + " "
									+ clientScreenName);
					entry.getValue().println(
							ChatStatus.USER_NAME_LIST + " " + map.keySet());
				}
				continue;
			}

			for (Entry<String, PrintWriter> entry : map.entrySet()) {
				entry.getValue().println(
						ChatStatus.MESSAGE_TEXT + " " + clientScreenName + ": "
								+ input);
			}
			
		}
    }
    
    public void refreshClientList(Map<String, PrintWriter> map) {
    	//System.out.println("Number of user logged in: " + map.keySet());
    }
    
	public void run() {
		
		try {
			serverBufferedReader = new BufferedReader(new InputStreamReader(
					listeningSocket.getInputStream()));
			serverPrintWriter = new PrintWriter(listeningSocket.getOutputStream(), true);
			
			chatStarting();
			chatContinue_End();
			
		} catch (IOException e) {
			System.out.println(e);
		} finally {
			if (map != null) {
				map.clear();
			}
			try {
				listeningSocket.close();
			} catch (IOException e) {
			}
		}
	}
}
