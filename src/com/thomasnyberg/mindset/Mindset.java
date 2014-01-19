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
import java.io.InputStream;

import android.os.Looper;
import android.os.Handler;
import android.os.Message;

public class Mindset extends Activity {
  private boolean HALT = false;
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

    /* I believe this is currently unbuffered! This is probably not a good
       idea! Should probably change to use class BufferedInputStream or
       something else! */
    try {
      dataStream = sock.getInputStream();
    } catch (IOException e) {
      term.writeLine("Error: Could not open data stream.");
      HALT = true;
      return;
    }

    mainHandler = new Handler() {
      public void handleMessage(Message msg) {
        Integer[] data = (Integer[]) msg.obj;
        String text = "";
        for (int i = 0; i < 7; i++) {
          text += Integer.toString(data[i]) + ", ";
        }
        text += Integer.toString(data[7]);
        term.writeLine(text);
      }
    };

    mindsetStreamThread = new MindsetStreamThread(mainHandler, dataStream);
    mindsetStreamThread.start();
  }

  @Override
  protected void onStop() {
    super.onStop();

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
        HALT = true;
      }
    }

    if (sock != null) {
      try {
        sock.close();
        sock = null;
      } catch (IOException e) {
        term.writeLine("Error: Could not close Mindset connection.");
        HALT = true;
      }
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
