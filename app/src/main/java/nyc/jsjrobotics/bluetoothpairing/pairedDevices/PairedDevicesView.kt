package nyc.jsjrobotics.bluetoothpairing.pairedDevices

import android.bluetooth.BluetoothDevice
import android.support.annotation.StringRes
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import io.reactivex.Observable
import io.reactivex.functions.Consumer
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

    private val adapter: BluetoothDeviceAdapter = BluetoothDeviceAdapter()

    private fun initDeviceList(devices: List<BluetoothDevice>) {
        deviceList = ArrayList(devices);
        adapter.addDevices(devices)
        deviceListView.adapter = adapter;
        deviceListView.layoutManager = LinearLayoutManager(root.context)
    }

    fun onDeviceSelected() : Observable<BluetoothDevice> {
        return adapter.onDeviceSelected()
    }

    fun addDevice(newDevice: BluetoothDevice) {
        if (deviceList.isEmpty()) {
            val singleItemList : ArrayList<BluetoothDevice> = ArrayList()
            singleItemList.add(newDevice)
            initDeviceList(singleItemList)
            updateDeviceListVisibility()
        } else {
            val noDuplicateExists : Boolean = deviceList.filter { seenDevice -> seenDevice.address == newDevice.address }.isEmpty()
            if (noDuplicateExists) {
                deviceList.add(newDevice)
                (deviceListView.adapter as BluetoothDeviceAdapter).addDevices(deviceList)
            }
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

    fun showToast(@StringRes stringResource: Int) {
        Toast.makeText(root.context, stringResource, Toast.LENGTH_LONG).show()
    }

}

