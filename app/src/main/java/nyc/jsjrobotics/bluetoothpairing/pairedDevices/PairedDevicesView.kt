package nyc.jsjrobotics.bluetoothpairing.pairedDevices

import android.bluetooth.BluetoothDevice
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import nyc.jsjrobotics.bluetoothpairing.R

class PairedDevicesView(inflater: android.view.LayoutInflater?, container: android.view.ViewGroup?, savedInstanceState: android.os.Bundle?) {
    private val root: FrameLayout
    private val deviceListView: RecyclerView
    private val noDevicesMessage: TextView

    init {
        root = inflater!!.inflate(nyc.jsjrobotics.bluetoothpairing.R.layout.paired_devices, container, false) as FrameLayout
        deviceListView = root.findViewById(R.id.device_list) as RecyclerView
        noDevicesMessage = root.findViewById(R.id.no_devices) as TextView
    }

    fun getRoot(): View? {
        return root
    }

    fun setDevices(pairedDevices: MutableSet<BluetoothDevice>?) {
        val deviceList : List<BluetoothDevice> = pairedDevices.orEmpty().toList()
        if (deviceList.isEmpty()) {
            deviceListView.visibility = View.GONE
            noDevicesMessage.visibility = View.VISIBLE
        }
        else {
            val adapter: BluetoothDeviceAdapter = BluetoothDeviceAdapter(deviceList)
            deviceListView.adapter = adapter;
            deviceListView.layoutManager = LinearLayoutManager(root.context)
            deviceListView.visibility = View.VISIBLE
            noDevicesMessage.visibility = View.GONE
        }

    }

}

