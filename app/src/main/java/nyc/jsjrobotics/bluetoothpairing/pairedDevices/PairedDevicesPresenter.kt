package nyc.jsjrobotics.bluetoothpairing.pairedDevices

import android.arch.lifecycle.*
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.util.Log
import io.reactivex.Observable
import nyc.jsjrobotics.bluetoothpairing.BuildConfig

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
        view.showStopSearching(enableDiscovery)
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
        subscriptions.add(view.clickStartDiscovery().subscribe(this::handleRequestDiscovery))
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun unRegister(source : LifecycleOwner ) {
        subscriptions.clear()
    }
}

