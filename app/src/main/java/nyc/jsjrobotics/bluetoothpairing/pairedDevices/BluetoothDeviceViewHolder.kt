package nyc.jsjrobotics.bluetoothpairing.pairedDevices

import android.bluetooth.BluetoothDevice
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import android.widget.TextView
import nyc.jsjrobotics.bluetoothpairing.R
import nyc.jsjrobotics.bluetoothpairing.inflate
import kotlinx.android.synthetic.main.bluetooth_device_viewholder.*
import kotlinx.android.synthetic.main.bluetooth_device_viewholder.view.*


class BluetoothDeviceViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(parent.inflate(LAYOUT_ID)) {
    companion object {
        private val LAYOUT_ID : Int = R.layout.bluetooth_device_viewholder
    }

    private val name: TextView
    private var bondState: TextView
    private val type: TextView

    init {
        name = itemView.contentFrame
        bondState = itemView.bondState
        type = itemView.type
    }



    fun bind(bluetoothDevice: BluetoothDevice) {
        itemView.contentFrame.setText(bluetoothDevice.name)
        bondState.setText(Integer.toString(bluetoothDevice.bondState))
        type.setText(Integer.toString(bluetoothDevice.type))
    }
}