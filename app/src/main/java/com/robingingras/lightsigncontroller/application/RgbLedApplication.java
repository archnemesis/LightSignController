package com.robingingras.lightsigncontroller.application;

import android.app.Application;

import androidx.room.Room;

import com.robingingras.lightsigncontroller.ble.RgbLedService;
import com.robingingras.lightsigncontroller.data.DeviceDatabase;
import com.robingingras.lightsigncontroller.data.repository.DeviceRepository;

public class RgbLedApplication extends Application {
    public static final String TAG = RgbLedApplication.class.getSimpleName();

    private static RgbLedApplication sApplication;
    private RgbLedService mService;
    private DeviceDatabase mDatabase;
    private DeviceRepository mDeviceRepository;

    @Override
    public void onCreate() {
        super.onCreate();
        sApplication = this;
        mService = new RgbLedService(this);

        mDatabase = Room.databaseBuilder(
                getApplicationContext(),
                DeviceDatabase.class,
                "devicedb").build();

        mDeviceRepository = new DeviceRepository(mDatabase);
    }

    public RgbLedService getService() {
        return mService;
    }

    public DeviceDatabase getDatabase() {
        return mDatabase;
    }

    public DeviceRepository getDeviceRepository() {
        return mDeviceRepository;
    }

    public static RgbLedApplication getInstance() {
        return sApplication;
    }
}
