package com.thomasnyberg.mindset;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class Mindset extends Activity {
  TextView mainWindow;

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

    for (int i = 0; i < 5; i++) {
      CharSequence text = mainWindow.getText();
      text += "\nShit ";
      text += Integer.toString(i);
      mainWindow.setText(text);
    }
  }
}
