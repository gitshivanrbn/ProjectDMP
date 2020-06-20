public class Queue {
	// contents is used to save the data
    private String contents;
    private boolean available = false;
	
	// Get data from the queue
	// As long as there is no data available; wait
	// When there is data available; set the available flag back to false, notify the waiting threads and return the contents
    public synchronized String get() {
		while (available == false) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		available = false;
		notifyAll();
		return contents;
	}
	
	// Put data in the queue
	// As long as there is already data in the queue; wait
	// When there is no data in the queue anymore; set the data in the queue, set the available flag to true and notify the waiting threads
    public synchronized void put(String value) {
       	while (available == true) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} 
       	}
		contents = value;
		available = true;
		notifyAll();
    }
 }

