package com.thomasnyberg.mindset;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import android.os.AsyncTask;
import java.lang.Thread;


public class Mindset extends Activity {
  Terminal term;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    term = new Terminal((TextView) findViewById(R.id.mainWindow));
  }

  @Override
  protected void onStart() {
    super.onStart();

    /*new UpdateGUI().execute();*/
  }

  protected void wasteSomeTime(int time) {
    try {
      Thread.sleep(time);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private class UpdateGUI extends AsyncTask<Void, Integer, Void> {
    protected Void doInBackground(Void... params) {
      int i = 0;
      for (;;) {
        wasteSomeTime(1000);
        publishProgress(++i);
      }
    }

    protected void onProgressUpdate(Integer... progress) {
      term.writeLine(progress[0]);
    }
  }
}
