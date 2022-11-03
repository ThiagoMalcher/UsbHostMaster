package com.malcher.usbhostmaster;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.malcher.usbhostmaster.utils.UsbManagerUtilities;


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

        mBtnCheckDevice = findViewById(R.id.btnCheckDevice);
        mBtnCheckDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(UsbManagerUtilities.checkIfExistDeviceConnected(getApplicationContext(), mUsbManager)) {
                    txtWaitDevice.setText("Device Connected");
                    txtNameDevice.setText(UsbManagerUtilities.getDeviceName(mUsbManager));
                }
            }
        });
    }

}


