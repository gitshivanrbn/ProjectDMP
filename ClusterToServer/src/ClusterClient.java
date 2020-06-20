import java.net.URI;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

public class ClusterClient extends WebSocketClient {
    
    // Queue used to get the commands from, so they can be send to the server
	Queue commandQueue;
	// Queue used to put the replies in, which are coming in from the server via onMessage()
    Queue replyQueue;
    
    //Constructor
    public ClusterClient(URI serverURI, Queue commandQueue, Queue replyQueue) {
		super(serverURI);
		this.commandQueue = commandQueue;
		this.replyQueue = replyQueue;
    }
    
    // This method is used to let the client wait for a command, send it directly to the server and wait for a new command
	public void communicate(){
		while(true){
			send(commandQueue.get());
		}
    }
    
    // When a message comes in from the server, onMessage is automatically called
	// The incoming message will be put in the reply queue
	@Override
	public void onMessage(String message) {
		replyQueue.put(message);
		System.out.println("received message: " + message);
    }
    
    // Let the user know via the command line that the client is connected with the server
	@Override
	public void onOpen(ServerHandshake handshakedata) {
		System.out.println("Connected to the server!");
    }
    
    // When the connection is closed, print the corresponding code and reason
	@Override
	public void onClose(int code, String reason, boolean remote) {
		System.out.println("closed with exit code " + code + " additional info: " + reason);
    }
    
    // If there is any error, print it
	@Override
	public void onError(Exception ex) {
		System.err.println("an error occurred:" + ex);
	}
}