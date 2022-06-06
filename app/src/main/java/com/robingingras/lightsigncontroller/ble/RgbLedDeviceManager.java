package com.robingingras.lightsigncontroller.ble;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.UUID;

import no.nordicsemi.android.ble.BleManager;
import no.nordicsemi.android.ble.callback.DataSentCallback;
import no.nordicsemi.android.ble.data.Data;

public class RgbLedDeviceManager extends BleManager {
    public static final String TAG = RgbLedDeviceManager.class.getSimpleName();

    public static final UUID RGBLED_SERVICE_UUID        = UUID.fromString("bd3664a8-9fc1-4f80-8ba4-1945e4149147");
    public static final UUID RGBLED_CHAR_COMMAND_UUID   = UUID.fromString("7226b7ea-5636-4479-9fe6-5c845ba91b81");
    public static final UUID RGBLED_CHAR_DATA_UUID      = UUID.fromString("3d00904c-6e4b-4a2e-88e9-22c9d5706763");

    private BluetoothGattCharacteristic mRgbCommandCharacteristic;
    private BluetoothGattCharacteristic mRgbDataCharacteristic;

    public RgbLedDeviceManager(@NonNull Context context) {
        super(context);
    }

    @NonNull
    @Override
    protected BleManagerGattCallback getGattCallback() {
        return new BleManagerGattCallback() {
            @Override
            protected void initialize() {
                Log.i(TAG, "Initializing BLE GATT...");
            }

            @Override
            protected boolean isRequiredServiceSupported(@NonNull BluetoothGatt gatt) {
                BluetoothGattService rgbledService = gatt.getService(RGBLED_SERVICE_UUID);
                if (rgbledService == null) {
                    return false;
                }

                mRgbCommandCharacteristic = rgbledService.getCharacteristic(RGBLED_CHAR_COMMAND_UUID);
                mRgbDataCharacteristic = rgbledService.getCharacteristic(RGBLED_CHAR_DATA_UUID);

                if (mRgbCommandCharacteristic == null || mRgbDataCharacteristic == null) {
                    return false;
                }

                Log.i(TAG, "Required GATT services have been found");

                return true;
            }

            @Override
            protected void onServicesInvalidated() {
                mRgbCommandCharacteristic = null;
                mRgbDataCharacteristic = null;
            }
        };
    }

    @Override
    protected boolean shouldClearCacheWhenDisconnected() {
        return true;
    }

    /**
     * Tell the controller to update the LED/display state.
     */
    public void setModeOff() {
        writeCharacteristic(
                mRgbCommandCharacteristic,
                RgbLedCommandData.setMode(RgbLedCommandData.Mode.MODE_OFF),
                BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
        ).with(new DataSentCallback() {
            @Override
            public void onDataSent(@NonNull BluetoothDevice device, @NonNull Data data) {
                Log.i(TAG, "Mode OFF command sent");
            }
        }).enqueue();
    }

    /**
     * Tell the controller to enter or leave sleep mode.
     */
    public void setModeStatic() {
        writeCharacteristic(
                mRgbCommandCharacteristic,
                RgbLedCommandData.setMode(RgbLedCommandData.Mode.MODE_STATIC),
                BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
        ).with(new DataSentCallback() {
            @Override
            public void onDataSent(@NonNull BluetoothDevice device, @NonNull Data data) {
                Log.i(TAG, "Mode STATIC command sent");
            }
        }).enqueue();
    }
}
