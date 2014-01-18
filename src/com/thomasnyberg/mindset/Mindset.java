package com.thomasnyberg.mindset;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.bluetooth.BluetoothAdapter;
import java.util.UUID;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.BluetoothDevice;

import android.os.AsyncTask;
import android.content.Intent;

import java.io.IOException;
import java.lang.Thread;

public class Mindset extends Activity {
  private boolean HALT = false;
  private Terminal term;
  private BluetoothAdapter myBluetoothAdapter;
  private final String mindsetAddress = "00:13:EF:00:3B:F6";
  private BluetoothDevice mindset;
  private final String uuidString = "00001101-0000-1000-8000-00805F9B34FB";
  private BluetoothSocket sock;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    /* Initialize main window terminal. */
    /* Here I apparently am assuming this operation will never fail... */
    term = new Terminal((TextView) findViewById(R.id.mainWindow));

    /* I am making sure the BluetoothAdapter is turned on in the UI thread
       out of convenience so that I don't need to start a new activity later.
       However, even if that is changed later, 
       BluetoothAdapter.getDefaultAdapter must still first be called here
       instead of first in the other thread by AsyncTask. It is a bug in
       Android. See the following link for more info:

          "http://stackoverflow.com/questions/5920578/" +
          "bluetoothadapter-getdefault-throwing" +
          "-runtimeexception-while-not-in-activity"
    */
    myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    if (myBluetoothAdapter == null) {
      term.writeLine("Error: Bluetooth is not supported on this platform.");
      HALT = true;
      return;
    }

    if (!myBluetoothAdapter.isEnabled()) {
      term.writeLine("Bluetooth currently disabled.");
      term.writeLine("Attempting to enable Bluetooth.");
      Intent enableBt = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE); 
      int REQUEST_ENABLE_BT = 1;
      startActivityForResult(enableBt, REQUEST_ENABLE_BT);
    }

    /* The enabling of Bluetooth is asynchronous which I do not like. This
       blocks until Bluetooth is enabled. */
    for (int i = 0; i < 120; i++) {
      wasteSomeTime(500);
      if (myBluetoothAdapter.isEnabled()) {
        break;
      }
    }

    if (!myBluetoothAdapter.isEnabled()) {
      term.writeLine("Error: Attempt at enabling Bluetooth timed out.");
      HALT = true;
      return;
    } else {
      term.writeLine("Successfully enabled Bluetooth.");
    }

    term.writeLine("Attempting to find Mindset device.");
    try {
      mindset = myBluetoothAdapter.getRemoteDevice(mindsetAddress);
    } catch (IllegalArgumentException e) {
      term.writeLine("Could not find Mindset device.");
      HALT = true;
      return;
    }
    term.writeLine("Successfully found Mindset device.");
  }

  @Override
  protected void onStart() {
    super.onStart();

    if (HALT) {
      term.writeLine("Error: General error.");
      term.writeLine("HALT.");
      return;
    }

    /* Create RFComm socket connection with Mindset. */
    /* The following string is the UUID for the serial port profile. */
    UUID uuid = UUID.fromString(uuidString);
    try {
      sock = mindset.createRfcommSocketToServiceRecord(uuid);
    } catch (IOException e) {
      term.writeLine("Error: Could not create RFComm socket.");
      HALT = true;
      return;
    }

    term.writeLine("Attempting to connect to Mindset.");
    try {
      sock.connect();
    } catch (IOException e) {
      term.writeLine("Could not connect to Mindset.");
      HALT = true;
      return;
    }
    term.writeLine("Successfully connected to Mindset.");
    /*new MindsetDataStream().execute();*/
  }

  @Override
  protected void onStop() {
    super.onStop();
    try {
      sock.close();
    } catch (IOException e) {
      term.writeLine("Error: Could not close Mindset connection.");
      HALT = true;
      return;
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
  }

  private void wasteSomeTime(int msec) {
    try {
      Thread.sleep(msec);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private class MindsetDataStream extends AsyncTask<Void, String, Void> {
    protected void onPreExecute() {}

    protected Void doInBackground(Void... params) {
      publishProgress("Attempting to connect to Mindstream.");

      BluetoothConn conn = new BluetoothConn();
      conn.connect();
      if (conn.error) {
        publishProgress("Error: " + conn.errorString);
        conn.error = false;
      } else {
        publishProgress("Successfully connected!");

        publishProgress("Receiving data...");
        String data;
        for (;;) {
          data = conn.getData();
          if (conn.error) {
            publishProgress("Error: " + conn.errorString);
            conn.error = false;
          } else {
            publishProgress(data);
          }
        }
      }
      return null;
    }

    protected void onProgressUpdate(String... progress) {
      term.writeLine(progress[0]);
    }

    protected void onPostExecute(Void... result) {}
  }
}
