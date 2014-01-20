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
  private TextView textView;
  private EditText concept1view, concept2view;
  private Button button;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.choose_concepts);
    
    textView = (TextView) findViewById(R.id.main_window);
    textView.setText("Choose two concepts.");

    concept1view = (EditText) findViewById(R.id.concept1);
    concept2view = (EditText) findViewById(R.id.concept2);

    addListenerOnButton();
  }

  private void addListenerOnButton() {
    final Context context = this;

    button = (Button) findViewById(R.id.button);

    button.setOnClickListener(new OnClickListener() {
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
