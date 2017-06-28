package nyc.jsjrobotics.bluetoothpairing.pairedDevices

import android.arch.lifecycle.*
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.util.Log
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import nyc.jsjrobotics.bluetoothpairing.BuildConfig
import java.util.*
import nyc.jsjrobotics.bluetoothpairing.R
import nyc.jsjrobotics.bluetoothpairing.BluetoothUnpairFunction


class PairedDevicesPresenter(val bluetoothAdapter: BluetoothAdapter,
                             val lifecycle: LifecycleRegistry) : LifecycleObserver {
    private var pairedDevices: MutableSet<BluetoothDevice>? = null;
    lateinit private var view: PairedDevicesView

    init {
        lifecycle.addObserver(this)
    }

    private val subscriptions: SubscriptionsManager = SubscriptionsManager()


    fun bindView(newView : PairedDevicesView) {
        view = newView;
    }

    fun handleRequestDiscovery(enableDiscovery : Boolean) {
        showStopSearching(enableDiscovery)
        if (enableDiscovery) {
            log("Enabling Discovery")
            bluetoothAdapter.startDiscovery()
        } else {
            log("Cancelling Discovery")
            bluetoothAdapter.cancelDiscovery()
        }
    }

    fun onMakeDiscoverable(): Observable<Boolean> {
        return view.clickMakeDiscoverable()
    }

    fun showStopDiscoverable(enableDiscoverable: Boolean) {
        view.showStopDiscoverable(enableDiscoverable)

    }

    private fun log(s: String) {
        if (BuildConfig.DEBUG) {
            Log.w("PairedDevicesPresenter", s)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun init(source : LifecycleOwner, event : Lifecycle.Event) {
        pairedDevices = bluetoothAdapter.bondedDevices
        view.setDevices(pairedDevices)
        val clickStartDiscovery : Disposable = view.clickStartDiscovery().subscribe(this::handleRequestDiscovery)
        val selectBluetoothDevice : Disposable = view.onDeviceSelected().subscribe(this::handleSelectedDevice)
        val unpairBluetoothDevice : Disposable = view.onUnpairSelected().subscribe(this::handleUnpairDevice)
        val subscriptionList : List<Disposable> = Arrays.asList(
                clickStartDiscovery,
                selectBluetoothDevice,
                unpairBluetoothDevice
        )
        subscriptions.addAll(subscriptionList);

    }

    private val TEMP_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    private val UUID_2 : UUID = UUID.fromString("0000111E-0000-1000-8000-00805F9B34FB")

    fun handleUnpairDevice(device : BluetoothDevice) {
        BluetoothUnpairFunction.unpairDevice(device)
        view.removeDevice(device)
    }

    fun handleSelectedDevice(device : BluetoothDevice) {
        if (device.bondState == BluetoothDevice.BOND_NONE) {
            view.showToast(R.string.pairing)
            device.createBond()
        } else if (device.bondState == BluetoothDevice.BOND_BONDED){
            view.showToast(R.string.already_bonded)
            connectToDevice(device)
        } else {
            view.showToast(R.string.bonding)
        }
    }

    private fun connectToDevice(device: BluetoothDevice) {
        try {
            var socket = device.createInsecureRfcommSocketToServiceRecord(UUID_2)
            socket.connect()
        } catch (e : Exception) {
            view.showToast("Failed to connect on socket")
            return
        }
        view.showToast("Successfully connection")
    }

    fun addDevice(device : BluetoothDevice) {
        view.addDevice(device)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun unRegister(source : LifecycleOwner ) {
        subscriptions.clear()
    }

    fun showStopSearching(showStopSearching: Boolean) {
        view.showStopSearching(showStopSearching)
    }
}

