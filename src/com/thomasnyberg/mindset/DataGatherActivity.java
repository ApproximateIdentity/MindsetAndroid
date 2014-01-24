package com.thomasnyberg.mindset;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import android.content.Context;
import android.widget.Button;
import android.view.View;
import android.view.View.OnClickListener;
import android.content.Intent;

import java.io.IOException;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.BluetoothDevice;

import java.lang.Thread;
import android.os.Handler;
import android.os.Message;

import java.io.InputStream;
import java.util.UUID;

import java.util.ArrayList;
import java.util.List;

public class DataGatherActivity extends Activity {
  private MindsetStreamThread mindsetStreamThread = null;
  private String concept1;
  private String concept2;
  private Terminal term;

  private Button predictButton;
  private Button toggleConceptButton, toggleSaveButton;

  private int toggleConcept = 1;
  private boolean toggleSave = false;

  private Handler mainHandler = null;
  private InputStream dataStream = null;

  private BluetoothAdapter myBluetoothAdapter = null;
  private BluetoothSocket sock = null;
  private BluetoothDevice mindset = null;
  private final String uuidString = "00001101-0000-1000-8000-00805F9B34FB";
  private final String mindsetAddress = "00:13:EF:00:3B:F6";

  private List<double[]> mindsetData;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Bundle extras = getIntent().getExtras();
    concept1 = extras.getString("concept1");
    concept2 = extras.getString("concept2");

    setContentView(R.layout.data_gather);

    term = new Terminal((TextView) findViewById(R.id.main_window), 22);

    predictButton = (Button) findViewById(R.id.predict);

    toggleConceptButton = (Button) findViewById(R.id.toggle_concept);
    toggleConceptButton.setText(concept1);

    toggleSaveButton = (Button) findViewById(R.id.toggle_save);
    toggleSaveButton.setText("Save: Off");

    addListenerOnButtons();

    startPrintingShit();

    /* Seems like this is out of place */
    myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    mindset = myBluetoothAdapter.getRemoteDevice(mindsetAddress);
  }

  @Override
  public void onStart() {
    super.onStart();

    mindsetData = new ArrayList<double[]>();

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

        /* First print the data */
        String text = "";
        text += Integer.toString(data[0]) + ", ";
        text += Integer.toString(data[1]) + ", ... ,";
        text += Integer.toString(data[9]) + ", ";
        text += Integer.toString(data[10]);
        text += " <---> ";
        if (toggleConcept == 1) {
          text += concept1;
        } else {
          text += concept2;
        }

        term.writeLine(text);

        /* Next save the data if necessary. */
        
        /* Replace first element (signal quality) with tag. */
        if (toggleSave) {
          data[0] = toggleConcept;
          int size = data.length;
          double[] doubleData = new double[size];
          for (int i = 0; i < size; i++) {
            doubleData[i] = (double) data[i];
          }

          mindsetData.add(doubleData);
        }
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
    closeBluetoothConnection(mindsetStreamThread);

    mindsetData = null;
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

  private void startPrintingShit() {
    term.writeLine("Data 1");
  }

  private void wasteSomeTime(int msec) {
    try {
      Thread.sleep(msec);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private void addListenerOnButtons() {
    final Context context = this;

    predictButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View arg0) {
        double[][] arrayData = new double[mindsetData.size()][];
        mindsetData.toArray(arrayData);
  
        /* Create logistic class and save coefficients. */
        LogisticRegression lr = new LogisticRegression();
        lr.data = arrayData;
        lr.fit();
        double[] coefficients = lr.getCoefficients();

        Intent intent = new Intent(context, PredictActivity.class);
        intent = intent.putExtra("concept1", concept1);
        intent = intent.putExtra("concept2", concept2);
        intent = intent.putExtra("coefficients", coefficients);
        startActivity(intent);
      }
    });

    toggleConceptButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View arg0) {
        if (toggleConcept == 1) {
          toggleConcept = 2;
          toggleConceptButton.setText(concept2);
        } else {
          toggleConcept = 1;
          toggleConceptButton.setText(concept1);
        }
      }
    });

    toggleSaveButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View arg0) {
        if (toggleSave) {
          toggleSave = false;
          toggleSaveButton.setText("Save: Off");
        } else {
          toggleSave = true;
          toggleSaveButton.setText("Save: On");
        }
      }
    });
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
