package com.thomasnyberg.mindset;

import android.widget.TextView;

public class Terminal {
  Terminal(TextView win, int lines) {
    window = win;
    maxLines = lines;
    buffer = new String[maxLines];
    initBuffer();
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

  private TextView window;
  /* Hardcoded value is bad, but works for my phone. */
  private int maxLines;
  private int nextLine;
  private String[] buffer;

  private void initBuffer() {
    nextLine = 0;

    for (int i = 0; i < maxLines; i++) {
      buffer[i] = "";
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
