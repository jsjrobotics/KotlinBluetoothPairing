package nyc.jsjrobotics.bluetoothpairing.bluetooth

import android.bluetooth.BluetoothA2dp
import android.bluetooth.BluetoothHeadset
import android.bluetooth.BluetoothProfile


class ServiceListener : BluetoothProfile.ServiceListener{
    override fun onServiceDisconnected(profile: Int) {
        updateBluetoothProfile(profile, null);
    }

    private var headset: BluetoothHeadset? = null
    private var speaker: BluetoothA2dp? = null

    override fun onServiceConnected(profile: Int, proxy: BluetoothProfile) {
        updateBluetoothProfile(profile, proxy)
    }

    fun updateBluetoothProfile(profile: Int, proxy: BluetoothProfile?) {
        val registerProfile = proxy == null
        when (profile) {
            BluetoothProfile.HEADSET -> {
                if (registerProfile) {
                    headset = proxy as BluetoothHeadset
                } else {
                    headset = null
                }
            }
            BluetoothProfile.A2DP -> {
                if (registerProfile) {
                    speaker = proxy as BluetoothA2dp
                } else {
                    speaker = null
                }

            }
        }
    }

}