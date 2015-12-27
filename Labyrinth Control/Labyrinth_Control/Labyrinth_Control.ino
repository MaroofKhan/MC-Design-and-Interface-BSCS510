#include <Event.h>
#include <Timer.h>

#define RIGHT_SERVO_PIN 5
#define LEFT_SERVO_PIN  6
#define UP_SERVO_PIN    10
#define DOWN_SERVO_PIN  11

#define RX 0
#define TX 1

#define STARTING_SERVO_ANGLE 90
#define MAX_SERVO_ANGLE      180
#define MIN_SERVO_ANGLE      90
#define INTIAL_SERVO_ANGLE   45
#define FINAL_SERVO_ANGLE    45

#include <Servo.h>
#include <Timer.h>

Servo rightServo, leftServo, upServo, downServo;

void setupServo(Servo servo, int pin) {
  servo.attach(pin);
  servo.write(STARTING_SERVO_ANGLE); 
}

void startGame() {
  setupServo(rightServo, RIGHT_SERVO_PIN);
  setupServo(leftServo, LEFT_SERVO_PIN);
  setupServo(upServo, UP_SERVO_PIN);
  setupServo(downServo, DOWN_SERVO_PIN);
}

void finishGame (){
  rightServo.write(FINAL_SERVO_ANGLE);
  leftServo.write (FINAL_SERVO_ANGLE);
  upServo.write   (FINAL_SERVO_ANGLE);
  downServo.write (FINAL_SERVO_ANGLE);
}

void setup() {
  startGame();
}

void loop() {

  delay(10000);
  finishGame();
}
