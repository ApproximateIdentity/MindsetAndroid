package com.thomasnyberg.mindset;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.BluetoothDevice;
import java.util.UUID;
import android.content.Intent;

import java.lang.Thread;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.io.InputStream;


public class Mindset extends Activity {
  private boolean NO_BLUETOOTH = false;
  private Terminal term = null;
  private BluetoothAdapter myBluetoothAdapter = null;
  private final String mindsetAddress = "00:13:EF:00:3B:F6";
  private BluetoothDevice mindset = null;
  private final String uuidString = "00001101-0000-1000-8000-00805F9B34FB";
  private BluetoothSocket sock = null;
  private final boolean mayInterruptIfRunning = true;
  private InputStream dataStream = null;
  private Handler mainHandler = null;
  private MindsetStreamThread mindsetStreamThread = null;
  private boolean initialized = false;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.main);

    /* Initialize main window terminal. */
    /* Here I apparently assume this operation will never fail... */
    term = new Terminal((TextView) findViewById(R.id.mainWindow), 30);
  }


  @Override
  protected void onStart() {
    super.onStart();

    /* If there is no Bluetooth everything should stop. */
    if (NO_BLUETOOTH) {
      return;
    }

    /* Initialize Bluetooth if necessary. */
    if (!initialized) {
      term.writeLine("Attempting to initialize Bluetooth");
      boolean result = initializeBluetooth();
      if (!result) { /* Bluetooth failed to initialize */
        term.writeLine("Error: Failed to initialize Bluetooth.");
        return;
      }
    }

    /* Open connection to Mindset. Attempt MAX_ATTEMPTS times. */
    final int MAX_ATTEMPTS = 5;
    boolean result = false;
    for (int i = 0; i < MAX_ATTEMPTS; i++) {
      term.writeLine("Attempting to connect to Bluetooth.");
      result = openBluetoothConnection();

      /* If result is true, connection opened successfully */
      if (result) {
        break;
      }
      term.writeLine("Error: Failed to connect to Bluetooth.");
      wasteSomeTime(500);
    }
    if (!result) {
      term.writeLine("Error: Attempt at initializing Bluetooth timed out.");
      return;
    }


    /* Create a callback which handles data sent back from worker threads. */
    mainHandler = new Handler() {
      public void handleMessage(Message msg) {
        Integer[] data = (Integer[]) msg.obj;
        String text = "";
        text += Integer.toString(data[0]) + ", ";
        text += Integer.toString(data[1]) + ", ... ,";
        text += Integer.toString(data[9]) + ", ";
        text += Integer.toString(data[10]);
        term.writeLine(text);
      }
    };

    /* Create Mindset worker thread. */
    mindsetStreamThread = new MindsetStreamThread(mainHandler, dataStream);
    mindsetStreamThread.start();
  }

  @Override
  protected void onStop() {
    super.onStop();

    /* Currently have no return value. Don't know what to do if this fails. */
    closeBluetoothConnection(mindsetStreamThread);
  }


  private boolean initializeBluetooth() {
    myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    if (myBluetoothAdapter == null) {
      term.writeLine("Error: Bluetooth is not supported on this device.");
      NO_BLUETOOTH = true;
      return false;
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
      return false;
    } else {
      term.writeLine("Successfully enabled Bluetooth.");
    }

    try {
      mindset = myBluetoothAdapter.getRemoteDevice(mindsetAddress);
    } catch (IllegalArgumentException e) {
      term.writeLine("Could not find Mindset device.");
      return false;
    }

    initialized = true;

    return true;
  }

  private boolean openBluetoothConnection() {
    /* Create RFComm socket connection with Mindset. */
    /* The following string is the UUID for the serial port profile. */
    UUID uuid = UUID.fromString(uuidString);
    try {
      sock = mindset.createRfcommSocketToServiceRecord(uuid);
    } catch (IOException e) {
      term.writeLine("Error: Could not create RFComm socket.");
      return false;
    }

    try {
      sock.connect();
    } catch (IOException e) {
      term.writeLine("Could not connect to Mindset.");
      return false;
    }
    term.writeLine("Successfully connected to Mindset.");

    /* I believe this is currently unbuffered! This is probably not a good
       idea! Should probably change to use class BufferedInputStream or
       something else! */
    try {
      dataStream = sock.getInputStream();
    } catch (IOException e) {
      term.writeLine("Error: Could not open data stream.");
      return false;
    }

    return true;
  }

  private void closeBluetoothConnection(MindsetStreamThread
                                        mindsetStreamThread) {
    if (mindsetStreamThread != null) {
      mindsetStreamThread.halt();
      try {
        mindsetStreamThread.join();
      } catch (InterruptedException e) {
        /* Not sure what to do in this case */
      }
      mindsetStreamThread = null;
    }

    if (dataStream != null) {
      try {
        dataStream.close();
        dataStream = null;
      } catch (IOException e) {
        term.writeLine("Error: Could not close data stream.");
      }
    }

    if (sock != null) {
      try {
        sock.close();
        sock = null;
      } catch (IOException e) {
        term.writeLine("Error: Could not close Mindset connection.");
      }
    }
  }

  private void wasteSomeTime(int msec) {
    try {
      Thread.sleep(msec);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private class MindsetStreamThread extends Thread {
    MindsetStreamThread(Handler mainHandler, InputStream stream) {
      dataStream = stream;
    }

    @Override
    public void run() {
      Parser parser = new Parser(dataStream);
      Integer[] data = new Integer[8];
      while (running) {
        /* Maybe should add some error-checking here. */
        data = parser.next();
        Message msg = Message.obtain();
        msg.obj = data;
        mainHandler.sendMessage(msg);
      }
    }

    public void halt() {
      running = false;
    }

    private boolean running = true;
    private InputStream dataStream = null;
  }
}
