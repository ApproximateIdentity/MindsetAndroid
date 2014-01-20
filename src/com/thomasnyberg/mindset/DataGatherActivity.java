package com.thomasnyberg.mindset;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import android.content.Context;
import android.widget.Button;
import android.view.View;
import android.view.View.OnClickListener;
import android.content.Intent;

public class DataGatherActivity extends Activity {
  private String concept1;
  private String concept2;
  private Terminal term;
  private Button toggleConcept, toggleSave;
  private Button predictButton;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Bundle extras = getIntent().getExtras();
    concept1 = extras.getString("concept1");
    concept2 = extras.getString("concept2");

    setContentView(R.layout.data_gather);

    term = new Terminal((TextView) findViewById(R.id.main_window), 10);
    term.writeLine("Choose two concepts.");

    String text = "Concept 1: " + concept1;
    term.writeLine(text);
    text = "Concept 2: " + concept2;
    term.writeLine(text);

    addListenerOnButton();
  }

  private void addListenerOnButton() {
    final Context context = this;

    predictButton = (Button) findViewById(R.id.predict);

    predictButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View arg0) {
        Intent intent = new Intent(context, PredictActivity.class);
        startActivity(intent);
      }
    });
  }
}
