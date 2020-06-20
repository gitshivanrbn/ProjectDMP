#include "HX711.h"
#include <SPI.h>
#include <RH_RF95.h>

//HX711
HX711 scale(7, 8 );

float calibration_factor = 48100;
float units;

//RFM95 
#define RESET 2
#define DIO0 3
#define NSS 4
#define FREQ 868.0 //433.0 //915.0

RH_RF95 rf95(NSS, DIO0);

#define LED 6

void setup() {
  Serial.begin(9600);
  pinMode(LED,OUTPUT);
  //HX711 Setup
  scale.set_scale();
  scale.tare();  //Reset the scale to 0

  long zero_factor = scale.read_average(); //Get a baseline reading\

  //RFM95 Setup
 //Reset the module
  pinMode(RESET, OUTPUT);
  digitalWrite(RESET, HIGH);

  digitalWrite(RESET, LOW);
  delay(10);
  digitalWrite(RESET, HIGH);
  delay(10);
  
  //initialize the lora connection
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

  scale.set_scale(calibration_factor); 

  Serial.print("Reading: ");
  units = scale.get_units(), 10;
  Serial.print(units);
  Serial.print(" kg"); 
  Serial.println();
  
  if (units < 0)
  {
    units = 0.00;
    digitalWrite(LED,LOW);
  }
  else if (units > 1.2){
    digitalWrite(LED,HIGH);
    char databuff[20]= "";
    dtostrf(units,5,2,databuff);
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
  delay(200);
}

double mapf(double x, double in_min, double in_max, double out_min, double out_max)
{
    return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
}

 long readVcc() {
  long result; // Read 1.1V reference against AVcc
  ADMUX = _BV(REFS0) | _BV(MUX3) | _BV(MUX2) | _BV(MUX1); 
  delay(2); // Wait for Vref to settle
  ADCSRA |= _BV(ADSC); // Convert 
  while (bit_is_set(ADCSRA,ADSC));
  result = ADCL; 
  result |= ADCH<<8;
  result = 1126400L / result; // Back-calculate AVcc in mV
  return result;
}
