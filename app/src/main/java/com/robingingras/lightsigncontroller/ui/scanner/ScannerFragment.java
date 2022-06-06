package com.robingingras.lightsigncontroller.ui.scanner;

import android.Manifest;
import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.robingingras.lightsigncontroller.R;
import com.robingingras.lightsigncontroller.databinding.FragmentDeviceSelectionBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat;
import no.nordicsemi.android.support.v18.scanner.ScanCallback;
import no.nordicsemi.android.support.v18.scanner.ScanFilter;
import no.nordicsemi.android.support.v18.scanner.ScanResult;
import no.nordicsemi.android.support.v18.scanner.ScanSettings;

public class ScannerFragment extends DialogFragment {
    private static final String TAG = ScannerFragment.class.getSimpleName();
    private static final long SCAN_DURATION = 30000;

    public static final String PARAM_SERVICE_UUID = "PARAM_SERVICE_UUID";

    private ParcelUuid mServiceUUID;
    private BluetoothLeScannerCompat mScanner;
    private Button mScanButton;
    private OnDeviceSelectedListener mDeviceSelectedListener;
    private FragmentDeviceSelectionBinding mBinding;
    private DeviceListAdapter mDeviceAdapter;
    private ActivityResultLauncher<String[]> mRequestLocationPermissions;
    private boolean mScanning = false;
    private final Handler mHandler = new Handler();

    public static ScannerFragment getInstance(final UUID uuid) {
        final ScannerFragment fragment = new ScannerFragment();
        final Bundle args = new Bundle();

        if (uuid != null) {
            args.putParcelable(PARAM_SERVICE_UUID, new ParcelUuid(uuid));
        }

        fragment.setArguments(args);
        return fragment;
    }

    public interface OnDeviceSelectedListener {
        void onDeviceSelected(@NonNull final BluetoothDevice device);
        void onDialogCancelled();
    }

