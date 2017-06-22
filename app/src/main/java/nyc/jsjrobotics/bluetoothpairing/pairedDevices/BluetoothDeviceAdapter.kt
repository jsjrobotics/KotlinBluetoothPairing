package nyc.jsjrobotics.bluetoothpairing.pairedDevices

import android.bluetooth.BluetoothDevice
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

class BluetoothDeviceAdapter(val pairedDevices: Set<BluetoothDevice>) : RecyclerView.Adapter<BluetoothDeviceViewHolder>() {
    override fun onBindViewHolder(holder: BluetoothDeviceViewHolder?, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): BluetoothDeviceViewHolder {
        var inflater : LayoutInflater = LayoutInflater.from(parent!!.context)
        inflater.inflate(R.layout.)
        return BluetoothDeviceViewHolder()
    }

    override fun getItemCount(): Int {
       return pairedDevices.size
    }

}