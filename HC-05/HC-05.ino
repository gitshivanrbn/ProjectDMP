#include <SoftwareSerial.h>

#define ATPIN 9

SoftwareSerial BTSerial(10, 11); //RX|TX

void setup() {
  //Set the 'Enable' pin on the hc-05 to high, so the module goes into command mode
  pinMode(ATPIN, OUTPUT);
  digitalWrite(ATPIN, HIGH);

  Serial.begin(9600);
  BTSerial.begin(38400);
}

void loop() {
  if (Serial.available()) BTSerial.write(Serial.read());
  if (BTSerial.available()) Serial.write(BTSerial.read());
}
