package com.thomasnyberg.mindset;

import java.lang.Thread;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import java.util.UUID;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;

public class BluetoothConn {
  BluetoothAdapter myBluetoothAdapter = null;
  public boolean error = false;
  public String errorString = "";

  BluetoothConn(String name) {
    myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    /* Make sure connection blocks until Bluetooth is enabled on the phone. */
    while (!myBluetoothAdapter.isEnabled()) {
      wasteSomeTime(500);
    }
  }

  private void wasteSomeTime(int msec) {
    try {
      Thread.sleep(msec);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public void connect() {
    BluetoothDevice mindset = null;
    BluetoothSocket btSock = null;

    /* The following is my Mindset's address. */
    final String mindsetAddress = "00:13:EF:00:3B:F6";

    try {
      mindset = myBluetoothAdapter.getRemoteDevice(mindsetAddress);
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    }

    /* The following string is the UUID for the serial port profile. */
    final String uuidString = "00001101-0000-1000-8000-00805F9B34FB";
    UUID uuid = UUID.fromString(uuidString);

    if (mindset != null) {
      try {
        btSock = mindset.createRfcommSocketToServiceRecord(uuid);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    if (btSock != null) {
      try {
        btSock.connect();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  private int counter = 1;
  public String getData() {
    wasteSomeTime(1000);
    return "data" + Integer.toString(counter++);
  }
}
