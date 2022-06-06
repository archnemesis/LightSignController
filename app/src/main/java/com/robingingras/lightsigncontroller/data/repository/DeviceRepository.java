package com.robingingras.lightsigncontroller.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.robingingras.lightsigncontroller.data.Device;
import com.robingingras.lightsigncontroller.data.DeviceDatabase;

import java.util.List;

public class DeviceRepository {
    private final DeviceDatabase mDatabase;

    public DeviceRepository(DeviceDatabase database) {
        mDatabase = database;
    }

    public LiveData<List<Device>> getAllDevices() {
        return mDatabase.devices().getAll();
    }
}
