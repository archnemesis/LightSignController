package com.robingingras.lightsigncontroller.ui.device;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.robingingras.lightsigncontroller.application.RgbLedApplication;
import com.robingingras.lightsigncontroller.data.Device;
import com.robingingras.lightsigncontroller.data.repository.DeviceRepository;

import java.util.List;

public class DeviceViewModel extends ViewModel {
    private final DeviceRepository mDeviceRepository;

    public DeviceViewModel() {
        mDeviceRepository = RgbLedApplication.getInstance().getDeviceRepository();
    }

    public LiveData<List<Device>> getAllDevices() {
        return mDeviceRepository.getAllDevices();
    }
}
