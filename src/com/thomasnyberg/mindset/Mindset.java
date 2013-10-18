package com.thomasnyberg.mindset;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import android.os.Handler;
import java.lang.Runnable;
import java.lang.Thread;
import android.util.Log;
import android.os.Message;
import android.content.Intent;

import android.bluetooth.BluetoothAdapter;

public class Mindset extends Activity
{
    TextView mainWindow;
    BluetoothAdapter myBluetoothAdapter;

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

        Message msg = new Message();
        Bundle b = new Bundle();
        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (myBluetoothAdapter == null) {
            b.putString("My Key", "Bluetooth Borked");
            msg.setData(b);
        } else {
            b.putString("My Key", "Bluetooth Success!");
            msg.setData(b);
        }
        handler.sendMessage(msg);

        /* All these handlers are not happy...possibly too many happending too
         * quickly? */
        if (!myBluetoothAdapter.isEnabled()) {
            b.putString("My Key", "Bluetooth Turned Off");
            msg.setData(b);
            /*handler.sendMessage(msg);*/
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            int REQUEST_ENABLE_BT = 1;
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);

            if (myBluetoothAdapter.isEnabled()) {
                b.putString("My Key", "Bluetooth Now Turned On");
            } else {
                b.putString("My Key", "Fucker Said No");
            }
            msg.setData(b);
            /*handler.sendMessage(msg);*/
        } else {
            b.putString("My Key", "Bluetooth Already Turned On");
            msg.setData(b);
            /*handler.sendMessage(msg);*/
        }



        /* The following gives an error and I don't really understand why:
         
        Thread bluetooth = new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = new Message();
                Bundle b = new Bundle();
                myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (myBluetoothAdapter == null) {
                    b.putString("My Key", "Bluetooth Borked");
                    msg.setData(b);
                } else {
                    b.putString("My Key", "Bluetooth Success!");
                    msg.setData(b);
                }
                handler.sendMessage(msg);
            }
        });
        
        */

        background.start();

        /*bluetooth.start();*/
    }
}
