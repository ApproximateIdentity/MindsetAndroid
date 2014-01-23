package com.thomasnyberg.mindset;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.EditText;

import android.content.Context;
import android.widget.Button;
import android.view.View;
import android.view.View.OnClickListener;
import android.content.Intent;

public class ChooseConceptsActivity extends Activity {
  private Terminal term = null;
  private EditText concept1view, concept2view;
  private Button goToDataGatherButton;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.choose_concepts);
    
    term = new Terminal((TextView) findViewById(R.id.main_window), 3);
    String text = "Welcome!\n\n";
    text += "Choose two concepts below to begin using the program.";
    term.writeLine(text);

    concept1view = (EditText) findViewById(R.id.concept1);
    concept2view = (EditText) findViewById(R.id.concept2);

    addListenerOnButton();
  }

  private void addListenerOnButton() {
    final Context context = this;

    goToDataGatherButton = (Button) findViewById(R.id.button);

    goToDataGatherButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View arg0) {
        String concept1 = concept1view.getText().toString();
        String concept2 = concept2view.getText().toString();

        Intent intent = new Intent(context, DataGatherActivity.class);
        intent = intent.putExtra("concept1", concept1);
        intent = intent.putExtra("concept2", concept2);
        startActivity(intent);
      }
    });
  }
}
