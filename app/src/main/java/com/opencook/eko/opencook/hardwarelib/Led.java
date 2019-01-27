package com.opencook.eko.opencook.hardwarelib;

import android.util.Log;

import java.io.DataOutputStream;

public class Led {

    public  static String buildString(String path,int value){
        return "echo '"+ Integer.toString(value) + "' > "+path + "\n";

    }
    public static void setLeds(int[] leds){
        {

            try{
                Process su = Runtime.getRuntime().exec("su");
                DataOutputStream outputStream = new DataOutputStream(su.getOutputStream());
                outputStream.writeBytes(buildString("/sys/devices/platform/leds-mt65xx/leds/red/brightness", leds[0]));
                outputStream.writeBytes(buildString("/sys/devices/platform/leds-mt65xx/leds/green/brightness", leds[1]));
                outputStream.writeBytes(buildString("/sys/devices/platform/leds-mt65xx/leds/blue/brightness", leds[2]));
                outputStream.flush();
                outputStream.writeBytes("exit\n");
                outputStream.flush();
                su.waitFor();
            }catch (Exception e){

                e.printStackTrace();
            }
        }


    }
}
