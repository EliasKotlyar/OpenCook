package com.opencook.eko.opencook;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.serialport.SerialPort;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import com.opencook.eko.opencook.hardwarelib.CookingManager;
import com.opencook.eko.opencook.hardwarelib.HardwareCommand;
import com.opencook.eko.opencook.hardwarelib.Led;
import com.opencook.eko.opencook.hardwarelib.Status;
import com.skydoves.colorpickerpreference.ColorEnvelope;
import com.skydoves.colorpickerpreference.ColorListener;
import com.skydoves.colorpickerpreference.ColorPickerDialog;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    EditText mReception;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        mReception = (EditText)findViewById(R.id.ConsoleLog);


        Button startCooking = (Button) findViewById(R.id.startCooking);
        startCooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HardwareCommand command = new HardwareCommand();
                command.setCommand(HardwareCommand.COMMAND.MOTOR_ON);
                command.setMotorSpeed(1);
                command.setTemperatureLevel(0);
                CookingManager.getInstance().sendCommand(command);
            }
        });

        Button stopCooking = (Button) findViewById(R.id.stopCooking);
        stopCooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HardwareCommand command = new HardwareCommand();
                command.setCommand(HardwareCommand.COMMAND.MOTOR_OFF);
                command.setMotorSpeed(0);
                command.setTemperatureLevel(0);
                CookingManager.getInstance().sendCommand(command);
            }
        });


        Button turboButton = (Button) findViewById(R.id.turboButton);
        turboButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HardwareCommand command = new HardwareCommand();
                command.setCommand(HardwareCommand.COMMAND.MODE_ROTATEONCE);
                command.setMotorSpeed(0);
                command.setTemperatureLevel(0);
                CookingManager.getInstance().sendCommand(command);
            }
        });


        Button ledButton = (Button) findViewById(R.id.ledPicker);
        final Context context = this;
        ledButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ColorPickerDialog.Builder builder = new ColorPickerDialog.Builder(context, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
                builder.setTitle("ColorPicker Dialog");
                builder.setPreferenceName("MyColorPickerDialog");
                //builder.setFlagView(new CustomFlag(this, R.layout.layout_flag));
                builder.setPositiveButton("Confirm", new ColorListener() {
                    @Override
                    public void onColorSelected(ColorEnvelope colorEnvelope) {
                        Led.setLeds(colorEnvelope.getColorRGB());
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                /* REfactoring needed - slows down the GUI
                builder.getColorPickerView().setColorListener(new ColorListener() {
                    @Override
                    public void onColorSelected(ColorEnvelope colorEnvelope) {
                        Led.setLeds(colorEnvelope.getColorRGB());
                    }
                });
                */

                builder.show();
            }
        });



        CookingManager.getInstance().startSerial();

        Application mApplication = (Application) getApplication();

        UpdateThread thread = new UpdateThread();
        thread.start();







    }




    private class UpdateThread extends Thread {

        @Override
        public void run() {
            super.run();
            while (!isInterrupted()) {
                try {
                    Thread.sleep(1000);
                }catch (Exception e){

                }

                runOnUiThread(new Runnable() {
                    public void run() {
                        Status status = CookingManager.getInstance().getLastStatus();
                        if(status != null){
                            String text = status.toString();
                            mReception.setText(text);
                        }

                    }
                });
            }
        }

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
}
