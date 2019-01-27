package com.opencook.eko.opencook.hardwarelib;

import android.util.Log;

import com.opencook.eko.opencook.Util;

public class Status {
    private static final String TAG = "CookingManager";
    protected int rpm = 0;
    protected int weight = 0;
    protected boolean coverOpen = false;
    protected String error = "";
    protected int firmwareVersion = 0;
    protected int temperatur;


    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("RPM:"+Integer.toString(rpm)+"\r\n");
        stringBuilder.append("Gewicht:"+Integer.toString(weight)+"\r\n");
        stringBuilder.append("Temperatur:"+Integer.toString(temperatur)+"\r\n");
        stringBuilder.append("Deckel ist auf: :"+Boolean.toString(coverOpen)+"\r\n");
        stringBuilder.append("Error:"+error+"\r\n");
        stringBuilder.append("Firmware Version:"+Integer.toString(firmwareVersion)+"\r\n");
        return stringBuilder.toString();
    }

    public boolean tryRead(byte[] buffer){
        String hex = Util.bytes2HexStr(buffer);
        boolean retVal = false;
        if(hex.substring(0,6).equals("551BB1")){
            Log.i(TAG,hex);
            this.readFromHex(buffer);
            retVal = true;
        }
        return retVal;
    }

    public void readFromHex(byte[] c) {
        int k;
        // RPM:
        k = c[9];
        this.rpm = c[10] & 0xFF | (k & 0xFF) << 8;
        // Weight:
        k = c[19];
        int m = c[18];
        int n = c[17];
        this.weight = (c[16] & 0xFF) << 24 | k & 0xFF | (m & 0xFF) << 8 | (n & 0xFF) << 16;
        // Cover Open*:
        if ((c[3] & 0x80) != 128) {
            this.coverOpen = false;
        } else {
            this.coverOpen = true;
        }
        // Firmware Version:

        k = c[20];
        this.firmwareVersion =  c[21] & 0xFF | (k & 0xFF) << 8;
        // Temperatur
        k = c[14];
        this.temperatur =  c[15] & 0xFF | (k & 0xFF) << 8;


        if ((c[3] & 0x10) != 16) {
            // No Error
            this.error = "";
        } else {
            switch (c[5]) {
                default:
                    this.error = "Unklar";
                    break;
                case 8:
                    this.error = "Maximale Laufzeit erreicht!";
                    break;
                case 7:
                    this.error = "Motor überhitzt!";
                    break;
                case 6:
                    this.error = "Kein Wasser mehr!";
                    break;
                case 5:
                    this.error = "Motorfehler!";
                    break;
                case 4:
                    this.error = "Überspannung!";
                    break;
                case 3:
                    this.error = "Unterspannung!";
                    break;
            }
        }
    }
}
