package com.thomasnyberg.mindset;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import android.os.AsyncTask;
import java.lang.Thread;

import android.text.method.ScrollingMovementMethod;
import android.text.Layout;

public class Mindset extends Activity {
  TextView mainWindow;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    mainWindow = (TextView) findViewById(R.id.mainWindow);
    mainWindow.setMovementMethod(new ScrollingMovementMethod());
  }

  @Override
  protected void onStart() {
    super.onStart();

    new UpdateGUI().execute();
  }

  private class UpdateGUI extends AsyncTask<Void, Integer, Void> {
    protected void wasteSomeTime(int time) {
      try {
        Thread.sleep(time);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    protected Void doInBackground(Void... params) {
      int i = 0;
      for (;;) {
        wasteSomeTime(1000);
        publishProgress(++i);
      }
    }

    protected void onProgressUpdate(Integer... progress) {
      Layout layout = mainWindow.getLayout();

      int lineCount = mainWindow.getLineCount();
      int windowHeight = mainWindow.getHeight();
      int lineTop = layout.getLineTop(lineCount);

      int scrollAmount = layout.getLineTop(lineCount) - windowHeight;

      if (scrollAmount > 0) {
        mainWindow.scrollTo(0, scrollAmount);
      } else {
        mainWindow.scrollTo(0, 0);
      }

      CharSequence text = mainWindow.getText();
      text += "Line: " + Integer.toString(lineCount) + "\n";
      mainWindow.setText(text);
    }
  }
}
