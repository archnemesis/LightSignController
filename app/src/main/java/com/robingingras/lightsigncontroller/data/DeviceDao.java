package com.robingingras.lightsigncontroller.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DeviceDao {
    @Query("SELECT * FROM devices")
    LiveData<List<Device>> getAll();
}
