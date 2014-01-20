package com.thomasnyberg.mindset;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class PredictActivity extends Activity {
  TextView textView;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.predict);

    textView = (TextView) findViewById(R.id.predict);
    textView.setText("Use this screen to make predictions.");
  }
}
