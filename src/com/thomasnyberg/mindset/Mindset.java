package com.thomasnyberg.mindset;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class Mindset extends Activity {
  Terminal term;

  private class Terminal {
    private TextView window;
    private int maxLines = 30; /*Hardcoded value is bad. Works for my phone.*/
    private String[] buffer = new String[maxLines];

    Terminal(TextView win) {
      window = win;
    }

    public void fillLines() {
      for (int i = 0; i < maxLines; i++) {
        buffer[i] = "Line " + Integer.toString(i + 1);
      }
      writeBuffer();
    }

    private void writeBuffer() {
      String text = "";
      for (int i = 0; i < maxLines; i++) {
        text += buffer[i] + "\n";
      }
      window.setText(text);
    }
  }
    

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    term = new Terminal((TextView) findViewById(R.id.mainWindow));
  }

  @Override
  protected void onStart() {
    super.onStart();
    term.fillLines();
  }

  @Override
  protected void onResume() {
    super.onResume();
  }
}
