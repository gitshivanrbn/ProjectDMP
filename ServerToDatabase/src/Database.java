import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {
	private java.sql.Connection con;
	private Statement st;

	// Connect java to the database on the server
	public Database(){
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/PR78DB?verifyServerCertificate=true&useSSL=true", "dev", "dev");
			st = con.createStatement();			
		} catch(Exception ex){
			System.out.println(ex);
		}
    }

	// Statements to update data in the database
	private void update(String query){
		try {
			st.executeUpdate(query);
		} catch (Exception ex) {
			System.out.println(ex);
		}
	}

	//Function to store the data into the database
	public String updateData(int clusterID, int noOfPeople, int batP1, int batP2, int batP3) {
		//Make a list of all sensorIDs inside of the cluster
		List<String> sensorIDs = getSensorID(clusterID);
		
		//If there are any sensors inside of the cluster update the battery percentages of these
		if(!sensorIDs.isEmpty()) {
			update("UPDATE Sensors SET BatteryPercentage = " + "\"" + batP1 + "\"" + "WHERE ClusterID = " + "\"" + clusterID + "\""+ " AND SensorID = " + sensorIDs.get(0));
			update("UPDATE Sensors SET BatteryPercentage = " + "\"" + batP2 + "\"" + "WHERE ClusterID = " + "\"" + clusterID + "\""+ " AND SensorID = " + sensorIDs.get(1));
			update("UPDATE Sensors SET BatteryPercentage = " + "\"" + batP3 + "\"" + "WHERE ClusterID = " + "\"" + clusterID + "\""+ " AND SensorID = " + sensorIDs.get(2));
		}
		
		//Line of code to create a new line of data inside the Data table
		update("INSERT INTO Data (ClusterID, NoOfPeople) VALUES (" + clusterID + ", " + noOfPeople + ")");
		return "done";
	}
	
	//Function to get the sensorIds of all the sensors inside of the cluster
	private List<String> getSensorID(int clusterID) {
		try {
			ResultSet rs = st.executeQuery("SELECT SensorID FROM Sensors WHERE ClusterID = " + "\"" + clusterID + "\"");
			List<String> sensors = new ArrayList<>();
				while(rs.next()) {
					sensors.add(rs.getString("SensorID"));
				}
			return sensors;
		}catch(Exception ex) {
			System.out.println(ex);
		}
		return null;
	}
}

