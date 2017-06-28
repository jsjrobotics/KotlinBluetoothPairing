package nyc.jsjrobotics.bluetoothpairing.pairedDevices

import android.bluetooth.BluetoothDevice
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import android.widget.TextView
import io.reactivex.functions.Consumer
import nyc.jsjrobotics.bluetoothpairing.R
import nyc.jsjrobotics.bluetoothpairing.inflate
import kotlinx.android.synthetic.main.bluetooth_device_viewholder.*
import kotlinx.android.synthetic.main.bluetooth_device_viewholder.view.*


class BluetoothDeviceViewHolder(val parent: ViewGroup,
                                val onSelected: Consumer<BluetoothDevice>) : RecyclerView.ViewHolder(parent.inflate(LAYOUT_ID)) {

    companion object {
        private val LAYOUT_ID : Int = R.layout.bluetooth_device_viewholder
    }

    private val name: TextView
    private var bondState: TextView


    fun bind(bluetoothDevice: BluetoothDevice) {
        val hasName = bluetoothDevice.name != null && bluetoothDevice.name.isNotEmpty();
        val name = if (hasName) bluetoothDevice.name else bluetoothDevice.address;
        itemView.contentFrame.setText(name)
        bondState.setText(bondStateToString(bluetoothDevice.bondState))
        itemView.setOnClickListener({ignored -> onSelected.accept(bluetoothDevice)})
    }

    private fun bondStateToString(bondState: Int): String {
        when (bondState) {
            BluetoothDevice.BOND_NONE -> return "BOND NONE"
            BluetoothDevice.BOND_BONDED -> return "BONDED"
            BluetoothDevice.BOND_BONDING -> return "BONDING"
            else -> throw IllegalStateException("Unknown Bond state")
        }
    }

    init {
        name = itemView.contentFrame
        bondState = itemView.bondState
    }
}