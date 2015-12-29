package com.remote_labyrinth.android.remotelabyrinth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    Switch connectionSwitch;
    Button startGame;

    SensorManager sensorManager;
    Sensor accelerometer;
    final float MAX_VALUE = (float) 5.0;
    final float MIN_VALUE = (float) -5.0;

    boolean isConnected;

    BluetoothAdapter bluetoothAdapter;
    BluetoothSocket bluetoothSocket;
    private static final String REMOTE_LABYRINTH_ADDRESS = "98:D3:31:90:3A:2E";
    private static BluetoothDevice remoteLabyrinth;

    IntentFilter filter;
    BroadcastReceiver reciever;

    private boolean sendCommand(String command) {

        try {
            bluetoothSocket.getOutputStream().write((command + '!').getBytes());
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private void turnOnBluetooth() {
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(intent, 1);
    }

    void disconnecBluetooth(boolean show) {
        bluetoothAdapter = null;
        bluetoothSocket = null;
        if (show)
            Toast.makeText(getApplicationContext(), "Remote Labyrinth Disconnected", Toast.LENGTH_LONG).show();
    }

    void setupBluetooth() {
        if (bluetoothAdapter == null) bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter.isEnabled()) {

        } else {
            filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            reciever = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                        if (bluetoothAdapter.getState() == BluetoothAdapter.STATE_OFF)
                            turnOnBluetooth();
                    }
                }
            };
        }
    }

    boolean connectToRemoteLabyrinth() {
        bluetoothAdapter.cancelDiscovery();
        try {
            if (bluetoothSocket == null) {
                remoteLabyrinth = bluetoothAdapter.getRemoteDevice(REMOTE_LABYRINTH_ADDRESS);
                UUID uuid = remoteLabyrinth.getUuids()[0].getUuid();
                bluetoothSocket = remoteLabyrinth.createRfcommSocketToServiceRecord(uuid);
                Thread.sleep(500);
                bluetoothSocket.connect();
                return true;
            }
        } catch (Exception exception) {
            return false;
        }

        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disconnecBluetooth(false);
    }

    void startNewGame() {
        startGame.setVisibility(View.INVISIBLE);
        connectionSwitch.setVisibility(View.INVISIBLE);

    }

    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float X = event.values[0];
        float Y = event.values[1];

        if (X > MAX_VALUE) X = MAX_VALUE;
        if (Y > MAX_VALUE) Y = MAX_VALUE;
        if (X < MIN_VALUE) X = MIN_VALUE;
        if (Y < MIN_VALUE) Y = MIN_VALUE;

        String commandY = "";
        String commandX = "";

        if (Y < 0) commandY += 'R' + String.format("%.2f", Y * -1);
        else commandY += 'L' + String.format("%.2f", Y);


        if (X < 0) commandX += 'D' + String.format("%.2f", X * -1);
        else  commandX += 'U' + String.format("%.2f", X);

        if (isConnected) {
            if (sendCommand(commandX) && sendCommand(commandY)) {

            } else {

            }
        } else {

        }

        startGame.setText("X: " + X + " Y: " + Y);

    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);


        startGame = (Button)findViewById(R.id.startGame);
        startGame.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isConnected) {
                    if (!sendCommand("R15!")) {

                    } else {

                    }
                }
            }
        });

        connectionSwitch = (Switch)findViewById(R.id.connectToRemoteLabyrinth);
        connectionSwitch.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(getApplicationContext(), "Connecting to Remote Labyrinth...", Toast.LENGTH_LONG).show();
                    disconnecBluetooth(false);
                    setupBluetooth();
                    if (connectToRemoteLabyrinth()) {
                        Toast.makeText(getApplicationContext(), "Connected to Remote Labyrinth.", Toast.LENGTH_LONG).show();
                        isConnected = true;

                        if (sendCommand("R15!")) startGame.setText("DONE");
                        else startGame.setText("NOT DONE");
                    } else {
                        Toast.makeText(getApplicationContext(), "Could not connect to Remote Labyrinth.", Toast.LENGTH_LONG).show();
                        connectionSwitch.setChecked(false);
                        isConnected = false;
                    }
                } else {
                    disconnecBluetooth(true);
                }
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
