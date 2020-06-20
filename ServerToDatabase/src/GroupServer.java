import java.net.InetSocketAddress;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

public class GroupServer extends WebSocketServer {

	Queue commandQueue;
	Queue replyQueue;
	Database database;

	public GroupServer(InetSocketAddress address, Queue commandQueue, Queue replyQueue, Database database) {
		super(address);
		this.commandQueue = commandQueue;
		this.replyQueue = replyQueue;
		this.database = database;
	}
	
	// Let the user know via the command line that the client is connected with the server
	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		System.out.println("new connection to " + conn.getRemoteSocketAddress());
	}
	
	//When the server receives a message, split the message into different parts and put it inside a list.
	@Override
	public void onMessage(WebSocket conn, String message) {
		System.out.println(conn.getRemoteSocketAddress() + ": " + message);
    	String[] data = message.split("-");
    	String function = data[0];		
    	function = function.toLowerCase();
    	
    	switch(function) {
    		//If the function is data assign the variables and send it to the database.
    		case "data":
    			int clusterID = Integer.parseInt(data[1]);
    	    	int noOfPeople = Integer.parseInt(data[2]);
    	    	int bat1 = Integer.parseInt(data[3]);
    	    	int bat2 = Integer.parseInt(data[4]);
    	    	int bat3 = Integer.parseInt(data[5]);
    	    	
    	    	conn.send(database.updateData(clusterID,noOfPeople,bat1,bat2,bat3));
    	    	break;
    	    	
    	    default:
    	    	conn.send("false");
    	    	break;
    	}
	}
	
	// When the connection is closed, print the corresponding code and reason
	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote) {
		System.out.println("closed " + conn.getRemoteSocketAddress() + " with exit code " + code + " additional info: " + reason);
	}
	
	// If there is any error, print it
	@Override
	public void onError(WebSocket conn, Exception ex) {
		System.err.println("an error occured on connection " + conn.getRemoteSocketAddress()  + ":" + ex);
	}
	
	// Let the user know the server has started
	@Override
	public void onStart() {
		System.out.println("server started successfully");
	}
}


