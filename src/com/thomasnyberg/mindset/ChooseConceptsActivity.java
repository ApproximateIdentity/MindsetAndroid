package com.thomasnyberg.mindset;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import android.content.Context;
import android.widget.Button;
import android.view.View;
import android.view.View.OnClickListener;
import android.content.Intent;

public class ChooseConceptsActivity extends Activity {
  TextView textView;
  Button button;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.choose_concepts);
    
    textView = (TextView) findViewById(R.id.main_window);
    textView.setText("Use this screen to set the concepts.");

    addListenerOnButton();
  }

  private void addListenerOnButton() {
    final Context context = this;

    button = (Button) findViewById(R.id.button);

    button.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View arg0) {
        Intent intent = new Intent(context, DataGatherActivity.class);
        startActivity(intent);
      }
    });
  }
}