    @Override
    public void onAttach(@NonNull final Context context) throws ClassCastException {
        super.onAttach(context);
        mDeviceSelectedListener = (OnDeviceSelectedListener) context;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Bundle args = getArguments();
        if (args != null && args.containsKey(PARAM_SERVICE_UUID)) {
            mServiceUUID = args.getParcelable(PARAM_SERVICE_UUID);
        }

        //
        // Android 12 has new Bluetooth permissions
        //
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            mRequestLocationPermissions = registerForActivityResult(
                    new ActivityResultContracts.RequestMultiplePermissions(),
                    new ActivityResultCallback<Map<String, Boolean>>() {
                        @Override
                        public void onActivityResult(Map<String, Boolean> result) {
                            Boolean scan = result.get(Manifest.permission.BLUETOOTH_SCAN);
                            Boolean cnct = result.get(Manifest.permission.BLUETOOTH_CONNECT);
                            if (scan != null && cnct != null) {
                                if (scan && cnct) {
                                    startScan();
                                    return;
                                }
                            }
                            mBinding.permissionRationale.setVisibility(View.VISIBLE);
                            Toast.makeText(
                                    getActivity(),
                                    "Permission denied.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
            );
        }
        else {
            mRequestLocationPermissions = registerForActivityResult(
                    new ActivityResultContracts.RequestMultiplePermissions(),
                    new ActivityResultCallback<Map<String, Boolean>>() {
                        @Override
                        public void onActivityResult(Map<String, Boolean> result) {
                            Boolean loc = result.get(Manifest.permission.ACCESS_FINE_LOCATION);
                            if (loc != null && loc) {
                                startScan();
                                return;
                            }

                            mBinding.permissionRationale.setVisibility(View.VISIBLE);
                            Toast.makeText(
                                    getActivity(),
                                    "Permission denied.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
            );
        }
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        mBinding = FragmentDeviceSelectionBinding.inflate(LayoutInflater.from(requireContext()));
        mBinding.list.setEmptyView(mBinding.getRoot().findViewById(android.R.id.empty));

        mDeviceAdapter = new DeviceListAdapter();
        mBinding.list.setAdapter(mDeviceAdapter);

        final AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Select Device");
        builder.setView(mBinding.getRoot());
        final AlertDialog dialog = builder.create();

        mBinding.list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                stopScan();
                dialog.dismiss();
                final BluetoothDevice device = (BluetoothDevice) mDeviceAdapter.getItem(position);
                mDeviceSelectedListener.onDeviceSelected(device);
            }
        });

        mBinding.actionCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mScanning) {
                    dialog.cancel();
                }
                else {
                    startScan();
                }
            }
        });

        if (savedInstanceState == null) {
            startScan();
        }

        return dialog;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopScan();
    }

    protected void startScan() {

        //
        // Android 12 has new Bluetooth permissions
        //
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Log.i(TAG, "Getting BLUETOOTH_SCAN and BLUETOOTH_CONNECT permissions for Android 12+");
            int perm_scan = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_SCAN);
            int perm_connect = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_CONNECT);

            if (perm_scan != PackageManager.PERMISSION_GRANTED ||
                    perm_connect != PackageManager.PERMISSION_GRANTED)
            {
                mRequestLocationPermissions.launch(
                        new String[]{
                                Manifest.permission.BLUETOOTH_CONNECT,
                                Manifest.permission.BLUETOOTH_SCAN,
                                Manifest.permission.ACCESS_FINE_LOCATION});
                return;
            }
        }
        else {
            int perm = ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION);
            if (perm != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                    if (mBinding.permissionRationale.getVisibility() == View.GONE) {
                        mBinding.permissionRationale.setVisibility(View.VISIBLE);
                        return;
                    }
                }

                mRequestLocationPermissions.launch(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,});
                return;
            }
        }

        addBoundDevices();

        mBinding.permissionRationale.setVisibility(View.GONE);

        mDeviceAdapter.clearDevices();
        mBinding.actionCancel.setText("Cancel");

        BluetoothLeScannerCompat scanner = BluetoothLeScannerCompat.getScanner();

        final ScanSettings settings = new ScanSettings.Builder()
//                .setLegacy(false)
//                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
//                .setReportDelay(250)
//                .setUseHardwareBatchingIfSupported(true)
                .setLegacy(false)
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .setReportDelay(0)
                .setUseHardwareFilteringIfSupported(false)
                .setUseHardwareBatchingIfSupported(false)
                .build();

        final List<ScanFilter> filters = new ArrayList<>();

        if (mServiceUUID != null) {
            filters.add(new ScanFilter.Builder().setServiceUuid(mServiceUUID).build());
        }

        mScanning = true;
        scanner.startScan(filters, settings, mScanCallback);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mScanning) {
                    stopScan();
                }
            }
        }, SCAN_DURATION);
    }

    protected void stopScan() {
        Log.i(TAG, "Request scan stop");
        if (mScanning) {
            mBinding.actionCancel.setText(R.string.scan);
            BluetoothLeScannerCompat scanner = BluetoothLeScannerCompat.getScanner();
            scanner.stopScan(mScanCallback);
            mScanning = false;
        }
    }

    protected void addBoundDevices() {
        final BluetoothManager manager = (BluetoothManager) requireContext().getSystemService(Context.BLUETOOTH_SERVICE);
        final Set<BluetoothDevice> devices = manager.getAdapter().getBondedDevices();
        mDeviceAdapter.addBondedDevices(devices);
    }

    private final ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            Log.i(TAG, String.format("Got scan result: %s", result.getDevice().getName()));
            mDeviceAdapter.addNonBondedDevice(result);
        }

        @Override
        public void onBatchScanResults(@NonNull final List<ScanResult> results) {
            Log.i(TAG, String.format("Got scan results: %d", results.size()));
            //mDeviceAdapter.addNonBondedDevices(results);
        }
    };
}
