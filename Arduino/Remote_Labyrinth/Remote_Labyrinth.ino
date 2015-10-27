
void setup() {
  Serial.begin(115200);
}

void loop() {
  if (Serial.available() > 0) {
    char dimension = Serial.read();
    char input[3] = Serial.readString();
    float value = ((float)atoi(input)/(float)100);
    switch (dimension) {
      case 'X':
        break;
      case 'Y':
        break;
      case 'Z':
        break;
    }
  }

}
