/*********
  Sending acceleration data received from an MPU-6050 sensor over a lora connection
  Modified from the examples of the Arduino LoRa library and "MPU6050" by Raushancpr
  (re)written in the notes app of my phone because my laptop broke down. Untested. -Herma 
*********/

#include <SPI.h>
#include <LoRa.h>
#include <Wire.h>
#include <Math.h>

#define NSS 5
#define RESET 14
#define DIO0 2
#define MPU_ADDR 0x68

float sensorData = 0;

void setup() {
  Serial.begin(9600);
  
  //Setup MPU
  Wire.beginTransmission(MPU_ADDR);
  Wire.write(0x6B); // PWR_MGMT_1 register
  Wire.write(0);
  Wire.endTransmission(true);

  //setup LoRa transceiver module
  LoRa.setPins(NSS, RESET, DIO0);
  while (!LoRa.begin(866E6)) {
    Serial.print(".");
    delay(500);
  }
  LoRa.setSyncWord(0xE7);
  Serial.println("LoRa started");
}

void loop() {
  Wire.beginTransmission(MPU_ADDR);
  Wire.write(0x3B); // starting with register 0x3B (ACCEL_XOUT_H) [MPU-6000 and MPU-6050 Register Map and Descriptions Revision 4.2, p.40]
  Wire.endTransmission(false); // the parameter indicates that the Arduino will send a restart. As a result, the connection is kept active.
  Wire.requestFrom(MPU_ADDR, 3*2, true); // request a total of 3*2=6 registers
  int16_t accelerometer_x = Wire.read()<<8 | Wire.read(); // reading registers: 0x3B (ACCEL_XOUT_H) and 0x3C (ACCEL_XOUT_L)
  int16_t accelerometer_y = Wire.read()<<8 | Wire.read(); // reading registers: 0x3D (ACCEL_YOUT_H) and 0x3E (ACCEL_YOUT_L)
  int16_t accelerometer_z = Wire.read()<<8 | Wire.read(); // reading registers: 0x3F (ACCEL_ZOUT_H) and 0x40 (ACCEL_ZOUT_L)
  sensorData = Math.sqrt(Math.sq(accelerometer_x)+Math.sq(accelerometer_y)+Math.sq(accelerometer_z)); //calculating 3d-acceleration

  //Sending LoRa packet
  LoRa.beginPacket();
  LoRa.print(sensorData);
  LoRa.endPacket();

  delay(10000);
}
