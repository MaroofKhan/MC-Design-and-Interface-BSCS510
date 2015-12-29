#define BAUD_RATE 57600

String command = "";
void execute (String command) {
  Serial.println(command)
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

