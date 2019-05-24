package com.vintlabs.starfish;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {


    BluetoothSPP bt;
    boolean crlf = true;

    Spinner spin;
    int lightChannel;

    // temp string for spinner
    String spinList[] = { "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight"};

    FloatingActionButton fab;

    //Performing action onItemSelected and onNothing selected
    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position,long id) {
        //Toast.makeText(getApplicationContext(), Integer.toString(position), Toast.LENGTH_LONG).show();
        lightChannel = position;
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
// TODO Auto-generated method stub

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bt = new BluetoothSPP(getApplicationContext());

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //      .setAction("Action", null).show();
                btSelect();
            }
        });

        //Log.v("com.vintlabs.starfish", "b4 intent");


        if (!bt.isBluetoothAvailable()) {
            Toast.makeText(getApplicationContext()
                    , "Bluetooth is not available"
                    , Toast.LENGTH_SHORT).show();
            finish();
        }


        bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() {
            public void onDeviceConnected(String name, String address) {
                Toast.makeText(getApplicationContext()
                        , "Connected to " + name + "\n" + address
                        , Toast.LENGTH_SHORT).show();
                //bt.send("{\"ch\": 0, \"dc\": 2000}", true);
                fab.setBackgroundTintList(ColorStateList.valueOf(Color.BLUE));
            }

            public void onDeviceDisconnected() {
                Toast.makeText(getApplicationContext()
                        , "Connection lost", Toast.LENGTH_SHORT).show();
                fab.setBackgroundTintList(ColorStateList.valueOf(Color.RED));

                Intent intent = new Intent(getApplicationContext(), DeviceList.class);
                startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);

            }

            public void onDeviceConnectionFailed() {
                Toast.makeText(getApplicationContext()
                        , "Unable to connect", Toast.LENGTH_SHORT).show();
            }
        });

        bt.enable();
        bt.setupService();
        bt.startService(false);

        Intent intent = new Intent(getApplicationContext(), DeviceList.class);
        startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);

        Log.v("com.vintlabs.starfish", "efter intent");


        final Switch allSwitch = (Switch) findViewById(R.id.allSwitch);
        allSwitch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (bt.getServiceState() == BluetoothState.STATE_CONNECTED) {

                    int val = (allSwitch.isChecked()) ? 100 : 0;

                    //String s = "{\"ch\": -1,\"dc\":" + Integer.toString(val) + "}";
                    //bt.send(s, true);
                    btSetLight(-1, val);
                }
            }
        });

        final Switch singleSwitch = (Switch) findViewById(R.id.singleSwitch);
        singleSwitch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (bt.getServiceState() == BluetoothState.STATE_CONNECTED) {

                    int val = (singleSwitch.isChecked()) ? 100 : 0;

                    //String s = "{\"ch\": -1,\"dc\":" + Integer.toString(val) + "}";
                    //bt.send(s, true);
                    btSetLight(lightChannel, val);
                }
            }
        });

        SeekBar dimSeekbar = (SeekBar) findViewById(R.id.dimSeekbar);

        dimSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //Toast.makeText(getApplicationContext(), "seekbar progress: " + progress, Toast.LENGTH_SHORT).show();
                //int val = (int) ( (double) progress * 40.96 - 1.0);
                //String s = "{\"ch\": -1, \"dc\":" + Integer.toString(val) + "}";
                //bt.send(s, true);
                btSetLight( lightChannel, progress);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //
            }
        });

        SeekBar allDimSeekbar = (SeekBar) findViewById(R.id.allDimSeekbar);

        allDimSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //Toast.makeText(getApplicationContext(), "seekbar progress: " + progress, Toast.LENGTH_SHORT).show();
                //int val = (int) ( (double) progress * 40.96 - 1.0);
                //String s = "{\"ch\": -1, \"dc\":" + Integer.toString(val) + "}";
                //bt.send(s, true);
                btSetLight( -1, progress);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //
            }
        });

        Spinner spin = (Spinner) findViewById(R.id.spinner);
        spin.setOnItemSelectedListener(this);

//Creating the ArrayAdapter instance having the bank name list
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,spinList);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//Setting the ArrayAdapter data on the Spinner
        spin.setAdapter(aa);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v("com.vintlabs.starfish", "In onActivityResult");
        Log.v( "com.vintlabs.starfish", "requestCode = " + requestCode);
        Log.v("com.vintlabs.starfish","data.len: " + data.toString());


        if ( data != null) {
            //Toast.makeText(getApplicationContext(),"In onActivityResult", Toast.LENGTH_SHORT).show();
            if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
                if (resultCode == Activity.RESULT_OK)
                {
                    Log.v("com.vintlabs.starfish", "connect device");
                    bt.connect(data);
                }
            } else if (requestCode == BluetoothState.REQUEST_ENABLE_BT) {
                if (resultCode == Activity.RESULT_OK) {
                    bt.setupService();
                } else {
                    Toast.makeText(getApplicationContext()
                            , "Bluetooth was not enabled."
                            , Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }

    public void btSelect()
    {

        Intent intent = new Intent(getApplicationContext(), DeviceList.class);
        startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
    }


    void btSetLight(int ch, int val)
    {
        val = (int) ( (double)val * 0.99 * 40.96);  // TODO: This might be better handled on ESP32 - maybe use 75% for 0-100 values
        String s = "{\"ch\": " + Integer.toString(ch) + ", \"dc\":" + Integer.toString(val) + "}";
        bt.send(s, crlf);
    }
}
