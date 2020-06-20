CREATE DATABASE PR78DB;
USE PR78DB;

CREATE TABLE Clusters (
	ClusterID INT NOT NULL AUTO_INCREMENT,
	NoOfSensors INT DEFAULT 3,
	Longitude DOUBLE,
	Latitude DOUBLE, 
	PRIMARY KEY (ClusterID)
);

CREATE TABLE Sensors (
	SensorID INT NOT NULL AUTO_INCREMENT,
	ClusterID INT,
	BatteryPercentage DOUBLE DEFAULT 100,
	PRIMARY KEY (SensorID),
	FOREIGN KEY (ClusterID) REFERENCES Clusters(ClusterID)
);

CREATE TABLE Data (
	DataID INT NOT NULL AUTO_INCREMENT,
	ClusterID INT,
	NoOfPeople DOUBLE,
	TimeOfDay DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY (DataID),
	FOREIGN KEY (ClusterID) REFERENCES Clusters(ClusterID)
);

#Create user for the server to connect to the database
CREATE USER 'dev'@'localhost' IDENTIFIED BY 'dev';
GRANT INSERT, UPDATE, SELECT ON PR78DB.* TO 'dev'@'%';
FLUSH PRIVILEGES;

#Creating first Cluster with sensors
#You can also use this to create more clusters
INSERT INTO Clusters (Longitude, Latitude) VALUES (0,0);
INSERT INTO Sensors (ClusterID) SELECT MAX(ClusterID) FROM  Clusters;
INSERT INTO Sensors (ClusterID) SELECT MAX(ClusterID) FROM  Clusters;
INSERT INTO Sensors (ClusterID) SELECT MAX(ClusterID) FROM  Clusters;