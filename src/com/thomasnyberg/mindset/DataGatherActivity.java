package com.thomasnyberg.mindset;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class DataGatherActivity extends Activity {
  TextView textView;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.data_gather);

    textView = (TextView) findViewById(R.id.main_window);
    textView.setText("Use this screen to gather data.");
  }
}
