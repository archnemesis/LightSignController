package com.robingingras.lightsigncontroller.ble;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import no.nordicsemi.android.ble.observer.BondingObserver;
import no.nordicsemi.android.ble.observer.ConnectionObserver;

public class RgbLedService implements BondingObserver, ConnectionObserver {
    public static final String TAG = RgbLedService.class.getSimpleName();

    public static final int STATE_LINK_LOSS = -1;
    public static final int STATE_DISCONNECTED = 0;
    public static final int STATE_CONNECTED = 1;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_DISCONNECTING = 3;
    public static final int STATE_READY = 4;

    public static final int BONDING_STATE_NOT_REQUIRED = 0;
    public static final int BONDING_STATE_REQUIRED = 1;
    public static final int BONDING_STATE_BONDING = 2;
    public static final int BONDING_STATE_FAILED = 3;
    public static final int BONDING_STATE_BONDED = 4;

    private RgbLedDeviceManager mManager;
    private BluetoothDevice mDevice;

    private MutableLiveData<Integer> mConnectionStateLiveData = new MutableLiveData<>();
    private MutableLiveData<Integer> mBondingStateLiveData = new MutableLiveData<>();

    public RgbLedService(@NonNull final Context context) {
        mManager = new RgbLedDeviceManager(context);
        mManager.setBondingObserver(this);
        mManager.setConnectionObserver(this);

        mConnectionStateLiveData.setValue(STATE_DISCONNECTED);
        mBondingStateLiveData.setValue(BONDING_STATE_NOT_REQUIRED);
    }

    public void connect(BluetoothDevice device) {
        mDevice = device;
        mManager.connect(device)
                .retry(3, 200)
                .enqueue();
    }

    public void disconnect() {
        mManager.disconnect().enqueue();
    }

    public void setModeOff() {
        mManager.setModeOff();
    }

    public LiveData<Integer> getConnectionState() {
        return mConnectionStateLiveData;
    }

    /**
     * Bonding is required by the peer.
     * @param device
     */
    @Override
    public void onBondingRequired(@NonNull BluetoothDevice device) {
        Log.i(TAG, "Bonding is required");
        mBondingStateLiveData.setValue(BONDING_STATE_REQUIRED);
    }

    /**
     * Bonding has been completed.
     * @param device
     */
    @Override
    public void onBonded(@NonNull BluetoothDevice device) {
        Log.i(TAG, "Bonding successful");
        mBondingStateLiveData.setValue(BONDING_STATE_BONDED);
    }

    /**
     * Bonding has failed.
     * @param device
     */
    @Override
    public void onBondingFailed(@NonNull BluetoothDevice device) {
        Log.i(TAG, "Bonding failed");
        mBondingStateLiveData.setValue(BONDING_STATE_FAILED);
    }

    /**
     * Device is connecting.
     * @param device
     */
    @Override
    public void onDeviceConnecting(@NonNull BluetoothDevice device) {
        Log.i(TAG, "Device is connecting");
        mConnectionStateLiveData.setValue(STATE_CONNECTING);
    }

    /**
     * Device is connected.
     * @param device
     */
    @Override
    public void onDeviceConnected(@NonNull BluetoothDevice device) {
        Log.i(TAG, "Device is connected");
        mConnectionStateLiveData.setValue(STATE_CONNECTED);
    }

    /**
     * Device has failed to connect.
     * @param device
     * @param reason
     */
    @Override
    public void onDeviceFailedToConnect(@NonNull BluetoothDevice device, int reason) {
        Log.i(TAG, String.format("Device failed to connect: reason=%d", reason));
        mConnectionStateLiveData.setValue(STATE_DISCONNECTED);
    }

    /**
     * Device is ready for communication.
     * @param device
     */
    @Override
    public void onDeviceReady(@NonNull BluetoothDevice device) {
        Log.i(TAG, "Device is ready");
        mConnectionStateLiveData.setValue(STATE_READY);
    }

    /**
     * Device is disconnecting.
     * @param device
     */
    @Override
    public void onDeviceDisconnecting(@NonNull BluetoothDevice device) {
        Log.i(TAG, "Device is disconnecting");
        mConnectionStateLiveData.setValue(STATE_DISCONNECTING);
    }

    /**
     * Device is disconnected.
     * @param device
     * @param reason
     */
    @Override
    public void onDeviceDisconnected(@NonNull BluetoothDevice device, int reason) {
        Log.i(TAG, String.format("Device is disconnected: reason=%d", reason));
        mConnectionStateLiveData.setValue(STATE_DISCONNECTED);
        mBondingStateLiveData.setValue(BONDING_STATE_NOT_REQUIRED);
    }
}
