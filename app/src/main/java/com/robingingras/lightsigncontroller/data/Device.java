package com.robingingras.lightsigncontroller.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "devices")
public class Device {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "device_id")
    protected String mDeviceId;

    public Device(@NonNull final String deviceId) {
        mDeviceId = deviceId;
    }

    public String getDeviceId() {
        return mDeviceId;
    }
}
