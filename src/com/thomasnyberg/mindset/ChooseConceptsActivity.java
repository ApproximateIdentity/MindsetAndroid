package com.thomasnyberg.mindset;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.EditText;

import android.content.Context;
import android.widget.Button;
import android.view.View;
import android.view.View.OnClickListener;
import android.content.Intent;

import java.lang.Thread;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

public class ChooseConceptsActivity extends Activity {
  private boolean NO_BLUETOOTH = false;
  private boolean initialized = false;
  private BluetoothAdapter myBluetoothAdapter = null;
  private final String mindsetAddress = "00:13:EF:00:3B:F6";
  private BluetoothDevice mindset = null;
  private Terminal term = null;
  private EditText concept1view, concept2view;
  private Button goToDataGatherButton;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.choose_concepts);
      
    term = new Terminal((TextView) findViewById(R.id.main_window), 3);

    if (NO_BLUETOOTH) {
      return;
    }

    /* Initialize Bluetooth if necessary. */
    if (!initialized) {
      boolean result = initializeBluetooth();
      if (!result) { /* Bluetooth failed to initialize */
        term.writeLine("Error: Failed to initialize Bluetooth.");
        return;
      }
    }

    setContentView(R.layout.choose_concepts);
      
    term = new Terminal((TextView) findViewById(R.id.main_window), 3);
    String text = "Welcome!\n\n";
    text += "Choose two concepts below to begin using the program.";
    term.writeLine(text);

    concept1view = (EditText) findViewById(R.id.concept1);
    concept2view = (EditText) findViewById(R.id.concept2);

    addListenerOnButton();
  }

  private void addListenerOnButton() {
    final Context context = this;

    goToDataGatherButton = (Button) findViewById(R.id.button);

    goToDataGatherButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View arg0) {
        String concept1 = concept1view.getText().toString();
        String concept2 = concept2view.getText().toString();

        Intent intent = new Intent(context, DataGatherActivity.class);
        intent = intent.putExtra("concept1", concept1);
        intent = intent.putExtra("concept2", concept2);
        startActivity(intent);
      }
    });
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

  private void wasteSomeTime(int msec) {
    try {
      Thread.sleep(msec);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
