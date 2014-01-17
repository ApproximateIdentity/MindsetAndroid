package com.thomasnyberg.mindset;

import java.lang.Thread;

public class BluetoothConn {
  BluetoothConn(String name) {}

  private void wasteSomeTime(int msec) {
    try {
      Thread.sleep(msec);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public void connect() {
    wasteSomeTime(2000);
  }

  private int counter = 1;
  public String getData() {
    wasteSomeTime(2000);
    return "data" + Integer.toString(counter++);
  }
}
