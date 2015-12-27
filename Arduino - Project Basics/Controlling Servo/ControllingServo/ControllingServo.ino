/*
Instructions:
* Google 'Servo Motor'
*/


#include <Servo.h>

Servo testServo;
int testPin = 9;

void setup() {
  testServo.attach(testPin);
}

void loop() {
  
  for (int pos = 0; pos <= 180; pos += 10) {
    testServo.write(pos);
    delay(15);
  }
  
  for (int pos = 180; pos >= 0; pos -= 10) {
    testServo.write(pos);
    delay(15);
  }

}
