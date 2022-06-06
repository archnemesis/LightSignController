package com.robingingras.lightsigncontroller.ui.scanner;

import android.bluetooth.BluetoothDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.robingingras.lightsigncontroller.R;
import com.robingingras.lightsigncontroller.databinding.DeviceListEmptyBinding;
import com.robingingras.lightsigncontroller.databinding.DeviceListRowBinding;
import com.robingingras.lightsigncontroller.databinding.DeviceListTitleBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import no.nordicsemi.android.support.v18.scanner.ScanResult;

public class DeviceListAdapter extends BaseAdapter {
    private static final int TYPE_TITLE = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_EMPTY = 2;

    private final ArrayList<BluetoothDevice> mBondedDeviceList = new ArrayList<>();
    private final ArrayList<BluetoothDevice> mDeviceList = new ArrayList<>();

    DeviceListAdapter() {

    }

    void addBondedDevices(@NonNull final Set<BluetoothDevice> devices) {
        mBondedDeviceList.clear();
        for (BluetoothDevice device : devices) {
            mBondedDeviceList.add(device);
        }
        notifyDataSetChanged();
    }

    void addNonBondedDevices(@NonNull final List<ScanResult> results) {
        for (final ScanResult result : results) {
            final BluetoothDevice device = findDevice(result);
            if (device == null) {
                mDeviceList.add(result.getDevice());
            }
        }
        notifyDataSetChanged();
    }

    void addNonBondedDevice(@NonNull final ScanResult result) {
        final BluetoothDevice device = findDevice(result);
        if (device == null) {
            mDeviceList.add(result.getDevice());
        }
        notifyDataSetChanged();
    }

    private BluetoothDevice findDevice(@NonNull final ScanResult result) {
        for (final BluetoothDevice device : mBondedDeviceList) {
            if (device.getAddress().equals(result.getDevice().getAddress())) {
                return device;
            }
        }

        for (final BluetoothDevice device : mDeviceList) {
            if (device.getAddress().equals(result.getDevice().getAddress())) {
                return device;
            }
        }

        return null;
    }

    void clearDevices() {
        mDeviceList.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        // add 1 for title
        final int bonded = mBondedDeviceList.size() + 1;
        // 1 for title, 1 for empty text
        final int scanned = mDeviceList.isEmpty() ? 2 : mDeviceList.size() + 1;

        if (bonded == 1) {
            return scanned;
        }

        return bonded + scanned;
    }

    @Override
    public Object getItem(int position) {
        final int bonded = mBondedDeviceList.size() + 1;
        if (mBondedDeviceList.isEmpty()) {
            if (position == 0) {
                return R.string.available_devices;
            }

            return mDeviceList.get(position - 1);
        }
        else {
            if (position == 0) {
                return R.string.bonded_devices;
            }
            else if (position < bonded) {
                return mBondedDeviceList.get(position - 1);
            }
            else if (position == bonded) {
                return R.string.available_devices;
            }

            return mDeviceList.get(position - bonded - 1);
        }
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public int getItemViewType(final int position) {
        if (position == 0) {
            return TYPE_TITLE;
        }

        if (!mBondedDeviceList.isEmpty() && position == mBondedDeviceList.size() + 1) {
            return TYPE_TITLE;
        }

        if (position == getCount() - 1 && mDeviceList.isEmpty()) {
            return TYPE_EMPTY;
        }

        return TYPE_ITEM;
    }

    @Override
    public boolean isEnabled(final int position) {
        return getItemViewType(position) == TYPE_ITEM;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final int type = getItemViewType(position);
        View view = convertView;

        switch (type) {
            case TYPE_EMPTY:
                if (view == null) {
                    view = DeviceListEmptyBinding.inflate(inflater, parent, false).getRoot();
                }
                break;
            case TYPE_TITLE:
                if (view == null) {
                    view = DeviceListTitleBinding.inflate(inflater, parent, false).getRoot();
                }
                final TextView title = (TextView) view;
                title.setText((Integer) getItem(position));
                break;
            default:
                if (view == null) {
                    final DeviceListRowBinding binding = DeviceListRowBinding.inflate(inflater, parent, false);
                    view = binding.getRoot();
                    final ViewHolder holder = new ViewHolder();
                    holder.name = binding.name;
                    holder.address = binding.address;
                    holder.rssi = binding.rssi;
                    view.setTag(holder);
                }

                final BluetoothDevice device = (BluetoothDevice) getItem(position);
                final ViewHolder holder = (ViewHolder) view.getTag();
                final String name = device.getName();
                holder.name.setText(name != null ? name : "N/A");
                holder.address.setText(device.getAddress());

                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    holder.rssi.setVisibility(View.VISIBLE);
                }
                else {
                    holder.rssi.setVisibility(View.GONE);
                }

                break;
        }

        return view;
    }

    private class ViewHolder {
        private TextView name;
        private TextView address;
        private ImageView rssi;
    }
}
