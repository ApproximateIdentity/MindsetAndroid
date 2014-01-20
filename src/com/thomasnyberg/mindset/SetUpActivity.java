package com.thomasnyberg.mindset;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class SetUpActivity extends Activity {
  TextView textView;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.set_up);

    textView = (TextView) findViewById(R.id.set_up);
    textView.setText("Use this screen to do a one-time setup.");
  }
}
