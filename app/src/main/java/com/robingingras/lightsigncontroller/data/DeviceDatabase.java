package com.robingingras.lightsigncontroller.data;

import androidx.room.RoomDatabase;

public abstract class DeviceDatabase extends RoomDatabase {
    public abstract DeviceDao devices();
}
