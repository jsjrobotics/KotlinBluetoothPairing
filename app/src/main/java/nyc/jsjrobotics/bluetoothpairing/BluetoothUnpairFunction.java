package nyc.jsjrobotics.bluetoothpairing;


import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.util.Log;

import java.lang.reflect.Method;

public class BluetoothUnpairFunction {

    // Add potentially as extension function
    // Function to unpair from passed in device
    public static void unpairDevice(BluetoothDevice device) {
        try {
            Method m = device.getClass().getMethod("removeBond", (Class[]) null);
            m.invoke(device, (Object[]) null);
        } catch (Exception e) { Log.e("BluetoothUnpairFunction", e.getMessage()); }
    }

    public static void connect(BluetoothDevice device) {
        try {
            Method m = BluetoothHeadset.class.getMethod("connect", (Class[]) null);
            m.invoke(device, (Object[]) null);
        } catch (Exception e) { Log.e("BluetoothConnectFunction", e.getMessage()); }
    }
}
