package com.malcher.usbhostmaster.utils;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class UsbManagerUtilities {

    static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    private static String usbDeviceList = null;
    private static String realDeviceName = null;
    private UsbDeviceConnection usbDeviceConnection;
    private byte[] readBytes = new byte[64];
    PendingIntent permissionIntent;


    public static boolean checkIfExistDeviceConnected(Context context, UsbManager usbManager) {

        try {
            HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
            if(deviceList.size() == 0) {
                Toast.makeText(context, "Device not Connected, check cable or device and try again...", Toast.LENGTH_SHORT).show();
                return false;
            }

            return true;
        }catch (Exception e) {
            Toast.makeText(context, "ERROR: not possible get UsbDevice", Toast.LENGTH_SHORT).show();
        }

        return false;
    }

    public static String getDeviceName(UsbManager usbManager) {

        UsbDevice usbDevice;
        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();

        while(deviceIterator.hasNext()) {
            usbDevice = deviceIterator.next();
            realDeviceName = usbDevice.getProductName();
        }
        if(realDeviceName == null) {
            realDeviceName = "NOT RETURN NAME";
        }

        return realDeviceName;
    }

    public void usbConnection(Context context, UsbManager usbManager) {
        if(checkIfExistDeviceConnected(context, usbManager)) {

        }
    }

    public static void checkPermission(Context context) {
        PendingIntent.getBroadcast(context, 0, new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter intentFilter = new IntentFilter(ACTION_USB_PERMISSION);
       // context.registerReceiver(usbReceiver, intentFilter);
    }

}
