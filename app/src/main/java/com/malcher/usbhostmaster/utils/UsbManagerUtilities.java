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
    private static UsbEndpoint input, output;

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
        for(String k : mUsbManager.getDeviceList().keySet()){
            UsbDevice device = mUsbManager.getDeviceList().get(k);
            if(mUsbManager.hasPermission(device)){
                mUsbDeviceConnection = mUsbManager.openDevice(device);
                if(mUsbDeviceConnection == null){
                    Toast.makeText(mContext, "ERROR: not possible connect UsbDevice", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(mContext, "Device connected with success, preparing to send file", Toast.LENGTH_SHORT).show();
                }
            }
        }


    }

}
