package nyc.jsjrobotics.bluetoothpairing;


import android.bluetooth.BluetoothDevice;
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
}
