package nyc.jsjrobotics.bluetoothpairing.pairedDevices

import android.bluetooth.BluetoothDevice
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import nyc.jsjrobotics.bluetoothpairing.R

class BluetoothDeviceAdapter(var pairedDevices: List<BluetoothDevice>) : RecyclerView.Adapter<BluetoothDeviceViewHolder>() {
    override fun onBindViewHolder(holder: BluetoothDeviceViewHolder, position: Int) {
        holder.bind(pairedDevices[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BluetoothDeviceViewHolder {
        return BluetoothDeviceViewHolder(parent)
    }

    override fun getItemCount(): Int {
       return pairedDevices.size
    }

    fun updateDevices(deviceList: ArrayList<BluetoothDevice>) {
        pairedDevices = deviceList
        notifyDataSetChanged()
    }

}