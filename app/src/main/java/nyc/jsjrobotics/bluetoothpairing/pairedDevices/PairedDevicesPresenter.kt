package nyc.jsjrobotics.bluetoothpairing.pairedDevices

import android.arch.lifecycle.*
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.util.Log
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import java.util.*
import android.content.ContentValues.TAG
import android.media.AudioManager
import android.os.ParcelUuid
import io.reactivex.functions.Consumer
import nyc.jsjrobotics.bluetoothpairing.*
import nyc.jsjrobotics.bluetoothpairing.BuildConfig
import nyc.jsjrobotics.bluetoothpairing.R


class PairedDevicesPresenter(val bluetoothAdapter: BluetoothAdapter,
                             val audioManager: AudioManager,
                             val lifecycle: LifecycleRegistry,
                             val setSpeakerToConnectTo: Consumer<BluetoothDevice>) : LifecycleObserver {
    private var pairedDevices: MutableSet<BluetoothDevice>? = null;

    // Values from https://android.googlesource.com/platform/hardware/libhardware/+/jb-mr0-release/include/hardware/audio.h
    private val AUDIO_PARAM_HEADSET_NAME : String = "bt_headset_name"
    private val AUDIO_PARAM_HEADSET_NREC = "bt_headset_nrec"
    private val AUDIO_PARAM_HEADSET_WBS = "bt_wbs"


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

    fun handleUnpairDevice(device : BluetoothDevice) {
        device.unBond()
        view.removeDevice(device)
    }

    fun handleSelectedDevice(device : BluetoothDevice) {
        if (device.bondState == BluetoothDevice.BOND_NONE) {
            view.showToast(R.string.pairing)
            device.createBond()
        } else if (device.bondState == BluetoothDevice.BOND_BONDED){
            view.showToast("Settting device as selected")
            setSpeakerToConnectTo.accept(device)
        } else {
            view.showToast(R.string.bonding)
        }
    }

    // from com/android/bluetooth/hfp/HeadsetStateMachine.java
    private fun setAudioParameters(device: BluetoothDevice) {
        audioManager.setParameters(AUDIO_PARAM_HEADSET_NREC + "=on")
        audioManager.setParameters(AUDIO_PARAM_HEADSET_NAME + "=" + device.name)
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

