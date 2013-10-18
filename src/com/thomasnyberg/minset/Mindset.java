package com.thomasnyberg.minset;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import android.os.Handler;
import java.lang.Runnable;
import java.lang.Thread;
import android.util.Log;
import android.os.Message;

public class Mindset extends Activity
{
    TextView mainWindow;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle b = msg.getData();
            String key = b.getString("My Key");
            mainWindow.setText(mainWindow.getText() + key + System.getProperty("line.separator"));
        }
    };

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mainWindow = (TextView) findViewById(R.id.mainWindow);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Thread background = new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i = 0; i < 10; i++) {
                    try {
                        Thread.sleep(1000);
                        Message msg = new Message();
                        Bundle b = new Bundle();
                        b.putString("My Key", "My Value: " + String.valueOf(i));
                        msg.setData(b);
                        handler.sendMessage(msg);
                    } catch (Exception e) {
                        Log.v("Error", e.toString());
                    }
                }
            }
        });
        
        background.start();
    }
}
