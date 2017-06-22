package nyc.jsjrobotics.bluetoothpairing.pairedDevices

import android.arch.lifecycle.*
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice

class PairedDevicesPresenter(val bluetoothAdapter: BluetoothAdapter,
                             val lifecycle: LifecycleRegistry) : LifecycleObserver {
    private var pairedDevices: MutableSet<BluetoothDevice>? = null;
    lateinit private var view: PairedDevicesView

    init {
        lifecycle.addObserver(this)
    }

    fun bindView(newView : PairedDevicesView) {
        view = newView;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun init(source : LifecycleOwner, event : Lifecycle.Event) {
        pairedDevices = bluetoothAdapter.bondedDevices
        view.setDevices(pairedDevices)
    }
}