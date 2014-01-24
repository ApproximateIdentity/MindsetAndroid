package com.thomasnyberg.mindset;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import java.io.IOException;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.BluetoothDevice;

import java.lang.Thread;
import android.os.Handler;
import android.os.Message;

import java.io.InputStream;
import java.util.UUID;

public class PredictActivity extends Activity {
  private LogisticRegression lr;
  private String concept1, concept2;
  private double[] coefficients;
  private Terminal term;
  private boolean connected = false;

  private Handler mainHandler = null;
  private InputStream dataStream = null;

  private MindsetStreamThread mindsetStreamThread = null;
  private BluetoothAdapter myBluetoothAdapter = null;
  private BluetoothSocket sock = null;
  private BluetoothDevice mindset = null;
  private final String uuidString = "00001101-0000-1000-8000-00805F9B34FB";
  private final String mindsetAddress = "00:13:EF:00:3B:F6";

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.predict);

    term = new Terminal((TextView) findViewById(R.id.main_window), 30);

    Bundle extras = getIntent().getExtras();
    concept1 = extras.getString("concept1");
    concept2 = extras.getString("concept2");

    coefficients = extras.getDoubleArray("coefficients");

    /* Seems like this is out of place */
    myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    mindset = myBluetoothAdapter.getRemoteDevice(mindsetAddress);
  }

  @Override
  public void onStart() {
    super.onStart();
    lr = new LogisticRegression();
    lr.coefficients = coefficients;

    /* Open connection to Mindset. Attempt MAX_ATTEMPTS times. */
    final int MAX_ATTEMPTS = 5;
    boolean result = false;
    for (int i = 0; i < MAX_ATTEMPTS; i++) {
      wasteSomeTime(1000);
      term.writeLine("Attempting to connect to Bluetooth.");
      result = openBluetoothConnection();

      /* If result is true, connection opened successfully */
      if (result) {
        connected = true;
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

        /* First print the data */
        String text = "";
        text += Integer.toString(data[0]) + ", ";
        text += Integer.toString(data[1]) + ", ... ,";
        text += Integer.toString(data[9]) + ", ";
        text += Integer.toString(data[10]);
        text += " <---> ";
        if (lr.predict(data) == 1) {
          text += concept1;
        } else {
          text += concept2;
        }

        term.writeLine(text);
      }
    };

    /* Create Mindset worker thread. */
    mindsetStreamThread = new MindsetStreamThread(mainHandler, dataStream);
    mindsetStreamThread.start();
  }

  @Override
  public void onStop() {
    super.onStop();

    mindsetStreamThread.halt();
    try {
      mindsetStreamThread.join();
    } catch (InterruptedException e) {
      /* Do something... */
    }

    /* Currently have no return value. Don't know what to do if this fails. */
    if (connected) {
      closeBluetoothConnection(mindsetStreamThread);
      connected = false;
    }
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

  private void wasteSomeTime(int msec) {
    try {
      Thread.sleep(msec);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
