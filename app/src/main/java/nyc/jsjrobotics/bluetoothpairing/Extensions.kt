package nyc.jsjrobotics.bluetoothpairing

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothHeadset
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


typealias FragmentSupplier = () -> Fragment

fun ViewGroup.inflate(layoutId : Int, attachToParent: Boolean = false) : View {
    val inflater = LayoutInflater.from(context)
    return inflater.inflate(layoutId, this, attachToParent)
}

fun BluetoothDevice.unBond() {
    BluetoothUnpairFunction.unpairDevice(this)
    if (true) return
    try {
        val m = this.javaClass.getMethod("removeBond", null )
        m.invoke(this, null)
    } catch (e: Exception) {
        Log.e("BluetoothUnpairFunction", e.message)
    }

}
fun BluetoothDevice.connectHeadset(headset: BluetoothHeadset) : Boolean {
    try {
        val m = BluetoothHeadset::class.java.getMethod("connect", BluetoothDevice::class.java)
        return m.invoke(headset, this) as Boolean
    } catch (e: Exception) {
        Log.e("BtConnectFunction", e.message)
    }
    return false
}