package com.thomasnyberg.mindset;

import android.widget.TextView;
import android.text.method.ScrollingMovementMethod;
import android.text.Layout;

public class Terminal {
  private TextView window;
  private Layout layout;
  private int lineCount;
  private int height;
  private int lineTop;

  Terminal(TextView win) {
    window = win;
    window.setMovementMethod(new ScrollingMovementMethod());

    layout = window.getLayout();
    height = layout.getHeight();
  }

  public void writeLine(int progress) {
    lineCount = window.getLineCount();
    lineTop = layout.getLineTop(lineCount);

    int scrollAmount = layout.getLineTop(lineCount) - height;

    if (scrollAmount > 0) {
      window.scrollTo(0, scrollAmount);
    } else {
      window.scrollTo(0, 0);
    }

    CharSequence text = window.getText();
    text += "Line: " + Integer.toString(lineCount) + "\n";
    window.setText(text);
  }
}
