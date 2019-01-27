package com.opencook.eko.opencook.hardwarelib;

import android.serialport.SerialPort;
import android.util.Log;

import com.opencook.eko.opencook.Util;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class CookingManager {

    private Status currentStatus;
    private HardwareCommand sendCommand;

    private static volatile CookingManager sSoleInstance = new CookingManager();

    public static CookingManager getInstance() {
        return sSoleInstance;
    }

    public void setStatus(Status status){
        this.currentStatus = status;
    }
    public Status getLastStatus() {
        return currentStatus;
    }

    private static final String TAG = "CookingManager";

    private SerialPort mSerialPort = null;
    private InputStream mInputStream;


    public void disableEnforce(){
        try{
            Process su = Runtime.getRuntime().exec("su");
            DataOutputStream outputStream = new DataOutputStream(su.getOutputStream());
            outputStream.writeBytes("setenforce 0\n");
            outputStream.flush();
            outputStream.writeBytes("exit\n");
            outputStream.flush();
            su.waitFor();
        }catch (Exception e){
            Log.e(TAG, "Received an exception", e);
            e.printStackTrace();
        }

    }
    public void startSerial() {
        this.disableEnforce();
        SerialPort.setSuPath("su");


        /* Open the serial port */
        try {
            mSerialPort = new SerialPort("/dev/ttyMT0", 4800, 0);
            mInputStream = mSerialPort.getInputStream();

            HardwareCommand command = new HardwareCommand();
            command.setCommand(HardwareCommand.COMMAND.MOTOR_OFF);
            command.setMotorSpeed(0);
            command.setTemperatureLevel(0);
            CookingManager.getInstance().sendCommand(command);

            /* Create a receiving thread */
            ReadThread mReadThread = new ReadThread();
            mReadThread.start();

            /* Create a sending thread */
            WriteThread mWriteThread = new WriteThread();
            mWriteThread.start();

        } catch (Exception e) {
            Log.e(TAG, "Received an exception", e);
            e.printStackTrace();
        }





    }

    public HardwareCommand getSendCommand() {
        return sendCommand;
    }

    private class ReadThread extends Thread {

        @Override
        public void run() {
            super.run();
            while (!isInterrupted()) {
                int size;
                try {
                    byte[] buffer = new byte[64];
                    if (mInputStream == null) return;
                    size = mInputStream.read(buffer);
                    if(size == 27){
                        Status status = new Status();
                        if(status.tryRead(buffer)){
                            CookingManager.getInstance().setStatus(status);
                        }
                    }

                } catch (IOException e) {
                    Log.e(TAG, "Received an exception", e);
                    e.printStackTrace();
                    return;
                }
            }
        }

    }

    private class WriteThread extends Thread {

        @Override
        public void run() {
            super.run();

            try {
                Thread.sleep(500);
            }catch (Exception e){

            }

            while (!isInterrupted()) {

                try {
                    OutputStream output = mSerialPort.getOutputStream();

                    HardwareCommand command = CookingManager.getInstance().getSendCommand();
                    output.write(command.toHex());
                    output.flush();
                } catch (Exception e) {
                    Log.e(TAG, "Received an exception", e);
                    e.printStackTrace();
                }
            }
        }

    }



    public void sendCommand(HardwareCommand command) {
        this.sendCommand = command;
    }
}
