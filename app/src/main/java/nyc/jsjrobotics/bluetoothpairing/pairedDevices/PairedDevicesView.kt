package nyc.jsjrobotics.bluetoothpairing.pairedDevices

import android.bluetooth.BluetoothDevice
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View

class PairedDevicesView(inflater: android.view.LayoutInflater?, container: android.view.ViewGroup?, savedInstanceState: android.os.Bundle?) {
    private var root: RecyclerView

    init {
        root = inflater!!.inflate(nyc.jsjrobotics.bluetoothpairing.R.layout.paired_devices, container, false) as RecyclerView
    }

    fun getRoot(): View? {
        return root
    }

    fun setDevices(pairedDevices: MutableSet<BluetoothDevice>?) {
        var adapter : BluetoothDeviceAdapter = BluetoothDeviceAdapter(pairedDevices.orEmpty())
        root.adapter = adapter;
        root.layoutManager = LinearLayoutManager(root.context);


    }

}

