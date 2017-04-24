package droidmentor.beacontransmitter.BluetoothHelper;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import droidmentor.beacontransmitter.MyApplication;


/**
 * Created by Jaison on 05/04/17.
 */

public class BluetoothHelper implements BluetoothListener
{

    private BluetoothAdapter mBluetoothAdapter;



    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void initializeBluetooth(OnBluetoothSupportedCheckListener listener) {

        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.

        if (!MyApplication.getAppContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            listener.onBLENotSupported();
            return;
        }

        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) MyApplication.getAppContext().getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            listener.onBluetoothNotSupported();
        }

    }

    @Override
    public void enableBluetooth(OnBluetoothEnabledCheckListener listener) {
        if (mBluetoothAdapter != null) {
            boolean enabled = mBluetoothAdapter.isEnabled();
            listener.onBluetoothEnabled(enabled);
        }
    }
}
