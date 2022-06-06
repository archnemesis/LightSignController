package com.robingingras.lightsigncontroller.ui.device;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.robingingras.lightsigncontroller.databinding.ActivityDeviceListBinding;

public class DeviceListActivity extends AppCompatActivity {
    private DeviceViewModel mViewModel;
    private ActivityDeviceListBinding mBinding;
    private DeviceListAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = ActivityDeviceListBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
    }
}
