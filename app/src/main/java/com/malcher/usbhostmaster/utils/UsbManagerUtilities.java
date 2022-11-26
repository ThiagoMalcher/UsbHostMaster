package com.malcher.usbhostmaster.utils;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;
import static androidx.core.app.ActivityCompat.startActivityForResult;
import static androidx.core.content.ContextCompat.startActivity;
import static com.google.android.material.internal.ContextUtils.getActivity;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.mtp.MtpDevice;
import android.net.Uri;
import android.os.Environment;
import android.os.Message;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.provider.MediaStore;
import android.provider.SyncStateContract;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class UsbManagerUtilities {

    public static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    private static String usbDeviceList = null;
    private static String realDeviceName = null;
    private static UsbDeviceConnection mUsbDeviceConnection;
    private byte[] readBytes = new byte[64];
    private static UsbManager mUsbManager;
    private static UsbDevice mUsbDevice;
    private static  StorageManager mStorageManager;
    private static Context mContext;
    private static PendingIntent permissionIntent;
    private static UsbInterface intf = null;
    private static UsbEndpoint mEndpointOut, mEndpointIn;

    public static void UsbManagerUtilities(Context context) {
        mContext = context;
        try {
            mUsbManager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
            mUsbDevice = mUsbManager.getDeviceList().values().iterator().next();
            mStorageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);

        }catch (Exception e) { }
    }

    public static boolean checkIfExistDeviceConnected() {

        try {
            HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
            if(deviceList.size() == 0) {
                Toast.makeText(mContext, "Device not Connected, check cable or device and try again...", Toast.LENGTH_SHORT).show();
                return false;
            }

            return true;
        }catch (Exception e) {
            Toast.makeText(mContext, "ERROR: not possible get UsbDevice", Toast.LENGTH_SHORT).show();
        }

        return false;
    }

    public static String getDeviceName() {

        UsbDevice usbDevice;
        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
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

    public static void deviceConnect() {
        String getFilePath = getFilePath();
        for(String k : mUsbManager.getDeviceList().keySet()) {
            mUsbDevice = mUsbManager.getDeviceList().get(k);
            if(mUsbManager.hasPermission(mUsbDevice)){
                mUsbDeviceConnection = mUsbManager.openDevice(mUsbDevice);
                if(mUsbDeviceConnection == null){
                    Toast.makeText(mContext, "ERROR: not possible connect UsbDevice", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(mContext, "Device connected with success, preparing to send file", Toast.LENGTH_SHORT).show();
                }
            }
        }

        intf = findAdbInterface(mUsbDevice);
        UsbEndpoint epOut = null;
        UsbEndpoint epIn = null;
        // look for our bulk endpoints
        for (int i = 0; i < intf.getEndpointCount(); i++) {
            UsbEndpoint ep = intf.getEndpoint(i);
            if (ep.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK) {
                if (ep.getDirection() == UsbConstants.USB_DIR_OUT) {
                    epOut = ep;
                } else {
                    epIn = ep;
                }
            }
        }
        if (epOut == null || epIn == null) {
            throw new IllegalArgumentException("not all endpoints found");
        }

        mEndpointOut = epOut;
        mEndpointIn = epIn;

        int offset = 0;
        int transferred = 0;

        try{
            File file = new File(getFilePath());
            FileInputStream fileInputStream = new FileInputStream(new File(getFilePath));
            BufferedReader buffer = new BufferedReader(new InputStreamReader(
                    fileInputStream),8*1024);
            int size = (int) file.length();
            byte[] tmp = new byte[size];
            System.arraycopy(buffer, 0, tmp, 0, size);

            while ((transferred = mUsbDeviceConnection.bulkTransfer(mEndpointOut, tmp, size - offset, 1000)) >= 0) {
                offset += transferred;
                if (offset >= size) {
                    break;
                } else {
                    System.arraycopy(buffer, offset, tmp, 0, size - offset);
                }
            }
            if (transferred < 0) {
                throw new IOException("bulk transfer fail");
            }

        }catch (Exception e){}
    }

    public static String getFilePath() {
        String filePath = Environment.DIRECTORY_DOCUMENTS.toString() + "fw.zip";
        File file =  new File(filePath);
        if(file.exists()){
            return filePath;
        }

        return null;
    }


    // searches for an adb interface on the given USB device
    private static UsbInterface findAdbInterface(UsbDevice device) {
        int count = device.getInterfaceCount();
        for (int i = 0; i < count; i++) {
            UsbInterface intf = device.getInterface(i);
            if (intf.getInterfaceClass() == 255 && intf.getInterfaceSubclass() == 66 &&
                    intf.getInterfaceProtocol() == 1) {
                return intf;
            }
        }
        return null;
    }

}
