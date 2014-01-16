package com.thomasnyberg.mindset;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import android.os.AsyncTask;

public class Mindset extends Activity {
  TextView mainWindow;

  Handler handler = new Handler() {
    @Override
    public void handleMessage(Message msg) {
      Bundle b = msg.getData();
      String key = b.getString("My Key");
      String linSep = System.getProperty("line.separator");
      mainWindow.setText(mainWindow.getText() + "Test" + linSep);
    }
  };

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    mainWindow = (TextView) findViewById(R.id.mainWindow);
  }

  @Override
  protected void onStart() {
    super.onStart();
  }
}
