import java.net.URI;
import java.net.URISyntaxException;
import org.java_websocket.client.WebSocketClient;

//Main class of the cluster which you will run to make a cluster.
public class Program {
    public static void main(String[] args) throws URISyntaxException {
    	//Making two queue's for the messages to and from the server.
        Queue commandQueue = new Queue();
        Queue replyQueue = new Queue();
        
        //Creating a new cluster.
        Cluster cluster = new Cluster(commandQueue, replyQueue);
        cluster.start();
        
        //Creating a connection to the server on 192.168.11.31:8080
        WebSocketClient client = new ClusterClient(new URI("ws://192.168.11.31:8080"), commandQueue, replyQueue);
        client.connect();

        ((ClusterClient)client).communicate();
    }
}