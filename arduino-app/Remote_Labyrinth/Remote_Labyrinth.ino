#include <Servo.h>

#define BAUD_RATE 57600

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

Servo rightServo, leftServo, upServo, downServo;

String getString (String characters, int startIndex, int endIndex) {
  String string = "";
  for (int index = startIndex; index < endIndex; index++)
    string += characters[index];
    return string;
}

void execute (Servo servo, double angle) {
  servo.write(angle);
}

double map (double value) {
  return (((90.0 / 5.0) * value) + 90.0);
}

String command = "";
void execute (String command) {
  char servo = command[0];
  String value = getString(command, 1, command.length());
  char characters[value.length()];
  value.toCharArray(characters,value.length());
  double _value = map (atof(characters));
  Serial.println(_value);
  switch (servo) {
    case 'R': 
      execute(rightServo, _value);
      break;
    case 'L':
      execute(leftServo, _value);
      break;
    case 'U':
      execute(upServo, _value);
      break;
    default:
      execute(downServo, _value);
      break;
  }
  
}

void setupBluetoothInput () {
  while (!Serial);
  Serial.begin(BAUD_RATE);
  Serial.println("Let's roll!");
}

void getBluetoothInput () {
  while (Serial.available() == 0);
  char input;
  while (Serial.available() > 0) {
    input = ((byte)Serial.read());
    if (input == '!') {
      execute(command);
      command = "";
      break;
    }
    else command += input;
  }
}

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

void setup () {
  setupBluetoothInput();
  startGame();
}

void loop () {
  getBluetoothInput();
}

