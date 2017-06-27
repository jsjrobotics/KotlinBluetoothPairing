package nyc.jsjrobotics.bluetoothpairing.pairedDevices

import android.bluetooth.BluetoothDevice
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import nyc.jsjrobotics.bluetoothpairing.R
import kotlin.collections.ArrayList

class PairedDevicesView(inflater: android.view.LayoutInflater?, container: android.view.ViewGroup?, savedInstanceState: android.os.Bundle?) {
    private val onStartClick: PublishSubject<Boolean> = PublishSubject.create()
    private val onMakeDiscoverableClick: PublishSubject<Boolean> = PublishSubject.create()

    private val root: ViewGroup
    private val deviceListView: RecyclerView
    private val noDevicesMessage: TextView
    private var startDiscovery: Button
    private var makeDiscoverable: Button


    private var requestSearchForDevices: Boolean = true
    private var requestMakeDiscoverable: Boolean = true


    init {
        root = inflater!!.inflate(nyc.jsjrobotics.bluetoothpairing.R.layout.paired_devices, container, false) as ViewGroup
        deviceListView = root.findViewById(R.id.device_list) as RecyclerView
        noDevicesMessage = root.findViewById(R.id.no_devices) as TextView
        startDiscovery = root.findViewById(R.id.startDiscovery) as Button
        startDiscovery.setOnClickListener({ignored -> onStartClick.onNext(requestSearchForDevices)})

        makeDiscoverable = root.findViewById(R.id.makeDiscoverable) as Button
        makeDiscoverable.setOnClickListener({ignored -> onMakeDiscoverableClick.onNext(requestMakeDiscoverable)})
    }


    fun clickStartDiscovery(): Observable<Boolean> {
        return onStartClick
    }

    fun clickMakeDiscoverable(): Observable<Boolean> {
        return onMakeDiscoverableClick
    }

    fun getRoot(): View? {
        return root
    }

    private var deviceList: ArrayList<BluetoothDevice> = ArrayList()

    fun setDevices(pairedDevices: MutableSet<BluetoothDevice>?) {
        deviceList = ArrayList(pairedDevices.orEmpty().toList())
        initDeviceList(deviceList)
        updateDeviceListVisibility()

    }

    private fun updateDeviceListVisibility() {
        if (deviceList.isEmpty()) {
            deviceListView.visibility = View.GONE
            noDevicesMessage.visibility = View.VISIBLE
        }
        else {
            deviceListView.visibility = View.VISIBLE
            noDevicesMessage.visibility = View.GONE
        }
    }

    private fun initDeviceList(devices: List<BluetoothDevice>) {
        deviceList = ArrayList(devices);
        val adapter: BluetoothDeviceAdapter = BluetoothDeviceAdapter(devices)
        deviceListView.adapter = adapter;
        deviceListView.layoutManager = LinearLayoutManager(root.context)
    }

    fun addDevice(device : BluetoothDevice) {
        if (deviceList.isEmpty()) {
            val singleItemList : ArrayList<BluetoothDevice> = ArrayList()
            singleItemList.add(device)
            initDeviceList(singleItemList)
            updateDeviceListVisibility()
        } else {
            deviceList.add(device)
            (deviceListView.adapter as BluetoothDeviceAdapter).updateDevices(deviceList)
        }
    }

    fun showStopDiscoverable(showStopDiscoverable: Boolean) {
        requestMakeDiscoverable = !showStopDiscoverable
        val resources = makeDiscoverable.resources
        if (showStopDiscoverable) {
            makeDiscoverable.text = resources.getString(R.string.stop_make_discoverable)
        } else {
            makeDiscoverable.text = resources.getString(R.string.make_discoverable)
        }
    }

    fun showStopSearching(showStopDiscovery: Boolean) {
        requestSearchForDevices = !showStopDiscovery
        val resources = startDiscovery.resources
        if (showStopDiscovery) {
            startDiscovery.text = resources.getString(R.string.stop_discovery)
        } else {
            startDiscovery.text = resources.getString(R.string.start_search_devices)
        }
    }

}

