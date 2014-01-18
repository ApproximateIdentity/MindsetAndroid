package com.thomasnyberg.mindset;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.bluetooth.BluetoothAdapter;

import android.os.AsyncTask;
import android.content.Intent;


public class Mindset extends Activity {
  Terminal term;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
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
    BluetoothAdapter myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    if (myBluetoothAdapter == null) {
      term.writeLine("Error: BluetoothAdapter");
    } else if (!myBluetoothAdapter.isEnabled()) {
      Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE); 
      int REQUEST_ENABLE_BT = 1;
      startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);

      new MindsetDataStream().execute();
    }

  }

  @Override
  protected void onStart() {
    super.onStart();
  }

  @Override
  protected void onResume() {
    super.onResume();
  }

  private class MindsetDataStream extends AsyncTask<Void, String, Void> {
    protected void onPreExecute() {}

    protected Void doInBackground(Void... params) {
      publishProgress("Attempting to connect to Mindstream.");

      BluetoothConn conn = new BluetoothConn("Mindset");
      conn.connect();
      if (conn.error) {
        publishProgress("Error: " + conn.errorString);
        conn.error = false;
      } else {
        publishProgress("Successfully connected!");

        publishProgress("Receiving data...");
        String data;
        for (;;) {
          data = conn.getData();
          if (conn.error) {
            publishProgress("Error: " + conn.errorString);
            conn.error = false;
          } else {
            publishProgress(data);
          }
        }
      }
      return null;
    }

    protected void onProgressUpdate(String... progress) {
      term.writeLine(progress[0]);
    }

    protected void onPostExecute(Void... result) {}
  }
}
