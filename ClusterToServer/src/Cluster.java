import java.util.Scanner;

public class Cluster extends Thread {
	//Define clusterID and variables used
    private String clusterID = "1";
    private Queue commandQueue;
    private Queue replyQueue;
    private String a,b,c,d;
    private Scanner in;
    
    //Constructor
    public Cluster(Queue commandQueue, Queue replyQueue){
        this.commandQueue = commandQueue;
        this.replyQueue = replyQueue;
        in = new Scanner(System.in);
        System.out.println("ClusterID: " + clusterID);
    }
    
    //Piece of code that will be run while the cluster is active.
    //This asks the amount of people and the battery percentages in the console
    //Then sends the data to the server
    public void run() {
    	while(true) {
    		System.out.print("NoOfPeople: ");
    		a = in.nextLine();
    		System.out.print("Percentage 1: ");
    		b = in.nextLine();
    		System.out.print("Percentage 2: ");
    		c = in.nextLine();
    		System.out.print("Percentage 3: ");
    		d = in.nextLine();
    		SendData(a,b,c,d);
    	}
    }
    
    //Function to send the data.
    private void SendData(String noOfPeople, String b1, String b2, String b3){
    	String a = "data-" + clusterID + "-" + noOfPeople + "-" + b1 + "-" + b2 + "-" + b3;
        commandQueue.put(a);
        System.out.println(replyQueue.get());
    }
     
}