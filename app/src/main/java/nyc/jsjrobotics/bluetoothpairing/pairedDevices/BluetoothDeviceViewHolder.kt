package nyc.jsjrobotics.bluetoothpairing.pairedDevices

import android.bluetooth.BluetoothDevice
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import io.reactivex.functions.Consumer
import nyc.jsjrobotics.bluetoothpairing.R
import nyc.jsjrobotics.bluetoothpairing.inflate
import kotlinx.android.synthetic.main.bluetooth_device_viewholder.view.*


class BluetoothDeviceViewHolder(val parent: ViewGroup,
                                val onSelected: Consumer<BluetoothDevice>,
                                val onUnpairSelected: Consumer<BluetoothDevice>) : RecyclerView.ViewHolder(parent.inflate(LAYOUT_ID)) {

    private val name: TextView
    private var bondState: TextView

    companion object {
        private val LAYOUT_ID : Int = R.layout.bluetooth_device_viewholder
    }

    init {
        name = itemView.contentFrame
        bondState = itemView.bondState
    }



    lateinit private var unpairButton: Button

    fun bind(bluetoothDevice: BluetoothDevice) {
        val hasName = bluetoothDevice.name != null && bluetoothDevice.name.isNotEmpty();
        val name = if (hasName) bluetoothDevice.name else bluetoothDevice.address;
        itemView.contentFrame.setText(name)
        unpairButton = itemView.unpair
        bondState.setText(bondStateToString(bluetoothDevice.bondState))
        if (bluetoothDevice.bondState == BluetoothDevice.BOND_BONDED) {
            showUnpairButton(bluetoothDevice)
        } else {
            hideUnpairButton()
        }
        itemView.setOnClickListener({ignored -> onSelected.accept(bluetoothDevice)})
    }

    private fun hideUnpairButton() {
        unpairButton.visibility = View.GONE
    }

    private fun showUnpairButton(bluetoothDevice: BluetoothDevice) {
        unpairButton.visibility = View.VISIBLE
        unpairButton.setOnClickListener({ignored -> onUnpairSelected.accept(bluetoothDevice)})
    }

    private fun bondStateToString(bondState: Int): String {
        when (bondState) {
            BluetoothDevice.BOND_NONE -> return "BOND NONE"
            BluetoothDevice.BOND_BONDED -> return "BONDED"
            BluetoothDevice.BOND_BONDING -> return "BONDING"
            else -> throw IllegalStateException("Unknown Bond state")
        }
    }
}