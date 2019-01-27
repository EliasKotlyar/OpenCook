package com.opencook.eko.opencook.hardwarelib;

import android.util.Log;

import com.opencook.eko.opencook.Util;

public class HardwareCommand {
    private static final String TAG = "HardwareCommand";
    public enum COMMAND {

        MOTOR_OFF("00"),
        MOTOR_ON("01"),
        HEATER_ON("02"),
        GAREN_AN("03"),
        MODE_UNKNOWN1("04"),
        MODE_UNKNOWN2("05"),
        MODE_GOTOSLEEP("0F"),
        MODE_ROTATEONCE("C6");

        private final String hexCode;   // in kilograms

        COMMAND(String hexCode) {
            this.hexCode = hexCode;
        }

        private String hexCode() {
            return hexCode;
        }
    }

    protected COMMAND command;

    public void setCommand(COMMAND command) {
        this.command = command;
    }

    public void setMotorSpeed(int motorSpeed) {
        this.motorSpeed = motorSpeed;
    }

    public void setTemperatureLevel(int temperatureLevel) {
        this.temperatureLevel = temperatureLevel;
    }

    protected int motorSpeed;
    protected int temperatureLevel;

    public int calculateChecksum(String hexString){
        byte[] paramArrayOfByte = Util.hexStr2bytes(hexString);
        int length = paramArrayOfByte.length;
        int i = 0;
        int checksum = 0;
        while (i < length)
        {
            checksum = (short)(checksum + paramArrayOfByte[i]);
            i++;
        }
        int checksum2 = (byte)checksum;
        if(checksum2 < 0){
            checksum2 += 256;
        }
        return checksum2;
    }


    public byte[] toHex(){
        StringBuffer buffer = new StringBuffer();
        buffer.append("55");
        buffer.append("0F");
        buffer.append("A1");
        buffer.append(this.command.hexCode());
        buffer.append("00");
        buffer.append(Util.decimal2fitHex(this.motorSpeed));
        buffer.append(Util.decimal2fitHex(this.temperatureLevel));
        buffer.append("00");
        buffer.append("00");
        buffer.append("00");
        buffer.append("00");
        // Motor Drehrichtung:
        buffer.append("00");
        // Waage Kalibrierung:
        buffer.append("00");
        int checkSum = this.calculateChecksum(buffer.toString());
        buffer.append(Util.decimal2fitHex(checkSum));
        buffer.append("AA");

        String hex = buffer.toString();
        Log.i(TAG, hex);
        return Util.hexStr2bytes(hex);

    }
}
