package com.thomasnyberg.mindset;

import android.bluetooth.BluetoothSocket;
import java.lang.Thread;

public class MindsetParser {
  MindsetParser(BluetoothSocket socket) {
    sock = socket;
  }

  public Integer[] next() {
    Integer[] data = new Integer[]{0,0,0,0,0,0,0,0};
    wasteSomeTime(1000);
    return data;
  }

  private BluetoothSocket sock;

  private void wasteSomeTime(int msec) {
    try {
      Thread.sleep(msec);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
