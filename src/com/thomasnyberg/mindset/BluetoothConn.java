package com.thomasnyberg.mindset;

import java.lang.Thread;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import java.util.UUID;
import android.bluetooth.BluetoothSocket;
import java.io.InputStream;

import java.io.IOException;

public class BluetoothConn {
  BluetoothConn() {
    myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    /* Make sure connection blocks until Bluetooth is enabled on the phone. */
    while (!myBluetoothAdapter.isEnabled()) {
      wasteSomeTime(500);
    }
  }

  public boolean error = false;
  public String errorString = "";

  public void connect() {
    /* The following is my Mindset's address. */
    final String mindsetAddress = "00:13:EF:00:3B:F6";

    try {
      mindset = myBluetoothAdapter.getRemoteDevice(mindsetAddress);
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
      error = true;
      errorString = "Could not get remote device.";
      return;
    }

    /* The following string is the UUID for the serial port profile. */
    final String uuidString = "00001101-0000-1000-8000-00805F9B34FB";
    UUID uuid = UUID.fromString(uuidString);

    try {
      btSock = mindset.createRfcommSocketToServiceRecord(uuid);
    } catch (IOException e) {
      error = true;
      errorString = "Could not create RFComm socket.";
      return;
    }

    try {
      btSock.connect();
    } catch (IOException e) {
      error = true;
      errorString = "Could not connect to RFComm socket.";
      return;
    }

    try {
      dataStream = btSock.getInputStream();
    } catch (IOException e) {
      error = true;
      errorString = "Could net get data stream.";
      return;
    }
  }

  public String getData() {
    wasteSomeTime(500);
    int datum = 0;
    try {
      datum = dataStream.read();
    } catch (IOException e) {
      error = true;
      errorString = "Could not read byte.";
      return "error";
    }
    return "data" + Integer.toString(datum);
  }

  private InputStream dataStream = null;
  private BluetoothAdapter myBluetoothAdapter = null;
  private BluetoothDevice mindset = null;
  private BluetoothSocket btSock = null;

  private void wasteSomeTime(int msec) {
    try {
      Thread.sleep(msec);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
