package com.robingingras.lightsigncontroller.ui.device;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.robingingras.lightsigncontroller.application.RgbLedApplication;
import com.robingingras.lightsigncontroller.ble.RgbLedDeviceManager;
import com.robingingras.lightsigncontroller.ble.RgbLedService;
import com.robingingras.lightsigncontroller.databinding.ActivityAddDeviceBinding;
import com.robingingras.lightsigncontroller.ui.scanner.ScannerFragment;

public class AddDeviceActivity extends AppCompatActivity implements ScannerFragment.OnDeviceSelectedListener {
    public static final String TAG = AddDeviceActivity.class.getSimpleName();
    public static final String EXTRA_DATA_DEVICE = "EXTRA_DATA_DEVICE";

    private ActivityAddDeviceBinding mBinding;
    private RgbLedService mService;
    private BluetoothDevice mSelectedDevice;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = ActivityAddDeviceBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mService = RgbLedApplication.getInstance().getService();

        mService.getConnectionState().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer state) {
                if (state == RgbLedService.STATE_CONNECTED) {
                    mBinding.newDevice.getRoot().setVisibility(View.GONE);
                    mBinding.newDeviceInfo.getRoot().setVisibility(View.VISIBLE);
                }
                if (state == RgbLedService.STATE_READY) {
                    mService.setModeOff();
                }
            }
        });

        mBinding.newDeviceInfo.buttonFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mService.disconnect();
                Intent result = new Intent();
                result.putExtra(EXTRA_DATA_DEVICE, mSelectedDevice);
                setResult(RESULT_OK, result);
                finish();
            }
        });

        mBinding.newDevice.scanForDevicesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ScannerFragment dialog = ScannerFragment.getInstance(RgbLedDeviceManager.RGBLED_SERVICE_UUID);
                dialog.show(getSupportFragmentManager(), "ScannerFragment");
            }
        });
    }

    @Override
    public void onDeviceSelected(@NonNull BluetoothDevice device) {
        mSelectedDevice = device;
        mBinding.newDevice.scanForDevicesButton.setEnabled(false);
        mService.connect(device);
    }

    @Override
    public void onDialogCancelled() {

    }
}
