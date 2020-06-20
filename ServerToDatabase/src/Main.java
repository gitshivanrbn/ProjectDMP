import java.net.InetSocketAddress;
import org.java_websocket.server.WebSocketServer;

class Main{
    public static void main(String[] args) {
        // Queues for the communication between cluster and the server.
        Queue commandQueue = new Queue();
        Queue replyQueue = new Queue();
        // Database function to retrieve information from our database
        Database database = new Database();
        //Group server to communicate with the Clusters
        WebSocketServer server = new GroupServer(new InetSocketAddress("192.168.11.31", 8080), commandQueue, replyQueue, database);
		server.start();
    }
}

