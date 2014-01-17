package com.thomasnyberg.mindset;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class Mindset extends Activity {
  Terminal term;

  private class Terminal {
    private TextView window;

    /* Hardcoded value is bad, but works for my phone. */
    private final int maxLines = 30;

    private int nextLine;
    private String[] buffer = new String[maxLines];

    Terminal(TextView win) {
      window = win;
      initBuffer();
    }

    private void initBuffer() {
      nextLine = 0;

      for (int i = 0; i < maxLines; i++) {
        buffer[i] = "\n";
      }
      writeBuffer();
    }

    public void writeLine(String text) {
      if (nextLine < maxLines) {
        buffer[nextLine] = text;
        nextLine++;
      } else {
        bufferShift();
        buffer[maxLines - 1] = text;
      }
      
      writeBuffer();
    }

    private void bufferShift() {
      for (int i = 0; i + 1 < maxLines; i++) {
        buffer[i] = buffer[i + 1];
      }
      buffer[maxLines - 1] = "\n";
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
    for (int i = 0; i < 31; i++) {
      term.writeLine("Line " + Integer.toString(i + 1));
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
  }
}
