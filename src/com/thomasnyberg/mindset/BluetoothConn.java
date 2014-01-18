package com.thomasnyberg.mindset;

import java.lang.Thread;
import android.bluetooth.BluetoothAdapter;

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

  public void connect() {}

  private int counter = 1;
  public String getData() {
    wasteSomeTime(1000);
    return "data" + Integer.toString(counter++);
  }
}
