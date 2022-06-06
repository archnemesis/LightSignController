package com.robingingras.lightsigncontroller.ui.device;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.robingingras.lightsigncontroller.data.Device;
import com.robingingras.lightsigncontroller.databinding.DeviceListItemBinding;

import java.util.List;

public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.ViewHolder> {

    private OnItemClickListener mListener;
    private Context mContext;
    private List<Device> mDeviceList;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(
                DeviceListItemBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Device device = mDeviceList.get(position);
        holder.deviceId.setText(device.getDeviceId());
    }

    @Override
    public int getItemCount() {
        return mDeviceList == null ? 0 : mDeviceList.size();
    }

    public final class ViewHolder extends RecyclerView.ViewHolder {

        TextView deviceId;

        private ViewHolder(final @NonNull DeviceListItemBinding binding) {
            super(binding.getRoot());
            deviceId = binding.deviceId;

            binding.container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {
                        mListener.onItemClick(mDeviceList.get(getAdapterPosition()));
                    }
                }
            });
        }
    }

    @FunctionalInterface
    public interface OnItemClickListener {
        void onItemClick(final Device device);
    }

}
