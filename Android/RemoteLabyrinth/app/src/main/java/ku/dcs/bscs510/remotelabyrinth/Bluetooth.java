package ku.dcs.bscs510.remotelabyrinth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.camera2.TotalCaptureResult;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;
import java.util.logging.LogRecord;

public class Bluetooth extends Activity {

    public static void getHandler(Handler handler) {
        mHandler = handler;
    }

    static Handler mHandler = new Handler();

    protected static final int SUCCESS_CONNECT = 0;
    protected static final int MESSAGE_READ = 1;
    protected static final String HC_05 = "HC05";

    static ConnectedThread connectedThread;
    static final UUID MY_UUID = UUID.fromString(" 00001101-0000-1000-8000-00805F9B34FB");

    static BluetoothAdapter BTAdapter;
    Set<BluetoothDevice> devicesArray;
    BluetoothDevice HC05;
    IntentFilter filter;
    BroadcastReceiver reciever;

    protected void disconnect() {
        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BTAdapter == null) {
            Toast.makeText(getApplicationContext(), "No Bluetooth", Toast.LENGTH_SHORT).show();
        } else {
            if (!(BTAdapter.isEnabled()))
                turnOnBT();
            getPairedDevices();
            startDiscovery();
        }

        initialize();
    }

    private void initialize() {
        BTAdapter = BluetoothAdapter.getDefaultAdapter();
        filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        reciever = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                    if (BTAdapter.getState() == BluetoothAdapter.STATE_OFF)
                        turnOnBT();
                }
            }
        };
    }

    private void turnOnBT() {
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(intent, 1);
    }

    private void getPairedDevices() {
        BTAdapter.cancelDiscovery();
        BTAdapter.startDiscovery();
    }
    private void startDiscovery() {
        devicesArray = BTAdapter.getBondedDevices();
        if (devicesArray.size() > 0) {
            for (BluetoothDevice device:
                 devicesArray) {
                if (device.getName().equals(HC_05))
                    HC05 = device;
            }

            if (HC05 == null) {
                Toast.makeText(getApplicationContext(), "Couldn't find the Remote Labyrinth.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "LETS ROLL!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(reciever);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_CANCELED)
            Toast.makeText(getApplicationContext(), "Bluetooth must be enabled to continue.", Toast.LENGTH_SHORT).show();
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            BluetoothSocket tmp = null;
            mmDevice = device;

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                // MY_UUID is the app's UUID string, also used by the server code
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException exception) { }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it will slow down the connection
            BTAdapter.cancelDiscovery();

            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                mmSocket.connect();
                //connectedThread = new ConnectedThread(mmSocket);
            } catch (IOException connectException) {
                // Unable to connect; close the socket and get out
                try {
                    mmSocket.close();
                } catch (IOException closeException) { }
                return;
            }

            // Do work to manage the connection (in a separate thread)
            mHandler.obtainMessage(SUCCESS_CONNECT, mmSocket).sendToTarget();
        }

        /** Will cancel an in-progress connection, and close the socket */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

    static class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }
        StringBuffer sbb = new StringBuffer();
        public void run() {

            byte[] buffer;  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    try {
                        sleep(30);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    buffer = new byte[1024];
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    // Send the obtained bytes to the UI activity
                    mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(String income) {

            try {
                mmOutStream.write(income.getBytes());
                for(int i=0;i<income.getBytes().length;i++)
                    Log.v("outStream" + Integer.toString(i), Character.toString((char) (Integer.parseInt(Byte.toString(income.getBytes()[i])))));
                try {
                    Thread.sleep(20);
                } catch (InterruptedException exception) {
                    // TODO Auto-generated catch block
                    exception.printStackTrace();
                }
            } catch (IOException exception) { }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException exception) { }
        }
    }
}
