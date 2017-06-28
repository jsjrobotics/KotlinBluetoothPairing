package nyc.jsjrobotics.bluetoothpairing.pairedDevices

import android.arch.lifecycle.*
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.util.Log
import android.widget.Toast
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import nyc.jsjrobotics.bluetoothpairing.BuildConfig
import java.util.*

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
        val subscriptionList : List<Disposable> = Arrays.asList(
                clickStartDiscovery,
                selectBluetoothDevice
        )
        subscriptions.addAll(subscriptionList);

    }

    fun handleSelectedDevice(device : BluetoothDevice) {
        System.out.print("Saw device selected")
    }

    fun addDevice(device :BluetoothDevice) {
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

