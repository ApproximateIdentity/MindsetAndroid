package com.thomasnyberg.mindset;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import android.content.Context;
import android.widget.Button;
import android.view.View;
import android.view.View.OnClickListener;
import android.content.Intent;

import java.lang.Thread;

public class DataGatherActivity extends Activity {
  private String concept1;
  private String concept2;
  private Terminal term;

  private Button predictButton;
  private Button toggleConceptButton, toggleSaveButton;

  private int toggleConcept = 1;
  private boolean toggleSave = false;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Bundle extras = getIntent().getExtras();
    concept1 = extras.getString("concept1");
    concept2 = extras.getString("concept2");

    setContentView(R.layout.data_gather);

    term = new Terminal((TextView) findViewById(R.id.main_window), 22);

    predictButton = (Button) findViewById(R.id.predict);

    toggleConceptButton = (Button) findViewById(R.id.toggle_concept);
    toggleConceptButton.setText(concept1);

    toggleSaveButton = (Button) findViewById(R.id.toggle_save);
    toggleSaveButton.setText("Save: Off");

    addListenerOnButtons();

    startPrintingShit();
  }

  private void startPrintingShit() {
    term.writeLine("Data 1");
  }

  private void wasteSomeTime(int msec) {
    try {
      Thread.sleep(msec);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private void addListenerOnButtons() {
    final Context context = this;

    predictButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View arg0) {
        Intent intent = new Intent(context, PredictActivity.class);
        startActivity(intent);
      }
    });

    toggleConceptButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View arg0) {
        if (toggleConcept == 1) {
          toggleConcept = 2;
          toggleConceptButton.setText(concept2);
        } else {
          toggleConcept = 1;
          toggleConceptButton.setText(concept1);
        }
      }
    });

    toggleSaveButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View arg0) {
        if (toggleSave) {
          toggleSave = false;
          toggleSaveButton.setText("Save: Off");
        } else {
          toggleSave = true;
          toggleSaveButton.setText("Save: On");
        }
      }
    });
  }
}
