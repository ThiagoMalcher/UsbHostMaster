package com.malcher.usbhostmaster;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.malcher.usbhostmaster.utils.UsbManagerUtilities;

import java.io.File;
import java.lang.reflect.InvocationTargetException;


public class MainActivity extends AppCompatActivity {

    private UsbManager mUsbManager;
    private TextView txtWaitDevice;
    private TextView txtNameDevice;
    private Button mBtnCheckDevice;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtWaitDevice = findViewById(R.id.txtDeviceIsConnected);
        txtNameDevice = findViewById(R.id.txtDeviceName);
        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);

        locationPermissionRequest.launch(new String[] {
                Manifest.permission.MANAGE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        });



        if(Build.VERSION.SDK_INT >= 30) {
            if(!Environment.isExternalStorageManager())
            {
                Uri uri = Uri.parse("package:" + getApplicationContext().getPackageName());

                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(uri);

                if (intent == null ) {
                    intent =  new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(uri);
                }
                startActivity(intent);
            }
        }

        UsbManagerUtilities.UsbManagerUtilities(getApplicationContext());
        if(UsbManagerUtilities.checkIfExistDeviceConnected()) {
            txtWaitDevice.setText("Device Connected");
            txtNameDevice.setText(UsbManagerUtilities.getDeviceName());
          //  UsbManagerUtilities.MTPDevice();
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getAbsolutePath());
            intent.setDataAndType(uri, "*/*");
            //startActivity(intent);
            startActivityForResult(Intent.createChooser(intent, "Escolha o fw"),1001);
            UsbManagerUtilities.deviceConnect();

        }

    }

    public void onActivityResult(int requestCode, int resultCode,
                                             Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        // super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1001) {
            Uri currFileURI = data.getData();
            File file = new File(currFileURI.getPath());
            final String[] split = file.getPath().split(":");
            String filePath = split[1];
            String path = currFileURI.getPath().toString();
            String usepath =  path;
        }
    }

    ActivityResultLauncher<String[]> locationPermissionRequest =
            registerForActivityResult(new ActivityResultContracts
                            .RequestMultiplePermissions(), result -> {
                        Boolean fineLocationGranted = result.getOrDefault(
                                Manifest.permission.MANAGE_EXTERNAL_STORAGE, false);
                        Boolean coarseLocationGranted = result.getOrDefault(
                                Manifest.permission.READ_EXTERNAL_STORAGE,false);
                        if (fineLocationGranted != null && fineLocationGranted) {
                            // Precise location access granted.
                        } else if (coarseLocationGranted != null && coarseLocationGranted) {
                            // Only approximate location access granted.
                        } else {
                            // No location access granted.
                        }
                    }
            );


}


