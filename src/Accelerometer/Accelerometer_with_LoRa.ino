#include <Wire.h>
#include <ADXL345.h>
#include <SPI.h>
#include <RH_RF95.h>

//Feedback LED 
#define LED 7

//RFM95 
#define RESET 2
#define DIO0 3
#define NSS 4
#define FREQ 868.0 //433.0 //915.0

RH_RF95 rf95(NSS, DIO0);

//ADXL345
ADXL345 adxl; //variable adxl is an instance of the ADXL345 library

//old position storage for the accelerometer
double oldPos[3] = {0,0,0};

//battery reading values
int value = 0;
float voltage;
float perc;

void setup() {
  Serial.begin(9600);
  pinMode(LED, OUTPUT);
  
  //battery reader
  analogReference(INTERNAL);

  //ADXL Setup
  adxl.powerOn();

  //set activity/ inactivity thresholds (0-255)
  adxl.setActivityThreshold(75); //62.5mg per increment
  adxl.setInactivityThreshold(75); //62.5mg per increment
  adxl.setTimeInactivity(10); // how many seconds of no activity is inactive?

  //look of activity movement on this axes
  adxl.setActivityX(1);
  adxl.setActivityY(1);
  adxl.setActivityZ(1);

  //set values for what is a tap, and what is a double tap 
  adxl.setTapThreshold(50); //62.5mg per increment
  adxl.setTapDuration(15); //625us per increment
  adxl.setDoubleTapLatency(80); //1.25ms per increment
  adxl.setDoubleTapWindow(200); //1.25ms per increment

  //set values for what is considered freefall (0-255)
  adxl.setFreeFallThreshold(7); 
  adxl.setFreeFallDuration(45); 

  //setting all interrupts to take place on int pin 1
  adxl.setInterruptMapping( ADXL345_INT_SINGLE_TAP_BIT,   ADXL345_INT1_PIN );
  adxl.setInterruptMapping( ADXL345_INT_DOUBLE_TAP_BIT,   ADXL345_INT1_PIN );
  adxl.setInterruptMapping( ADXL345_INT_FREE_FALL_BIT,    ADXL345_INT1_PIN );
  adxl.setInterruptMapping( ADXL345_INT_ACTIVITY_BIT,     ADXL345_INT1_PIN );
  adxl.setInterruptMapping( ADXL345_INT_INACTIVITY_BIT,   ADXL345_INT1_PIN );

  //register interrupt actions - 1 == on; 0 == off
  adxl.setInterrupt( ADXL345_INT_SINGLE_TAP_BIT, 1);
  adxl.setInterrupt( ADXL345_INT_DOUBLE_TAP_BIT, 1);
  adxl.setInterrupt( ADXL345_INT_FREE_FALL_BIT,  1);
  adxl.setInterrupt( ADXL345_INT_ACTIVITY_BIT,   1);
  adxl.setInterrupt( ADXL345_INT_INACTIVITY_BIT, 1);

 //RFM95 Setup
 //Reset the module
  pinMode(RESET, OUTPUT);
  digitalWrite(RESET, HIGH);

  digitalWrite(RESET, LOW);
  delay(10);
  digitalWrite(RESET, HIGH);
  delay(10);
  
  //initialize the lora connection keep retrying until connection is established
  while(!rf95.init()) {
  Serial.println("Initializing LoRa module...");
  while (1);
  }
  Serial.println("Succeeded initializing");

  //set the lora frequency
  if (!rf95.setFrequency(FREQ)) {
    Serial.println("Cant set frequency");
    while (1);
  }
  Serial.print("Set freq to: "); Serial.println(FREQ);
  //set the transmission power from 5 to 23
  rf95.setTxPower(5, false);
  Serial.println("Client ready for operation");
}

void loop() {
  //array for reading incoming values of the ADXL345
  double newPos[3];

  //get the acceleration
  adxl.getAcceleration(newPos);

  //subtract to get the change in speed
  double deltaPos[3] =  {oldPos[0] - newPos[0],oldPos[1] - newPos[1], oldPos[2] - newPos[2]};
  
  //magnitude calculation
  double magnitude = sqrt(pow(deltaPos[0],2) + pow(deltaPos[1],2) + pow(deltaPos[2],2));
  
  //multiply with 9.81 to get the acceleration as the length of a vector
  double acceleration = 9.81 * magnitude;
  
  //check if there is noticeable movement in all 3 directions.
  if (acceleration > 10){
    digitalWrite(LED,HIGH);
    char databuff[20]= "";
    dtostrf(acceleration,5,2,databuff);
    rf95.send((uint8_t *) databuff, 20);
    delay(10);
    rf95.waitPacketSent();

    //reading main power source voltage
    char batterybuff[20];
    float voltage = readVcc() / 1000.00;
    float percentage = mapf(voltage,3.10,4.20,0.00,100.00);
    dtostrf(percentage,5,2,batterybuff);
    rf95.send((uint8_t *) batterybuff, 20);
    delay(10);
    rf95.waitPacketSent();
  }
  else {
    digitalWrite(LED,LOW);
  }
  
  Serial.print("Acceleration: ");
  Serial.print(acceleration);
  Serial.println(" M/S^2");

  //set the oldposition to the new position
  oldPos[0] = newPos[0];
  oldPos[1] = newPos[1];
  oldPos[2] = newPos[2];
  delay(100);
}

//map function converted to double
double mapf(double x, double in_min, double in_max, double out_min, double out_max)
{
    return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
}

//read internal voltage of the arduino
 long readVcc() {
  long result; // Read 1.1V reference against AVcc
  ADMUX = (1 << REFS0) | (1 << MUX3) | (1 << MUX2) | (1 << MUX1); 
  delay(2); // Wait for Vref to settle
  ADCSRA |= ( 1 << ADSC); // Convert 
  while (bit_is_set(ADCSRA,ADSC));
  result = ADCL; 
  result |= ADCH<<8;
  result = 1126400L / result; // Back-calculate AVcc in mV
  return result;
}
