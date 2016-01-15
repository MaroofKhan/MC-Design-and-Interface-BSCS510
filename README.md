#Remote Labyrinth

##Objective:
Control a labyrinth, via servo motors, which, in turn are controlled by an android device, via Bluetooth.

##Components:
*	HC 05 (Bluetooth Module)
*	Servo motors (x3)
*	Arduino UNO
*	Android Device.
*	Several plastic components.

##Theory:
###Servo Motor:
A servomotor is a rotary actuator or linear actuator that allows for precise control of angular or linear position, velocity and acceleration.[1] It consists of a suitable motor coupled to a sensor for position feedback. It also requires a relatively sophisticated controller, often a dedicated module designed specifically for use with servomotors.
Servomotors are not a specific class of motor although the term servomotor is often used to refer to a motor suitable for use in a closed-loop control system.
Servomotors are used in applications such as robotics, CNC machinery or automated manufacturing.

Servo with Arduino UNO, a simple program:

````c
#include <Servo.h>

Servo testServo;
int testPin = 9;

void setup() {
	testServo.attach(testPin);
}

void loop() {  
	testServo.write(45);
}
````

###HC 05 (Bluetooth Module):
HC serial Bluetooth products consist of Bluetooth serial interface module and Bluetooth adapter. Bluetooth serial module is used for converting serial port to Bluetooth. These modules have two modes: master and slaver device. The device named after even number is defined to be master or slaver when out of factory and can’t be changed to the other mode.
	
HC 05 with Arduino UNO, a simple program:

````c
#define BAUD_RATE 57600

String command = "";
void execute (String command) {
	Serial.println(command);
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

void setup () {
	setupBluetoothInput();
}

void loop () {
	getBluetoothInput();
}
````

##Logic:
All the servo are connected to the arduino UNO.
	
*Right servo is connected to pin 9.
*Left servo is connected to pin 6.
*Upper servo is connected to pin 10.
*Lower servo is connected to pin 11.

Android phone send a command to the UNO, via HC 05, in the format of L0.5! 
L = Left.
R = Right.
U = Up.
D = Down.
0.5 indicates the movement servo should execute.

The minimum servo angle is 90 degrees, and maximum is 180 degrees, which is mapped onto 0.0 – 5.0.

With the android phones movement, it send commands to arduino UNO via HC 05, which rotates the servo motors.
