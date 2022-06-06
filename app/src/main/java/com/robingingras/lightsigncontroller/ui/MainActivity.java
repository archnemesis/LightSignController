package com.robingingras.lightsigncontroller.ui;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.robingingras.lightsigncontroller.ble.RgbLedDeviceManager;
import com.robingingras.lightsigncontroller.databinding.ActivityMainBinding;
import com.robingingras.lightsigncontroller.ui.device.AddDeviceActivity;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    private ActivityMainBinding mBinding;
    private RgbLedDeviceManager mManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mManager = new RgbLedDeviceManager(this);

        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mBinding.fabAddDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddDeviceActivity.class);
                mAddDeviceActivityResultLauncher.launch(intent);
            }
        });
    }

    /**
     * This launcher receives the selected device result.
     */
    private final ActivityResultLauncher<Intent> mAddDeviceActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.i(TAG, "Got result!");
                    if (result.getResultCode() == RESULT_OK) {
                        BluetoothDevice device = result.getData().getParcelableExtra(AddDeviceActivity.EXTRA_DATA_DEVICE);
                        if (device != null) {
                            Log.i(TAG, String.format("Got a device: %s", device.getAddress()));
                        }
                    }
                }
            }
    );
}