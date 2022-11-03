package com.malcher.usbhostmaster.utils;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class UsbManagerUtilities {

    private static String usbDeviceList = null;
    private static String realDeviceName = null;

    public static boolean checkIfExistDeviceConnected(Context context, UsbManager usbManager) {

        boolean existOrNot = false;
        try {
            UsbDevice device;
            HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
            Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();

            while(deviceIterator.hasNext()) {
                device = deviceIterator.next();
                usbDeviceList = device.getDeviceName();
            }

            if(usbDeviceList != null) {
                existOrNot = true;
            }
        }catch (Exception e) {
            Toast.makeText(context, "ERROR: not possible get UsbDevice", Toast.LENGTH_SHORT).show();
        }

        if(!existOrNot) {
            Toast.makeText(context, "Device not Connected, check cable or device and try again...", Toast.LENGTH_SHORT).show();
        }

        return existOrNot;
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
}
